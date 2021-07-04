package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.JSONDataValue;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface DataValues extends Jsonable {
    Charset ENCODING = StandardCharsets.UTF_8;
    String HTTP_VERSION = "HTTP/1.1";
    String HTTP_PREFIX = HTTP_VERSION + " %status%\r\nContent-Type: application/json\r\nCharset: " + ENCODING.displayName() + "\r\n\r\n";
    String HTTP_SUCCESS_200 = HTTP_PREFIX.replace("%status%", "200 OK");
    String HTTP_ERROR_404 = HTTP_PREFIX.replace("%status%", "404 ERROR") + "Stop trying to connect, ya bottom feeder. Your IP has been logged and will be blocked if you continue trying to connect.";

    int WL_PROXY_PORT = 0;
    int WL_COUNTRIES_PORT = getPort(1);
    int WL_ENVIRONMENT_PORT = getPort(2);
    int WL_FEEDBACK_PORT = getPort(3);
    int WL_LAWS_PORT = getPort(4);
    int WL_NEWS_PORT = getPort(5);
    int WL_SERVICES_PORT = getPort(6);
    int WL_SPACE_PORT = getPort(7);
    int WL_TECHNOLOGY_PORT = getPort(8);
    int WL_UPCOMING_EVENTS_PORT = getPort(9);
    int WL_WEATHER_PORT = getPort(10);

    String WL_COUNTRIES_SERVER_IP = getLocalIP(WL_COUNTRIES_PORT);
    String WL_ENVIRONMENT_SERVER_IP = getLocalIP(WL_ENVIRONMENT_PORT);
    String WL_FEEDBACK_SERVER_IP = getLocalIP(WL_FEEDBACK_PORT);
    String WL_LAWS_SERVER_IP = getLocalIP(WL_LAWS_PORT);
    String WL_NEWS_SERVER_IP = getLocalIP(WL_NEWS_PORT);
    String WL_SERVICES_SERVER_IP = getLocalIP(WL_SERVICES_PORT);
    String WL_SPACE_SERVER_IP = getLocalIP(WL_SPACE_PORT);
    String WL_TECHNOLOGY_SERVER_IP = getLocalIP(WL_TECHNOLOGY_PORT);
    String WL_UPCOMING_EVENTS_SERVER_IP = getLocalIP(WL_UPCOMING_EVENTS_PORT);
    String WL_WEATHER_SERVER_IP = getLocalIP(WL_WEATHER_PORT);

    private static String getLocalIP(int port)  {
        return "http://localhost:" + port;
    }
    private static int getPort(int offset) {
        return WL_PROXY_PORT + offset;
    }

    int TWITCH_REQUEST_LIMIT = 100;
    String TWITCH_CLIENT_ID = "***REMOVED***";
    String TWITCH_ACCESS_TOKEN = "***REMOVED***";

    int YOUTUBE_REQUEST_LIMIT = 49;
    String YOUTUBE_KEY = "***REMOVED***";
    String YOUTUBE_KEY_IDENTIFIER = "***REMOVED***";
    String YOUTUBE_KEY_VALUE = "***REMOVED***";

    String GOOGLE_CIVIC_API_KEY = "***REMOVED***";

    String YAHOO_FINANCE_RAPID_API_KEY = "***REMOVED***";

    String NASA_API_KEY = "***REMOVED***";

    default void getJSONDataValue(JSONDataValue value, CompletionHandler handler) {
        final String identifier = value.getIdentifier();
        getDataValuesJSON(new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject dataValuesJSON) {
                final JSONObject json = dataValuesJSON.has(identifier) ? dataValuesJSON.getJSONObject(identifier) : new JSONObject();
                handler.handleJSONObject(json);
            }
        });
    }
    default void setJSONDataValue(JSONDataValue value, JSONObject json) {
        final FileType fileType = FileType.OTHER;
        final String fileName = "data values", identifier = value.getIdentifier();
        getDataValuesJSON(new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject dataValuesJSON) {
                dataValuesJSON.put(identifier, json);
                setFileJSONObject(fileType, fileName, dataValuesJSON);
            }
        });
    }
    private void getDataValuesJSON(CompletionHandler handler) {
        final FileType fileType = FileType.OTHER;
        final String fileName = "data values";
        getJSONObject(fileType, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handler.handleJSONObject(new JSONObject());
            }

            @Override
            public void handleJSONObject(JSONObject dataValuesJSON) {
                handler.handleJSONObject(dataValuesJSON);
            }
        });
    }
}