package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public interface RestAPI {
    HashMap<String, String> CONTENT_HEADERS = new HashMap<>() {{
        put("Content-Type", "application/json");
    }};

    default void requestJSONArray(String url, RequestMethod method, CompletionHandler handler) {
        requestJSONArray(url, method, CONTENT_HEADERS, handler);
    }
    default void requestJSONArray(String url, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        requestJSONArray(url, method, headers, null, handler);
    }
    default void requestJSONArray(String url, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        request(url, method, headers, query, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                if(object != null) {
                    final JSONArray json = new JSONArray(object.toString());
                    handler.handleJSONArray(json);
                }
            }
        });
    }

    default void requestJSONObject(String url, RequestMethod method, CompletionHandler handler) {
        requestJSONObject(url, method, CONTENT_HEADERS, handler);
    }
    default void requestJSONObject(String url, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        requestJSONObject(url, method, headers, null, handler);
    }
    default void requestJSONObject(String url, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        request(url, method, headers, query, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                if(object != null) {
                    final JSONObject json = new JSONObject(object.toString());
                    handler.handleJSONObject(json);
                } else {
                    handler.handleJSONObject(null);
                }
            }
        });
    }

    default void request(String targetURL, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        HttpURLConnection connection = null;
        try {
            final StringBuilder target = new StringBuilder(targetURL);
            int i = 0;
            if (query != null) {
                target.append("?");
                for (Map.Entry<String, String> entry : query.entrySet()) {
                    final String key = entry.getKey(), value = entry.getValue();
                    if (i != 0) {
                        target.append("&");
                    }
                    target.append(key).append("=").append(value);
                    i++;
                }
            }
            final String targeturl = target.toString();
            final URL url = new URL(targeturl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10_000);
            connection.setRequestMethod(method.getName());
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    final String key = entry.getKey(), value = entry.getValue();
                    connection.setRequestProperty(key, value);
                }
            }
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            final int responseCode = connection.getResponseCode();
            if(responseCode >= 200 && responseCode < 400) {
                final InputStream is = connection.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                final StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append('\r');
                }
                reader.close();
                handler.handle(response.toString());
            } else {
                final StringBuilder builder = new StringBuilder("RestAPI ERROR!");
                builder.append("\nDiagnoses = responseCode=").append(responseCode);
                builder.append("\ntargeturl=").append(targeturl);
                builder.append("\nheaderFields=").append(connection.getHeaderFields().toString());
                try {
                    final String string = connection.getRequestProperties().toString();
                    builder.append("\nrequestProperties=").append(string);
                } catch (Exception ignored) {
                }
                builder.append("\nheaders=").append(headers != null ? headers.toString() : "null");
                builder.append("\nerrorStream=").append(connection.getErrorStream());
                WLLogger.log(Level.WARN, builder.toString());
                handler.handle(null);
            }
        } catch (Exception e) {
            if(!targetURL.startsWith("http://localhost")) {
                WLLogger.log(Level.ERROR, "[REST API] - \"(" + e.getStackTrace()[0].getClassName() + ") " + e.getMessage() + "\" with url \"" + targetURL + "\" with headers: " + (headers != null ? headers.toString() : "null") + ", and query: " + (query != null ? query.toString() : "null"));
            }
            handler.handle(null);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
