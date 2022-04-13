package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.proxy.ClientHeaders;

import java.net.Socket;

public final class WLClient {
    private final Socket httpClient;
    private final ClientHeaders headers;

    public WLClient(Socket client) {
        httpClient = client;
        headers = ClientHeaders.getFrom(client, false);
    }

    public Socket getClient() {
        return httpClient;
    }
    public ClientHeaders getHeaders() {
        return headers;
    }

    public void sendResponse(@NotNull String response) {
        final String path = DataValues.HTTP_SUCCESS_200 + (response == null || response.equals("null") ? WLUtilities.SERVER_EMPTY_JSON_RESPONSE : response);
        WLUtilities.writeClientOutput(httpClient, path);
    }
}
