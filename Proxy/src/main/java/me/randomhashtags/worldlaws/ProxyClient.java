package me.randomhashtags.worldlaws;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;

public final class ProxyClient extends Thread implements RestAPI {

    private final Socket client;
    private String headers;

    ProxyClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        setupHeaders();
        sendResponse();
    }

    private void sendResponse() {
        final long started = System.currentTimeMillis();
        final String[] headers = getHeaderList();
        final String ip = client.getInetAddress().toString(), platform = getPlatform(headers), identifier = getIdentifier(headers);
        final boolean isValidRequest = true;//platform != null && identifier != null;
        final String target = isValidRequest ? getTarget(headers) : null;
        final String prefix = "[" + platform + ", " + identifier + "] " + ip + " - ";
        if(target != null) {
            final String finalTarget = target.split("\\?q=")[0];
            if(finalTarget.contains("/")) {
                final String[] finalTargetValues = finalTarget.split("/");
                final String serverString = finalTargetValues[1];
                final TargetServer targetServer = TargetServer.valueOfBackendID(serverString);
                if(targetServer != null) {
                    final String versionString = finalTargetValues[0];
                    final APIVersion version = APIVersion.valueOfInput(versionString);
                    final HashSet<String> query = getQuery(target);
                    String targetRequest = finalTarget.substring(versionString.length() + 1);
                    if(targetServer.isRealServer()) {
                        targetRequest = targetRequest.substring(serverString.length() + 1);
                    }
                    if(targetRequest.startsWith("/")) {
                        targetRequest = target.substring(1);
                    }
                    final String request = targetRequest;
                    targetServer.sendResponse(version, identifier, RequestMethod.GET, targetRequest, query, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            final boolean connected = string != null;
                            if(connected) {
                                final String response = DataValues.HTTP_SUCCESS_200 + string;
                                writeOutput(client, response);
                            } else {
                                WLLogger.logInfo(prefix + "Failed to connect to \"" + request + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                            }
                        }
                    });
                    return;
                }
            }
        }
        WLLogger.logWarning(prefix + "INVALID");
        writeOutput(client, DataValues.HTTP_ERROR_404);
    }
    private void writeOutput(Socket client, String input) {
        if(client.isOutputShutdown() || client.isClosed()) {
            return;
        }
        try {
            final OutputStream outToClient = client.getOutputStream();
            outToClient.write(input.getBytes(DataValues.ENCODING));
            closeClient(outToClient, client);
        } catch (SocketException ignored) {
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void closeClient(OutputStream outToClient, Socket client) throws Exception {
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
            } catch (SocketException ignored) {
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
    }
}
