package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;

public interface DeviceTokenController extends RestAPI, Jsonable {
    void save();
    void register(RemoteNotificationCategory category, String deviceToken);
    void unregister(RemoteNotificationCategory category, String deviceToken);
    void sendNotification(RemoteNotification notification);
}
