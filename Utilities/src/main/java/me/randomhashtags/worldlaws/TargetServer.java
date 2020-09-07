package me.randomhashtags.worldlaws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public enum TargetServer implements DataValues, RestAPI {
    CANADA(null, WL_CANADA_SERVER_IP, "", ""),
    USA(null, WL_USA_SERVER_IP, "", ""),
    UK(null, WL_UK_SERVER_IP, "", ""),

    CATEGORIES(null, null),
    WHATS_NEW(null, null),
    ;

    private static final String CATEGORIES_RESPONSE, WHATS_NEW_RESPONSE;

    static {
        final StringBuilder builder = new StringBuilder("{\"data\":[");
        boolean first = true;
        final HashSet<TargetServer> servers = new HashSet<>(Arrays.asList(TargetServer.values()));
        servers.remove(CATEGORIES);
        servers.remove(WHATS_NEW);
        for(TargetServer server : servers) {
            final String name = server.getName(), showcasedImageURL = server.getShowcaseImageURL();
            builder.append(first ? "" : ",");
            builder.append("{\"name\":\"").append(name.substring(0, 1)).append(name.substring(1).toLowerCase()).append("\",");
            builder.append("\"tagline\":\"").append(server.getTagline()).append("\"");
            if(showcasedImageURL != null) {
                builder.append(",\"showcasedImageURL\":\"").append(showcasedImageURL).append("\"");
            }
            builder.append("}");
            first = false;
        }
        builder.append("]}");
        CATEGORIES_RESPONSE = builder.toString();

        WHATS_NEW_RESPONSE = "";
    }

    private String name, ipAddress, tagline, showcaseImageURL;
    private int port;

    TargetServer(String ipAddress, String tagline) {
        this(null, ipAddress, tagline);
    }
    TargetServer(String name, String ipAddress, String tagline) {
        this(name, ipAddress, tagline, null);
    }
    TargetServer(String name, String ipAddress, String tagline, String showcaseImageURL) {
        this.name = name == null ? name() : name;
        this.ipAddress = ipAddress;
        if(ipAddress != null) {
            final String[] values = ipAddress.split(":");
            this.port = Integer.parseInt(values[values.length-1]);
        }
        this.tagline = tagline;
        this.showcaseImageURL = showcaseImageURL;
    }

    public String getName() {
        return name;
    }
    public String getIPAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }
    public String getTagline() {
        return tagline;
    }
    public String getShowcaseImageURL() {
        return showcaseImageURL;
    }

    public void sendResponse(RequestMethod method, String request, CompletionHandler handler) {
        switch (this) {
            case CATEGORIES:
                handler.handle(CATEGORIES_RESPONSE);
                break;
            case WHATS_NEW:
                handler.handle(WHATS_NEW_RESPONSE);
                break;
            default:
                final HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Charset", DataValues.ENCODING.name());
                request(ipAddress + "/" + request, method, headers, null, handler);
                break;
        }
    }
}
