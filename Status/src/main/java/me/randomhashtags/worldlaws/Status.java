package me.randomhashtags.worldlaws;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;

public final class Status implements DataValues, RestAPI {

    private final Scanner scanner;
    private final HashMap<TargetServer, Boolean> statuses;
    private String status;
    private int COMPLETED_HANDLERS;

    public static void main(String[] args) {
        new Status();
    }

    private Status() {
        scanner = new Scanner(System.in);
        statuses = new HashMap<>();
        new Thread(this::startServer).start();
        getResponse();
    }

    private String getUserInput() {
        return scanner.nextLine();
    }
    private void getResponse() {
        executeUserInput(getUserInput());
    }
    private void executeUserInput(String input) {
        switch (input) {
            default:
                break;
        }
        WLLogger.log(Level.INFO, "Status - input=" + input);
        getResponse();
    }

    private void startServer() {
        pingAll();
        LocalServer.start("Status", WL_STATUS_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                client.sendResponse(status);
            }
        });
    }

    private void pingAll() {
        COMPLETED_HANDLERS = 0;
        final TargetServer[] servers = new TargetServer[] {
                TargetServer.COUNTRIES,
                TargetServer.LAWS,
                TargetServer.NEWS,
                TargetServer.TECHNOLOGY,
                TargetServer.UPCOMING_EVENTS,
                TargetServer.WEATHER
        };
        final int max = servers.length;
        for(TargetServer server : servers) {
            ping(server, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(getCompletedHandlers() == max) {
                        updateStatus();
                    }
                }
            });
        }
    }
    private void ping(TargetServer server, CompletionHandler handler) {
        statuses.put(server, false);
        server.sendResponse(RequestMethod.GET, "ping", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                if(object instanceof Boolean) {
                    statuses.put(server, (Boolean) object);
                }
                completedHandler();
                handler.handle(null);
            }
        });
    }
    private void updateStatus() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<TargetServer, Boolean> map : statuses.entrySet()) {
            final String server = map.getKey().getBackendID();
            final boolean value = map.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(server).append("\":").append(value);
            isFirst = false;
        }
        builder.append("}");
        status = builder.toString();
        WLLogger.log(Level.INFO, "Status - updated server status (" + status + ")");
    }

    private synchronized void completedHandler() {
        COMPLETED_HANDLERS += 1;
    }
    private synchronized int getCompletedHandlers() {
        return COMPLETED_HANDLERS;
    }
}
