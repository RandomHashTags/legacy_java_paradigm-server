package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class MLBTeam {

    public static MLBTeam parse(JSONObject json) {
        return new MLBTeam(json);
    }

    private final String name, scheduleURL, logoURL;

    public MLBTeam(String name, String scheduleURL, String logoURL) {
        this.name = LocalServer.fixEscapeValues(name);
        this.scheduleURL = scheduleURL;
        this.logoURL = logoURL;
    }
    private MLBTeam(JSONObject json) {
        name = json.getString("name");
        scheduleURL = json.getString("scheduleURL");
        logoURL = json.getString("logoURL");
    }

    public String getName() {
        return name;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", name);
        json.put("scheduleURL", scheduleURL);
        json.put("logoURL", logoURL);
        return json;
    }
}
