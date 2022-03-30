package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.settings.Settings;
import org.apache.commons.text.StringEscapeUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class LocalServer implements UserServer, DataValues {
    private final WLServer wlserver;
    private final String serverName;
    private HashMap<String, ServerRequestType> requestTypes;
    private CompletionHandler handler;
    private ServerSocket server;
    private HashSet<Timer> timers;

    private ConcurrentHashMap<String, Integer> totalRequests;
    private ConcurrentHashMap<String, HashSet<String>> uniqueRequests;
    private HashSet<String> totalUniqueIdentifiers;

    public LocalServer(WLServer server) {
        wlserver = server;
        final TargetServer targetServer = server.getServer();
        this.serverName = targetServer.getName();
    }

    @Override
    public TargetServer getTargetServer() {
        return wlserver.getServer();
    }

    @Override
    public void start() {
        uniqueRequests = new ConcurrentHashMap<>();
        totalRequests = new ConcurrentHashMap<>();
        totalUniqueIdentifiers = new HashSet<>();
        final TargetServer server = wlserver.getServer();
        if(server.recordsStatistics()) {
            final long interval = UpdateIntervals.SAVE_STATISTICS;
            registerFixedTimer(interval, this::saveStatistics);
        }
        registerFixedTimer(UpdateIntervals.REFRESH_SETTINGS, Settings::refresh);
        setupHttpServer(server.getPort());
    }

    public void setCompletionHandler(CompletionHandler handler) {
        this.handler = handler;
        start();
    }

    @Override
    public void stop() {
        final long started = System.currentTimeMillis();
        WLLogger.logInfo(serverName + " - shutting down server...");
        if(timers != null) {
            for(Timer timer : timers) {
                timer.cancel();
            }
        }
        wlserver.stop();
        if(wlserver.getServer().recordsStatistics()) {
            saveStatistics();
        }
        try {
            server.close();
            WLLogger.logInfo(serverName + " - server has shut down (took " + WLUtilities.getElapsedTime(started) + ")");
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    public void registerFixedTimer(long interval, Runnable runnable) {
        registerFixedTimer(null, interval, runnable);
    }
    public void registerFixedTimerStartingAt(LocalDateTime startingDate, long interval, Runnable runnable) {
        registerFixedTimer(startingDate, interval, runnable);
    }
    private void registerFixedTimer(LocalDateTime startingDate, long interval, Runnable runnable) {
        if(timers == null) {
            timers = new HashSet<>();
        }
        final Timer timer = WLUtilities.getTimer(startingDate, interval, runnable);
        timers.add(timer);
    }

    public ServerRequestType parseRequestType(String type) {
        if(requestTypes == null) {
            requestTypes = new HashMap<>();
            final ServerRequestType[] types = wlserver.getRequestTypes();
            if(types != null) {
                for(ServerRequestType requestType : types) {
                    requestTypes.put(requestType.name().toLowerCase(), requestType);
                }
            }
        }
        return requestTypes.get(type);
    }

    public void madeRequest(String identifier, String target) {
        if(wlserver.getServer().recordsStatistics()) {
            uniqueRequests.putIfAbsent(target, new HashSet<>());
            uniqueRequests.get(target).add(identifier);
            totalRequests.put(target, totalRequests.getOrDefault(target, 0) + 1);
            totalUniqueIdentifiers.add(identifier);
        }
    }
    @Override
    public void saveStatistics() {
        Statistics.INSTANCE.save(wlserver.getServer(), totalUniqueIdentifiers, uniqueRequests, totalRequests);
        totalRequests.clear();
        totalUniqueIdentifiers.clear();
        uniqueRequests.clear();
    }

    private void setupHttpServer(int port) {
        try {
            server = new ServerSocket(port);
            connectClients(port);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void connectClients(int port) {
        WLLogger.logInfo(serverName + " - Listening for clients on port " + port + "...");
        acceptClients();
    }
    private void acceptClients() {
        while (!server.isClosed()) {
            try {
                final Socket socket = server.accept();
                final WLClient client = new WLClient(socket);
                CompletableFuture.runAsync(() -> {
                    try {
                        if(!socket.isOutputShutdown()) {
                            handler.handleClient(client);
                        }
                    } catch (Exception e) {
                        WLUtilities.saveException(e);
                    }
                });
            } catch (SocketException | SocketTimeoutException ignored) {
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
    }

    public static String fixEscapeValues(String input) {
        return StringEscapeUtils.escapeJava(input);
    }
    public static String fixUnescapeValues(String input) {
        return StringEscapeUtils.unescapeJava(input);
    }
    public static String removeWikipediaTranslations(String input) {
        return input.replaceAll(" \\(.*?:.*?\\)", "");
    }
    public static String removeWikipediaReferences(String string) {
        return string != null && !string.isEmpty() ? string.replaceAll("\\[.*?]", "") : string;
    }

    public static String toCorrectCapitalization(String input, String...excludedWords) {
        final String lowercase = input.toLowerCase();
        final String[] values = lowercase.split("_");
        final StringBuilder builder = new StringBuilder();
        final List<String> excluded = new ArrayList<>();
        if(excludedWords != null) {
            for(String string : excludedWords) {
                excluded.add(string.toLowerCase());
            }
        }
        boolean first = true;
        for(String string : values) {
            builder.append(first ? "" : " ");
            if(first || !excluded.contains(string)) {
                builder.append(string.substring(0, 1).toUpperCase()).append(string.substring(1));
                first = false;
            } else {
                builder.append(string);
            }
        }
        return builder.toString();
    }
    public static String toCorrectCapitalization(String input, boolean onlyFirstWordIsCapitalized, String...excluded) {
        if(onlyFirstWordIsCapitalized) {
            String words = input.toLowerCase().replace("_", " ");
            for(String string : excluded) {
                words = words.replace(string.toLowerCase(), string);
            }
            return words.substring(0, 1).toUpperCase() + words.substring(1);
        } else {
            return toCorrectCapitalization(input, excluded);
        }
    }
}
