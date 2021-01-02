package me.randomhashtags.worldlaws;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class Feedback implements DataValues {

    public static void main(String[] args) {
        new Feedback();
    }

    private Feedback() {
        startServer();
    }

    private void startServer() {
        LocalServer.start("Feedback", WL_FEEDBACK_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                getResponse(client);
            }
        });
    }

    private void getResponse(WLClient client) {
        final String target = client.getTarget();
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "bug_report":
            case "feature_request":
                final String text = getText(client.getHeaderList());
                createFile(key + "s", text);
                client.sendResponse("{\"recorded\":true}");
                break;
            default:
                break;
        }
    }
    private String getText(String[] headers) {
        final String target = "Text: ";
        for(String string : headers) {
            if(string.startsWith(target)) {
                return string.substring(target.length());
            }
        }
        return null;
    }

    private void createFile(String folder, String text) {
        try {
            final Path folderPath = Paths.get(folder);
            if(!Files.exists(folderPath)) {
                createFolder(folderPath);
            }
            final List<String> lines = Arrays.asList(text);
            final Path path = Paths.get(folder + File.separator + System.currentTimeMillis() + ".txt");
            WLLogger.log(Level.INFO, "Feedback;path=" + path.toAbsolutePath().toString());
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createFolder(Path folder) throws Exception {
        Files.createDirectory(folder);
    }
}
