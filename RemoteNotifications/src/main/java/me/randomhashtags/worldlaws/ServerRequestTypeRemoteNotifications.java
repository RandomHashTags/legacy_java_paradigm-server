package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

public enum ServerRequestTypeRemoteNotifications implements ServerRequestType {
    CATEGORIES,

    PUSH_PENDING,
    REGISTER,
    UNREGISTER,
    IS_REGISTERED,
    GET_REGISTERED,
    DELETE,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        return httpExchange -> {
            final String[] values = httpExchange.getShortPathValues();
            switch (this) {
                case CATEGORIES:
                    return getCategories();

                case PUSH_PENDING:
                    if(httpExchange.getIdentifier().equals(Settings.Server.getUUID())) {
                        pushPending();
                    }
                    return null;
                case REGISTER:
                    return handleDeviceToken(values, true);
                case UNREGISTER:
                    return handleDeviceToken(values, false);
                case GET_REGISTERED:
                    return getAllRegisteredSubcategories(values);
                case DELETE:
                    switch (values[0]) {
                        case "apple": return delete(AppleNotifications.INSTANCE, values);
                        default: return null;
                    }
                default:
                    return null;
            }
        };
    }
    public static JSONObjectTranslatable getCategories() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("version", ResponseVersions.REMOTE_NOTIFICATIONS.getValue());
        for(RemoteNotificationCategory category : RemoteNotificationCategory.values()) {
            final RemoteNotificationSubcategory[] subcategories = category.getSubcategories();
            if(subcategories != null) {
                final JSONObjectTranslatable categoryJSON = new JSONObjectTranslatable("subcategories");
                categoryJSON.put("name", category.getName());
                final JSONObjectTranslatable subcategoriesJSON = new JSONObjectTranslatable();
                for(RemoteNotificationSubcategory subcategory : subcategories) {
                    final String id = subcategory.getID();
                    subcategoriesJSON.put(id, subcategory.toJSONObject());
                    subcategoriesJSON.addTranslatedKey(id);
                }
                categoryJSON.put("subcategories", subcategoriesJSON);
                final String id = category.getID();
                json.put(id, categoryJSON);
                json.addTranslatedKey(id);
            }
        }
        return json;
    }

    private JSONTranslatable handleDeviceToken(String[] values, boolean register) {
        final DeviceTokenType tokenType = DeviceTokenType.valueOfString(values[0]);
        if(tokenType != null) {
            final RemoteNotificationDeviceTokenController controller = tokenType.getController();
            final RemoteNotificationCategory category = RemoteNotificationCategory.valueOfString(values[1]);
            if(controller != null && category != null) {
                final RemoteNotificationSubcategory subcategory = category.valueOfSubcategory(values[2]);
                if(subcategory != null) {
                    final String token = values[3];
                    if(register) {
                        controller.register(subcategory, token);
                    } else {
                        controller.unregister(subcategory, token);
                    }
                    final JSONObjectTranslatable json = new JSONObjectTranslatable();
                    json.put("value", true);
                    return json;
                }
            }
        }
        return null;
    }
    private JSONObjectTranslatable getAllRegisteredSubcategories(String[] values) {
        final DeviceTokenType tokenType = DeviceTokenType.valueOfString(values[0]);
        if(tokenType != null) {
            final RemoteNotificationDeviceTokenController controller = tokenType.getController();
            if(controller != null) {
                final JSONObjectTranslatable json = new JSONObjectTranslatable();
                final String token = values[2];
                for(RemoteNotificationCategory category : RemoteNotificationCategory.values()) {
                    final RemoteNotificationSubcategory[] subcategories = category.getSubcategories();
                    if(subcategories != null) {
                        final JSONArrayTranslatable subcategoriesArray = new JSONArrayTranslatable();
                        for(RemoteNotificationSubcategory subcategory : subcategories) {
                            final boolean isRegistered = controller.isRegistered(subcategory, token);
                            if(isRegistered) {
                                subcategoriesArray.put(subcategory.getID());
                            }
                        }
                        if(!subcategoriesArray.isEmpty()) {
                            json.put(category.getID(), subcategoriesArray);
                        }
                    }
                }
                return json;
            }
        }
        return null;
    }
    private JSONObjectTranslatable delete(RemoteNotificationDeviceTokenController controller, String[] values) {
        final String token = values[1];
        for(RemoteNotificationCategory category : RemoteNotificationCategory.values()) {
            final RemoteNotificationSubcategory[] subcategories = category.getSubcategories();
            if(subcategories != null) {
                for(RemoteNotificationSubcategory subcategory : subcategories) {
                    controller.unregister(subcategory, token);
                }
            }
        }
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("value", true);
        return json;
    }

    @Override
    public boolean isConditional() {
        switch (this) {
            case REGISTER:
            case UNREGISTER:
            case IS_REGISTERED:
            case GET_REGISTERED:
            case DELETE:
                return true;
            default:
                return false;
        }
    }


    private void pushPending() {
        final Instant nowInstant = Instant.now();
        final LocalDate now = LocalDate.now();
        final int year = now.getYear(), day = now.getDayOfMonth();
        final Month month = now.getMonth();
        final Folder folder = Folder.REMOTE_NOTIFICATIONS;
        final String folderFileName = folder.getFolderName().replace("%year%", Integer.toString(year)).replace("%month%", month.name()).replace("%day%", Integer.toString(day));
        folder.setCustomFolderName("recentRemoteNotifications", folderFileName);
        final HashSet<Path> files = folder.getAllFilePaths("recentRemoteNotifications");
        int amount = 0;
        if(!files.isEmpty()) {
            amount = files.size();
            final HashSet<RemoteNotification> notifications = new HashSet<>();
            for(Path path : files) {
                final String fileName = path.getFileName().toString();
                try {
                    final BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                    if(attributes.creationTime().toInstant().plusSeconds(60).isAfter(nowInstant)) {
                        final JSONObject json = getJSONObject(folder, fileName, null);
                        if(json != null) {
                            final RemoteNotification notification = new RemoteNotification(json);
                            notifications.add(notification);
                        }
                    }
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
            }
            final AppleNotifications apple = AppleNotifications.INSTANCE;
            apple.tryUpdatingToken();
            new CompletableFutures<RemoteNotification>().stream(notifications, apple::sendNotification);
        }
        WLLogger.logInfo("RemoteNotifications - sending " + amount + " remote notification" + (amount > 1 ? "s" : ""));
    }
}
