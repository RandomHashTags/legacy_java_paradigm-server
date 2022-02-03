package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public interface UserServer {
    ConcurrentHashMap<UserServer, Scanner> INPUT_SCANNERS = new ConcurrentHashMap<>();

    void start();
    void stop();

    default void listenForUserInput() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
        new Thread(() -> executeUserInput(getUserInput())).start();
    }
    default void stopListeningForUserInput() {
        if(INPUT_SCANNERS.containsKey(this)) {
            INPUT_SCANNERS.get(this).close();
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
            case "shutdown":
            case "exit":
            case "close":
            case "end":
                stop();
                stopListeningForUserInput();
                return;
            case "restart":
            case "reload":
            case "reboot":
                restart();
                return;
            case "save":
                saveStatistics();
                break;
            case "refresh":
                Settings.refresh();
                break;
            case "beginmaintenance":
            case "startmaintenance":
                TargetServer.setMaintenanceMode(true, input.substring(key.length()+1));
                break;
            case "endmaintenance":
            case "stopmaintenance":
                TargetServer.setMaintenanceMode(false, null);
                break;
            case "execute":
                executeCommand(input.substring(key.length()+1));
                break;
            default:
                break;
        }
        executeUserInput(getUserInput());
    }

    default void saveStatistics() {
    }
    private void restart() {
        stop();
        start();
    }
    private void executeCommand(String command) {
        try {
            new ProcessBuilder(command).start().waitFor();
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
}
