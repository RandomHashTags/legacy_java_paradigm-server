package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;

public final class TVShowEvent extends UpcomingEvent {

    private final String language, countryCode, officialSite, network, episodeName, episodeSummary;
    private final int popularity, runtimeMinutes, season, episode;
    private final JSONArray genres;

    public TVShowEvent(EventDate date, String title, String description, String imageURL, JSONArray youtubeVideoIDs, int popularity, String language, String countryCode, String officialSite, String network, int runtimeMinutes, int season, int episode, String episodeName, String episodeSummary, JSONArray genres, EventSources sources) {
        super(date, title, description, imageURL, null, youtubeVideoIDs, sources);
        this.popularity = popularity;
        this.language = language;
        this.countryCode = countryCode;
        this.officialSite = officialSite;
        this.network = network;
        this.runtimeMinutes = runtimeMinutes;
        this.season = season;
        this.episode = episode;
        this.episodeName = episodeName;
        this.episodeSummary = episodeSummary;
        this.genres = genres;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TV_SHOW;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(popularity > 0) {
            json.put("popularity", popularity);
        }
        if(language != null) {
            json.put("language", language);
        }
        if(countryCode != null) {
            json.put("countryCode", countryCode);
        }
        if(officialSite != null) {
            json.put("officialSite", officialSite);
        }
        if(network != null) {
            json.put("network", network);
        }
        if(runtimeMinutes > 0) {
            json.put("runtimeMinutes", runtimeMinutes);
        }
        json.put("season", season);
        json.put("episode", episode);
        json.put("episodeName", episodeName);
        if(episodeSummary != null) {
            json.put("episodeSummary", episodeSummary);
        }
        json.put("genres", genres);
        return json;
    }
}
