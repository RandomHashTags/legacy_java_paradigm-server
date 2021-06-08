package me.randomhashtags.worldlaws;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public final class WLClient extends Thread {
    private Socket httpClient;
    private final CompletionHandler handler;
    private DataOutputStream output;
    private String headers, target;

    public WLClient(Object client, CompletionHandler handler) {
        httpClient = (Socket) client;
        this.handler = handler;
    }

    public void run() {
        try {
            output = new DataOutputStream(httpClient.getOutputStream());
            setupHeaders(httpClient);
            if(!httpClient.isOutputShutdown()) {
                handler.handleClient(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getClient() {
        return httpClient;
    }
    public String getHeaders() {
        return headers;
    }
    public String[] getHeaderList() {
        return headers.replaceAll("\r", "").split("\n");
    }
    public String getTarget() {
        return target;
    }

    public void sendResponse(@NotNull String response) {
        final String path = DataValues.HTTP_SUCCESS_200 + response;
        writeOutput(path);
    }
    private void writeOutput(String input) {
        if(httpClient.isOutputShutdown() || httpClient.isClosed() || !httpClient.isConnected()) {
            return;
        }
        try {
            output.write(input.getBytes(DataValues.ENCODING));
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close() {
        if(httpClient.isOutputShutdown() || httpClient.isClosed() || !httpClient.isConnected()) {
            return;
        }
        try {
            output.close();
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        if(headers.contains("\r\n\r\n")) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.headers = headers;
                setupTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void setupTarget() {
        if(target == null) {
            for(String string : getHeaderList()) {
                if(string.startsWith("POST") && string.endsWith("HTTP/1.1")) {
                    target = string.split("POST ")[1].split(" HTTP/1\\.1")[0].replaceFirst("/", "");
                    break;
                } else if(string.startsWith("GET") && string.endsWith("HTTP/1.1")) {
                    target = string.split("GET ")[1].split(" HTTP/1\\.1")[0].replaceFirst("/", "");
                    break;
                }
            }
        }
    }
}
