package me.randomhashtags.worldlaws;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

public interface RestAPI {
    HashMap<String, String> CONTENT_HEADERS = new HashMap<>() {{
        put("Content-Type", "application/json");
    }};

    default void requestJSON(String url, RequestMethod method, CompletionHandler handler) {
        requestJSON(url, method, CONTENT_HEADERS, handler);
    }
    default void requestJSON(String url, RequestMethod method, HashMap<String, String> headers, CompletionHandler handler) {
        requestJSON(url, method, headers, null, handler);
    }
    default void requestJSON(String url, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        request(url, method, headers, query, handler);
    }

    default void request(String targetURL, RequestMethod method, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        HttpURLConnection connection = null;
        try {
            final StringBuilder target = new StringBuilder(targetURL);
            int i = 0;
            if(query != null) {
                target.append("?");
                for(String s : query.keySet()) {
                    if(i != 0) {
                        target.append("&");
                    }
                    final String value = query.get(s);
                    target.append(s).append("=").append(value);
                    i++;
                }
            }
            final String targeturl = target.toString();
            final URL url = new URL(targeturl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method.getName());
            if(headers != null) {
                for(String s : headers.keySet()) {
                    final String value = headers.get(s);
                    connection.setRequestProperty(s, value);
                }
            }
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            final InputStream is;
            final int responseCode = connection.getResponseCode();
            if(responseCode >= 200 && responseCode < 400) {
                is = connection.getInputStream();
            } else {
                final StringBuilder builder = new StringBuilder("RestAPI ERROR!");
                builder.append("\nDiagnoses = response code < 200 || response code >= 400");
                builder.append("\nresponseCode=").append(responseCode);
                builder.append("\ntargeturl=").append(targeturl);
                builder.append("\nheaderFields=").append(connection.getHeaderFields().toString());
                try {
                    final String string = connection.getRequestProperties().toString();
                    builder.append("\nrequestProperties=").append(string);
                } catch (Exception ignored) {
                }
                builder.append("\nheaders=").append(headers != null ? headers.toString() : "null");
                builder.append("\nerrorStream=").append(connection.getErrorStream());
                WLLogger.log(Level.WARNING, builder.toString());
                return;
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            final StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append('\r');
            }
            reader.close();
            handler.handle(response.toString());
        } catch (Exception e) {
            if(!targetURL.startsWith("http://localhost")) {
                WLLogger.log(Level.WARNING, "[REST API] ERROR - \"(" + e.getStackTrace()[0].getClassName() + ") " + e.getMessage() + "\" with url \"" + targetURL + "\" with headers: " + (headers != null ? headers.toString() : "null") + ", and query: " + (query != null ? query.toString() : "null"));
            }
            handler.handle(null);
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
