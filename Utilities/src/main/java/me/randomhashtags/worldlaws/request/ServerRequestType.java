package me.randomhashtags.worldlaws.request;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.Jsonable;

public interface ServerRequestType extends Jsonable {
    String name();
    default WLHttpHandler getHandler(APIVersion version) {
        return null;
    }
    default boolean isConditional() {
        return false;
    }
}
