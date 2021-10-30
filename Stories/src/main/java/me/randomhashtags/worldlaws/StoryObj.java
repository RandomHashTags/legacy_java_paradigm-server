package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.stories.Story;

public final class StoryObj implements Story {

    private final WLCountry country;
    private final SovereignStateSubdivision subdivision;
    private final String title, description, imageURL, videoURL;
    private final EventSources sources;

    public StoryObj(WLCountry country, SovereignStateSubdivision subdivision, String title, String description, String imageURL, String videoURL, EventSources sources) {
        this.country = country;
        this.subdivision = subdivision;
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.videoURL = videoURL;
        this.sources = sources;
    }

    @Override
    public WLCountry getCountry() {
        return country;
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return subdivision;
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
    public String getVideoURL() {
        return videoURL;
    }

    @Override
    public EventSources getSources() {
        return sources;
    }
}
