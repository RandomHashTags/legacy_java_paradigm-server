package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.event.UpcomingEvent;

public final class MovieEvent implements UpcomingEvent {
    private final EventDate date;
    private final String title, description, posterURL, productionCompany, releaseInfo;
    private final EventSources sources;

    public MovieEvent(String title, String description, String posterURL, String productionCompany, String releaseInfo, EventSources sources) {
        this.date = null;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.posterURL = posterURL;
        this.productionCompany = productionCompany;
        this.releaseInfo = LocalServer.fixEscapeValues(releaseInfo);
        this.sources = sources;
    }

    @Override
    public EventDate getDate() {
        return date;
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
                "\"productionCompany\":\"" + productionCompany + "\"" +
                "}";
    }

    public String getProductionCompany() {
        return productionCompany;
    }
}
