package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.UUID;

public final class RemoteNotifications implements WLServer {

    public static void main(String[] args) {
        new RemoteNotifications().tryLoading();
    }

    private void tryLoading() {
        //test();
        load();
    }

    private void test() {
        final UUID uuid = UUID.randomUUID();
        WLLogger.logInfo("RemoteNotifications;uuid=" + uuid.toString());
    }

    @Override
    public void stop() {
        AppleNotifications.INSTANCE.save();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.REMOTE_NOTIFICATIONS;
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, String target) {
        final String[] values = target.split("/");
        switch (values[0]) {
            case "register":
                String token = values[2];
                switch (values[1]) {
                    case "apple":
                        AppleNotifications.INSTANCE.register(token);
                        break;
                    case "google":
                        break;
                    default:
                        break;
                }
                break;
            case "unregister":
                token = values[2];
                switch (values[1]) {
                    case "apple":
                        AppleNotifications.INSTANCE.unregister(token);
                        break;
                    default:
                        break;
                }
                break;
            case "trySendingNew":
                if(identifier.equals(Settings.Server.getUUID())) {
                    trySendingNew();
                }
                break;
            default:
                break;
        }
        return null;
    }

    private void trySendingNew() {
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
            for(RemoteNotification notification : notifications) {
                new Thread(() -> AppleNotifications.INSTANCE.sendNotification(notification)).start();
            }
        }
        WLLogger.logInfo("RemoteNotifications - sending " + amount + " remote notifications");
    }
}
