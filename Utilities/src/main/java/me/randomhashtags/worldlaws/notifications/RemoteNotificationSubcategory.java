package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface RemoteNotificationSubcategory {
    String name();

    RemoteNotificationCategory getCategory();
    String getName();

    default String getID() {
        return name().toLowerCase();
    }

    default JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", getName());
        return json;
    }
}
