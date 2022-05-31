package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface BillStatus {
    String getID();
    String getName();
    String getPageName();

    default JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", getName());
        json.put("pageName", getPageName());
        return json;
    }
}
