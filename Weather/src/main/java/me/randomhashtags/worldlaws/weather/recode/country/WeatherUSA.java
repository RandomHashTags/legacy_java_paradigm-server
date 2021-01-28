package me.randomhashtags.worldlaws.weather.recode.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;
import me.randomhashtags.worldlaws.weather.recode.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum WeatherUSA implements NewWeatherController {
    INSTANCE;

    private String eventsJSON;
    private HashMap<String, String> alertIDs, zones, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, String>> territoryPreAlerts;
    private HashMap<String, NewWeatherPreAlert> preAlertIDs;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("U.S. National Weather Service", "https://www.weather.gov");
    }

    @Override
    public String getEvents() {
        return eventsJSON;
    }

    public HashMap<String, String> getTerritories() {
        return new HashMap<>() {{
            put("AL", "Alabama");
            put("AK", "Alaska");
            put("AZ", "Arizona");
            put("AR", "Arkansas");
            put("CA", "California");
            put("CO", "Colorado");
            put("CT", "Connecticut");
            put("DE", "Delaware");
            put("FL", "Florida");
            put("GA", "Georgia");
            put("HI", "Hawaii");
            put("ID", "Idaho");
            put("IL", "Illinois");
            put("IN", "Indiana");
            put("IA", "Iowa");
            put("KS", "Kansas");
            put("KY", "Kentucky");
            put("LA", "Louisiana");
            put("ME", "Maine");
            put("MD", "Maryland");
            put("MA", "Massachusetts");
            put("MI", "Michigan");
            put("MN", "Minnesota");
            put("MS", "Mississippi");
            put("MO", "Missouri");
            put("MT", "Montana");
            put("NE", "Nebraska");
            put("NV", "Nevada");
            put("NH", "New Hampshire");
            put("NJ", "New Jersey");
            put("NM", "New Mexico");
            put("NY", "New York");
            put("NC", "North Carolina");
            put("ND", "North Dakota");
            put("OH", "Ohio");
            put("OK", "Oklahoma");
            put("OR", "Oregon");
            put("PA", "Pennsylvania");
            put("RI", "Rhode Island");
            put("SC", "South Carolina");
            put("SD", "South Dakota");
            put("TN", "Tennessee");
            put("TX", "Texas");
            put("UT", "Utah");
            put("VT", "Vermont");
            put("VA", "Virginia");
            put("WA", "Washington");
            put("WV", "West Virginia");
            put("WI", "Wisconsin");
            put("WY", "Wyoming");

            put("DC", "District of Columbia");
            put("AS", "American Samoa");
            put("GU", "Guam");
            put("MP", "Northern Mariana Islands");
            put("PR", "Puerto Rico");
            put("VI", "U.S. Virgin Islands");
        }};
    }

    @Override
    public void refresh(CompletionHandler handler) {
        eventsJSON = "[]";
        alertIDs = new HashMap<>();
        eventPreAlerts = new HashMap<>();
        territoryEvents = new HashMap<>();
        territoryPreAlerts = new HashMap<>();
        preAlertIDs = new HashMap<>();
        final String country = getCountry().getBackendID(), url = "https://api.weather.gov/alerts/active?status=actual";

        final HashMap<String, Integer> eventsMap = new HashMap<>();
        final HashMap<String, HashSet<NewWeatherPreAlert>> eventPreAlertsMap = new HashMap<>();
        final HashMap<String, HashSet<NewWeatherEvent>> territoryEventsMap = new HashMap<>();
        final HashMap<String, HashMap<String, HashSet<NewWeatherPreAlert>>> territoryPreAlertsMap = new HashMap<>();

        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("User-Agent", "(World Laws.Weather, ***REMOVED***)");
        final String zonePrefix = "https://api\\.weather\\.gov/zones/";
        requestJSONObject(url, RequestMethod.GET, headers, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final HashMap<String, String> territories = getTerritories();
                    final JSONArray array = json.getJSONArray("features");
                    for(Object obj : array) {
                        final JSONObject jsonAlert = (JSONObject) obj;
                        final String id = jsonAlert.getString("id").split("/alerts/")[1];
                        final JSONObject properties = jsonAlert.getJSONObject("properties");
                        final JSONArray affectedZones = properties.getJSONArray("affectedZones");
                        final HashSet<String> zoneIDs = new HashSet<>();
                        for(Object object : affectedZones) {
                            final String zoneURL = (String) object;
                            final String zoneID = zoneURL.split(zonePrefix)[1];
                            zoneIDs.add(zoneID);
                        }

                        final String[] senderName = properties.getString("senderName").split(" ");
                        final int senderNameLength = senderName.length;
                        final String territoryAbbreviation = senderName[senderNameLength-1];
                        final String territory = territories.getOrDefault(territoryAbbreviation, "Unknown");
                        final String severityString = properties.getString("severity"), severity = severityString.equals("Unknown") ? "-1" : severityString;
                        final String certainty = properties.getString("certainty");
                        final String event = properties.getString("event");
                        final String headline = properties.has("headline") ? properties.getString("headline") : null;
                        final String description = properties.getString("description");
                        final String instruction = properties.has("instruction") && properties.get("instruction") instanceof String ? properties.getString("instruction").replace("\n", "\\n") : null;
                        final String sent = properties.getString("sent");
                        final String effective = properties.getString("effective");
                        final String expires = properties.getString("expires");
                        final String ends = properties.get("ends") instanceof String ? properties.getString("ends") : null;
                        final WeatherAlertTime time = new WeatherAlertTime(sent, effective, expires, ends);

                        final int defcon = getSeverityDEFCON(severity);
                        if(!eventsMap.containsKey(event)) {
                            eventsMap.put(event, defcon);
                        }

                        if(!territoryEventsMap.containsKey(territory)) {
                            territoryEventsMap.put(territory, new HashSet<>());
                        }
                        final HashSet<NewWeatherEvent> territorySet = territoryEventsMap.get(territory);
                        boolean hasEvent = false;
                        for(NewWeatherEvent newWeatherEvent : territorySet) {
                            if(event.equals(newWeatherEvent.getEvent())) {
                                hasEvent = true;
                                break;
                            }
                        }
                        if(!hasEvent) {
                            final NewWeatherEvent weatherEvent = new NewWeatherEvent(event, defcon);
                            territoryEventsMap.get(territory).add(weatherEvent);
                        }

                        /*final String areaDesc = properties.getString("areaDesc");
                        final HashMap<String, HashSet<String>> areas = new HashMap<>();
                        if(areaDesc.contains(";")) {
                            final HashSet<String> territoryAreas = new HashSet<>();
                            for(String value : areaDesc.split(";")) {
                                final String[] values = value.split(" ");
                                final String lastValue = values[values.length-1];
                                final String targetTerritory = territories.getOrDefault(lastValue, null);
                                if(targetTerritory != null) {
                                    value = value.substring(0, value.length()-3);
                                    if(!areas.containsKey(targetTerritory)) {
                                        areas.put(targetTerritory, new HashSet<>());
                                    }
                                    areas.get(targetTerritory).add(value);
                                } else {
                                    territoryAreas.add(value);
                                }
                            }
                            areas.put(territory, territoryAreas);
                        }*/

                        final NewWeatherPreAlert preAlert = new NewWeatherPreAlert(event, id, country, territory, severity, certainty, headline, instruction, description, zoneIDs, time);
                        preAlertIDs.put(id, preAlert);

                        if(!eventPreAlertsMap.containsKey(event)) {
                            eventPreAlertsMap.put(event, new HashSet<>());
                        }
                        eventPreAlertsMap.get(event).add(preAlert);

                        if(!territoryPreAlertsMap.containsKey(territory)) {
                            territoryPreAlertsMap.put(territory, new HashMap<>());
                        }
                        if(!territoryPreAlertsMap.get(territory).containsKey(event)) {
                            territoryPreAlertsMap.get(territory).put(event, new HashSet<>());
                        }
                        territoryPreAlertsMap.get(territory).get(event).add(preAlert);
                    }
                }

                updateEventPreAlerts(eventPreAlertsMap);
                updateTerritoryEvents(territoryEventsMap);
                updateTerritoryPreAlerts(territoryPreAlertsMap);
                updateEventsJSON(eventsMap);

                if(handler != null) {
                    handler.handle(eventsJSON);
                }
            }
        });
    }
    private void updateEventPreAlerts(HashMap<String, HashSet<NewWeatherPreAlert>> hashmap) {
        for(Map.Entry<String, HashSet<NewWeatherPreAlert>> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final HashSet<NewWeatherPreAlert> preAlerts = map.getValue();
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(NewWeatherPreAlert preAlert : preAlerts) {
                builder.append(isFirst ? "" : ",").append(preAlert.toString());
                isFirst = false;
            }
            final String string = builder.append("]").toString();
            eventPreAlerts.put(event.toLowerCase().replace(" ", ""), string);
        }
    }
    private void updateTerritoryEvents(HashMap<String, HashSet<NewWeatherEvent>> hashmap) {
        boolean isFirst = true;
        for(Map.Entry<String, HashSet<NewWeatherEvent>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final HashSet<NewWeatherEvent> events = map.getValue();
            final StringBuilder builder = new StringBuilder("[");
            for(NewWeatherEvent newWeatherEvent : events) {
                builder.append(isFirst ? "" : ",").append(newWeatherEvent.toString());
                isFirst = false;
            }
            builder.append("]");
            territoryEvents.put(territory.toLowerCase().replace(" ", ""), builder.toString());
        }
    }
    private void updateTerritoryPreAlerts(HashMap<String, HashMap<String, HashSet<NewWeatherPreAlert>>> hashmap) {
        for(Map.Entry<String, HashMap<String, HashSet<NewWeatherPreAlert>>> map : hashmap.entrySet()) {
            final String territory = map.getKey();
            final HashMap<String, HashSet<NewWeatherPreAlert>> eventsMap = map.getValue();
            final HashMap<String, String> preAlertsMap = new HashMap<>();
            for(Map.Entry<String, HashSet<NewWeatherPreAlert>> eventMap : eventsMap.entrySet()) {
                final String event = eventMap.getKey();
                final HashSet<NewWeatherPreAlert> preAlerts = eventMap.getValue();
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(NewWeatherPreAlert preAlert : preAlerts) {
                    builder.append(isFirst ? "" : ",").append(preAlert.toString());
                    isFirst = false;
                }
                final String string = builder.append("]").toString();
                preAlertsMap.put(event, string);
            }
            territoryPreAlerts.put(territory.toLowerCase().replace(" ", ""), preAlertsMap);
        }
    }
    private void updateEventsJSON(HashMap<String, Integer> hashmap) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Map.Entry<String, Integer> map : hashmap.entrySet()) {
            final String event = map.getKey();
            final Integer value = map.getValue();
            builder.append(isFirst ? "" : ",").append("{\"event\":\"").append(event).append("\",\"defcon\":").append(value).append("}");
            isFirst = false;
        }
        builder.append("]");
        eventsJSON = builder.toString();
    }

    @Override
    public void getPreAlerts(String event, CompletionHandler handler) {
        if(eventPreAlerts.containsKey(event)) {
            handler.handle(eventPreAlerts.get(event));
        }
    }
    @Override
    public void getAlert(String id, CompletionHandler handler) {
        if(alertIDs == null) {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    tryGettingAlert(id, handler);
                }
            });
        } else {
            tryGettingAlert(id, handler);
        }
    }
    private void tryGettingAlert(String id, CompletionHandler handler) {
        if(alertIDs.containsKey(id)) {
            handler.handle(alertIDs.get(id));
        } else if(preAlertIDs.containsKey(id)) {
            final NewWeatherPreAlert preAlert = preAlertIDs.get(id);
            final HashSet<String> zones = preAlert.getZoneIDs();
            final int max = zones.size();
            final AtomicInteger completed = new AtomicInteger(0);
            final StringBuilder builder = new StringBuilder("[");
            zones.parallelStream().forEach(zoneID -> getZone(zoneID, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    builder.append(completed.get() == 0 ? "" : ",").append(object);
                    final int value = completed.addAndGet(1);
                    if(value == max) {
                        builder.append("]");
                        final String zonesJSON = builder.toString();
                        final NewWeatherAlert alert = new NewWeatherAlert(preAlert, zonesJSON);
                        final String string = alert.toString();
                        alertIDs.put(id, string);
                        preAlertIDs.remove(id);
                        handler.handle(string);
                    }
                }
            }));
        }
    }

    private void getZone(String zoneID, CompletionHandler handler) {
        if(zones == null) {
            zones = new HashMap<>();
        }
        if(zones.containsKey(zoneID)) {
            handler.handle(zones.get(zoneID));
        } else {
            final String url = "https://api.weather.gov/zones/" + zoneID;
            requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject object) {
                    final HashMap<String, String> territories = getTerritories();
                    final JSONObject geometryJSON = object.getJSONObject("geometry"), properties = object.getJSONObject("properties");
                    final Object state = properties.get("state");
                    final String name = properties.getString("name"), territory = state instanceof String ? territories.getOrDefault(state, "Unknown") : "Unknown";
                    final JSONArray coordinates = geometryJSON.getJSONArray("coordinates").getJSONArray(0).getJSONArray(0);
                    final List<Location> geometry = new ArrayList<>();
                    for(Object coordinate : coordinates) {
                        final JSONArray array = (JSONArray) coordinate;
                        final double longitude = array.getDouble(0), latitude = array.getDouble(1);
                        final Location location = new Location(latitude, longitude);
                        geometry.add(location);
                    }

                    final WeatherZone zone = new WeatherZone(zoneID, name, territory, geometry);
                    final String string = zone.toString();
                    zones.put(zoneID, string);
                    handler.handle(string);
                }
            });
        }
    }

    @Override
    public void getTerritoryEvents(String territory, CompletionHandler handler) {
        if(territoryEvents.containsKey(territory)) {
            handler.handle(territoryEvents.get(territory));
        }
    }

    @Override
    public void getTerritoryPreAlerts(String territory, String event, CompletionHandler handler) {
        if(territoryPreAlerts.containsKey(territory) && territoryPreAlerts.get(territory).containsKey(event)) {
            handler.handle(territoryPreAlerts.get(territory).get(event));
        }
    }
}
