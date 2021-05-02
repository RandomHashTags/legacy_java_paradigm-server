package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class LocalServer implements DataValues {

    private static final Scanner INPUT_SCANNER = new Scanner(System.in);
    private final String serverName;
    private final int port;
    private final CompletionHandler handler;
    private ServerSocket server;

    private LocalServer(String serverName, int port, CompletionHandler handler) {
        this.serverName = serverName;
        this.port = port;
        this.handler = handler;
        start();
    }

    public static void start(String serverName, int port, CompletionHandler handler) {
        new LocalServer(serverName, port, handler);
    }
    private void start() {
        setupHttpServer();
    }
    private void stop() {
        WLLogger.log(Level.INFO, serverName + " - shutting down server...");
        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        executeUserInput();
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

    private void executeUserInput() {
        new Thread(() -> executeUserInput(getUserInput())).start();
    }
    private String getUserInput() {
        try {
            return INPUT_SCANNER.next();
        } catch (Exception ignored) {
            return null;
        }
    }
    private void executeUserInput(String input) {
        if(input == null) {
            return;
        }
        switch (input.toLowerCase()) {
            case "stop":
            case "shutdown":
            case "exit":
                stop();
                INPUT_SCANNER.close();
                return;
            case "restart":
            case "reload":
            case "reboot":
                stop();
                start();
                break;
            default:
                break;
        }
        executeUserInput(getUserInput());
    }

    public static String fixEscapeValues(String input) {
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
