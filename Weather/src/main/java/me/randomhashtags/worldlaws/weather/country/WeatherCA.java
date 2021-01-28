package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.weather.WeatherAlert;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.WeatherEvent;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum WeatherCA implements WeatherController {
    INSTANCE;

    private String alertEvents;
    private HashMap<String, String> eventAlerts;
    private HashMap<String, HashMap<String, String>> territoryEventAlerts;
    private HashMap<String, String> territoryAlerts;

    @Override
    public WLCountry getCountryBackendID() {
        return WLCountry.CANADA;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("Canadian Government", "https://weather.gc.ca/warnings/index_e.html");
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

    @Override
    public void refreshAlerts(CompletionHandler handler) {
        eventAlerts = new HashMap<>();
        territoryEventAlerts = new HashMap<>();
        territoryAlerts = new HashMap<>();
        final String url = "https://weather.gc.ca/warnings/index_e.html";
        final Document doc = getDocument(url);
        final StringBuilder alertEventBuilder = new StringBuilder("[");
        final HashSet<String> loadedAlertEvents = new HashSet<>();
        final StringBuilder builder = new StringBuilder("[");
        if(doc != null) {
            final Elements rows = doc.select("main.container table.table tbody tr");
            String territory = "";
            boolean isFirst = true;
            WeatherAlert previousAlert = null;
            final HashMap<String, StringBuilder> eventAlertBuilders = new HashMap<>();
            final HashMap<String, StringBuilder> territoryAlertBuilders = new HashMap<>();
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
                            final String eventName = warningText.split(span)[0];
                            final boolean isWind = eventName.equalsIgnoreCase("wind");
                            final String event = (isWind ? "High Wind" : eventName).replace(" storm", " Storm" + (isWarning ? " Warning" : isWatch ? " Watch" : "")).replace("thunderstorm", "Thunderstorm") + (isThunderstorm || isWind ? " " + (isWarning ? "Warning" : "Watch") : "");
                            final String severity = isWarning ? "Severe" : isWatch ? "Moderate" : "-1";
                            final String warningHref = warning.attr("href");
                            final Document detailsDoc = getDocument("https://weather.gc.ca" + warningHref);
                            if(detailsDoc != null && !warningHref.equals("/hurricane/statements_e.html")) {
                                if(!loadedAlertEvents.contains(event)) {
                                    final boolean isfirst = loadedAlertEvents.isEmpty();
                                    loadedAlertEvents.add(event);
                                    final int defcon = getSeverityDEFCON(severity);
                                    final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
                                    alertEventBuilder.append(isfirst ? "" : ",").append(weatherEvent.toString());
                                }
                                final Elements table = detailsDoc.select("main.container div.row div.col-xs-12 p");
                                final String headline = null;
                                final String description = table.last().text().replace("\n", "\\n");
                                final String instruction = null;//properties.has("instruction") && properties.get("instruction") instanceof String ? properties.getString("instruction").replace("\n", "\\n") : null;
                                final boolean isTheSame = previousAlert != null && territory.equalsIgnoreCase(previousAlert.getTerritory()) && event.equalsIgnoreCase(previousAlert.getEvent()) && severity.equalsIgnoreCase(previousAlert.getSeverity()) && description.equalsIgnoreCase(previousAlert.getDescription());
                                if(isTheSame) {
                                    previousAlert.addArea(targetArea);
                                } else {
                                    if(previousAlert != null) {
                                        final boolean isFirstFinal = isFirst;
                                        final String alertTerritory = previousAlert.getTerritory(), string = previousAlert.toString(), previousEvent = previousAlert.getEvent();

                                        isFirst = !eventAlertBuilders.containsKey(previousEvent);
                                        if(isFirst) {
                                            eventAlertBuilders.put(previousEvent, new StringBuilder("["));
                                        }
                                        eventAlertBuilders.get(previousEvent).append(isFirst ? "" : ",").append(string);

                                        isFirst = isFirstFinal;
                                        builder.append(isFirst ? "" : ",").append(string);

                                        isFirst = !territoryAlertBuilders.containsKey(alertTerritory);
                                        if(isFirst) {
                                            territoryAlertBuilders.put(alertTerritory, new StringBuilder("["));
                                        }
                                        territoryAlertBuilders.get(alertTerritory).append(isFirst ? "" : ",").append(string);

                                        isFirst = false;
                                    }
                                    final WeatherAlertTime time = new WeatherAlertTime(null, null, null, null);
                                    previousAlert = new WeatherAlert(territory, targetArea, severity, certainty, event, headline, description, instruction, time);
                                }
                            }
                        }
                    }
                }
            }
            if(previousAlert != null) {
                builder.append(isFirst ? "" : ",").append(previousAlert.toString());

                final String alertTerritory = previousAlert.getTerritory();
                final boolean first = !territoryAlertBuilders.containsKey(alertTerritory);
                if(first) {
                    territoryAlertBuilders.put(alertTerritory, new StringBuilder("["));
                }
                territoryAlertBuilders.get(alertTerritory).append(first ? "" : ",").append(previousAlert.toString());
            }
            for(Map.Entry<String, StringBuilder> builders : eventAlertBuilders.entrySet()) {
                final String event = builders.getKey().toLowerCase().replace(" ", "");
                final String string = builders.getValue().append("]").toString();
                eventAlerts.put(event, string);
            }
            for(Map.Entry<String, StringBuilder> builders : territoryAlertBuilders.entrySet()) {
                final String alertTerritory = builders.getKey();
                final String string = builders.getValue().append("]").toString();
                territoryAlerts.put(alertTerritory, string);
            }
        }
        builder.append("]");
        final String string = alertEventBuilder.append("]").toString();
        alertEvents = string;
        if(handler != null) {
            handler.handle(string);
        }
    }
}
