package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class MLBTeamObj {
    private final String name, scheduleURL, logoURL, wikipediaURL;

    public MLBTeamObj(JSONObject json) {
        name = json.getString("name");
        scheduleURL = json.getString("scheduleURL");
        logoURL = json.optString("logoURL", null);
        wikipediaURL = json.optString("wikipediaURL", null);
    }
    public MLBTeamObj(String name, String scheduleURL, String logoURL, String wikipediaURL) {
        this.name = name;
        this.scheduleURL = scheduleURL;
        this.logoURL = logoURL;
        this.wikipediaURL = wikipediaURL;
    }


    public String getName() {
        return name;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", name);
        json.put("scheduleURL", scheduleURL);
        json.put("logoURL", logoURL);
        if(wikipediaURL != null) {
            json.put("wikipediaURL", wikipediaURL);
        }
        return json;
    }
}
