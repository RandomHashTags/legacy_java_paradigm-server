package me.randomhashtags.worldlaws;

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
                stop();
                stopListeningForUserInput();
                start();
                break;
            case "save":
                saveStatistics();
                break;
            case "startmaintenance":
                TargetServer.setMaintenanceMode(true, input.substring(key.length()+1));
                break;
            case "endmaintenance":
                TargetServer.setMaintenanceMode(false, null);
                break;
            default:
                break;
        }
        executeUserInput(getUserInput());
    }

    default void saveStatistics() {
    }
}
