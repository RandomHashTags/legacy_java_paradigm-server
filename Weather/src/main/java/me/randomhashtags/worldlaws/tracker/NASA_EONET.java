package me.randomhashtags.worldlaws.tracker;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public enum NASA_EONET implements WLService {
    // https://eonet.sci.gsfc.nasa.gov/docs/v3#eventsAPI
    // https://eonet.sci.gsfc.nasa.gov/docs/changelog
    INSTANCE;

    private final HashMap<APIVersion, String> cache;
    private final HashMap<String, String> territoryEvents;

    NASA_EONET() {
        cache = new HashMap<>();
        territoryEvents = new HashMap<>();
    }

    @Override
    public SovereignStateInfo getInfo() {
        return null;
    }

    public void getCurrent(APIVersion version, CompletionHandler handler) {
        if(cache.containsKey(version)) {
            handler.handleString(cache.get(version));
        } else {
            final long started = System.currentTimeMillis();
            final HashMap<String, HashSet<String>> homeValues = new HashMap<>();
            final String url = "https://eonet.sci.gsfc.nasa.gov/api/v3/events?status=open&days=30";
            requestJSONObject(url, RequestMethod.GET, CONTENT_HEADERS, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    final JSONArray eventsArray = json.getJSONArray("events");
                    final int max = eventsArray.length();
                    final AtomicInteger completed = new AtomicInteger(0);
                    StreamSupport.stream(eventsArray.spliterator(), true).forEach(obj -> {
                        final JSONObject eventJSON = (JSONObject) obj;
                        String place = eventJSON.getString("title");
                        if(!place.startsWith("Iceberg ")) {
                            final String[] startingReplacements = new String[] {
                                    "Wildfires - ",
                                    "Wildfire - ",
                                    "Wildfire "
                            };
                            for(String replacement : startingReplacements) {
                                if(place.startsWith(replacement)) {
                                    place = place.substring(place.length());
                                    break;
                                }
                            }

                            final String id = eventJSON.getString("id");
                            final String category = eventJSON.getJSONArray("categories").getJSONObject(0).getString("title");
                            homeValues.putIfAbsent(category, new HashSet<>());

                            final JSONArray geometriesArray = eventJSON.getJSONArray("geometry");
                            final JSONObject targetGeometryJSON = geometriesArray.getJSONObject(geometriesArray.length()-1);
                            final String geometryType = targetGeometryJSON.getString("type");
                            Location location = null;
                            switch (geometryType) {
                                case "Point":
                                    final JSONArray targetGeometry = targetGeometryJSON.getJSONArray("coordinates");
                                    location = new Location(targetGeometry.getDouble(1), targetGeometry.getDouble(0));
                                    break;
                                case "Polygon":
                                    break;
                                default:
                                    break;
                            }

                            final EventSources sources = new EventSources();
                            final JSONArray sourcesArray = eventJSON.getJSONArray("sources");
                            for(Object sourceObj : sourcesArray) {
                                final JSONObject sourceJSON = (JSONObject) sourceObj;
                                final String siteName = sourceJSON.getString("id"), url = sourceJSON.getString("url").replace("&amp;", "&");
                                sources.addSource(new EventSource(siteName, url));
                            }

                            final NaturalEvent naturalEvent = new NaturalEvent(id, place, location, sources);
                            homeValues.get(category).add(naturalEvent.toString());
                        }

                        if(completed.addAndGet(1) == max) {
                            String string = null;
                            if(!homeValues.isEmpty()) {
                                final StringBuilder builder = new StringBuilder("{");
                                boolean isFirstCategory = true;
                                for(Map.Entry<String, HashSet<String>> map : homeValues.entrySet()) {
                                    builder.append(isFirstCategory ? "" : ",").append("\"").append(map.getKey()).append("\":{");
                                    boolean isFirstValue = true;
                                    for(String value : map.getValue()) {
                                        builder.append(isFirstValue ? "" : ",").append(value);
                                        isFirstValue = false;
                                    }
                                    isFirstCategory = false;
                                    builder.append("}");
                                }
                                builder.append("}");
                                string = builder.toString();
                            }
                            cache.put(version, string);
                            WLLogger.log(Level.INFO, "NASA_EONET - loaded " + homeValues.size() + " events (took " + (System.currentTimeMillis()-started) + "ms)");
                            handler.handleString(string);
                        }
                    });
                }
            });
        }
    }

    private static final class NaturalEvent {

        private final String id, place;
        private final Location location;
        private final EventSources sources;

        NaturalEvent(String id, String place, Location location, EventSources sources) {
            this.id = id;
            this.place = LocalServer.fixEscapeValues(place);
            this.location = location;
            this.sources = sources;
        }

        @Override
        public String toString() {
            return "\"" + id + "\":{" +
                    "\"place\":\"" + place + "\"," +
                    (location != null ? "\"location\":" + location.toString() + "," : "") +
                    "\"sources\":" + sources.toString() +
                    "}";
        }
    }
}
