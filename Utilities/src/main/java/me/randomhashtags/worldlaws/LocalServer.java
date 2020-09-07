package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.location.Country;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public interface LocalServer {
    static void start(String serverName, int port, CompletionHandler handler) {
        setupHttpServer(serverName, port, handler);
    }
    static void start(Country country, CompletionHandler handler) {
        setupHttpServer(country.getName(), country.getServer().getPort(), handler);
    }
    private static void connectClients(ServerSocket server, String serverName, int port, CompletionHandler handler) {
        new Thread(() -> {
            out.println("\n" + serverName + " - Listening for clients on port " + port + "...");
            while (true) {
                try {
                    new WLClient(server.accept(), handler).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static void setupHttpServer(String serverName, int port, CompletionHandler handler) {
        try {
            connectClients(new ServerSocket(port), serverName, port, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String fixEscapeValues(String input) {
        return input != null ?
                input.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                //.replace("\'", "\'")
                .replace("\"", "\\u0022")
                : null;
    }

    static String toCorrectCapitalization(String input, String...excludedWords) {
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
    static String toCorrectCapitalization(String input, boolean onlyFirstWordIsCapitalized, String...excluded) {
        if(onlyFirstWordIsCapitalized) {
            String words = input.toLowerCase().replace("_", " ");
            for(String string : excluded) {
                words = words.replace(string.toLowerCase(), string);
            }
            return words.substring(0, 1).toUpperCase() + words.substring(1);
        } else {
            return toCorrectCapitalization(input);
        }
    }
}
