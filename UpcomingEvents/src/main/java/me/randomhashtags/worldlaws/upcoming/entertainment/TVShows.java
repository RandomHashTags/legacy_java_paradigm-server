package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.TVShowEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;

public final class TVShows extends LoadedUpcomingEventController {
    private boolean hasScheduledTasks;

    public TVShows() {
        hasScheduledTasks = false;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TV_SHOW;
    }

    @Override
    public void load() {
        if(!hasScheduledTasks) {
            hasScheduledTasks = true;
            final UpcomingEvents events = UpcomingEvents.INSTANCE;
            events.registerFixedTimer(UpdateIntervals.UpcomingEvents.TV_SHOW_SCHEDULE, this::refreshSchedule);
        }
        refreshSchedule();
    }

    private void refreshSchedule() {
        final String url = "https://api.tvmaze.com/schedule/full";
        final UpcomingEventType eventType = getType();
        final JSONArray array = requestJSONArray(url, RequestMethod.GET);
        if(array != null) {
            final int max = array.length();
            if(max > 0) {
                final LocalDate nextWeek = LocalDate.now().plusWeeks(1);
                new ParallelStream<JSONObject>().stream(array.spliterator(), json -> {
                    final int season = json.getInt("season");
                    final JSONObject showJSON = json.getJSONObject("_embedded").getJSONObject("show");
                    if(season < 1000 && !showJSON.getString("status").equalsIgnoreCase("ended")) {
                        final String[] airdateValues = json.getString("airdate").split("-");
                        final int airYear = Integer.parseInt(airdateValues[0]), airMonth = Integer.parseInt(airdateValues[1]), airDay = Integer.parseInt(airdateValues[2]);
                        final EventDate eventDate = new EventDate(Month.of(airMonth), airDay, airYear);
                        if(eventDate.getLocalDate().isBefore(nextWeek)) {
                            final String dateString = eventDate.getDateString();
                            final int popularity = showJSON.getInt("weight");

                            final String episodeName = json.getString("name");
                            final int episode = json.get("number") instanceof Integer ? json.getInt("number") : -1;
                            final String tag = "Season " + season + ", Episode " + episode;

                            final int showID = showJSON.getInt("id");
                            final String showTitle = showJSON.getString("name");

                            final String showURL = showJSON.getString("url");
                            final String officialSite = showJSON.get("officialSite") instanceof String ? showJSON.getString("officialSite").replace("http://", "https://") : null;
                            final String language = showJSON.get("language") instanceof String ? showJSON.getString("language") : null;
                            final JSONArray genres = showJSON.getJSONArray("genres");

                            final EventSources sources = new EventSources();
                            sources.add(new EventSource("TVMaze: " + showTitle, showURL));

                            final String identifier = getEventDateIdentifier(dateString, showID + "_" + tag);
                            String imageURL = showJSON.has("image") && showJSON.get("image") instanceof JSONObject ? showJSON.getJSONObject("image").getString("original") : null;

                            final String episodeSummary = json.get("summary") instanceof String ? json.getString("summary") : null;
                            final int runtimeMinutes = json.get("runtime") instanceof Integer ? json.getInt("runtime") : -1;
                            final JSONObject networkJSON = showJSON.get("network") instanceof JSONObject ? showJSON.getJSONObject("network") : null;
                            String network = null, countryCode = null;
                            if(networkJSON != null) {
                                network = networkJSON.getString("name");
                                countryCode = networkJSON.has("code") ? networkJSON.getString("code") : null;
                            }

                            if(network == null) {
                                final JSONObject webChannel = showJSON.get("webChannel") instanceof JSONObject ? showJSON.getJSONObject("webChannel") : null;
                                network = webChannel != null ? webChannel.getString("name") : null;
                            }

                            if(network != null && officialSite != null) {
                                sources.add(new EventSource(network + ": " + showTitle, officialSite));
                            }

                            final TVShowEvent tvShowEvent = new TVShowEvent(showTitle, null, imageURL, null, popularity, language, countryCode, officialSite, network, runtimeMinutes, season, episode, episodeName, episodeSummary, genres, sources);
                            HashMap<String, Object> customValues = null;
                            if(popularity > 0) {
                                customValues = new HashMap<>();
                                customValues.put("popularity", popularity);
                            }

                            tvShowEvent.setTitle(showTitle);
                            final String preUpcomingEvent = tvShowEvent.toPreUpcomingEventJSON(eventType, identifier, tag, null, customValues);
                            putLoadedPreUpcomingEvent(identifier, preUpcomingEvent);
                            putUpcomingEvent(identifier, tvShowEvent.toString());
                        }
                    }
                });
            }
        }
    }
}
