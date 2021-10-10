package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface RestAPI {
    HashMap<String, String> CONTENT_HEADERS = new HashMap<>() {{
        put("Content-Type", "application/json");
    }};
    ConcurrentHashMap<String, HashSet<CompletionHandler>> PENDING_SAME_REQUESTS = new ConcurrentHashMap<>();

    default void requestJSONArray(String url, RequestMethod method, CompletionHandler handler) {
        requestJSONArray(url, method, CONTENT_HEADERS, handler);
    }
    default void requestJSONArray(String url, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        requestJSONArray(url, method, headers, null, handler);
    }
    default void requestJSONArray(String url, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        request(url, method, headers, query, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                if(string != null) {
                    final JSONArray json = new JSONArray(string);
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
    default void requestJSONObject(String url, boolean isLimited, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        requestJSONObject(url, isLimited, method, headers, null, handler);
    }
    default void requestJSONObject(String url, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query, CompletionHandler handler) {
        requestJSONObject(url, true, method, headers, query, handler);
    }
    default void requestJSONObject(String url, boolean isLimited, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query, CompletionHandler handler) {
        request(url, isLimited, method, headers, query, new CompletionHandler() {
            @Override
            public void handleString(String string) {
                if(string != null) {
                    final JSONObject json = new JSONObject(string);
                    handler.handleJSONObject(json);
                } else {
                    handler.handleJSONObject(null);
                }
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
            }
        });
    }

    default void request(String targetURL, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query, CompletionHandler handler) {
        request(targetURL, true, method, headers, query, handler);
    }
    default void request(String targetURL, boolean isLimited, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query, CompletionHandler handler) {
        final boolean isLocal = targetURL.startsWith("http://localhost");

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
        if(isLimited && !isLocal) {
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
            switch (method) {
                case POST:
                    connection.setFixedLengthStreamingMode(0);
                    break;
                default:
                    break;
            }
            if(headers != null) {
                for(Map.Entry<String, String> entry : headers.entrySet()) {
                    final String key = entry.getKey(), value = entry.getValue();
                    connection.setRequestProperty(key, value);
                }
            }
            if(isLocal) {
                connection.setRequestProperty("Accept-Charset", DataValues.ENCODING.displayName());
            }
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            final int responseCode = connection.getResponseCode();
            String responseString = null;
            if(responseCode >= 200 && responseCode < 400) {
                final InputStream is = connection.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                final StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append('\r');
                }
                reader.close();
                responseString = response.toString();
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
            }
            handler.handleString(responseString);
            if(PENDING_SAME_REQUESTS.containsKey(targetURL)) {
                final HashSet<CompletionHandler> sameRequests = PENDING_SAME_REQUESTS.get(targetURL);
                for(CompletionHandler pendingHandler : sameRequests) {
                    pendingHandler.handleString(responseString);
                }
                PENDING_SAME_REQUESTS.remove(targetURL);
            }

        } catch (Exception e) {
            if(!isLocal) {
                final StackTraceElement[] stackTrace = e.getStackTrace();
                WLLogger.log(Level.ERROR, "[REST API] - \"(" + stackTrace[0].getClassName() + ") " + e.getMessage() + " with url \"" + targetURL + "\" with headers: " + (headers != null ? headers.toString() : "null") + ", and query: " + (query != null ? query.toString() : "null"));
                WLUtilities.saveException(e);
            }
            handler.handleString(null);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
