package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONArray;
import org.json.JSONObject;

public final class MovieEvent extends UpcomingEvent {
    private final String releaseInfo, ratings;
    private final JSONArray productionCompanies;
    private final JSONObject imdbInfo;

    public MovieEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        releaseInfo = properties.optString(UpcomingEventValue.MOVIE_RELEASE_INFO.getKey(), null);
        ratings = properties.optString("ratings", null);
        imdbInfo = properties.optJSONObject("imdbInfo");
        productionCompanies = properties.getJSONArray("productionCompanies");
        insertProperties();
    }
    public MovieEvent(EventDate date, String title, String description, String posterURL, JSONArray productionCompanies, String releaseInfo, JSONObject imdbInfo, String ratings, JSONArray youtubeVideoIDs, EventSources sources) {
        super(date, title, description, posterURL, null, youtubeVideoIDs, sources);
        this.productionCompanies = productionCompanies;
        this.releaseInfo = LocalServer.fixEscapeValues(releaseInfo);
        this.imdbInfo = imdbInfo;
        this.ratings = ratings;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable(UpcomingEventValue.MOVIE_RELEASE_INFO.getKey());
        if(releaseInfo != null) {
            json.put(UpcomingEventValue.MOVIE_RELEASE_INFO.getKey(), releaseInfo);
        }
        if(ratings != null) {
            json.put("ratings", ratings);
        }
        if(imdbInfo != null) {
            json.put("imdbInfo", imdbInfo.toString());
        }
        json.put("productionCompanies", productionCompanies);
        return json;
    }
}
