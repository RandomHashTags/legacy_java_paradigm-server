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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class TVShows extends LoadedUpcomingEventController {
    private String showNamesCache;
    private boolean hasScheduledTasks;

    public TVShows() {
        hasScheduledTasks = false;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TV_SHOW;
    }

    @Override
    public void load(CompletionHandler handler) {
        if(!hasScheduledTasks) {
            hasScheduledTasks = true;
            final UpcomingEvents events = UpcomingEvents.INSTANCE;
            events.registerFixedTimer(WLUtilities.UPCOMING_EVENTS_TV_SHOW_UPDATE_INTERVAL, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    refreshSchedule(null);
                }
            });
            events.registerFixedTimer(WLUtilities.UPCOMING_EVENTS_TV_SHOW_IDS_UPDATE_INTERVAL, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    updateAllShowNames(null);
                }
            });
        }
        refreshSchedule(handler);
    }

    @Override
    public void getResponse(String input, CompletionHandler handler) {
        final String[] values = input.split("/");
        final String key = values[0];
        switch (key) {
            case "ids":
                getAllShowIDs(handler);
                break;
            default:
                break;
        }
    }

    private void getAllShowIDs(CompletionHandler handler) {
        if(showNamesCache != null) {
            handler.handleString(showNamesCache);
        } else {
            getJSONObject(Folder.UPCOMING_EVENTS_TV_SHOWS, "ids", new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    updateAllShowNames(handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    showNamesCache = json.toString();
                    handler.handleString(showNamesCache);
                }
            });
        }
    }
    public void updateAllShowNames(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final JSONObject showNames = new JSONObject();

        final long sleepDuration = TimeUnit.SECONDS.toMillis(15);
        final AtomicBoolean lock = new AtomicBoolean(true);
        final AtomicInteger page = new AtomicInteger(0);
        // RATE LIMIT IS 20 REQUESTS PER 10 SECONDS, PER IP ADDRESS
        final String imageURLPrefix = "https://static.tvmaze.com/uploads/images/original_untouched/";
        final int imageURLPrefixLength = imageURLPrefix.length();
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                if(array != null && !array.isEmpty()) {
                    for(Object obj : array) {
                        final JSONObject json = (JSONObject) obj;
                        final String id = Integer.toString(json.getInt("id"));
                        final String name = json.getString("name"), status = json.getString("status");
                        String imageURL = json.has("image") && json.get("image") instanceof JSONObject ? json.getJSONObject("image").getString("original") : null;

                        final JSONObject show = new JSONObject();
                        show.put("name", name);
                        if(imageURL != null) {
                            if(imageURL.startsWith(imageURLPrefix)) {
                                imageURL = imageURL.substring(imageURLPrefixLength);
                            }
                            show.put("imageURL", imageURL);
                        }
                        if(!showNames.has(status)) {
                            showNames.put(status, new JSONObject());
                        }
                        showNames.getJSONObject(status).put(id, show);
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
        };
        while (lock.get()) {
            final int pageNumber = page.get();
            getShowsFromPage(pageNumber, completionHandler);
        }
        final String string = showNames.toString();
        showNamesCache = string;
        setFileJSON(Folder.UPCOMING_EVENTS_TV_SHOWS, "ids", string);
        WLLogger.logInfo("TVShows - updated all show names (took " + (System.currentTimeMillis()-started) +"ms)");
        if(handler != null) {
            handler.handleJSONObject(showNames);
        }
    }
    private void getShowsFromPage(int page, CompletionHandler showHandler) {
        final String url = "https://api.tvmaze.com/shows?page=" + page;
        requestJSONArray(url, RequestMethod.GET, showHandler);
    }

    private void refreshSchedule(CompletionHandler handler) {
        final String url = "https://api.tvmaze.com/schedule/full";
        final UpcomingEventType eventType = getType();
        requestJSONArray(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONArray(JSONArray array) {
                if(array != null) {
                    final int max = array.length();
                    if(max == 0) {
                        handler.handleString(null);
                    } else {
                        final LocalDate nextWeek = LocalDate.now().plusWeeks(1);
                        final AtomicInteger completed = new AtomicInteger(0);
                        ParallelStream.stream(array.spliterator(), obj -> {
                            final JSONObject json = (JSONObject) obj;
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
                                    sources.append(new EventSource("TVMaze: " + showTitle, showURL));

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
                                        sources.append(new EventSource(network + ": " + showTitle, officialSite));
                                    }

                                    final TVShowEvent tvShowEvent = new TVShowEvent(showTitle, null, imageURL, null, popularity, language, countryCode, officialSite, network, runtimeMinutes, season, episode, episodeName, episodeSummary, genres, sources);
                                    HashMap<String, Object> customValues = null;
                                    if(popularity > 0) {
                                        customValues = new HashMap<>();
                                        customValues.put("popularity", popularity);
                                    }

                                    final String preUpcomingEvent = tvShowEvent.toPreUpcomingEventJSON(eventType, identifier, showTitle, null, customValues);
                                    putLoadedPreUpcomingEvent(identifier, preUpcomingEvent);
                                    putUpcomingEvent(identifier, tvShowEvent.toString());
                                }
                            }

                            if(completed.addAndGet(1) == max && handler != null) {
                                handler.handleString(null);
                            }
                        });
                    }
                } else if(handler != null) {
                    handler.handleString(null);
                }
            }
        });
    }
}
