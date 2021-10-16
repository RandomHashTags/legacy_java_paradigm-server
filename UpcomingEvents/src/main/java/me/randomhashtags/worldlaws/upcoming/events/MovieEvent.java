package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

public final class MovieEvent extends UpcomingEvent {
    private final String productionCompany, releaseInfo, ratings;
    private final JSONObject imdbInfo;

    public MovieEvent(String title, String description, String posterURL, String productionCompany, String releaseInfo, JSONObject imdbInfo, String ratings, JSONArray youtubeVideoIDs, EventSources sources) {
        super(title, description, posterURL, null, youtubeVideoIDs, sources);
        this.productionCompany = productionCompany;
        this.releaseInfo = LocalServer.fixEscapeValues(releaseInfo);
        this.imdbInfo = imdbInfo;
        this.ratings = ratings;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (releaseInfo != null ? "\"releaseInfo\":\"" + LocalServer.fixEscapeValues(releaseInfo) + "\"," : "") +
                (ratings != null ? "\"ratings\":" + ratings + "," : "") +
                (imdbInfo != null ? "\"imdbInfo\":" + imdbInfo.toString() + "," : "") +
                "\"productionCompany\":\"" + productionCompany + "\"" +
                "}";
    }
}
