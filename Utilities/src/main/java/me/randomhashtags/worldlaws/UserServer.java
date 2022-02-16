package me.randomhashtags.worldlaws;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public interface UserServer {
    ConcurrentHashMap<UserServer, Scanner> INPUT_SCANNERS = new ConcurrentHashMap<>();

    TargetServer getTargetServer();
    void start();
    void stop();

    default void listenForUserInput() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
        new Thread(() -> executeUserInput(getUserInput())).start();
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
            case "shutdown":
                long started = System.currentTimeMillis();
                TargetServer.shutdownServers();
                WLLogger.logInfo("UserServer - shutdown Paradigm Servers (took " + WLUtilities.getElapsedTime(started) + ")");
                break;
            case "spinup":
                started = System.currentTimeMillis();
                TargetServer.spinUpServers();
                WLLogger.logInfo("UserServer - spun up Paradigm Servers (took " + WLUtilities.getElapsedTime(started) + ")");
                break;
            case "reboot":
            case "restart":
                TargetServer.rebootServers();
                return;
            case "beginmaintenance":
            case "startmaintenance":
                TargetServer.setMaintenanceMode(true, input.substring(key.length()+1));
                break;
            case "endmaintenance":
            case "stopmaintenance":
                TargetServer.setMaintenanceMode(false, null);
                break;
            case "execute":
                WLUtilities.executeCommand(input.substring(key.length()+1));
                break;
            default:
                break;
        }
        executeUserInput(getUserInput());
    }

    default void saveStatistics() {
    }
}
