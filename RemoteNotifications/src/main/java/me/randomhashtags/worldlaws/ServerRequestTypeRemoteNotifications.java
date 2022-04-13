package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
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
    PUSH_PENDING,
    REGISTER,
    UNREGISTER,
    ;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        return httpExchange -> {
            final String[] values = httpExchange.getShortPathValues();
            switch (this) {
                case PUSH_PENDING:
                    if(httpExchange.getIdentifier().equals(Settings.Server.getUUID())) {
                        pushPending();
                    }
                    return null;
                case REGISTER:
                    switch (values[0]) {
                        case "apple":
                            handleDeviceToken(AppleNotifications.INSTANCE, values, true);
                            return null;
                        case "google":
                            return null;
                        default:
                            return null;
                    }
                case UNREGISTER:
                    switch (values[0]) {
                        case "apple":
                            handleDeviceToken(AppleNotifications.INSTANCE, values, false);
                            return null;
                        default:
                            return null;
                    }
                default:
                    return null;
            }
        };
    }
    private void handleDeviceToken(DeviceTokenController controller, String[] values, boolean register) {
        final String token = values[2];
        final RemoteNotificationCategory category = RemoteNotificationCategory.valueOfString(values[1]);
        if(category != null) {
            if(register) {
                controller.register(category, token);
            } else {
                controller.unregister(category, token);
            }
        }
    }

    @Override
    public boolean isConditional() {
        switch (this) {
            case REGISTER:
            case UNREGISTER:
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
        if(files != null) {
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
            new CompletableFutures<RemoteNotification>().stream(notifications, apple::sendNotification);
        }
        WLLogger.logInfo("RemoteNotifications - sending " + amount + " remote notifications");
    }
}
