package me.randomhashtags.worldlaws.request.server;

import me.randomhashtags.worldlaws.request.ServerRequestType;

public enum ServerRequestTypeRemoteNotifications implements ServerRequestType {
    PUSH_PENDING,
    REGISTER,
    UNREGISTER,
    ;
}
