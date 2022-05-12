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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum ServerRequestTypeRemoteNotifications implements ServerRequestType {
    CATEGORIES,

    PUSH,
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

                case PUSH:
                    if(httpExchange.getActualRequestMethod() == RequestMethod.POST && Settings.Server.getUUID().equals(httpExchange.getIdentifier())) {
                        return push(httpExchange.getRequestBodyJSON());
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
                        if(values.length == 4) {
                            controller.register(subcategory, token);
                        } else {
                            final String conditionalValue = values[4].replace(" ", "_");
                            controller.register(subcategory, token, conditionalValue);
                        }
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


    private JSONObjectTranslatable push(JSONObject requestBody) {
        if(requestBody != null && requestBody.has("remote_notifications")) {
            final JSONArray array = requestBody.getJSONArray("remote_notifications");
            final HashSet<RemoteNotification> parsedNotifications = new HashSet<>();
            for(Object obj : array) {
                final JSONObject json = (JSONObject) obj;
                final RemoteNotification notification = new RemoteNotification(json);
                parsedNotifications.add(notification);
            }
            final HashMap<RemoteNotificationDeviceTokenController, HashSet<RemoteNotification>> sendableNotifications = new HashMap<>();
            for(DeviceTokenType tokenType : DeviceTokenType.values()) {
                final RemoteNotificationDeviceTokenController controller = tokenType.getController();
                if(controller != null) {
                    controller.update();
                    final HashSet<RemoteNotification> sendable = new HashSet<>();
                    for(RemoteNotification notification : parsedNotifications) {
                        if(controller.shouldSendNotification(notification)) {
                            notification.save();
                            sendable.add(notification);
                        }
                    }
                    if(!sendable.isEmpty()) {
                        sendableNotifications.put(controller, sendable);
                    }
                }
            }

            final int amount = sendableNotifications.size();
            if(amount > 0) {
                WLLogger.logInfo("RemoteNotifications - sending " + amount + " remote notification" + (amount > 1 ? "s" : ""));
                for(Map.Entry<RemoteNotificationDeviceTokenController, HashSet<RemoteNotification>> entry : sendableNotifications.entrySet()) {
                    final RemoteNotificationDeviceTokenController controller = entry.getKey();
                    final HashSet<RemoteNotification> sendable = entry.getValue();
                    new CompletableFutures<RemoteNotification>().stream(sendable, controller::sendNotification);
                }
                return new JSONObjectTranslatable();
            }
        }
        return null;
    }
}
