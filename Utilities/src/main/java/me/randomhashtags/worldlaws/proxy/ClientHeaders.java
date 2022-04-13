package me.randomhashtags.worldlaws.proxy;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public final class ClientHeaders {

    public static ClientHeaders getFrom(Socket client, boolean fromServerHandler) {
        return new ClientHeaders(client, fromServerHandler);
    }
    public static ClientHeaders getWith(String ipAddress, String identifier, String platform, String version, String target) {
        final ClientHeaders headers = new ClientHeaders();
        headers.test(ipAddress, target);
        headers.headers.put("***REMOVED***", identifier);
        headers.headers.put("***REMOVED***", platform);
        headers.headers.put("***REMOVED***", version);
        return headers;
    }

    private boolean fromServerHandler;
    private RequestMethod method;
    private String ipAddress, fullRequest, targetRequestType, totalRequest, request;
    private APIVersion apiVersion;
    private TargetServer server;
    private HashSet<String> query;
    private boolean sandbox;

    private final HashMap<String, String> headers;
    private Language language;
    private LanguageTranslator languageType;

    private ClientHeaders() {
        headers = new HashMap<>();
    }
    private ClientHeaders(Socket client, boolean fromServerHandler) {
        headers = new HashMap<>();
        this.fromServerHandler = fromServerHandler;
        String[] headers = {};
        String target = "";
        try {
            //final InputStream inputStream = client.getInputStream();
            //final HttpRequest.BodyPublisher test = HttpRequest.BodyPublishers.ofInputStream(() -> inputStream);
            final BufferedInputStream inputStream = new BufferedInputStream(client.getInputStream());
            //final String allHeaders = readLine(inputStream);
            target = loadHeaders(inputStream);
            //headers = allHeaders.replaceAll("\r", "").split("\n");
        } catch (SocketException ignored) {
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        return;
        /*

        if(method == null) {
            method = RequestMethod.GET;
        }

        for(String header : headers) {
            if(header.contains(": ") && (
                        header.startsWith("***REMOVED***") ||
                        header.startsWith("***REMOVED***") ||
                        header.startsWith("***REMOVED***")
                    )) {
                final String[] values = header.split(": ");
                final String key = values[0], value = header.substring(key.length()+2);
                this.headers.put(key, value);
            }
            WLLogger.logInfo("ClientHeaders;init;method=" + method.name() + ";header=" + header);
        }

        final String ipAddress = client.getInetAddress().toString();
        final String targetLanguage = getTargetLanguage();
        if(targetLanguage != null) {
            language = Language.valueOfString(targetLanguage);
        }
        if(language == null) {
            language = Language.ENGLISH;
        }
        final String targetLanguageType = getTargetLanguageType();
        if(targetLanguageType != null) {
            languageType = LanguageTranslator.valueOfString(targetLanguageType);
        }
        if(languageType == null) {
            languageType = LanguageTranslator.ARGOS;
        }
        test(ipAddress, target);*/
    }
    private String loadHeaders(BufferedInputStream inputStream) throws Exception {
        final String request = readLine(inputStream);
        final String[] requestValues = request.split(" ");
        final String target = requestValues[1].substring(1);
        method = RequestMethod.valueOf(requestValues[0]);

        String line;
        WLLogger.logInfo("ClientHeaders;loadHeaders;test1");
        line = readLine(inputStream);
        WLLogger.logInfo("ClientHeaders;loadHeaders;test2;" + line);
        WLLogger.logInfo("ClientHeaders;loadHeaders;test3;" + readLine(inputStream));
        do {
            line = readLine(inputStream);
            if(line.isEmpty()) {
                break;
            }
            WLLogger.logInfo("ClientHeaders;loadHeaders;line=" + line);
        } while (true);
        WLLogger.logInfo("ClientHeaders;loadHeaders;method=" + method.name());
        if(method == RequestMethod.POST) {
            do {
                line = readLine(inputStream);
                int size = line.length();
                if(size == 0) break;
                readLine(inputStream);
            } while (true);
        }
        return target;
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
        WLLogger.logInfo("ClientHeaders;readLine;builder=" + builder.toString());
        return builder.toString();
    }

    public void test(String ipAddress, String fullRequest) {
        this.ipAddress = ipAddress;
        this.fullRequest = fullRequest;
        final String finalTarget = fullRequest.split("\\?q=")[0];
        if(finalTarget.contains("/")) {
            final String[] finalTargetValues = finalTarget.split("/");
            if(finalTargetValues.length >= 2) {
                final String serverString = finalTargetValues[1];
                server = fromServerHandler ? null : TargetServer.valueOfBackendID(serverString);

                final String versionString = finalTargetValues[0];
                totalRequest = finalTarget.substring(versionString.length() + 1);
                apiVersion = APIVersion.valueOfInput(versionString);
                query = getQuery(fullRequest);
                if(server != null) {
                    String targetRequest = totalRequest;
                    if(server.isRealServer()) {
                        targetRequest = targetRequest.substring(serverString.length() + 1);
                    }
                    if(!targetRequest.isEmpty()) {
                        targetRequestType = targetRequest.split("/")[0];
                        final int length = targetRequestType.length(), extra = targetRequest.length() == length ? 0 : 1;
                        targetRequest = targetRequest.substring(length + extra);
                    }
                    if(targetRequest.startsWith("/")) {
                        targetRequest = fullRequest.substring(1);
                    }
                    request = targetRequest;
                } else {
                    targetRequestType = serverString;
                    final int length = targetRequestType.length(), extra = totalRequest.length() == length ? 0 : 1;
                    request = totalRequest.substring(length + extra);
                }
            }
        }
    }
    private HashSet<String> getQuery(String target) {
        final boolean hasQuery = target.contains("?q=");
        return hasQuery ? new HashSet<>(Arrays.asList(target.split("\\?q=")[1].split("&"))) : new HashSet<>();
    }

    public boolean isValidRequest() {
        return identifierIsValid() && getVersion() != null && getPlatform() != null;
    }
    public boolean identifierIsValid() {
        final String identifier = getIdentifier();
        if(identifier != null) {
            final String regex = "[0-9a-zA-Z]+";
            return identifier.matches(regex + "-" + regex + "-" + regex + "-" + regex + "-" + regex);
        }
        return false;
    }
    public RequestMethod getMethod() {
        return method;
    }
    public String getIPAddress() {
        return ipAddress;
    }
    public APIVersion getAPIVersion() {
        return apiVersion;
    }
    public String getTargetRequestType() {
        return targetRequestType;
    }
    public TargetServer getServer() {
        return server;
    }
    public HashSet<String> getQuery() {
        return query;
    }
    public String getFullRequest() {
        return fullRequest;
    }
    public String getTotalRequest() {
        return totalRequest;
    }
    public String getRequest() {
        return request;
    }
    public boolean isSandbox() {
        return sandbox;
    }

    public String getIdentifier() {
        return headers.get("***REMOVED***");
    }
    public String getPlatform() {
        return headers.get("***REMOVED***");
    }
    public String getVersion() {
        return headers.get("***REMOVED***");
    }
    public String getTargetLanguage() {
        return headers.get("***REMOVED***");
    }
    public Language getLanguage() {
        return language;
    }
    public String getTargetLanguageType() {
        return headers.get("***REMOVED***");
    }
    public LanguageTranslator getLanguageType() {
        return languageType;
    }
}
