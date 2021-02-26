package me.randomhashtags.worldlaws.weather.recode.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.location.WLCountry;
import me.randomhashtags.worldlaws.weather.WeatherAlert;
import me.randomhashtags.worldlaws.weather.WeatherAlertTime;
import me.randomhashtags.worldlaws.weather.recode.NewWeatherAlert;
import me.randomhashtags.worldlaws.weather.recode.NewWeatherController;
import me.randomhashtags.worldlaws.weather.recode.NewWeatherEvent;
import me.randomhashtags.worldlaws.weather.recode.NewWeatherPreAlert;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public enum WeatherCA implements NewWeatherController {
    INSTANCE;

    private String eventsJSON;
    private HashMap<String, String> alertIDs, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, String>> territoryPreAlerts;
    private HashMap<String, NewWeatherPreAlert> preAlertIDs;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CANADA;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("Canadian Government", "https://weather.gc.ca/warnings/index_e.html");
    }

    @Override
    public String getEvents() {
        return eventsJSON;
    }

    @Override
    public HashMap<String, String> getEventPreAlerts() {
        return eventPreAlerts;
    }

    @Override
    public HashMap<String, String> getTerritoryEvents() {
        return territoryEvents;
    }

    @Override
    public HashMap<String, HashMap<String, String>> getTerritoryPreAlerts() {
        return territoryPreAlerts;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        eventsJSON = "[]";
        alertIDs = new HashMap<>();
        eventPreAlerts = new HashMap<>();
        territoryEvents = new HashMap<>();
        territoryPreAlerts = new HashMap<>();
        preAlertIDs = new HashMap<>();
        final String country = getCountry().getBackendID(), url = "https://weather.gc.ca/warnings/index_e.html";

        final HashMap<String, Integer> eventsMap = new HashMap<>();
        final HashMap<String, HashSet<NewWeatherPreAlert>> eventPreAlertsMap = new HashMap<>();
        final HashMap<String, HashSet<NewWeatherEvent>> territoryEventsMap = new HashMap<>();
        final HashMap<String, HashMap<String, HashSet<NewWeatherPreAlert>>> territoryPreAlertsMap = new HashMap<>();

        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements rows = doc.select("main.container table.table tbody tr");
            String territory = "";
            NewWeatherPreAlert previousAlert = null;
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
                            final String certainty = "Unknown", spanValue = span.toLowerCase();
                            final boolean isWarning = spanValue.contains("warning"), isWatch = spanValue.contains("watch"), isThunderstorm = spanValue.contains("thunderstorm");
                            final String eventName = warningText.split(span)[0];
                            final boolean isWind = eventName.equalsIgnoreCase("wind");
                            final String event = (isWind ? "High Wind" : eventName).replace(" storm", " Storm" + (isWarning ? " Warning" : isWatch ? " Watch" : "")).replace("thunderstorm", "Thunderstorm") + (isThunderstorm || isWind ? " " + (isWarning ? "Warning" : "Watch") : "");
                            final String severity = isWarning ? "Severe" : isWatch ? "Moderate" : "-1";
                            final String warningHref = warning.attr("href");
                            final Document detailsDoc = getDocument("https://weather.gc.ca" + warningHref);
                            if(detailsDoc != null && !warningHref.equals("/hurricane/statements_e.html")) {
                                final String[] values = warningHref.split("/");
                                final String id = values[1].split("\\?")[1].split("#")[0];
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

                                final Elements table = detailsDoc.select("main.container div.row div.col-xs-12 p");
                                final String headline = null;
                                final String description = table.last().text().replace("\n", "\\n");
                                final String instruction = null;//properties.has("instruction") && properties.get("instruction") instanceof String ? properties.getString("instruction").replace("\n", "\\n") : null;
                                final boolean isTheSame = previousAlert != null && territory.equalsIgnoreCase(previousAlert.getTerritory()) && event.equalsIgnoreCase(previousAlert.getEvent()) && severity.equalsIgnoreCase(previousAlert.getSeverity()) && description.equalsIgnoreCase(previousAlert.getDescription());
                                if(!isTheSame) {
                                    if(previousAlert != null) {
                                        preAlertIDs.put(previousAlert.getID(), previousAlert);
                                    }
                                    final WeatherAlertTime time = new WeatherAlertTime(null, null, null, null);
                                    previousAlert = new NewWeatherPreAlert(event, id, country, territory, severity, certainty, headline, instruction, description, null, time);
                                }
                            }
                        }
                    }
                }
            }
        }

        putEventPreAlerts(eventPreAlerts, eventPreAlertsMap);
        putTerritoryEvents(territoryEvents, territoryEventsMap);
        putTerritoryPreAlerts(territoryPreAlerts, territoryPreAlertsMap);
        eventsJSON = getEventsJSON(eventsMap);

        if(handler != null) {
            handler.handle(eventsJSON);
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
            final EventSource source = getSource();
            final NewWeatherPreAlert preAlert = preAlertIDs.get(id);
            final NewWeatherAlert alert = new NewWeatherAlert(preAlert, null, source);
            handler.handle(alert.toString());
            /*
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
                        final NewWeatherAlert alert = new NewWeatherAlert(preAlert, zonesJSON, source);
                        final String string = alert.toString();
                        alertIDs.put(id, string);
                        preAlertIDs.remove(id);
                        handler.handle(string);
                    }
                }
            }));*/
        }
    }
}
