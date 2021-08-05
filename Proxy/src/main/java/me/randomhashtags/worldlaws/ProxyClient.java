package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;

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
        final String target = isValidRequest ? getTarget(headers) : null;
        final String prefix = "[" + platform + ", " + identifier + "] " + ip + " - ";
        if(target != null) {
            final APIVersion version = APIVersion.valueOfInput(target.split("/")[0]);
            final HashSet<String> query = getQuery(target);
            final String finalTarget = target.split("\\?q=")[0];
            final TargetServer targetServer = TargetServer.valueOfBackendID(finalTarget.split("/")[1]);
            targetServer.sendResponse(version, identifier, RequestMethod.GET, finalTarget, query, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final boolean connected = string != null;
                    WLLogger.log(Level.INFO, prefix + (connected ? "Connected" : "Unable to connect") + " to \"" + target + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    if(connected) {
                        final String response = DataValues.HTTP_SUCCESS_200 + string;
                        writeOutput(client, response);
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
    private void closeClient(Socket client) throws Exception {
        outToClient.close();
        client.close();
    }

    private String getHeaderThatStartsWith(String[] headers, String string) {
        return Arrays.stream(headers).filter(target -> target.startsWith(string)).findFirst().orElse(null);
    }
    private String getPlatform(@NotNull String[] headers) {
        return getHeaderThatStartsWith(headers, "***REMOVED***");
    }
    private String getIdentifier(@NotNull String[] headers) {
        return getHeaderThatStartsWith(headers, "***REMOVED***");
    }

    public HashSet<String> getQuery(String target) {
        final boolean hasQuery = target.contains("?q=");
        return hasQuery ? new HashSet<>(Arrays.asList(target.split("\\?q=")[1].split(","))) : new HashSet<>();
    }

    private String getTarget(String[] headers) {
        final String httpVersion = DataValues.HTTP_VERSION;
        for(String string : headers) {
            if(string.startsWith("GET ") && string.endsWith(httpVersion)) {
                return string.split("GET ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
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
                int character;
                while ((character = reader.read()) != -1) {
                    headers += (char) character;
                    if(headers.contains("\r\n\r\n")) {
                        break;
                    }
                }
                this.headers = headers;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
