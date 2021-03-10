package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.service.CountryService;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface Jsonable {
    private static String getFolder(FileType type) {
        final String folderName = type.getFolderName();
        return "downloaded_pages" + (folderName != null ? File.separator + folderName : "");
    }
    private String getLocalFileString(FileType type, String fileName) {
        final String currentPath = System.getProperty("user.dir") + File.separator;

        final String folder = currentPath + getFolder(type);
        final File folderFile = new File(folder);
        if(!folderFile.exists()) {
            try {
                Files.createDirectory(folderFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final String directory = folder + File.separator + fileName + ".json";
        final File file = new File(directory);
        if(file.exists()) {
            final Path path = file.toPath();
            final List<String> lines;
            try {
                lines = Files.readAllLines(path);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            final StringBuilder builder = new StringBuilder();
            for(String line : lines) {
                builder.append(line);
            }
            return builder.toString();
        }
        return null;
    }
    private JSONObject getLocalFileJSONObject(FileType type, String fileName) {
        final String string = getLocalFileString(type, fileName);
        return string != null ? new JSONObject(string) : null;
    }
    private JSONArray getLocalFileJSONArray(FileType type, String fileName) {
        final String string = getLocalFileString(type, fileName);
        return string != null ? new JSONArray(string) : null;
    }
    default void getJSONObject(CountryService service, CompletionHandler handler) {
        getJSONObject(service.getFileType(), service.getInfo().getTitle(), handler);
    }
    default void getJSONObject(FileType type, String fileName, CompletionHandler handler) {
        final JSONObject localFile = getLocalFileJSONObject(type, fileName);
        if(localFile != null) {
            handler.handleJSONObject(localFile);
        } else {
            handler.load(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String string = object.toString();
                    saveFileJSON(type, fileName, object);
                    handler.handleJSONObject(new JSONObject(string));
                }
            });
        }
    }
    default void getJSONArray(CountryService service, CompletionHandler handler) {
        getJSONArray(service.getFileType(), service.getInfo().getTitle(), handler);
    }
    default void getJSONArray(FileType type, String fileName, CompletionHandler handler) {
        final JSONArray localFile = getLocalFileJSONArray(type, fileName);
        if(localFile != null) {
            handler.handleJSONArray(localFile);
        } else {
            handler.load(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String string = object.toString();
                    saveFileJSON(type, fileName, object);
                    handler.handleJSONArray(new JSONArray(string));
                }
            });
        }
    }
    private void saveFileJSON(FileType type, String fileName, Object value) {
        final String currentPath = System.getProperty("user.dir") + File.separator;

        final String folder = currentPath + getFolder(type);
        final String directory = folder + File.separator + fileName + ".json";
        final File file = new File(directory);
        if(!file.exists()) {
            final Path path = file.toPath();
            WLLogger.log(Level.INFO, "Jsonable - saving file at path " + path.toAbsolutePath().toString());
            try {
                Files.writeString(path, value.toString(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            WLLogger.log(Level.WARN, "Jsonable - saveFileJSON(" + fileName + ") - already exists at " + directory + "!");
        }
    }
}
