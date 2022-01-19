package me.randomhashtags.worldlaws.weather.country;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.weather.WeatherAlert;
import me.randomhashtags.worldlaws.weather.WeatherController;
import me.randomhashtags.worldlaws.weather.WeatherEvent;
import me.randomhashtags.worldlaws.weather.WeatherPreAlert;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public enum WeatherCA implements WeatherController {
    INSTANCE;

    private HashMap<String, String> alertIDs, eventPreAlerts, territoryEvents;
    private HashMap<String, HashMap<String, String>> territoryPreAlerts;
    private HashMap<String, WeatherPreAlert> preAlertIDs;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CANADA;
    }

    @Override
    public EventSource getSource() {
        return new EventSource("Canadian Government", "https://weather.gc.ca/warnings/index_e.html");
    }

    @Override
    public HashMap<String, String> getEventPreAlerts() {
        return eventPreAlerts;
    }

    @Override
    public HashMap<String, String> getSubdivisionEvents() {
        return territoryEvents;
    }

    @Override
    public HashMap<String, HashMap<String, String>> getSubdivisionPreAlerts() {
        return territoryPreAlerts;
    }

    @Override
    public String refresh() {
        alertIDs = new HashMap<>();
        eventPreAlerts = new HashMap<>();
        territoryEvents = new HashMap<>();
        territoryPreAlerts = new HashMap<>();
        preAlertIDs = new HashMap<>();
        final String url = "https://weather.gc.ca/warnings/index_e.html";

        final ConcurrentHashMap<String, Integer> eventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, HashSet<WeatherPreAlert>> eventPreAlertsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, HashSet<WeatherEvent>> territoryEventsMap = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, ConcurrentHashMap<String, HashSet<WeatherPreAlert>>> territoryPreAlertsMap = new ConcurrentHashMap<>();

        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements rows = doc.select("main.container table.table tbody tr");
            String territory = "";
            WeatherPreAlert previousAlert = null;
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
                                final HashSet<WeatherEvent> territorySet = territoryEventsMap.get(territory);
                                boolean hasEvent = false;
                                for(WeatherEvent newWeatherEvent : territorySet) {
                                    if(event.equals(newWeatherEvent.getEvent())) {
                                        hasEvent = true;
                                        break;
                                    }
                                }
                                if(!hasEvent) {
                                    final WeatherEvent weatherEvent = new WeatherEvent(event, defcon);
                                    territoryEventsMap.get(territory).add(weatherEvent);
                                }

                                final Elements table = detailsDoc.select("main.container div.row div.col-xs-12 p");
                                final String headline = null;
                                final String description = table.last().text().replace("\n", "\\n");
                                final String instruction = null;//properties.has("instruction") && properties.get("instruction") instanceof String ? properties.getString("instruction").replace("\n", "\\n") : null;
                                /*final boolean isTheSame = previousAlert != null && territory.equalsIgnoreCase(previousAlert.getSubdivision()) && event.equalsIgnoreCase(previousAlert.getEvent()) && defcon == previousAlert.getDefcon() && description.equalsIgnoreCase(previousAlert.getDescription());
                                if(!isTheSame) {
                                    if(previousAlert != null) {
                                        preAlertIDs.put(previousAlert.getID(), previousAlert);
                                    }
                                    final WeatherAlertTime time = new WeatherAlertTime(null, null, null, null);
                                    previousAlert = new WeatherPreAlert(defcon, event, id, territory, certainty, headline, instruction, description, null, time);
                                }*/
                            }
                        }
                    }
                }
            }
        }

        putEventPreAlerts(eventPreAlerts, eventPreAlertsMap);
        putSubdivisionEvents(territoryEvents, territoryEventsMap);
        putSubdivisionPreAlerts(territoryPreAlerts, territoryPreAlertsMap);

        return getEventsJSON(eventsMap);
    }

    @Override
    public String getZones(String[] zones) {
        return null;
    }

    @Override
    public String getZone(String zoneID) {
        return null;
    }

    @Override
    public String getAlert(String id) {
        if(alertIDs == null) {
            final String string = refresh();
        }
        return tryGettingAlert(id);
    }
    private String tryGettingAlert(String id) {
        if(alertIDs.containsKey(id)) {
            return alertIDs.get(id);
        } else if(preAlertIDs.containsKey(id)) {
            final EventSource source = getSource();
            final WeatherPreAlert preAlert = preAlertIDs.get(id);
            final WeatherAlert alert = new WeatherAlert(preAlert, null, source);
            return alert.toString();
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
        } else {
            return null;
        }
    }
}
