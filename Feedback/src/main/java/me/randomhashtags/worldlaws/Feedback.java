package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public final class Feedback implements WLServer {

    public static void main(String[] args) {
        new Feedback();
    }

    private Feedback() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.FEEDBACK;
    }

    private void test() {
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "bug_report":
            case "feature_request":
                final String text = "test";//getText(client.getHeaderList());
                createFile(key + "s", text);
                handler.handle("{\"recorded\":true}");
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return null;
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
