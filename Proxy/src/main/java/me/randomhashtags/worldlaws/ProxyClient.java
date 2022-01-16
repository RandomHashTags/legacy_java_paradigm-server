package me.randomhashtags.worldlaws;

import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

public final class ProxyClient extends Thread implements RestAPI {

    private final Socket client;
    private ProxyHeaders headers;

    ProxyClient(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        headers = ProxyHeaders.getFrom(client);
        sendResponse();
    }

    private void sendResponse() {
        final long started = System.currentTimeMillis();
        final String identifier = headers.getIdentifier(), platform = headers.getPlatform();
        final boolean isValidRequest = true;//platform != null && identifier != null;
        final String ip = client.getInetAddress().toString();
        final String prefix = "[" + platform + ", " + identifier + "] " + ip + " - ";
        if(isValidRequest) {
            final TargetServer server = headers.getServer();
            if(server != null) {
                final APIVersion version = headers.getAPIVersion();
                final HashSet<String> query = headers.getQuery();
                final String request = headers.getRequest();
                final String string = server.sendResponse(version, identifier, RequestMethod.GET, request, query);
                final boolean connected = string != null;
                if(connected) {
                    final String response = DataValues.HTTP_SUCCESS_200 + string;
                    writeOutput(client, response);
                } else if(!TargetServer.isMaintenanceMode()) {
                    WLLogger.logInfo(prefix + "Failed to connect to \"" + request + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                }
                return;
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
    private static void closeClient(OutputStream outToClient, Socket client) throws Exception {
        outToClient.close();
        client.close();
    }
}
