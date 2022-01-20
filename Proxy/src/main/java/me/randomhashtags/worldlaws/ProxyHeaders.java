package me.randomhashtags.worldlaws;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;

public final class ProxyHeaders {

    private String identifier, platform, request;
    private APIVersion apiVersion;
    private TargetServer server;
    private HashSet<String> query;

    private ProxyHeaders(Socket client) {
        try {
            final Reader reader = new InputStreamReader(client.getInputStream());
            String allHeaders = "";
            int character;
            while ((character = reader.read()) != -1) {
                allHeaders += (char) character;
                if(allHeaders.contains("\r\n\r\n")) {
                    break;
                }
            }
            final String[] headers = allHeaders.replaceAll("\r", "").split("\n");

            identifier = getHeaderThatStartsWith(headers, "***REMOVED***");
            platform = getHeaderThatStartsWith(headers, "***REMOVED***");

            String target = "";
            final String httpVersion = DataValues.HTTP_VERSION;
            for(String string : headers) {
                if(string.startsWith("GET ") && string.endsWith(httpVersion)) {
                    target = string.split("GET ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
                    break;
                }
            }
            final String finalTarget = target.split("\\?q=")[0];
            if(finalTarget.contains("/")) {
                final String[] finalTargetValues = finalTarget.split("/");
                final String serverString = finalTargetValues[1];
                server = TargetServer.valueOfBackendID(serverString);
                if(server != null) {
                    final String versionString = finalTargetValues[0];
                    apiVersion = APIVersion.valueOfInput(versionString);
                    query = getQuery(target);
                    String targetRequest = finalTarget.substring(versionString.length() + 1);
                    if(server.isRealServer()) {
                        targetRequest = targetRequest.substring(serverString.length() + 1);
                    }
                    if(targetRequest.startsWith("/")) {
                        targetRequest = target.substring(1);
                    }
                    request = targetRequest;
                }
            }

        } catch (SocketException ignored) {
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }

    private HashSet<String> getQuery(String target) {
        final boolean hasQuery = target.contains("?q=");
        return hasQuery ? new HashSet<>(Arrays.asList(target.split("\\?q=")[1].split(","))) : new HashSet<>();
    }

    public String getIdentifier() {
        return identifier;
    }
    public String getPlatform() {
        return platform;
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
                return string;
            }
        }
        return null;
    }

    public static ProxyHeaders getFrom(Socket client) {
        return new ProxyHeaders(client);
    }
}