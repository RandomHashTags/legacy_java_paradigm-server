package me.randomhashtags.worldlaws;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public interface UserServer {
    ConcurrentHashMap<UserServer, Scanner> INPUT_SCANNERS = new ConcurrentHashMap<>();

    TargetServer getTargetServer();
    void start();
    void stop();

    default void listenForUserInput() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
        CompletableFuture.runAsync(() -> executeUserInput(getUserInput()));
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
    private void executeUserInput(String input) {
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
            case "execute":
                WLUtilities.executeCommand(input.substring(key.length()+1), true);
                break;
            default:
                final HashMap<String, Runnable> customCommands = getCustomUserCommands();
                if(customCommands != null && customCommands.containsKey(key)) {
                    customCommands.get(key).run();
                }
                break;
        }
        executeUserInput(getUserInput());
    }

    default HashMap<String, Runnable> getCustomUserCommands() {
        return null;
    }

    default void saveStatistics() {
    }
}
