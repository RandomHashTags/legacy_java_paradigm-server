package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public interface RestAPI {
    String USER_AGENT = "(Project Paradigm - Java Application/11, ***REMOVED***)";
    LinkedHashMap<String, String> GET_CONTENT_HEADERS = new LinkedHashMap<>() {{
        put("Accept", "application/json");
        put("User-Agent", USER_AGENT);
    }};

    HttpClient CLIENT = getClient();
    HashMap<String, JSONArray> RESPONSE_TIMES = new HashMap<>();
    private static Duration getConnectionTimeout() {
        return Duration.ofSeconds(20);
    }

    private static HttpClient getClient() {
        final HttpClient.Builder client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(getConnectionTimeout())
                ;
        try {
            client.sslContext(SSLContext.getDefault());
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        return client.build();
    }
    static void saveResponseTimes() {
        if(!RESPONSE_TIMES.isEmpty()) {
            final LocalDate now = LocalDate.now();
            final Folder folder = Folder.LOGS;
            final String fileName = "response_times", folderName = folder.getFolderName().replace("%year%", Integer.toString(now.getYear())).replace("%month%", now.getMonth().name()).replace("%day%", Integer.toString(now.getDayOfMonth())).replace("%type%", "ResponseTimes").replace("%server%", "RestAPI");
            folder.setCustomFolderName(fileName, folderName);
            final JSONObject json = new JSONObject();
            for(Map.Entry<String, JSONArray> entry : RESPONSE_TIMES.entrySet()) {
                final String url = entry.getKey();
                json.put(url, entry.getValue());
            }
            Jsonable.setFileJSONObject(folder, fileName, json);
        }
    }


    default JSONArray requestJSONArray(String url) {
        return requestJSONArray(url, GET_CONTENT_HEADERS);
    }
    default JSONArray requestJSONArray(String url, LinkedHashMap<String, String> headers) {
        return requestJSONArray(url, headers, null);
    }
    default JSONArray requestJSONArray(String url, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        final String string = requestStatic(url, headers, query);
        if(string != null) {
            try {
                return new JSONArray(string);
            } catch (Exception e) {
                final String stackTrace = WLUtilities.getThrowableStackTrace(e);
                final String value = "failed to parse JSONObject from url \"" + url + "\" with string\n" +
                        "\"" + string + "\"\n\n" +
                        "headers=" + (headers != null ? headers.toString() : "null") + "\n" +
                        "query=" + (query != null ? query.toString() : "null") + "\n" +
                        "stackTrace=\n" + stackTrace;
                WLUtilities.saveLoggedError("JSONException", value);
            }
        }
        return null;
    }

    default JSONObject requestJSONObject(String url) {
        return requestJSONObject(url, GET_CONTENT_HEADERS);
    }
    default JSONObject requestJSONObject(String url, LinkedHashMap<String, String> headers) {
        return requestJSONObject(url, headers, null);
    }
    default JSONObject requestJSONObject(String url, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStaticJSONObject(url, headers, query);
    }
    static JSONObject requestStaticJSONObject(String url, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        final String string = requestStatic(url, headers, query);
        if(string != null) {
            try {
                return new JSONObject(string);
            } catch (Exception e) {
                final String stackTrace = WLUtilities.getThrowableStackTrace(e);
                final String value = "failed to parse JSONObject from url \"" + url + "\" with string\n" +
                        "\"" + string + "\"\n\n" +
                        "headers=" + (headers != null ? headers.toString() : "null") + "\n" +
                        "query=" + (query != null ? query.toString() : "null") + "\n" +
                        "stackTrace=\n" + stackTrace;
                WLUtilities.saveLoggedError("JSONException", value);
            }
        }
        return null;
    }

    default JSONObject postJSONObject(String url, LinkedHashMap<String, Object> postData, LinkedHashMap<String, String> headers) {
        return postStaticJSONObject(url, postData, headers);
    }
    default JSONObject postJSONObject(String url, LinkedHashMap<String, Object> postData, boolean postDataIsJSONObject, LinkedHashMap<String, String> headers) {
        return postStaticJSONObject(url, postData, postDataIsJSONObject, headers);
    }
    static JSONObject postStaticJSONObject(String url, LinkedHashMap<String, Object> postData, LinkedHashMap<String, String> headers) {
        return postStaticJSONObject(url, postData, false, headers);
    }
    static JSONObject postStaticJSONObject(String url, LinkedHashMap<String, Object> postData, boolean postDataIsJSONObject, LinkedHashMap<String, String> headers) {
        if(postData == null) {
            postData = new LinkedHashMap<>();
        }
        if(headers == null) {
            headers = new LinkedHashMap<>();
        }
        if(!headers.containsKey("User-Agent")) {
            headers.put("User-Agent", USER_AGENT);
        }
        if(!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/json");
        }
        final String string = requestStatic(url, postData, postDataIsJSONObject, headers, null);
        return string != null && string.startsWith("{") && string.endsWith("}") ? new JSONObject(string) : null;
    }

    default String request(String targetURL, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, headers, query);
    }
    static String requestStatic(String targetURL, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, null, headers, query);
    }

    static String requestStatic(String targetURL, LinkedHashMap<String, Object> postData, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, postData, false, headers, query);
    }
    static String requestStatic(String targetURL, LinkedHashMap<String, Object> postData, boolean postDataIsJSONObject, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        final long started = System.currentTimeMillis();
        final boolean isLocal = targetURL.startsWith("http://localhost") || targetURL.startsWith("http://192.168");

        final StringBuilder target = new StringBuilder(targetURL);
        if(query != null) {
            target.append("?");
            final String queryString = parseQuery(query, false);
            target.append(queryString);
        }
        targetURL = target.toString();

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(targetURL));
        if(!isLocal) {
            requestBuilder.timeout(getConnectionTimeout());
        }
        if(postData != null) {
            final HttpRequest.BodyPublisher publisher = parsePostData(postData, postDataIsJSONObject);
            requestBuilder.POST(publisher);
        } else {
            requestBuilder.GET();
        }
        if(headers != null) {
            for(Map.Entry<String, String> entry : headers.entrySet()) {
                final String key = entry.getKey(), value = entry.getValue();
                if(key != null && value != null) {
                    requestBuilder.header(key, value);
                }
            }
        }
        final String finalURL = targetURL;
        if(!isLocal) {
            RESPONSE_TIMES.putIfAbsent(finalURL, new JSONArray());
        }
        final HttpRequest request = requestBuilder.build();
        final int timeoutSeconds = isLocal ? 15 : 30;
        final CompletableFuture<String> bruh = CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .thenApply(HttpResponse::body).exceptionally(throwable -> {
            final Exception e = throwable instanceof Exception ? (Exception) throwable : null;
            if(e != null) {
                if(isLocal && (e instanceof CompletionException || e instanceof ConnectException)) {
                    return null;
                }
            }
            if(!isLocal) {
                RESPONSE_TIMES.get(finalURL).put("error occurred");
            }
            final String value = "failed getting response with url \"" + finalURL + "\"\n\n" +
                    "HttpRequest=" + request.toString() + "\n\n" +
                    "stackTrace=\n" + WLUtilities.getThrowableStackTrace(throwable);
            WLUtilities.saveLoggedError("RestAPI", value);
            return null;
        });
        String string;
        try {
            string = bruh.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            string = null;
            final String errorName = e.getClass().getSimpleName();
            WLUtilities.saveLoggedError("RestAPI" + File.separator + errorName, WLUtilities.getThrowableStackTrace(e));
        }
        if(!isLocal) {
            RESPONSE_TIMES.get(finalURL).put(WLUtilities.getElapsedTime(started));
        }
        return string;
    }
    private static HttpRequest.BodyPublisher parsePostData(LinkedHashMap<String, Object> data, boolean isJSON) {
        final String string = parseQuery(data, isJSON);
        return HttpRequest.BodyPublishers.ofString(string);
    }
    static String parseQuery(LinkedHashMap<String, ?> data, boolean isJSON) {
        final StringBuilder builder = new StringBuilder(isJSON ? "{" : "");
        boolean isFirst = true;
        for(Map.Entry<String, ?> map : data.entrySet()) {
            if(!isFirst) {
                builder.append(isJSON ? "," : "&");
            }
            final String key = map.getKey();
            final Object value = map.getValue();

            if(isJSON) {
                final boolean isString = value instanceof String;
                builder.append("\"").append(key).append("\":").append(isString ? "\"" : "").append(value.toString()).append(isString ? "\"" : "");
            } else {
                builder.append(URLEncoder.encode(key, StandardCharsets.UTF_8));
                builder.append("=");
                builder.append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
            }
            isFirst = false;
        }
        builder.append(isJSON ? "}" : "");
        return builder.toString();
    }
}
