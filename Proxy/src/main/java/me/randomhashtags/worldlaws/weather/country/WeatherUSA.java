package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;
import me.randomhashtags.worldlaws.location.territories.USTerritory;
import me.randomhashtags.worldlaws.weather.CountryWeather;
import me.randomhashtags.worldlaws.weather.WeatherAlert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum WeatherUSA implements CountryWeather {
    INSTANCE;

    private String alerts;
    private HashMap<String, Set<WeatherAlert>> territoryAlerts;
    private HashMap<String, String> territoryAlertsJSON;

    @Override
    public Country getCountry() {
        return Country.UNITED_STATES_OF_AMERICA;
    }
    @Override
    public EventSource getSourceURL() {
        return new EventSource("Weather.gov", "https://www.weather.gov");
    }

    @Override
    public void getAlerts(CompletionHandler handler) {
        if(alerts != null) {
            handler.handle(alerts);
        } else {
            updateAlerts(handler);
        }
    }

    @Override
    public void getAlerts(String territory, CompletionHandler handler) {
        final String value = territoryAlertsJSON.getOrDefault(territory.toLowerCase(), "[]");
        handler.handle(value);
    }

    @Override
    public void updateAlerts(CompletionHandler handler) {
        final long time = System.currentTimeMillis();
        final String url = "https://api.weather.gov/alerts/active?status=actual";
        final Territory[] territories = USTerritory.TERRITORIES;
        requestJSON(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final StringBuilder builder = new StringBuilder("[");
                final JSONObject json = new JSONObject(object.toString());
                final JSONArray array = json.getJSONArray("features");
                final HashSet<String> areas = new HashSet<>();
                boolean isFirst = true;
                for(Object obj : array) {
                    final JSONObject jsonAlert = (JSONObject) obj;
                    final JSONObject properties = jsonAlert.getJSONObject("properties");
                    final String[] senderName = properties.getString("senderName").split(" ");
                    final String territoryAbbreviation = senderName[senderName.length-1];
                    final Territory abbreviation = Territory.valueOfAbbreviation(territoryAbbreviation, territories), name = Territory.valueOfName(territoryAbbreviation, territories);
                    final Territory usterritory = abbreviation != null ? abbreviation : name != null ? name : Territory.valueOfName(senderName[senderName.length-2] + " " + territoryAbbreviation, territories);
                    final String territory = usterritory != null ? usterritory.getName() : "Unknown";
                    final String severityString = properties.getString("severity"), severity = severityString.equals("Unknown") ? "-1" : severityString;
                    final String certainty = properties.getString("certainty");
                    final String event = properties.getString("event");
                    final String headline = properties.has("headline") ? properties.getString("headline") : null;
                    final String description = properties.getString("description");
                    final String instruction = properties.has("instruction") && properties.get("instruction") instanceof String ? properties.getString("instruction").replace("\n", "\\n") : null;
                    String areaDesc = properties.getString("areaDesc");

                    if(areaDesc.contains(",")) {
                        if(areaDesc.contains(";")) {
                            final String desc = areaDesc;
                            for(String string : desc.split("; ")) {
                                final String[] values = string.split(" ");
                                final String stateAbbreviation = values[values.length-1];
                                final Territory state = Territory.valueOfAbbreviation(stateAbbreviation, territories);
                                if(state != null) {
                                    final boolean isSemiColon = areaDesc.contains("; " + string);
                                    areaDesc = areaDesc.replace((isSemiColon ? "; " : "") + string, "");
                                    final String area = string.split(", " + stateAbbreviation)[0];
                                    final String targetArea = event.replace(" ", "") + "." + state.getAbbreviation() + "." + area;
                                    if(!areas.contains(targetArea)) {
                                        final WeatherAlert alert = new WeatherAlert(territory, area, severity, certainty, event, headline, description, instruction);
                                        addTerritoryAlert(usterritory, alert);
                                        builder.append(isFirst ? "" : ",").append(alert.toString());
                                        isFirst = false;
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
                        final String targetArea = event.replace(" ", "") + "." + usterritory.getAbbreviation() + "." + area;
                        if(!areas.contains(targetArea)) {
                            final WeatherAlert alert = new WeatherAlert(territory, area, severity, certainty, event, headline, description, instruction);
                            addTerritoryAlert(usterritory, alert);
                            builder.append(isFirst ? "" : ",").append(alert.toString());
                            isFirst = false;
                            areas.add(targetArea);
                        }
                    }
                }
                builder.append("]");
                final String string = builder.toString();
                alerts = string;
                updateTerritoryAlertsJSON();
                final long elapsedTime = System.currentTimeMillis()-time;
                System.out.println("Weather - updated USA alerts (took " + elapsedTime + "ms)");
                if(handler != null) {
                    handler.handle(string);
                }
            }
        });
    }
    private void addTerritoryAlert(Territory territory, WeatherAlert alert) {
        final String abbreviation = territory.getAbbreviation().toLowerCase();
        if(territoryAlerts == null) {
            territoryAlerts = new HashMap<>();
        }
        if(territoryAlerts.containsKey(abbreviation)) {
            territoryAlerts.get(abbreviation).add(alert);
        } else {
            final Set<WeatherAlert> set = new HashSet<>();
            set.add(alert);
            territoryAlerts.put(abbreviation, set);
        }
    }
    private void updateTerritoryAlertsJSON() {
        territoryAlertsJSON = new HashMap<>();
        for(String territory : territoryAlerts.keySet()) {
            final Set<WeatherAlert> alerts = territoryAlerts.get(territory);
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(WeatherAlert alert : alerts) {
                builder.append(isFirst ? "" : ",").append(alert.toString());
                isFirst = false;
            }
            builder.append("]");
            territoryAlertsJSON.put(territory, builder.toString());
        }
    }
}
