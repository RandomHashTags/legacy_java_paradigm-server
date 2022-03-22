package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;
import org.json.JSONObject;

public final class MovieProductionCompanyDetails {

    public static MovieProductionCompanyDetails parse(JSONObject json) {
        return new MovieProductionCompanyDetails(json);
    }

    private final String[] aliases;
    private final String description, imageURL;
    private final EventSources sources;

    public MovieProductionCompanyDetails(String[] aliases, String description, String imageURL, EventSources sources) {
        this.aliases = aliases;
        this.description = LocalServer.fixEscapeValues(LocalServer.removeWikipediaReferences(LocalServer.removeWikipediaTranslations(description)));
        this.imageURL = imageURL;
        this.sources = sources;
    }
    private MovieProductionCompanyDetails(JSONObject json) {
        final JSONArray aliasesArray = json.optJSONArray("aliases");
        aliases = aliasesArray != null ? aliasesArray.toList().toArray(new String[aliasesArray.length()]) : null;
        description = json.getString("description");
        imageURL = json.getString("imageURL");
        sources = new EventSources(json.getJSONObject("sources"));
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("description");
        if(aliases != null) {
            json.put("aliases", new JSONArray(aliases));
            json.addTranslatedKey("aliases");
        }
        json.put("description", description);
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
