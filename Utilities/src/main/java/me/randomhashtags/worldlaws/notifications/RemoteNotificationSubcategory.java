package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface RemoteNotificationSubcategory extends Jsonable, RestAPI {
    String name();

    RemoteNotificationCategory getCategory();
    String getName();
    default boolean isConditional() {
        return false;
    }
    default JSONObjectTranslatable getAllValues() {
        return null;
    }

    default String getID() {
        return name().toLowerCase();
    }

    default JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name", "allValues");
        json.put("name", getName());
        final JSONObjectTranslatable allValues = getAllValues();
        if(allValues != null && !allValues.isEmpty()) {
            json.put("allValues", allValues);
        }
        return json;
    }
}
