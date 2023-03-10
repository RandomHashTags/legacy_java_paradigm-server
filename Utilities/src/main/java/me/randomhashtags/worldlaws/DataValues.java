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
    String HTTP_MAINTENANCE_MODE = HTTP_PREFIX.replace("%status%", "404 ERROR");

    default JSONObject getJSONDataValue(JSONDataValue value) {
        final String identifier = value.getIdentifier();
        final JSONObject dataValuesJSON = getDataValuesJSON();
        return dataValuesJSON.optJSONObject(identifier, new JSONObject());
    }
    default void setJSONDataValue(JSONDataValue value, JSONObject json) {
        final Folder folder = Folder.OTHER;
        final String fileName = "data values", identifier = value.getIdentifier();
        final JSONObject dataValuesJSON = getDataValuesJSON();
        dataValuesJSON.put(identifier, json);
        Jsonable.setFileJSONObject(folder, fileName, dataValuesJSON);
    }
    private JSONObject getDataValuesJSON() {
        final Folder folder = Folder.OTHER;
        final String fileName = "data values";
        return getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return new JSONObject();
            }
        });
    }
}