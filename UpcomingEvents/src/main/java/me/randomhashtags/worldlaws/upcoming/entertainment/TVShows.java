package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum TVShows implements LoadedUpcomingEventController {
    INSTANCE;

    private HashMap<String, String> upcomingEvents;
    private String allShowsCache;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TV_SHOW;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        refresh(handler);
        UpcomingEvents.INSTANCE.registerFixedTimer(WLUtilities.UPCOMING_EVENTS_TV_SHOW_UPDATE_INTERVAL, null);
    }

    @Override
    public void getResponse(String input, CompletionHandler handler) {
        final String[] values = input.split("/");
        final String key = values[0];
        switch (key) {
            case "ids":
                getAllShowNames(handler);
                break;
            default:
                break;
        }
    }

    public void getAllShowNames(CompletionHandler handler) {
        if(allShowsCache != null) {
            handler.handleString(allShowsCache);
        } else {
            allShowsCache = "{}";
            getJSONObject(Folder.UPCOMING_EVENTS_TV_SHOWS, "showNames", new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    updateAllShowNames(handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final String string = json.toString();
                    allShowsCache = string;
                    handler.handleString(string);
                }
            });
        }
    }
    private void updateAllShowNames(CompletionHandler handler) {
        final JSONObject showNames = new JSONObject();
        final long sleepDuration = TimeUnit.SECONDS.toMillis(15);
        final AtomicBoolean lock = new AtomicBoolean(true);
        final AtomicInteger page = new AtomicInteger(0);
        // RATE LIMIT IS 20 REQUESTS PER 10 SECONDS, PER IP ADDRESS

        while (lock.get()) {
            final int pageNumber = page.get();
            getShowsFromPage(pageNumber, new CompletionHandler() {
                @Override
                public void handleJSONArray(JSONArray array) {
                    if(array != null) {
                        for(Object obj : array) {
                            final JSONObject json = (JSONObject) obj;
                            showNames.put(json.getString("name"), json.getInt("id"));
                        }
                        if(page.addAndGet(1) % 20 == 0) {
                            try {
                                Thread.sleep(sleepDuration);
                            } catch (Exception e) {
                                WLUtilities.saveException(e);
                            }
                        }
                    } else {
                        lock.set(false);
                    }
                }
            });
        }
        handler.handleJSONObject(showNames);
    }
    private void getShowsFromPage(int page, CompletionHandler handler) {
        final String url = "https://api.tvmaze.com/shows?page=" + page;
        requestJSONArray(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array == null || array.isEmpty() ? null : array);
            }
        });
    }

    private void refresh(CompletionHandler handler) {
        final String url = "https://api.tvmaze.com/schedule/full";
        requestJSONArray(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                if(array != null) {
                    upcomingEvents = new HashMap<>();
                    final LocalDate nextWeek = LocalDate.now().plusWeeks(1);
                    final int max = array.length();
                    final AtomicInteger completed = new AtomicInteger(0);
                    StreamSupport.stream(array.spliterator(), true).forEach(obj -> {
                        final JSONObject json = (JSONObject) obj;
                        final int season = json.getInt("season");
                        final JSONObject showJSON = json.getJSONObject("_embedded").getJSONObject("show");
                        if(season < 1000 && !showJSON.getString("status").equalsIgnoreCase("ended")) {
                            final String[] airdateValues = json.getString("airdate").split("-");
                            final int airYear = Integer.parseInt(airdateValues[0]), airMonth = Integer.parseInt(airdateValues[1]), airDay = Integer.parseInt(airdateValues[2]);
                            final EventDate eventDate = new EventDate(Month.of(airMonth), airDay, airYear);
                            if(eventDate.getLocalDate().isBefore(nextWeek)) {
                                final String dateString = eventDate.getDateString();

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
                                sources.addSource(new EventSource("TVMaze: " + showTitle, showURL));

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
                                    sources.addSource(new EventSource(network + ": " + showTitle, officialSite));
                                }

                                final TVShowEvent tvShowEvent = new TVShowEvent(showTitle, null, imageURL, null, language, countryCode, officialSite, network, runtimeMinutes, season, episode, episodeName, episodeSummary, genres, sources);
                                LOADED_PRE_UPCOMING_EVENTS.put(identifier, tvShowEvent.toPreUpcomingEventJSON(identifier, showTitle));
                                upcomingEvents.put(identifier, tvShowEvent.toJSON());
                            }
                        }

                        if(completed.addAndGet(1) == max) {
                            handler.handleString(null);
                        }
                    });
                } else {
                    handler.handleString(null);
                }
            }
        });
    }
}
