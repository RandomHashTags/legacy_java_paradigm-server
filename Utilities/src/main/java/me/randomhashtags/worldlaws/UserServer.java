package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public interface UserServer {
    ConcurrentHashMap<UserServer, Scanner> INPUT_SCANNERS = new ConcurrentHashMap<>();

    void start();
    void stop();
    default void rebootProxy() {
        final String command = Settings.Server.getRebootProxyCommand();
        stop();
        WLUtilities.executeCommand(command, true);
    }

    default void listenForUserInput() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
    }
    default void stopListeningForUserInput() {
        final Scanner scanner = INPUT_SCANNERS.get(this);
        if(scanner != null) {
            scanner.close();
            INPUT_SCANNERS.remove(this);
        }
    }
    default String getUserInput() {
        final Scanner scanner = INPUT_SCANNERS.get(this);
        return scanner != null && scanner.hasNextLine() ? scanner.nextLine() : null;
    }
    default void executeUserInput(String input) {
        if(input == null) {
            return;
        }
        final String[] values = input.toLowerCase().split(" ");
        final String key = values[0];
        switch (key) {
            case "stop":
            case "exit":
            case "close":
            case "end":
                stop();
                return;
            case "reboot":
                rebootProxy();
                return;
            case "execute":
                WLUtilities.executeCommand(input.substring(key.length()+1), true);
                break;
            default:
                final HashMap<String, Runnable> commands = getAllCommands();
                if(commands.containsKey(key)) {
                    commands.get(key).run();
                }
                break;
        }
        executeUserInput(getUserInput());
    }

    private HashMap<String, Runnable> getAllCommands() {
        final HashMap<String, Runnable> commands = new HashMap<>() {{
            put("ping", () -> WLLogger.logInfo("pong!"));
        }};
        final HashMap<String, Runnable> customUserCommands = getCustomUserCommands();
        if(customUserCommands != null) {
            commands.putAll(customUserCommands);
        }
        return commands;
    }
    default HashMap<String, Runnable> getCustomUserCommands() {
        return null;
    }

    default void saveStatistics() {
    }
}
