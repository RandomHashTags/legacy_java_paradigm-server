package me.randomhashtags.worldlaws.request;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public final class WLHttpExchange extends HttpExchange {

    private final HttpExchange exchange;
    private String actualRequestBody;

    public WLHttpExchange(HttpExchange exchange) {
        this.exchange = exchange;
    }

    public String getIPAddress(boolean remote) {
        final InetSocketAddress address = (remote ? exchange.getRemoteAddress() : exchange.getLocalAddress());
        return address != null ? address.getAddress().toString() : "null";
    }

    public RequestMethod getActualRequestMethod() {
        switch (getRequestMethod()) {
            case "POST": return RequestMethod.POST;
            case "GET": return RequestMethod.GET;
            default: return null;
        }
    }
    public String getPath(boolean excludeQuery) {
        final String path = exchange.getRequestURI().toString().substring(1);
        return excludeQuery ? path.split("\\?")[0] : path;
    }
    public String getPath() {
        return getPath(true);
    }
    public String[] getPathValues() {
        return getPath().split("/");
    }
    public String[] getPathValuesExcludingFirst(int amount) {
        final List<String> array = new ArrayList<>(Arrays.asList(getPathValues()));
        for(int i = 0; i < amount; i++) {
            array.remove(0);
        }
        return array.toArray(new String[array.size()]);
    }
    public String getShortPath() {
        final String path = getPath();
        final String[] values = path.split("/");
        String string = path.substring(values[0].length() + values[1].length() + 1);
        if(string.startsWith("/")) {
            string = string.substring(1);
        }
        return string;
    }
    public String[] getShortPathValues() {
        return getShortPath().split("/");
    }
    public APIVersion getAPIVersion() {
        final String path = getPath().split("/")[0];
        return APIVersion.valueOfInput(path);
    }
    public LinkedHashMap<String, String> getQuery() {
        final String path = getPath(false);
        if(path.contains("?")) {
            final LinkedHashMap<String, String> query = new LinkedHashMap<>();
            final String[] values = path.split("\\?")[1].split("&");
            for(String string : values) {
                final String[] queryValues = string.split("=");
                query.put(queryValues[0], queryValues[1]);
            }
            return query;
        }
        return null;
    }


    public String getHeader(String key) {
        return getHeader(key, null);
    }
    public String getHeader(String key, String defaultValue) {
        final Headers headers = exchange.getRequestHeaders();
        if(headers != null) {
            final String string = headers.getFirst(key);
            return string != null ? string : defaultValue;
        }
        return defaultValue;
    }

    public String getIdentifier() {
        return getHeader("***REMOVED***");
    }
    public boolean isValidRequest() {
        final String version = getVersion();
        return isValidIdentifier() && isValidPlatform() && version != null;
    }
    public boolean isValidIdentifier() {
        return isValidIdentifier(getIdentifier());
    }
    private static boolean isValidIdentifier(String identifier) {
        final String regex = "[0-9a-zA-Z]";
        final StringBuilder builder = new StringBuilder();
        final int[] test = { 8, 4, 4, 4, 12 };
        for(int amount : test) {
            if(builder.length() > 0) {
                builder.append("-");
            }
            builder.append(regex.repeat(amount));
        }
        final String regexString = builder.toString();
        return identifier != null && identifier.matches(regexString);
    }

    public String getPlatform() {
        return getHeader("***REMOVED***");
    }
    public boolean isValidPlatform() {
        return isValidPlatform(getPlatform());
    }
    public static boolean isValidPlatform(String platform) {
        if(platform != null) {
            final String[] values = platform.split("/");
            if(values.length == 2) {
                final String target = values[1];
                switch (values[0]) {
                    case "***REMOVED***":
                    case "***REMOVED***":
                    case "***REMOVED***":
                    case "***REMOVED***":
                    case "***REMOVED***":
                        final String regex = "[0-9]+\\.?[0-9]?+\\.?[0-9]?";
                        return target.matches(regex);
                    case "***REMOVED***":
                        return Settings.Server.getSandboxUUID().equals(target);
                    case "***REMOVED***":
                        return Settings.Server.getUUID().equals(target);
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    public String getVersion() {
        return getHeader("***REMOVED***");
    }
    public String getTargetLanguage() {
        return getHeader("***REMOVED***", "en");
    }
    public Language getLanguage() {
        final String target = getTargetLanguage();
        final Language language = Language.valueOfString(target);
        return language != null ? language : Language.ENGLISH;
    }
    public String getTargetLanguageType() {
        return getHeader("***REMOVED***", "ARGOS");
    }
    public LanguageTranslator getLanguageType() {
        final String target = getTargetLanguageType();
        final LanguageTranslator translator = LanguageTranslator.valueOfString(target);
        return translator != null ? translator : LanguageTranslator.ARGOS;
    }
    public boolean isSandbox() {
        return "true".equals(getHeader("***REMOVED***"));
    }
    public boolean isPremium() {
        return "true".equals(getHeader("***REMOVED***"));
    }

    public String getActualRequestBody() {
        if(actualRequestBody == null) {
            final InputStream inputStream = getRequestBody();
            final BufferedInputStream in = new BufferedInputStream(inputStream);
            final StringBuilder builder = new StringBuilder();
            String line;
            try {
                do {
                    line = readLine(in);
                    if(line.isEmpty()) {
                        break;
                    }
                    builder.append(builder.length() > 0 ? "\n" : "");
                    builder.append(line);
                } while (true);
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
            actualRequestBody = builder.toString();
            try {
                in.close();
                inputStream.close();
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
        return actualRequestBody;
    }
    public JSONObject getRequestBodyJSON() {
        final String string = getActualRequestBody();
        return string.startsWith("{") && string.endsWith("}") ? new JSONObject(string) : null;
    }
    private String readLine(BufferedInputStream in) throws Exception {
        final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.US_ASCII);
        final StringBuilder builder = new StringBuilder();
        int c;
        while ((c = reader.read()) >= 0) {
            char character = (char) c;
            if(character == '\n') {
                break;
            }
            if(character == '\r') {
                c = reader.read();
                if(c < 0) {
                    break;
                }
                character = (char) c;
                if(character == '\n') {
                    break;
                }
                builder.append('\r');
            }
            builder.append(character);
        }
        return builder.toString();
    }

    @Override
    public Headers getRequestHeaders() {
        return exchange.getRequestHeaders();
    }

    @Override
    public Headers getResponseHeaders() {
        return exchange.getResponseHeaders();
    }

    @Override
    public URI getRequestURI() {
        return exchange.getRequestURI();
    }

    @Override
    public String getRequestMethod() {
        return exchange.getRequestMethod();
    }

    @Override
    public HttpContext getHttpContext() {
        return exchange.getHttpContext();
    }

    @Override
    public void close() {
        exchange.close();
    }

    @Override
    public InputStream getRequestBody() {
        return exchange.getRequestBody();
    }

    @Override
    public OutputStream getResponseBody() {
        return exchange.getResponseBody();
    }

    @Override
    public void sendResponseHeaders(int i, long l) throws IOException {
        exchange.sendResponseHeaders(i, l);
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return exchange.getRemoteAddress();
    }

    @Override
    public int getResponseCode() {
        return exchange.getResponseCode();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return exchange.getLocalAddress();
    }

    @Override
    public String getProtocol() {
        return exchange.getProtocol();
    }

    @Override
    public Object getAttribute(String s) {
        return exchange.getAttribute(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        exchange.setAttribute(s, o);
    }

    @Override
    public void setStreams(InputStream inputStream, OutputStream outputStream) {
        exchange.setStreams(inputStream, outputStream);
    }

    @Override
    public HttpPrincipal getPrincipal() {
        return exchange.getPrincipal();
    }
}
