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
    String HTTP_ERROR_404 = HTTP_PREFIX.replace("%status%", "404 ERROR") + "Access Denied. Your IP has been logged and will be banned if you continue trying to connect.";

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
        final Folder folder = Folder.OTHER;
        final String fileName = "data values", identifier = value.getIdentifier();
        getDataValuesJSON(new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject dataValuesJSON) {
                dataValuesJSON.put(identifier, json);
                setFileJSONObject(folder, fileName, dataValuesJSON);
            }
        });
    }
    private void getDataValuesJSON(CompletionHandler handler) {
        final Folder folder = Folder.OTHER;
        final String fileName = "data values";
        getJSONObject(folder, fileName, new CompletionHandler() {
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