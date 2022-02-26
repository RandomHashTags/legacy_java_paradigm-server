package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public interface RestAPI {
    HashMap<String, String> CONTENT_HEADERS = new HashMap<>() {{
        put("Content-Type", "application/json");
        put("User-Agent", "(Paradigm Proxy - Java Application, ***REMOVED***)");
    }};

    default JSONArray requestJSONArray(String url, RequestMethod method) {
        return requestJSONArray(url, method, CONTENT_HEADERS);
    }
    default JSONArray requestJSONArray(String url, RequestMethod method, HashMap<String, String> headers) {
        return requestJSONArray(url, method, headers, null);
    }
    default JSONArray requestJSONArray(String url, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query) {
        final String string = request(url, method, headers, query);
        return string != null ? new JSONArray(string) : null;
    }

    default JSONObject requestJSONObject(String url, RequestMethod method) {
        return requestJSONObject(url, method, CONTENT_HEADERS);
    }
    default JSONObject requestJSONObject(String url, RequestMethod method, HashMap<String, String> headers) {
        return requestJSONObject(url, method, headers, null);
    }
    default JSONObject requestJSONObject(String url, boolean isLimited, RequestMethod method, HashMap<String, String> headers) {
        return requestJSONObject(url, isLimited, method, headers, null);
    }
    default JSONObject requestJSONObject(String url, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestJSONObject(url, true, method, headers, query);
    }
    default JSONObject requestJSONObject(String url, boolean isLimited, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query) {
        final String string = request(url, isLimited, method, headers, query);
        return string != null ? new JSONObject(string) : null;
    }

    default String request(String targetURL, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return request(targetURL, true, method, headers, query);
    }
    default String request(String targetURL, boolean isLimited, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestStatic(targetURL, isLimited, method, headers, query);
    }

    static String requestStatic(String targetURL, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestStatic(targetURL, true, method, headers, query);
    }
    static String requestStatic(String targetURL, boolean isLimited, RequestMethod method, HashMap<String, String> headers, AbstractMap<String, String> query) {
        final boolean isLocal = targetURL.startsWith("http://localhost") || targetURL.startsWith("http://192.168");

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

            String responseString = null;
            try {
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
                    responseString = response.toString();
                } else {
                    WLLogger.logError("RestAPI", "invalid response code (" + responseCode + ") for url \"" + targetURL + "\"!");
                }
            } catch (Exception e) {
                if(!isLocal) {
                    WLUtilities.saveException(e);
                }
            }
            return responseString;
        } catch (Exception e) {
            if(!isLocal) {
                final StackTraceElement[] stackTrace = e.getStackTrace();
                WLLogger.logWarning("[REST API] - \"(" + stackTrace[0].getClassName() + ") " + e.getMessage() + " with url \"" + targetURL + "\" with headers: " + (headers != null ? headers.toString() : "null") + ", and query: " + (query != null ? query.toString() : "null"));
                WLUtilities.saveException(e);
            }
            return null;
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
