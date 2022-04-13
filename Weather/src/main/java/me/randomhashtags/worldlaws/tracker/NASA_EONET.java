package me.randomhashtags.worldlaws.tracker;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.WLSubdivisions;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum NASA_EONET implements WLService {
    // https://eonet.sci.gsfc.nasa.gov/docs/v3#eventsAPI
    // https://eonet.sci.gsfc.nasa.gov/docs/changelog
    INSTANCE;

    private final HashMap<APIVersion, JSONObjectTranslatable> cache;
    private final HashMap<String, String> territoryEvents, cachedVolcanoes;

    NASA_EONET() {
        cache = new HashMap<>();
        territoryEvents = new HashMap<>();
        cachedVolcanoes = new HashMap<>();
    }

    public JSONObjectTranslatable getCurrent(APIVersion version) {
        if(!cache.containsKey(version)) {
            refresh(version);
        }
        return cache.get(version);
    }
    public void clearCachedVolcanoes() {
        cachedVolcanoes.clear();
    }
    public void refresh(APIVersion version) {
        final long started = System.currentTimeMillis();
        final String wikipediaPrefix = "https://en.wikipedia.org/wiki/";
        final HashMap<String, String> volcanoWikipediaPages = Settings.ServerValues.Weather.getVolcanoWikipediaPages();
        final ConcurrentHashMap<String, HashSet<NaturalEvent>> homeValues = new ConcurrentHashMap<>();
        final String url = "https://eonet.gsfc.nasa.gov/api/v3/events?status=open&days=30";
        final JSONObject json = requestJSONObject(url);
        int amount = 0;
        if(json != null) {
            final String unitedStatesBackendID = WLCountry.UNITED_STATES.getBackendID();
            final JSONArray eventsArray = json.getJSONArray("events");
            amount += eventsArray.length();
            new CompletableFutures<JSONObject>().stream(eventsArray, eventJSON -> {
                String place = eventJSON.getString("title").replace("&#039;", "'");
                if(!place.startsWith("Iceberg ")) {
                    final String[] startingReplacements = new String[] {
                            "Wildfires - ",
                            "Wildfire - ",
                            "Wildfire "
                    };
                    for(String replacement : startingReplacements) {
                        if(place.startsWith(replacement)) {
                            place = place.substring(replacement.length());
                            break;
                        }
                    }
                    String country = null, subdivision = null;
                    final String[] replacements = new String[] {
                            ", ",
                            " - ",
                            " "
                    };
                    for(String string : replacements) {
                        if(place.contains(string)) {
                            final String[] values = place.split(string);
                            final String value = values[values.length-1];
                            final String targetCountry = value.toLowerCase().replace(" ", "");
                            final WLCountry wlcountry = WLCountry.valueOfString(targetCountry);
                            if(wlcountry != null) {
                                country = wlcountry.getBackendID();
                                place = place.split(string + value)[0];
                                break;
                            }
                        }
                    }
                    for(String string : replacements) {
                        if(place.contains(string)) {
                            final String[] values = place.split(string);
                            final String value = values[values.length-1];
                            final String targetSubdivision = value.toLowerCase().replace(" ", "");
                            final SovereignStateSubdivision sss = WLSubdivisions.valueOfString(targetSubdivision);
                            if(sss != null) {
                                subdivision = sss.getName();
                                place = place.split(string + value)[0];
                                break;
                            }
                        }
                    }
                    if(place.endsWith("(CA)")) {
                        country = unitedStatesBackendID;
                        subdivision = SubdivisionsUnitedStates.CALIFORNIA.getName();
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

                    final EventSources sources = new EventSources(new EventSource("NASA: Earth Observatory Natural Event Tracker", "https://eonet.gsfc.nasa.gov"));
                    final JSONArray sourcesArray = eventJSON.getJSONArray("sources");
                    String description = null;
                    for(Object sourceObj : sourcesArray) {
                        final JSONObject sourceJSON = (JSONObject) sourceObj;
                        String siteName = sourceJSON.getString("id");
                        final String sourceURL = sourceJSON.getString("url").replace("&amp;", "&");
                        switch (siteName) {
                            case "SIVolcano":
                                siteName = "Global Volcanism Program, Smithsonian Institution";
                                description = getLatestVolcanoDescription(sourceURL);
                                break;
                            default:
                                break;
                        }
                        sources.add(new EventSource(siteName, sourceURL));
                    }
                    if(place.contains(" Volcano")) {
                        final String targetVolcano = place.split(" Volcano")[0];
                        if(volcanoWikipediaPages.containsKey(targetVolcano)) {
                            final String value = volcanoWikipediaPages.get(targetVolcano);
                            sources.add(new EventSource("Wikipedia: " + targetVolcano + " Volcano", wikipediaPrefix + value));
                        }
                    }

                    final NaturalEvent naturalEvent = new NaturalEvent(id, place, country, subdivision, location, description, sources);
                    homeValues.get(category).add(naturalEvent);
                }
            });

            JSONObjectTranslatable translatable = null;
            if(!homeValues.isEmpty()) {
                translatable = new JSONObjectTranslatable();
                for(Map.Entry<String, HashSet<NaturalEvent>> map : homeValues.entrySet()) {
                    final String key = map.getKey();
                    final JSONObjectTranslatable eventsJSON = new JSONObjectTranslatable();
                    for(NaturalEvent value : map.getValue()) {
                        final String id = value.id;
                        eventsJSON.put(id, value.toJSONObject());
                        eventsJSON.addTranslatedKey(id);
                    }
                    translatable.put(key, eventsJSON);
                    translatable.addTranslatedKey(key);
                }
            }
            cache.put(version, translatable);
        }
        WLLogger.logInfo("NASA_EONET - loaded " + amount + " events (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private String getLatestVolcanoDescription(String url) {
        final String id = url.split("=")[1];
        if(!cachedVolcanoes.containsKey(id)) {
            final Document doc = getDocument(url);
            String string = null;
            if(doc != null) {
                final Element tabbedContent = doc.selectFirst("div.tabbed-content");
                if(tabbedContent != null) {
                    final Element paragraph = tabbedContent.selectFirst("p.tab");
                    if(paragraph != null) {
                        final StringBuilder builder = new StringBuilder();
                        boolean isFirst = true;
                        for(TextNode node : paragraph.textNodes()) {
                            builder.append(isFirst ? "" : "\n\n").append(node.text());
                            isFirst = false;
                        }
                        string = builder.toString();
                    }
                }
            }
            if(string == null) {
                WLUtilities.saveLoggedError("Weather", "failed to getLatestVolcanoDescription for \"" + url + "\"!");
            }
            cachedVolcanoes.put(id, string);
        }
        return cachedVolcanoes.get(id);
    }

    private static final class NaturalEvent {

        private final String id, place, country, subdivision, description;
        private final Location location;
        private final EventSources sources;

        NaturalEvent(String id, String place, String country, String subdivision, Location location, String description, EventSources sources) {
            this.id = id;
            this.place = place;
            this.country = country;
            this.subdivision = subdivision;
            this.location = location;
            this.description = description;
            this.sources = sources;
        }

        public String getID() {
            return id;
        }

        public JSONObjectTranslatable toJSONObject() {
            final JSONObjectTranslatable json = new JSONObjectTranslatable("place");
            json.put("place", place);
            if(country != null) {
                json.put("country", country);
            }
            if(subdivision != null) {
                json.put("subdivision", subdivision);
            }
            if(location != null) {
                json.put("location", location.toJSONArray());
            }
            if(description != null) {
                json.put("description", description);
            }
            json.put("sources", sources.toJSONObject());
            return json;
        }
    }
}
