package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.weather.CountryWeather;
import me.randomhashtags.worldlaws.weather.WeatherAlert;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum WeatherCA implements CountryWeather {
    INSTANCE;

    private String alerts;
    private HashMap<String, Set<WeatherAlert>> territoryAlerts;
    private HashMap<String, String> territoryAlertsJSON;

    @Override
    public Country getCountry() {
        return Country.CANADA;
    }

    @Override
    public EventSource getSourceURL() {
        return new EventSource("Canada.ca", "https://weather.gc.ca/warnings/index_e.html");
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
        final String url = "https://weather.gc.ca/warnings/index_e.html";
        final Document doc = getDocument(url);
        final StringBuilder builder = new StringBuilder("[");
        if(doc != null) {
            final Elements rows = doc.select("main.container table.table tbody tr");
            String territory = "";
            boolean isFirst = true;
            WeatherAlert previousAlert = null;
            for(Element element : rows) {
                final Elements targetTerritory = element.select("th.alertHead a[href]");
                if(!targetTerritory.isEmpty()) {
                    territory = targetTerritory.text();
                } else {
                    final String targetArea = element.select("td a[href]").get(0).text();
                    final Elements warnings = element.select("td ul.margin-bottom-small li a[href]");
                    for(Element warning : warnings) {
                        final String span = warning.select("span").text();
                        if(!span.isEmpty()) {
                            final String warningText = warning.text().replace("(", "").replace(")", "");
                            final String certainty = "Unknown";
                            final String spanValue = span.toLowerCase();
                            final boolean isWarning = spanValue.contains("warning"), isWatch = spanValue.contains("watch"), isThunderstorm = spanValue.contains("thunderstorm");
                            final String event = warningText.split(span)[0].replace("thunderstorm", "Thunderstorm") + (isThunderstorm ? " " + (isWarning ? "Warning" : "Watch") : "");
                            final String severity = isWarning ? "Severe" : isWatch ? "Moderate" : "-1";
                            final Document detailsDoc = getDocument("https://weather.gc.ca" + warning.attr("href"));
                            if(detailsDoc != null) {
                                final Elements table = detailsDoc.select("main.container div.row div.col-xs-12 p");
                                final String headline = null;
                                final String description = table.last().text().replace("\n", "\\n");
                                final String instruction = null;//properties.has("instruction") && properties.get("instruction") instanceof String ? properties.getString("instruction").replace("\n", "\\n") : null;
                                final boolean isTheSame = previousAlert != null && territory.equalsIgnoreCase(previousAlert.getTerritory()) && event.equalsIgnoreCase(previousAlert.getEvent()) && severity.equalsIgnoreCase(previousAlert.getSeverity()) && description.equalsIgnoreCase(previousAlert.getDescription());
                                if(isTheSame) {
                                    previousAlert.addArea(targetArea);
                                } else {
                                    if(previousAlert != null) {
                                        builder.append(isFirst ? "" : ",").append(previousAlert.toString());
                                        isFirst = false;
                                        addTerritoryAlert(previousAlert.getTerritory(), previousAlert);
                                    }
                                    previousAlert = new WeatherAlert(territory, targetArea, severity, certainty, event, headline, description, instruction);
                                }
                            }
                        }
                    }
                }
            }
            if(previousAlert != null) {
                builder.append(isFirst ? "" : ",").append(previousAlert.toString());
                addTerritoryAlert(previousAlert.getTerritory(), previousAlert);
            }
        }
        builder.append("]");
        final String string = builder.toString();
        alerts = string;
        updateTerritoryAlertsJSON();
        final long elapsedTime = System.currentTimeMillis()-time;
        System.out.println("Weather - updated Canada alerts (took " + elapsedTime + "ms)");
        if(handler != null) {
            handler.handle(string);
        }
    }
    private void addTerritoryAlert(String territory, WeatherAlert alert) {
        final String lowercase = territory.toLowerCase();
        if(territoryAlerts == null) {
            territoryAlerts = new HashMap<>();
        }
        if(territoryAlerts.containsKey(lowercase)) {
            territoryAlerts.get(lowercase).add(alert);
        } else {
            final Set<WeatherAlert> set = new HashSet<>();
            set.add(alert);
            territoryAlerts.put(lowercase, set);
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
