package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.holiday.Holidays;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.weather.Weather;

public final class Proxy implements RestAPI {

    private static Proxy INSTANCE;
    private String countries;

    public static void main(String[] args) {
        INSTANCE = new Proxy();
        INSTANCE.init();
    }

    private void init() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Country country : Country.values()) {
            builder.append(isFirst ? "" : ",").append(country.toString());
            isFirst = false;
        }
        builder.append("]");
        countries = builder.toString();

        LocalServer.setupHttpServer("Proxy", DataValues.WL_PROXY_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String response = object.toString();
                        client.sendResponse(response);
                    }
                });
            }
        });
    }

    private void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        switch (values[0]) {
            case "countries":
                handler.handle(countries);
                break;
            case "holidays":
                getHolidaysResponse(values[1], handler);
                break;
            case "upcomingevents":
                getUpcomingEventsResponse(values[1], handler);
                break;
            case "weather":
                getWeatherResponse(values[1], value.substring(values[0].length()+values[1].length()+2), handler);
                break;
            default:
                final String targetServer = values[0];
                final Country country = Country.valueOfBackendID(targetServer);
                if(country != null) {
                    if(values.length == 1) {
                        handler.handle(country.getInformationJSON());
                    } else {
                        switch (values[1]) {
                            case "territories":
                                handler.handle(country.getTerritoriesJSON());
                                break;
                            default:
                                getServerResponse(country, value.substring(targetServer.length()+1), handler);
                                break;
                        }
                    }
                }
                break;
        }
    }

    private void getHolidaysResponse(String value, CompletionHandler handler) {
        switch (value) {
            case "all":
                Holidays.INSTANCE.getAllHolidays(handler);
                break;
            case "near":
                Holidays.INSTANCE.getNearHolidays(handler);
                break;
            default:
                final Country country = Country.valueOfBackendID(value);
                if(country != null) {
                    Holidays.INSTANCE.getHolidaysFor(country, handler);
                }
                break;
        }
    }

    private void getUpcomingEventsResponse(String value, CompletionHandler handler) {
        switch (value) {
            case "all":
                UpcomingEvents.INSTANCE.getUpcomingWorldwideEvents(handler);
                break;
            default:
                final Country country = Country.valueOfBackendID(value);
                if(country != null) {
                    UpcomingEvents.INSTANCE.getUpcomingEvents(country, handler);
                }
                break;
        }
    }

    private void getWeatherResponse(String key, String value, CompletionHandler handler) {
        switch (key) {
            case "alerts":
                getWeatherAlertsResponse(value, handler);
                break;
            default:
                break;
        }
    }
    private void getWeatherAlertsResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        switch (values[0]) {
            case "all":
                Weather.INSTANCE.getAllAlerts(handler);
                break;
            default:
                final Country country = Country.valueOfBackendID(value);
                if(country != null) {
                    if(values.length == 2) {
                        Weather.INSTANCE.getAlerts(country, values[1], handler);
                    } else {
                        Weather.INSTANCE.getAlerts(country, handler);
                    }
                }
                break;
        }
    }

    private void getServerResponse(Country country, String value, CompletionHandler handler) {
        final TargetServer server = country.getServer();
        server.sendResponse(RequestMethod.POST, value, handler);
    }
}
