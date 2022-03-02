package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public interface RestAPI {
    HashMap<String, String> CONTENT_HEADERS = new HashMap<>() {{
        put("Content-Type", "application/json");
        put("User-Agent", "(Paradigm Proxy - Java Application, ***REMOVED***)");
    }};

    HttpClient CLIENT = HttpClient.newHttpClient();


    default JSONArray requestJSONArray(String url) {
        return requestJSONArray(url, CONTENT_HEADERS);
    }
    default JSONArray requestJSONArray(String url, HashMap<String, String> headers) {
        return requestJSONArray(url, headers, null);
    }
    default JSONArray requestJSONArray(String url, HashMap<String, String> headers, HashMap<String, String> query) {
        final String string = requestStatic(url, headers, query);
        return string != null ? new JSONArray(string) : null;
    }

    default JSONObject requestJSONObject(String url) {
        return requestJSONObject(url, CONTENT_HEADERS);
    }
    default JSONObject requestJSONObject(String url, HashMap<String, String> headers) {
        return requestJSONObject(url, headers, null);
    }
    default JSONObject requestJSONObject(String url, boolean isLimited, HashMap<String, String> headers) {
        return requestJSONObject(url, isLimited, headers, null);
    }
    default JSONObject requestJSONObject(String url, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestJSONObject(url, true, headers, query);
    }
    default JSONObject requestJSONObject(String url, boolean isLimited, HashMap<String, String> headers, AbstractMap<String, String> query) {
        final String string = request(url, isLimited, headers, query);
        return string != null ? new JSONObject(string) : null;
    }
    default JSONObject postJSONObject(String url, Map<String, String> postData, boolean isLimited, HashMap<String, String> headers, AbstractMap<String, String> query) {
        final String string = requestStatic(url, postData, isLimited, headers, query);
        return string != null ? new JSONObject(string) : null;
    }

    default String request(String targetURL, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return request(targetURL, true, headers, query);
    }
    default String request(String targetURL, boolean isLimited, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestStatic(targetURL, isLimited, headers, query);
    }
    static String requestStatic(String targetURL, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestStatic(targetURL, true, headers, query);
    }
    static String requestStatic(String targetURL, boolean isLimited, HashMap<String, String> headers, AbstractMap<String, String> query) {
        return requestStatic(targetURL, null, isLimited, headers, query);
    }

    static String requestStatic(String targetURL, Map<String, String> postData, boolean isLimited, HashMap<String, String> headers, AbstractMap<String, String> query) {
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

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(targetURL))
                .header("Content-Language", "en-US")
                .timeout(Duration.ofSeconds(10));
        if(postData != null) {
            final HttpRequest.BodyPublisher publisher = parsePostData(postData);
            requestBuilder.POST(publisher);
        } else {
            requestBuilder.GET();
        }
        if(isLocal) {
            requestBuilder.header("Accept-Charset", DataValues.ENCODING.displayName());
        }
        if(headers != null) {
            for(Map.Entry<String, String> entry : headers.entrySet()) {
                final String key = entry.getKey(), value = entry.getValue();
                if(key != null && value != null) {
                    requestBuilder.header(key, value);
                }
            }
        }
        String string = null;
        final CompletableFuture<String> test = CLIENT.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).exceptionally(throwable -> {
            final Exception e = throwable instanceof Exception ? (Exception) throwable : null;
            if(e != null) {
                if(e instanceof CompletionException || e instanceof ConnectException) {
                    final String msg = e.getMessage();
                    if(msg.contains("Connection refused") && isLocal) {
                        return null;
                    }
                }
                WLUtilities.saveException(e);
            }
            return null;
        });
        //final long started = System.currentTimeMillis();
        try {
            string = test.get();
            //WLLogger.logInfo("RestAPI;requestStaticGET;targetURL=" + targetURL + " ;serverResponseTime=" + WLUtilities.getElapsedTime(started));
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        return string;
    }
    private static HttpRequest.BodyPublisher parsePostData(Map<String, String> data) {
        final StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, String> map : data.entrySet()) {
            if(builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(map.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(map.getValue(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
