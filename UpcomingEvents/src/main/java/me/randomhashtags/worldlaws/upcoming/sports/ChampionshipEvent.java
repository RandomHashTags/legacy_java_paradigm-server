package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;

public final class ChampionshipEvent implements UpcomingEvent {

    private final String title, description, imageURL, location;
    private final EventSources sources;

    public ChampionshipEvent(String title, String description, String imageURL, String location, EventSources sources) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.location = location;
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
        return imageURL;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String getPropertiesJSONObject() {
        return null;
    }
}
