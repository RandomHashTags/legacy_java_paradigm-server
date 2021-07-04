package me.randomhashtags.worldlaws;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class LocalServer implements UserServer, DataValues {
    private final String serverName;
    private final int port;
    private CompletionHandler handler;
    private ServerSocket server;
    private HashMap<String, Integer> requests;
    private HashSet<String> uniqueIdentifiers;

    private LocalServer(TargetServer server) {
        this.serverName = server.getName();
        this.port = server.getPort();
    }

    public static LocalServer get(TargetServer server) {
        return new LocalServer(server);
    }

    @Override
    public void start() {
        requests = new HashMap<>();
        uniqueIdentifiers = new HashSet<>();
        setupHttpServer();
    }

    public void setCompletionHandler(CompletionHandler handler) {
        this.handler = handler;
        start();
    }

    @Override
    public void stop() {
        final long started = System.currentTimeMillis();
        WLLogger.log(Level.INFO, serverName + " - shutting down server...");
        stopListeningForUserInput();
        saveStatistics();
        try {
            server.close();
            WLLogger.log(Level.INFO, serverName + " - server has shut down (took " + (System.currentTimeMillis()-started) + "ms)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void madeRequest(String identifier, String target) {
        requests.put(target, requests.getOrDefault(target, 0) + 1);
        uniqueIdentifiers.add(identifier);
    }
    private void saveStatistics() {
        final long started = System.currentTimeMillis();
        Statistics.INSTANCE.save(serverName, uniqueIdentifiers, requests);
        WLLogger.log(Level.INFO, serverName + " - Saved statistics (took " + (System.currentTimeMillis()-started) + "ms)");
        requests.clear();
        uniqueIdentifiers.clear();
    }

    private void setupHttpServer() {
        try {
            server = new ServerSocket(port);
            connectClients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void connectClients() {
        listenForUserInput();
        WLLogger.log(Level.INFO, serverName + " - Listening for clients on port " + port + "...");
        acceptClients();
    }
    private void acceptClients() {
        while (!server.isClosed()) {
            try {
                final Socket socket = server.accept();
                new WLClient(socket, handler).start();
            } catch (SocketException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String fixEscapeValues(String input) {
        return StringEscapeUtils.escapeJava(input);
    }
    public static String removeWikipediaTranslations(String input) {
        return input.replaceAll(" \\(.*?:.*?\\)", "");
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
