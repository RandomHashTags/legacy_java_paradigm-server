package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;

public final class ProxyClient extends Thread implements RestAPI {

    private final Socket client;
    private OutputStream outToClient;
    private String headers;

    ProxyClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            setupHeaders();
            sendResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponse() throws Exception {
        final long started = System.currentTimeMillis();
        outToClient = client.getOutputStream();

        final String[] headers = getHeaderList();
        final String ip = client.getInetAddress().toString(), platform = getPlatform(headers), identifier = getIdentifier(headers);
        final boolean isValidRequest = platform != null && identifier != null;

        final String target;
        final TargetServer targetServer;
        if(isValidRequest) {
            target = getTarget();
            targetServer = getTargetServer(target.split("/")[0]);
        } else {
            target = null;
            targetServer = null;
        }
        final boolean hasTargetServer = targetServer != null;
        final String prefix = "[" + platform + ", " + identifier + "] " + ip + " - ";
        if(hasTargetServer) {
            targetServer.sendResponse(RequestMethod.GET, target, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final boolean connected = object != null;
                    WLLogger.log(Level.INFO, prefix + (connected ? "Connected" : "Unable to connect") + " to \"" + target + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    if(connected) {
                        try {
                            final String response = DataValues.HTTP_SUCCESS_200 + object.toString();
                            writeOutput(client, response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            WLLogger.log(Level.WARN, prefix + "INVALID");
            writeOutput(client, DataValues.HTTP_ERROR_404);
        }
    }
    private void writeOutput(Socket client, String input) {
        if(client.isOutputShutdown() || client.isClosed()) {
            return;
        }
        try {
            outToClient.write(input.getBytes(DataValues.ENCODING));
            closeClient(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void closeClient(Socket client) {
        try {
            outToClient.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPlatform(@NotNull String[] headers) {
        for(String string : headers) {
            if(string.startsWith("***REMOVED***")) {
                return string.split("***REMOVED***")[1];
            }
        }
        return null;
    }
    private String getIdentifier(@NotNull String[] headers) {
        for(String string : headers) {
            if(string.startsWith("***REMOVED***")) {
                return string.split("***REMOVED***")[1];
            }
        }
        return null;
    }

    private TargetServer getTargetServer(String input) {
        try {
            return TargetServer.valueOfBackendID(input);
        } catch (Exception ignored) {
            return null;
        }
    }
    private String getTarget() {
        for(String string : getHeaderList()) {
            if(string.startsWith("GET ") && string.endsWith("HTTP/1.1")) {
                return string.split("GET ")[1].split(" HTTP/1\\.1")[0].replaceFirst("/", "");
            }
        }
        return "";
    }

    private String[] getHeaderList() {
        return headers.replaceAll("\r", "").split("\n");
    }
    private void setupHeaders() {
        if(headers == null) {
            try {
                final Reader reader = new InputStreamReader(client.getInputStream());
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
