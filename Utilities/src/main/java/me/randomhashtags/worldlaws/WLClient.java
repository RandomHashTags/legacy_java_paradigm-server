package me.randomhashtags.worldlaws;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketException;

public final class WLClient extends Thread {
    private final Socket httpClient;
    private final CompletionHandler handler;
    private String headers, target;

    public WLClient(Socket client, CompletionHandler handler) {
        httpClient = client;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            setupHeaders(httpClient);
            if(!httpClient.isOutputShutdown()) {
                handler.handleClient(this);
            }
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }

    public Socket getClient() {
        return httpClient;
    }
    public String getHeaders() {
        return headers;
    }
    public String getIdentifier() {
        return getFirstHeaderThatStartsWith("***REMOVED***");
    }
    public String getPlatform() {
        return getFirstHeaderThatStartsWith("***REMOVED***");
    }
    private String getFirstHeaderThatStartsWith(String key) {
        for(String string : getHeaderList()) {
            if(string.startsWith(key)) {
                return string.substring(key.length());
            }
        }
        return "null";
    }
    public String[] getHeaderList() {
        return headers.replaceAll("\r", "").split("\n");
    }
    public String getTarget() {
        return target;
    }

    public void sendResponse(@NotNull String response) {
        final String path = DataValues.HTTP_SUCCESS_200 + (response == null || response.equals("null") ? WLUtilities.SERVER_EMPTY_JSON_RESPONSE : response);
        WLUtilities.writeClientOutput(httpClient, path);
    }

    private void setupHeaders(@NotNull Socket client) {
        if(headers == null) {
            try {
                final InputStream input = client.getInputStream();
                final Reader reader = new InputStreamReader(input);
                String headers = "";
                try {
                    int c;
                    while ((c = reader.read()) != -1) {
                        headers += (char) c;
                        if (headers.contains("\r\n\r\n")) {
                            break;
                        }
                    }
                } catch (SocketException ignored) {
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
                this.headers = headers;
                setupTarget();
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
    }
    private void setupTarget() {
        if(target == null) {
            final String httpVersion = DataValues.HTTP_VERSION;
            for(String string : getHeaderList()) {
                if(string.endsWith(httpVersion)) {
                    if(string.startsWith("POST")) {
                        target = string.split("POST ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
                        break;
                    } else if(string.startsWith("GET")) {
                        target = string.split("GET ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
                        break;
                    }
                }
            }
        }
    }
}
