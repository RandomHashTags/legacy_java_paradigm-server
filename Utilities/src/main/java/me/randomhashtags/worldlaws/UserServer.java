package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;

import java.util.HashMap;
import java.util.Scanner;

public interface UserServer {
    Scanner SCANNER = new Scanner(System.in);
    void start();
    void stop();
    default void rebootProxy() {
        final String command = Settings.Server.getBootServerCommand().replace("%server%", "Proxy");
        stop();
        WLUtilities.executeCommand(command, true);
    }
    default String getUserInput() {
        String string = null;
        try {
            string = SCANNER.hasNextLine() ? SCANNER.nextLine() : null;
        } catch (Exception ignored) {
        }
        return string;
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
