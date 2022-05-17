package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.LoadedPreUpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.events.TVShowEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public final class TVShows extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TV_SHOW;
    }

    @Override
    public void load() {
        refreshSchedule();
    }

    private void refreshSchedule() {
        final String url = "https://api.tvmaze.com/schedule/full";
        final UpcomingEventType eventType = getType();
        final JSONArray array = requestJSONArray(url);
        if(array != null) {
            final int max = array.length();
            if(max > 0) {
                final long todayStartOfDayMill = WLUtilities.getTodayStartOfDayMilliseconds();
                final long nextWeek = todayStartOfDayMill + TimeUnit.DAYS.toMillis(7);
                new CompletableFutures<JSONObject>().stream(array, json -> {
                    final int season = json.getInt("season");
                    final JSONObject showJSON = json.getJSONObject("_embedded").getJSONObject("show");
                    if(season < 1000 && !showJSON.getString("status").equalsIgnoreCase("ended")) {
                        final String airstamp = json.getString("airstamp");
                        final long exactTime = WLUtilities.parseDateFormatToMilliseconds(DateTimeFormatter.ISO_OFFSET_DATE_TIME, airstamp);
                        if(exactTime < nextWeek) {
                            final int popularity = showJSON.getInt("weight");
                            final String episodeName = json.getString("name");
                            final int episode = json.optInt("number", -1);
                            final String tag = "Season " + season + ", Episode " + episode;

                            final int showID = showJSON.getInt("id");
                            final String showTitle = showJSON.getString("name");

                            final String showURL = showJSON.getString("url");
                            final String officialSite = showJSON.optString("officialSite", null);
                            final String language = showJSON.optString("language", null);
                            final JSONArray genres = showJSON.getJSONArray("genres");

                            final EventSources sources = new EventSources();
                            sources.add(new EventSource("TVMaze: " + showTitle, showURL));

                            final String identifier = getEventDateIdentifier(exactTime, showID + "_" + tag);
                            final String imageURL = showJSON.has("image") && showJSON.get("image") instanceof JSONObject ? showJSON.getJSONObject("image").getString("original") : null;

                            final String episodeSummary = json.optString("summary", null);
                            final int runtimeMinutes = json.optInt("runtime", -1);
                            final JSONObject networkJSON = showJSON.optJSONObject("network", null);
                            String network = null, countryCode = null;
                            if(networkJSON != null) {
                                network = networkJSON.getString("name");
                                countryCode = networkJSON.optString("code", null);
                            }

                            if(network == null) {
                                final JSONObject webChannel = showJSON.optJSONObject("webChannel", null);
                                network = webChannel != null ? webChannel.getString("name") : null;
                            }

                            if(network != null && officialSite != null) {
                                sources.add(new EventSource(network + ": " + showTitle, officialSite));
                            }

                            final TVShowEvent tvShowEvent = new TVShowEvent(exactTime, showTitle, null, imageURL, null, popularity, language, countryCode, officialSite, network, runtimeMinutes, season, episode, episodeName, episodeSummary, genres, sources);

                            JSONObjectTranslatable customValues = null;
                            if(popularity > 0) {
                                customValues = new JSONObjectTranslatable();
                                customValues.put("popularity", popularity);
                            }
                            final LoadedPreUpcomingEvent loadedPreUpcomingEvent = tvShowEvent.toPreUpcomingEventJSON(eventType, identifier, tag, null, customValues);
                            putLoadedPreUpcomingEvent(loadedPreUpcomingEvent);
                            putUpcomingEvent(identifier, tvShowEvent);
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean isExactTime() {
        return true;
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return null;
    }
}
