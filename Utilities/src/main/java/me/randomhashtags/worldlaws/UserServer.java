package me.randomhashtags.worldlaws;

import java.util.HashMap;
import java.util.Scanner;

public interface UserServer {
    HashMap<UserServer, Scanner> INPUT_SCANNERS = new HashMap<>();

    void start();
    void stop();

    default void listenForUserInput() {
        INPUT_SCANNERS.put(this, new Scanner(System.in));
        new Thread(() -> executeUserInput(getUserInput())).start();
    }
    default void stopListeningForUserInput() {
        INPUT_SCANNERS.get(this).close();
        INPUT_SCANNERS.remove(this);
    }
    private String getUserInput() {
        return INPUT_SCANNERS.get(this).next();
    }
    private void executeUserInput(String input) {
        if(input == null) {
            return;
        }
        switch (input.toLowerCase()) {
            case "stop":
            case "shutdown":
            case "exit":
            case "close":
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
            default:
                break;
        }
        executeUserInput(getUserInput());
    }
}
