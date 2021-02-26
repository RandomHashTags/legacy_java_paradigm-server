package me.randomhashtags.worldlaws.event.entertainment;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.UpcomingEventType;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.event.UpcomingEvent;

public final class MovieEvent implements UpcomingEvent {
    private final EventDate date;
    private final String title, description, posterURL, productionCompany, releaseInfo;
    private final EventSources sources;

    public MovieEvent(EventDate releaseDate, String title, String description, String posterURL, String productionCompany, String releaseInfo, EventSources sources) {
        this.date = releaseDate;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.posterURL = posterURL;
        this.productionCompany = productionCompany;
        this.releaseInfo = LocalServer.fixEscapeValues(releaseInfo);
        this.sources = sources;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.MOVIE;
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
                "\"productionCompany\":\"" + productionCompany + "\"," +
                "\"releaseInfo\":\"" + LocalServer.fixEscapeValues(releaseInfo) + "\"" +
                "}";
    }

    public String getProductionCompany() {
        return productionCompany;
    }
}
