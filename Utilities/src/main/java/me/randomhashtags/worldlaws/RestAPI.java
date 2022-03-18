package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.net.ConnectException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public interface RestAPI {
    String USER_AGENT = "(Project Paradigm - Java Application/11, ***REMOVED***)";
    LinkedHashMap<String, String> GET_CONTENT_HEADERS = new LinkedHashMap<>() {{
        put("Accept", "application/json");
        put("User-Agent", USER_AGENT);
    }};

    HttpClient CLIENT = getClient();

    private static HttpClient getClient() {
        final HttpClient.Builder client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                ;
        try {
            client.sslContext(SSLContext.getDefault());
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        return client.build();
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
                WLUtilities.saveLoggedError("JSONException", "string=" + string + "\n\n" + stackTrace);
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
    default JSONObject requestJSONObject(String url, boolean isLimited, LinkedHashMap<String, String> headers) {
        return requestJSONObject(url, isLimited, headers, null);
    }
    default JSONObject requestJSONObject(String url, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestJSONObject(url, true, headers, query);
    }
    default JSONObject requestJSONObject(String url, boolean isLimited, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStaticJSONObject(url, isLimited, headers, query);
    }
    static JSONObject requestStaticJSONObject(String url, boolean isLimited, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        final String string = requestStatic(url, isLimited, headers, query);
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

    default JSONObject postJSONObject(String url, LinkedHashMap<String, String> postData, boolean isLimited, LinkedHashMap<String, String> headers) {
        return postStaticJSONObject(url, postData, isLimited, headers);
    }
    static JSONObject postStaticJSONObject(String url, LinkedHashMap<String, String> postData, boolean isLimited, LinkedHashMap<String, String> headers) {
        return postStaticJSONObject(url, postData, false, isLimited, headers);
    }
    static JSONObject postStaticJSONObject(String url, LinkedHashMap<String, String> postData, boolean postDataIsJSONObject, boolean isLimited, LinkedHashMap<String, String> headers) {
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
        final String string = requestStatic(url, postData, postDataIsJSONObject, isLimited, headers, null);
        return string != null ? new JSONObject(string) : null;
    }

    default String request(String targetURL, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return request(targetURL, true, headers, query);
    }
    default String request(String targetURL, boolean isLimited, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, isLimited, headers, query);
    }
    static String requestStatic(String targetURL, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, true, headers, query);
    }
    static String requestStatic(String targetURL, boolean isLimited, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, null, isLimited, headers, query);
    }

    static String requestStatic(String targetURL, LinkedHashMap<String, String> postData, boolean isLimited, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestStatic(targetURL, postData, false, isLimited, headers, query);
    }
    static String requestStatic(String targetURL, LinkedHashMap<String, String> postData, boolean postDataIsJSONObject, boolean isLimited, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
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

        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(targetURL));
        if(!isLocal) {
            requestBuilder.timeout(Duration.ofSeconds(15));
        }
        if(postData != null) {
            final HttpRequest.BodyPublisher publisher = parsePostData(postData, postDataIsJSONObject);
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
        final String finalURL = targetURL;
        final HttpRequest request = requestBuilder.build();
        final CompletableFuture<HttpResponse<String>> bruh = CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        final String string = bruh.thenApply(HttpResponse::body).exceptionally(throwable -> {
            final Exception e = throwable instanceof Exception ? (Exception) throwable : null;
            if(e != null) {
                if(isLocal && (e instanceof CompletionException || e instanceof ConnectException)) {
                    return null;
                }
            }
            final String value = "failed getting response with url \"" + finalURL + "\"\n\n" +
                    "HttpRequest=" + request.toString() + "\n\n" +
                    "stackTrace=\n" + WLUtilities.getThrowableStackTrace(throwable);
            WLUtilities.saveLoggedError("RestAPI", value);
            return null;
        }).join();
        return string;
    }
    private static HttpRequest.BodyPublisher parsePostData(LinkedHashMap<String, String> data, boolean isJSON) {
        final StringBuilder builder = new StringBuilder(isJSON ? "{" : "");
        boolean isFirst = true;
        for(Map.Entry<String, String> map : data.entrySet()) {
            if(!isFirst) {
                builder.append(isJSON ? "," : "&");
            }
            if(isJSON) {
                builder.append("\"").append(map.getKey()).append("\":\"").append(map.getValue()).append("\"");
            } else {
                builder.append(URLEncoder.encode(map.getKey(), StandardCharsets.UTF_8));
                builder.append("=");
                builder.append(URLEncoder.encode(map.getValue(), StandardCharsets.UTF_8));
            }
            isFirst = false;
        }
        builder.append(isJSON ? "}" : "");
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
