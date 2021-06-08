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
import java.util.HashSet;
import java.util.Map;

public interface RestAPI {
    HashMap<String, String> CONTENT_HEADERS = new HashMap<>() {{
        put("Content-Type", "application/json");
    }};
    HashMap<String, HashSet<CompletionHandler>> PENDING_SAME_REQUESTS = new HashMap<>();

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
                } else {
                    handler.handleJSONArray(null);
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
        final boolean isLocal = targetURL.startsWith("http://localhost") || targetURL.startsWith("http://192.168.1.96:0");

        final StringBuilder target = new StringBuilder(targetURL);
        int i = 0;
        if(query != null) {
            target.append("?");
            for(Map.Entry<String, String> entry : query.entrySet()) {
                final String key = entry.getKey(), value = entry.getValue();
                if(i != 0) {
                    target.append("&");
                }
                target.append(key).append("=").append(value);
                i++;
            }
        }
        targetURL = target.toString();

        WLLogger.log(Level.INFO, "RestAPI - making " + (isLocal ? "local " : "") + "request to \"" + targetURL + "\"");
        if(!isLocal) {
            if(PENDING_SAME_REQUESTS.containsKey(targetURL)) {
                PENDING_SAME_REQUESTS.get(targetURL).add(handler);
                return;
            } else {
                PENDING_SAME_REQUESTS.put(targetURL, new HashSet<>());
            }
        }
        HttpURLConnection connection = null;
        try {
            final URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10_000);
            connection.setRequestMethod(method.name());
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
                builder.append("\ntargeturl=").append(targetURL);
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

                final HashSet<CompletionHandler> sameRequests = PENDING_SAME_REQUESTS.get(targetURL);
                if(sameRequests.size() > 0) {
                    for(CompletionHandler pendingHandler : sameRequests) {
                        pendingHandler.handle(null);
                    }
                }
                PENDING_SAME_REQUESTS.remove(targetURL);
            }
        } catch (Exception e) {
            if(!isLocal) {
                final StackTraceElement[] stackTrace = e.getStackTrace();
                WLLogger.log(Level.ERROR, "[REST API] - \"(" + stackTrace[0].getClassName() + ") " + e.getMessage() + " with url \"" + targetURL + "\" with headers: " + (headers != null ? headers.toString() : "null") + ", and query: " + (query != null ? query.toString() : "null"));
                e.printStackTrace();
            }
            handler.handle(null);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
