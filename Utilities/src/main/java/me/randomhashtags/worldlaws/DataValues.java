package me.randomhashtags.worldlaws;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface DataValues {
    Charset ENCODING = StandardCharsets.UTF_8;
    String HTTP_SUCCESS_200 = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nCharset: " + ENCODING.displayName() + "\r\n\r\n";
    String HTTP_ERROR_404 = "HTTP/1.1 404 ERROR\r\nContent-Type: application/json\r\nCharset: " + ENCODING.displayName() + "\r\n\r\nStop trying to connect, ya turd.";

    int WL_PROXY_PORT = 10461;

    int WL_USA_PORT = getPort(1);
    int WL_CANADA_PORT = getPort(2);
    int WL_UK_PORT = getPort(3);

    String WL_USA_SERVER_IP = getLocalIP(WL_USA_PORT);
    String WL_CANADA_SERVER_IP = getLocalIP(WL_CANADA_PORT);
    String WL_UK_SERVER_IP = getLocalIP(WL_UK_PORT);

    private static String getLocalIP(int port) {
        return "http://localhost:" + port;
    }
    private static int getPort(int offset) {
        return WL_PROXY_PORT + offset;
    }
}
