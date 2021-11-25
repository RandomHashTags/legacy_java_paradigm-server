package me.randomhashtags.worldlaws;

import org.apache.commons.text.StringEscapeUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class LocalServer implements UserServer, DataValues {
    private final String serverName;
    private final int port;
    private CompletionHandler handler;
    private ServerSocket server;
    private HashSet<Timer> timers;

    private ConcurrentHashMap<String, Integer> totalRequests;
    private ConcurrentHashMap<String, HashSet<String>> uniqueRequests;
    private HashSet<String> totalUniqueIdentifiers;

    private LocalServer(TargetServer server) {
        this.serverName = server.getName();
        this.port = server.getPort();
    }

    public static LocalServer get(TargetServer server) {
        return new LocalServer(server);
    }

    @Override
    public void start() {
        uniqueRequests = new ConcurrentHashMap<>();
        totalRequests = new ConcurrentHashMap<>();
        totalUniqueIdentifiers = new HashSet<>();
        final long interval = WLUtilities.SAVE_STATISTICS_INTERVAL;
        registerFixedTimer(interval, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                saveStatistics();
            }
        });
        setupHttpServer();
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
        saveStatistics();
        try {
            server.close();
            WLLogger.logInfo(serverName + " - server has shut down (took " + (System.currentTimeMillis()-started) + "ms)");
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    public void registerFixedTimer(long interval, CompletionHandler handler) {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(handler != null) {
                    handler.handleObject(null);
                }
            }
        }, interval, interval);
        if(timers == null) {
            timers = new HashSet<>();
        }
        timers.add(timer);
    }

    public void madeRequest(String identifier, String target) {
        uniqueRequests.putIfAbsent(target, new HashSet<>());
        uniqueRequests.get(target).add(identifier);
        totalRequests.put(target, totalRequests.getOrDefault(target, 0) + 1);
        totalUniqueIdentifiers.add(identifier);
    }
    @Override
    public void saveStatistics() {
        final long started = System.currentTimeMillis();
        Statistics.INSTANCE.save(serverName, totalUniqueIdentifiers, uniqueRequests, totalRequests, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                WLLogger.logInfo(serverName + " - Saved statistics (took " + (System.currentTimeMillis()-started) + "ms)");
                totalRequests.clear();
                totalUniqueIdentifiers.clear();
                uniqueRequests.clear();
            }
        });
    }

    private void setupHttpServer() {
        try {
            server = new ServerSocket(port);
            connectClients();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void connectClients() {
        listenForUserInput();
        WLLogger.logInfo(serverName + " - Listening for clients on port " + port + "...");
        acceptClients();
    }
    private void acceptClients() {
        while (!server.isClosed()) {
            try {
                final Socket socket = server.accept();
                new WLClient(socket, handler).start();
            } catch (SocketException | SocketTimeoutException ignored) {
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
    }

    public static String fixEscapeValues(String input) {
        return StringEscapeUtils.escapeJava(input);
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
            if(!excluded.contains(string)) {
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
