package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;

public interface DeviceTokenController extends RestAPI, Jsonable {
    void save();
    void register(String deviceToken);
    void unregister(String deviceToken);
    void sendNotification(RemoteNotification notification);
}
