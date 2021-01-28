package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.weather.WeatherAlert;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.WeatherEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public enum WeatherUS implements WeatherController {
    INSTANCE;

    private String alertEvents;
    private HashMap<String, String> eventAlerts;
    private HashMap<String, HashMap<String, String>> territoryEventAlerts;
    private HashMap<String, String> territoryAlerts;
    private HashMap<String, String> alerts;

    @Override
    public WLCountry getCountryBackendID() {
        return WLCountry.UNITED_STATES;
    }
    @Override
    public EventSource getSource() {
        return new EventSource("U.S. National Weather Service", "https://www.weather.gov");
    }

    @Override
    public String getAlertEvents() {
        return alertEvents;
    }

    @Override
    public HashMap<String, String> getEventAlerts() {
        return eventAlerts;
    }
    @Override
    public HashMap<String, HashMap<String, String>> getTerritoryEventAlerts() {
        return territoryEventAlerts;
    }
    @Override
    public HashMap<String, String> getTerritoryAlerts() {
        return territoryAlerts;
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
    public void refreshAlerts(CompletionHandler handler) {
        eventAlerts = new HashMap<>();
        territoryEventAlerts = new HashMap<>();
        territoryAlerts = new HashMap<>();
        final String url = "https://api.weather.gov/alerts/active?status=actual";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("User-Agent", "(World Laws.Weather, ***REMOVED***)");
        requestJSONObject(url, RequestMethod.GET, headers, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final HashMap<String, String> territories = getTerritories();
                    final JSONArray array = json.getJSONArray("features");
                    final HashMap<String, WeatherEvent> events = new HashMap<>();
                    final HashMap<String, List<WeatherAlert>> eventAlertsMap = new HashMap<>();
                    final HashSet<String> areas = new HashSet<>();
                    for(Object obj : array) {
                        final JSONObject jsonAlert = (JSONObject) obj;
                        final String id = jsonAlert.getString("id").split("/alerts/")[1];
                        final JSONObject properties = jsonAlert.getJSONObject("properties");
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

                        if(!events.containsKey(event)) {
                            final int defcon = getSeverityDEFCON(severity);
                            events.put(event, new WeatherEvent(event, defcon));
                            eventAlertsMap.put(event, new ArrayList<>());
                        }
                        String areaDesc = properties.getString("areaDesc");

                        if(areaDesc.contains(",")) {
                            if(areaDesc.contains(";")) {
                                final String desc = areaDesc;
                                for(String string : desc.split("; ")) {
                                    final String[] values = string.split(" ");
                                    final String stateAbbreviation = values[values.length-1];
                                    final String state = territories.getOrDefault(stateAbbreviation, null);
                                    if(state != null) {
                                        final boolean isSemiColon = areaDesc.contains("; " + string);
                                        areaDesc = areaDesc.replace((isSemiColon ? "; " : "") + string, "");
                                        final String area = string.split(", " + stateAbbreviation)[0];
                                        final String targetArea = event.replace(" ", "") + "." + stateAbbreviation + "." + area;
                                        if(!areas.contains(targetArea)) {
                                            final WeatherAlert alert = new WeatherAlert(territory, area, severity, certainty, event, headline, description, instruction, time);
                                            eventAlertsMap.get(event).add(alert);
                                            areas.add(targetArea);
                                        }
                                    }
                                }
                            }
                        }

                        String area = areaDesc.replace(";", ",").replace(", " + territoryAbbreviation, "");
                        if(area.endsWith(",")) {
                            area = area.substring(0, area.length()-1);
                        }
                        if(!area.isEmpty()) {
                            final String targetArea = event.replace(" ", "") + "." + territoryAbbreviation + "." + area;
                            if(!areas.contains(targetArea)) {
                                final WeatherAlert alert = new WeatherAlert(territory, area, severity, certainty, event, headline, description, instruction, time);
                                eventAlertsMap.get(event).add(alert);
                                areas.add(targetArea);
                            }
                        }
                    }

                    final StringBuilder eventBuilder = new StringBuilder("[");
                    boolean isFirst = true;
                    for(WeatherEvent event : events.values()) {
                        eventBuilder.append(isFirst ? "" : ",").append(event.toString());
                        isFirst = false;
                    }
                    eventBuilder.append("]");
                    alertEvents = eventBuilder.toString();

                    final HashMap<String, Set<String>> loadedTerritoryEvents = new HashMap<>();
                    final HashMap<String, StringBuilder> territoryAlertBuilders = new HashMap<>();
                    for(Map.Entry<String, List<WeatherAlert>> eventAlert : eventAlertsMap.entrySet()) {
                        final String event = eventAlert.getKey(), eventBackendID = event.toLowerCase().replace(" ", "");
                        final List<WeatherAlert> alerts = eventAlert.getValue();
                        final int defcon = getSeverityDEFCON(alerts.get(0).getSeverity());
                        final HashMap<String, HashMap<String, StringBuilder>> territoryEventAlertBuilders = new HashMap<>();
                        for(WeatherAlert alert : alerts) {
                            final String territory = alert.getTerritory().toLowerCase();

                            if(!loadedTerritoryEvents.containsKey(eventBackendID)) {
                                loadedTerritoryEvents.put(eventBackendID, new HashSet<>());
                            }
                            if(!loadedTerritoryEvents.get(eventBackendID).contains(territory)) {
                                isFirst = !territoryAlertBuilders.containsKey(territory);
                                if(isFirst) {
                                    territoryAlertBuilders.put(territory, new StringBuilder("["));
                                }
                                final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
                                territoryAlertBuilders.get(territory).append(isFirst ? "" : ",").append(weatherEvent.toString());
                                loadedTerritoryEvents.get(eventBackendID).add(territory);
                            }

                            isFirst = !territoryEventAlertBuilders.containsKey(territory);
                            if(isFirst) {
                                territoryEventAlertBuilders.put(territory, new HashMap<>());
                            }
                            isFirst = !territoryEventAlertBuilders.get(territory).containsKey(eventBackendID);
                            if(isFirst) {
                                territoryEventAlertBuilders.get(territory).put(eventBackendID, new StringBuilder("["));
                            }
                            territoryEventAlertBuilders.get(territory).get(eventBackendID).append(isFirst ? "" : ",").append(alert.toString());
                        }
                        for(Map.Entry<String, HashMap<String, StringBuilder>> maps : territoryEventAlertBuilders.entrySet()) {
                            final String territory = maps.getKey();
                            final HashMap<String, StringBuilder> builders = maps.getValue();
                            for(Map.Entry<String, StringBuilder> builder : builders.entrySet()) {
                                final String eventAlertKey = builder.getKey();
                                final String value = builder.getValue().append("]").toString();
                                if(!territoryEventAlerts.containsKey(territory)) {
                                    territoryEventAlerts.put(territory, new HashMap<>());
                                }
                                territoryEventAlerts.get(territory).put(eventAlertKey, value);
                            }
                        }

                        isFirst = true;
                        final StringBuilder builder = new StringBuilder("[");
                        for(WeatherAlert alert : alerts) {
                            builder.append(isFirst ? "" : ",").append(alert.toString());
                            isFirst = false;
                        }
                        builder.append("]");
                        eventAlerts.put(eventBackendID, builder.toString());
                    }
                    for(Map.Entry<String, StringBuilder> builders : territoryAlertBuilders.entrySet()) {
                        final String territory = builders.getKey();
                        territoryAlerts.put(territory, builders.getValue().append("]").toString());
                    }
                }
                if(handler != null) {
                    handler.handle(alertEvents);
                }
            }
        });
    }
}
