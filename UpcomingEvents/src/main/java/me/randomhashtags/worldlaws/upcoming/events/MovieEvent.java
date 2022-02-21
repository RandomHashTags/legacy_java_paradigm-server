package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

public final class MovieEvent extends UpcomingEvent {
    private final String releaseInfo, ratings;
    private final JSONArray productionCompanies;
    private final JSONObject imdbInfo;

    public MovieEvent(String title, String description, String posterURL, JSONArray productionCompanies, String releaseInfo, JSONObject imdbInfo, String ratings, JSONArray youtubeVideoIDs, EventSources sources) {
        super(title, description, posterURL, null, youtubeVideoIDs, sources);
        this.productionCompanies = productionCompanies;
        this.releaseInfo = LocalServer.fixEscapeValues(releaseInfo);
        this.imdbInfo = imdbInfo;
        this.ratings = ratings;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
    }

    private String getProductionCompaniesJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Object obj : productionCompanies) {
            final String company = (String) obj;
            builder.append(isFirst ? "" : ",").append("\"").append(company).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (releaseInfo != null ? "\"releaseInfo\":\"" + LocalServer.fixEscapeValues(releaseInfo) + "\"," : "") +
                (ratings != null ? "\"ratings\":" + ratings + "," : "") +
                (imdbInfo != null ? "\"imdbInfo\":" + imdbInfo.toString() + "," : "") +
                "\"productionCompanies\":" + getProductionCompaniesJSON() + "" +
                "}";
    }
}
