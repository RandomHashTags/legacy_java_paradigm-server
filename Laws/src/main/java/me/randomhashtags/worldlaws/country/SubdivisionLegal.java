package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface SubdivisionLegal {
    String getID();
    String getTitle();
    void setTitle(String title);
    JSONObjectTranslatable toJSONObject();
}
