package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import org.json.JSONArray;

public final class TVShowEvent implements UpcomingEvent {

    private final String title, description, imageURL, language, countryCode, officialSite, network, episodeName, episodeSummary;
    private final JSONArray youtubeVideoIDs;
    private final int runtimeMinutes, season, episode;
    private final JSONArray genres;
    private final EventSources sources;

    public TVShowEvent(String title, String description, String imageURL, JSONArray youtubeVideoIDs, String language, String countryCode, String officialSite, String network, int runtimeMinutes, int season, int episode, String episodeName, String episodeSummary, JSONArray genres, EventSources sources) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.imageURL = imageURL;
        this.youtubeVideoIDs = youtubeVideoIDs;
        this.language = language;
        this.countryCode = countryCode;
        this.officialSite = officialSite;
        this.network = network;
        this.runtimeMinutes = runtimeMinutes;
        this.season = season;
        this.episode = episode;
        this.episodeName = LocalServer.fixEscapeValues(episodeName);
        this.episodeSummary = LocalServer.fixEscapeValues(episodeSummary);
        this.genres = genres;
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
        return null;
    }

    @Override
    public JSONArray getYouTubeVideoIDs() {
        return youtubeVideoIDs;
    }

    @Override
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                (language != null ? "\"language\":\"" + language + "\"," : "") +
                (countryCode != null ? "\"countryCode\":\"" + countryCode + "\"," : "") +
                (officialSite != null ? "\"officialSite\":\"" + officialSite + "\"," : "") +
                (network != null ? "\"network\":\"" + network + "\"," : "") +
                (runtimeMinutes > 0 ? "\"runtimeMinutes\":" + runtimeMinutes + "," : "") +
                "\"season\":" + season + "," +
                "\"episode\":" + episode + "," +
                "\"episodeName\":\"" + episodeName + "\"," +
                (episodeSummary != null ? "\"episodeSummary\":\"" + episodeSummary + "\"," : "") +
                "\"genres\":" + genres.toString() +
                "}";
    }
}
