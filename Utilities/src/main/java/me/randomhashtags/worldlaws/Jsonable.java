package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface Jsonable {
    String CURRENT_FOLDER = System.getProperty("user.dir") + File.separator;

    static String getFilePath(Folder folder, String fileName, String extension) {
        return folder.getFullFolderPath(fileName) + File.separator + fileName + "." + extension;
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

    static String getStaticLocalFileString(Folder folder, String fileName, String extension) {
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
    static JSONObject getStaticLocalFileJSONObject(Folder folder, String fileName) {
        final String string = getStaticLocalFileString(folder, fileName, "json");
        return string != null && string.startsWith("{") && string.endsWith("}") ? new JSONObject(string) : null;
    }
    static JSONArray getStaticFileJSONArray(Folder folder, String fileName) {
        final String string = getStaticLocalFileString(folder, fileName, "json");
        return string != null && string.startsWith("[") && string.endsWith("]") ? new JSONArray(string) : null;
    }
    static JSONObject getStaticJSONObject(Folder folder, String fileName, CompletionHandler handler) {
        JSONObject json = getStaticLocalFileJSONObject(folder, fileName);
        if(json == null && handler != null) {
            String string = handler.loadJSONObjectString();
            if(string != null) {
                try {
                    json = new JSONObject(string);
                } catch (Exception e) {
                    WLUtilities.saveLoggedError("Jsonable", "failed parsing JSONObject from string\n\n" + string);
                }
            } else {
                json = handler.loadJSONObject();
                string = json != null ? json.toString() : null;
            }
            if(json != null) {
                saveFileJSON(folder, fileName, string);
            }
        }
        return json;
    }

    default String getLocalFileString(Folder folder, String fileName, String extension) {
        return getStaticLocalFileString(folder, fileName, extension);
    }
    default JSONObject getLocalFileJSONObject(Folder folder, String fileName) {
        return getStaticLocalFileJSONObject(folder, fileName);
    }
    default JSONArray getLocalFileJSONArray(Folder folder, String fileName) {
        return getStaticFileJSONArray(folder, fileName);
    }
    default JSONObject getJSONObject(Folder folder, String fileName, CompletionHandler handler) {
        return getStaticJSONObject(folder, fileName, handler);
    }

    default JSONArray getJSONArray(Folder type, String fileName, CompletionHandler handler) {
        JSONArray array = getLocalFileJSONArray(type, fileName);
        if(array == null && handler != null) {
            String string = handler.loadJSONArrayString();
            if(string != null) {
                try {
                    array = new JSONArray(string);
                } catch (Exception e) {
                    WLUtilities.saveLoggedError("Jsonable", "failed parsing JSONArray from string\n\n" + string);
                }
            } else {
                array = handler.loadJSONArray();
                string = array != null ? array.toString() : null;
            }
            if(array != null) {
                saveFileJSON(type, fileName, string);
            }
        }
        return array;
    }
    static void setFileJSONObject(Folder folder, String fileName, JSONObject json) {
        setFileJSONObject(folder, fileName, json, true);
    }
    static void setFileJSONObject(Folder folder, String fileName, JSONObject json, boolean async) {
        setFileJSON(folder, fileName, json, async);
    }
    static void setFileJSONArray(Folder folder, String fileName, JSONArray array) {
        setFileJSON(folder, fileName, array);
    }
    default void setFileJSON(Folder folder, String fileName, String value) {
        setFileJSON(folder, fileName, (Object) value);
    }
    private static void setFileJSON(Folder folder, String fileName, Object value) {
        setFileJSON(folder, fileName, value, true);
    }
    private static void setFileJSON(Folder folder, String fileName, Object value, boolean async) {
        writeFile(null, folder, fileName, value, "json", true, async);
    }
    static void saveFileJSON(Folder folder, String fileName, String value) {
        saveFile(folder, fileName, value, "json");
    }
    static void saveFile(Folder folder, String fileName, String value, String extension) {
        saveFile(null, folder, fileName, value, extension);
    }
    static void saveFile(String sender, Folder folder, String fileName, String value, String extension, boolean canExist, boolean async) {
        writeFile(sender, folder, fileName, value, extension, canExist, async);
    }
    static void saveFile(String sender, Folder folder, String fileName, String value, String extension) {
        writeFile(sender, folder, fileName, value, extension, false);
    }
    private static void writeFile(String sender, Folder folder, String fileName, Object value, String extension, boolean canExist) {
        writeFile(sender, folder, fileName, value, extension, canExist, true);
    }
    private static void writeFile(String sender, Folder folder, String fileName, Object value, String extension, boolean canExist, boolean async) {
        if(value != null) {
            final String directory = getFilePath(folder, fileName, extension), paradigmPath = folder.getFolderPath(fileName) + File.separator + fileName;
            final Path path = Paths.get(directory);
            sender = sender != null ? "[" + sender + "] " : "";
            final boolean alreadyExists = Files.exists(path);
            if(alreadyExists) {
                if(!canExist) {
                    WLLogger.logError("Jsonable", sender + "Jsonable - writeFile(" + fileName + ") - already exists at " + directory + " (folder=" + folder.name() + ")!");
                    folder.removeCustomFolderName(fileName);
                } else {
                    WLLogger.logInfo(sender + "Jsonable - overriding file with folder " + folder.name() + " at " + paradigmPath);
                    write(folder, fileName, path, value, async);
                }
            } else {
                WLLogger.logInfo(sender + "Jsonable - creating file with folder " + folder.name() + " at " + paradigmPath);
                tryCreatingParentFolders(path);
                write(folder, fileName, path, value, async);
            }
        } else {
            folder.removeCustomFolderName(fileName);
        }
    }
    private static void write(Folder folder, String fileName, Path path, Object value, boolean async) {
        if(async) {
            CompletableFuture.runAsync(() -> writeString(folder, fileName, path, value));
        } else {
            writeString(folder, fileName, path, value);
        }
    }
    private static void writeString(Folder folder, String fileName, Path path, Object value) {
        try {
            Files.writeString(path, value.toString(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        folder.removeCustomFolderName(fileName);
    }
}
