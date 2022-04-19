package me.randomhashtags.worldlaws.request;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.TargetServer;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;

public final class WLClientHeaders {

    private String ipAddress, identifier, platform, version, totalRequest, request;
    private Language language;
    private LanguageTranslator languageType;
    private APIVersion apiVersion;
    private TargetServer server;
    private HashSet<String> query;

    private WLClientHeaders() {
    }
    private WLClientHeaders(Socket client) {
        String[] headers = {};
        String target = "";
        try {
            String allHeaders = "";
            final Reader reader = new InputStreamReader(client.getInputStream());
            int character;
            while ((character = reader.read()) != -1) {
                allHeaders += (char) character;
                if(allHeaders.contains("\r\n\r\n")) {
                    break;
                }
            }
            headers = allHeaders.replaceAll("\r", "").split("\n");

            final String httpVersion = DataValues.HTTP_VERSION;
            for(String string : headers) {
                if(string.startsWith("GET ") && string.endsWith(httpVersion)) {
                    target = string.split("GET ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
                    break;
                }
            }
        } catch (SocketException ignored) {
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        final String ipAddress = client.getInetAddress().toString();
        final String identifier = getHeaderThatStartsWith(headers, "***REMOVED***");
        final String platform = getHeaderThatStartsWith(headers, "***REMOVED***");
        final String version = getHeaderThatStartsWith(headers, "***REMOVED***");
        final String targetLanguage = getHeaderThatStartsWith(headers, "***REMOVED***");
        if(targetLanguage != null) {
            language = Language.valueOfString(targetLanguage);
        }
        if(language == null) {
            language = Language.ENGLISH;
        }
        final String targetLanguageType = getHeaderThatStartsWith(headers, "***REMOVED***");
        if(targetLanguageType != null) {
            languageType = LanguageTranslator.valueOfString(targetLanguageType);
        }
        if(languageType == null) {
            languageType = LanguageTranslator.ARGOS;
        }
        test(ipAddress, identifier, platform, version, target);
    }

    public void test(String ipAddress, String identifier, String platform, String version, String target) {
        this.ipAddress = ipAddress;
        this.identifier = identifier;
        this.version = version;
        this.platform = platform;
        final String finalTarget = target.split("\\?q=")[0];
        if(finalTarget.contains("/")) {
            final String[] finalTargetValues = finalTarget.split("/");
            if(finalTargetValues.length >= 2) {
                final String serverString = finalTargetValues[1];
                server = TargetServer.valueOfBackendID(serverString);

                final String versionString = finalTargetValues[0];
                totalRequest = finalTarget.substring(versionString.length() + 1);
                apiVersion = APIVersion.valueOfInput(versionString);
                query = getQuery(target);
                if(server != null) {
                    String targetRequest = totalRequest;
                    if(server.isRealServer()) {
                        targetRequest = targetRequest.substring(serverString.length() + 1);
                    }
                    if(targetRequest.startsWith("/")) {
                        targetRequest = target.substring(1);
                    }
                    request = targetRequest;
                }
            }
        }
    }

    private HashSet<String> getQuery(String target) {
        final boolean hasQuery = target.contains("?q=");
        return hasQuery ? new HashSet<>(Arrays.asList(target.split("\\?q=")[1].split("&"))) : new HashSet<>();
    }

    public boolean isValidRequest() {
        final String platform = getPlatform(), version = getVersion();
        return isValidIdentifier() && platform != null && version != null;
    }
    public boolean isValidIdentifier() {
        final String identifier = getIdentifier(), regex = "[0-9a-zA-Z]";
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
    public String getIPAddress() {
        return ipAddress;
    }
    public String getIdentifier() {
        return identifier;
    }
    public String getPlatform() {
        return platform;
    }
    public String getVersion() {
        return version;
    }
    public Language getLanguage() {
        return language;
    }
    public String getTotalRequest() {
        return totalRequest;
    }
    public String getRequest() {
        return request;
    }
    public APIVersion getAPIVersion() {
        return apiVersion;
    }
    public TargetServer getServer() {
        return server;
    }
    public HashSet<String> getQuery() {
        return query;
    }

    private String getHeaderThatStartsWith(String[] headers, String string) {
        for(String header : headers) {
            if(header.startsWith(string)) {
                return header.substring(string.length());
            }
        }
        return null;
    }

    public static WLClientHeaders getFrom(Socket client) {
        return new WLClientHeaders(client);
    }
    public static WLClientHeaders getWith(String ipAddress, String identifier, String platform, String version, String target) {
        final WLClientHeaders headers = new WLClientHeaders();
        headers.test(ipAddress, identifier, platform, version, target);
        return headers;
    }
}