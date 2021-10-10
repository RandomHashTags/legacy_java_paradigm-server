package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;

public final class TVShowEvent extends UpcomingEvent {

    private final String language, countryCode, officialSite, network, episodeName, episodeSummary;
    private final int runtimeMinutes, season, episode;
    private final JSONArray genres;

    public TVShowEvent(String title, String description, String imageURL, JSONArray youtubeVideoIDs, String language, String countryCode, String officialSite, String network, int runtimeMinutes, int season, int episode, String episodeName, String episodeSummary, JSONArray genres, EventSources sources) {
        super(title, description, imageURL, null, youtubeVideoIDs, sources);
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
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TV_SHOW;
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
