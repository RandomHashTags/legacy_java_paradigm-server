package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class MovieEvent implements UpcomingEvent {
    private final String title, description, posterURL, productionCompany, releaseInfo, ratings;
    private final EventSources sources;

    public MovieEvent(String title, String description, String posterURL, String productionCompany, String releaseInfo, String ratings, EventSources sources) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.posterURL = posterURL;
        this.productionCompany = productionCompany;
        this.releaseInfo = LocalServer.fixEscapeValues(releaseInfo);
        this.ratings = ratings;
        this.sources = sources;
    }

    @Override
    public String getTitle() {
        return title;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public String getImageURL() {
        return posterURL;
    }
    @Override
    public String getLocation() {
        return null;
    }
    @Override
    public EventSources getSources() {
        return sources;
    }
    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (releaseInfo != null ? "\"releaseInfo\":\"" + LocalServer.fixEscapeValues(releaseInfo) + "\"," : "") +
                (ratings != null ? "\"ratings\":" + ratings + "," : "") +
                "\"productionCompany\":\"" + productionCompany + "\"" +
                "}";
    }

    public String getProductionCompany() {
        return productionCompany;
    }
}
