package me.randomhashtags.worldlaws.tracker;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.*;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.stream.ParallelStream;
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

    private final HashMap<APIVersion, String> cache;
    private final HashMap<String, String> territoryEvents, cachedVolcanoes;

    NASA_EONET() {
        cache = new HashMap<>();
        territoryEvents = new HashMap<>();
        cachedVolcanoes = new HashMap<>();
    }

    @Override
    public SovereignStateInfo getInfo() {
        return null;
    }

    public String getCurrent(APIVersion version) {
        if(!cache.containsKey(version)) {
            final Weather weather = Weather.INSTANCE;
            weather.registerFixedTimer(WLUtilities.WEATHER_NASA_WEATHER_EVENT_TRACKER_UPDATE_INTERVAL, () -> refresh(version));
            weather.registerFixedTimer(WLUtilities.WEATHER_NASA_WEATHER_VOLCANO_UPDATE_INTERVAL, cachedVolcanoes::clear);
            refresh(version);
        }
        return cache.get(version);
    }
    private void refresh(APIVersion version) {
        final long started = System.currentTimeMillis();
        final String wikipediaPrefix = "https://en.wikipedia.org/wiki/";
        final HashMap<String, String> volcanoWikipediaPages = getVolcanoWikipediaPages();
        final ConcurrentHashMap<String, HashSet<String>> homeValues = new ConcurrentHashMap<>();
        final String url = "https://eonet.sci.gsfc.nasa.gov/api/v3/events?status=open&days=30";
        final JSONObject json = requestJSONObject(url, RequestMethod.GET, CONTENT_HEADERS);
        int amount = 0;
        if(json != null) {
            final String unitedStatesBackendID = WLCountry.UNITED_STATES.getBackendID();
            final JSONArray eventsArray = json.getJSONArray("events");
            amount += eventsArray.length();
            ParallelStream.stream(eventsArray.spliterator(), obj -> {
                final JSONObject eventJSON = (JSONObject) obj;
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
                            final WLCountry wlcountry = WLCountry.valueOfBackendID(targetCountry);
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
                    homeValues.get(category).add(naturalEvent.toString());
                }
            });

            String string = null;
            if(!homeValues.isEmpty()) {
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirstCategory = true;
                for(Map.Entry<String, HashSet<String>> map : homeValues.entrySet()) {
                    builder.append(isFirstCategory ? "" : ",").append("\"").append(map.getKey()).append("\":{");
                    boolean isFirstValue = true;
                    final HashSet<String> values = map.getValue();
                    for(String value : values) {
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
        }
        WLLogger.logInfo("NASA_EONET - loaded " + amount + " events (took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private HashMap<String, String> getVolcanoWikipediaPages() {
        return new HashMap<>() {{
            put("Ambae", "Manaro_Voui");
            put("Aira", "Aira_Caldera");
            put("Shiveluch", "Shiveluch");
            put("Sheveluch", "Shiveluch");
            put("San Cristóbal", "San_Cristobal_Volcano");
            put("Hunga Tonga Hunga Ha'apai", "Hunga_Tonga");
            put("Piton de la Fournaise", "Piton_de_la_Fournaise");
            put("Wolf", "Wolf_Volcano");
        }};
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
            this.place = LocalServer.fixEscapeValues(place);
            this.country = country;
            this.subdivision = subdivision;
            this.location = location;
            this.description = LocalServer.fixEscapeValues(description);
            this.sources = sources;
        }

        @Override
        public String toString() {
            return "\"" + id + "\":{" +
                    "\"place\":\"" + place + "\"," +
                    (country != null ? "\"country\":\"" + country + "\"," : "") +
                    (subdivision != null ? "\"subdivision\":\"" + subdivision + "\"," : "") +
                    (location != null ? "\"location\":" + location.toString() + "," : "") +
                    (description != null ? "\"description\":\"" + description + "\"," : "") +
                    "\"sources\":" + sources.toString() +
                    "}";
        }
    }
}
