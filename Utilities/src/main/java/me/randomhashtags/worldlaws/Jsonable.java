package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public interface Jsonable {
    String USER_DIR = System.getProperty("user.dir") + File.separator;

    private static String getFilePath(Folder folder, String fileName, String extension) {
        return folder.getFolderPath(fileName) + File.separator + fileName + "." + extension;
    }
    static void tryCreatingParentFolders(Path file) {
        final Path parent = file.getParent();
        if(!Files.exists(parent)) {
            try {
                Files.createDirectories(parent);
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
    }

    default String getLocalFileString(Folder folder, String fileName, String extension) {
        final String directory = getFilePath(folder, fileName, extension);
        final Path path = Paths.get(directory);
        if(Files.exists(path)) {
            try {
                return Files.lines(path).collect(Collectors.joining(System.lineSeparator()));
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
        return null;
    }
    private JSONObject getLocalFileJSONObject(Folder folder, String fileName) {
        final String string = getLocalFileString(folder, fileName, "json");
        return string != null ? new JSONObject(string) : null;
    }
    private JSONArray getLocalFileJSONArray(Folder type, String fileName) {
        final String string = getLocalFileString(type, fileName, "json");
        return string != null ? new JSONArray(string) : null;
    }
    default void getJSONObject(Folder folder, String fileName, CompletionHandler handler) {
        final JSONObject localFile = getLocalFileJSONObject(folder, fileName);
        if(localFile != null) {
            handler.handleJSONObject(localFile);
        } else {
            final AtomicBoolean saved = new AtomicBoolean(false);
            handler.load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    JSONObject json = null;
                    if(string != null && !string.isEmpty()) {
                        saveFileJSON(folder, fileName, string);
                        json = new JSONObject(string);
                        saved.set(true);
                    }
                    handler.handleJSONObject(json);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json != null && !saved.get()) {
                        saveFileJSON(folder, fileName, json.toString());
                    }
                    folder.removeCustomFolderName(fileName);
                    handler.handleJSONObject(json);
                }
            });
        }
    }

    default void getJSONArray(Folder type, String fileName, CompletionHandler handler) {
        final JSONArray localFile = getLocalFileJSONArray(type, fileName);
        if(localFile != null) {
            handler.handleJSONArray(localFile);
        } else {
            final AtomicBoolean saved = new AtomicBoolean(false);
            handler.load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    JSONArray array = null;
                    if(string != null && !string.isEmpty()) {
                        saveFileJSON(type, fileName, string);
                        array = new JSONArray(string);
                        saved.set(true);
                    }
                    handler.handleJSONArray(array);
                }

                @Override
                public void handleJSONArray(JSONArray array) {
                    if(!saved.get() && array != null) {
                        saveFileJSON(type, fileName, array.toString());
                    }
                    handler.handleJSONArray(array);
                }
            });
        }
    }
    default void setFileJSONObject(Folder type, String fileName, JSONObject json) {
        setFileJSON(type, fileName, json);
    }
    default void setFileJSONArray(Folder type, String fileName, JSONArray array) {
        setFileJSON(type, fileName, array);
    }
    default void setFileJSON(Folder type, String fileName, String value) {
        setFileJSON(type, fileName, (Object) value);
    }
    private void setFileJSON(Folder folder, String fileName, Object value) {
        writeFile(null, Level.INFO, folder, fileName, value, "json", true);
    }
    static void saveFileJSON(Folder folder, String fileName, String value) {
        saveFile(folder, fileName, value, "json");
    }
    static void saveFile(Folder folder, String fileName, String value, String extension) {
        saveFile(null, Level.INFO, folder, fileName, value, extension);
    }
    static void saveFile(String sender, Level level, Folder folder, String fileName, String value, String extension) {
        writeFile(sender, level, folder, fileName, value, extension, false);
    }
    private static void writeFile(String sender, Level level, Folder folder, String fileName, Object value, String extension, boolean canExist) {
        if(value != null) {
            final String directory = getFilePath(folder, fileName, extension);
            final Path path = Paths.get(directory);
            sender = sender != null ? "[" + sender + "] " : "";
            final boolean alreadyExists = Files.exists(path);
            if(alreadyExists) {
                if(!canExist) {
                    WLLogger.log(Level.WARN, sender + "Jsonable - writeFile(" + fileName + ") - already exists at " + directory + " (folder=" + folder.name() + ")!");
                } else {
                    WLLogger.log(level, sender + "Jsonable - overriding file with folder " + folder.name() + " at " + path.toAbsolutePath().toString());
                    write(path, value);
                }
            } else {
                WLLogger.log(level, sender + "Jsonable - creating file with folder " + folder.name() + " at " + path.toAbsolutePath().toString());
                tryCreatingParentFolders(path);
                write(path, value);
            }
        }
        folder.removeCustomFolderName(fileName);
    }
    private static void write(Path path, Object value) {
        try {
            Files.writeString(path, value.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
}
