package me.randomhashtags.worldlaws;

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

    private static JSONObject getDefaultSettingsJSON() {
        final JSONObject json = new JSONObject();

        final JSONObject serverJSON = new JSONObject();
        serverJSON.put("proxy_port", 0);
        serverJSON.put("default_address", "http://localhost");
        final JSONObject serversJSON = new JSONObject();
        for(TargetServer server : TargetServer.values()) {
            if(server.isRealServer()) {
                serversJSON.put(server.getNameLowercase(), getDefaultServerSettingsJSON(server));
            }
        }
        serverJSON.put("servers", serversJSON);

        final JSONObject settingsJSON = new JSONObject();

        final JSONObject googleJSON = new JSONObject();
        googleJSON.put("civic_api_key", "");
        settingsJSON.put("google", googleJSON);

        final JSONObject nasaJSON = new JSONObject();
        nasaJSON.put("api_key", "***REMOVED***");
        settingsJSON.put("nasa", nasaJSON);

        final JSONObject twitchJSON = new JSONObject();
        twitchJSON.put("request_limit", 100);
        twitchJSON.put("client_id", "");
        twitchJSON.put("access_token", "");
        settingsJSON.put("twitch", twitchJSON);

        final JSONObject yahooFinanceJSON = new JSONObject();
        yahooFinanceJSON.put("rapid_api_key", "");
        settingsJSON.put("yahoo_finance", yahooFinanceJSON);

        final JSONObject youtubeJSON = new JSONObject();
        youtubeJSON.put("request_limit", 49);
        youtubeJSON.put("key", "");
        youtubeJSON.put("key_identifier", "***REMOVED***");
        youtubeJSON.put("key_value", "");
        settingsJSON.put("youtube", youtubeJSON);

        json.put("server", serverJSON);
        json.put("settings", settingsJSON);
        return json;
    }
    private static JSONObject getDefaultServerSettingsJSON(TargetServer server) {
        final JSONObject json = new JSONObject();
        json.put("port", server.getDefaultPort());
        switch (server) {
            case COUNTRIES:
                json.put("response_version", 5);
                break;
            default:
                break;
        }
        return json;
    }

    static JSONObject getSettingsJSON() {
        final JSONObject existing = getStaticLocalFileJSONObject(Folder.OTHER, "settings");
        return existing != null ? existing : getDefaultSettingsJSON();
    }
    static JSONObject getSettingsPrivateValuesJSON() {
        return getSettingsJSON().getJSONObject("private_values");
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
        return string != null ? new JSONObject(string) : null;
    }
    static JSONArray getStaticFileJSONArray(Folder folder, String fileName) {
        final String string = getStaticLocalFileString(folder, fileName, "json");
        return string != null ? new JSONArray(string) : null;
    }
    static void getStaticJSONObject(Folder folder, String fileName, CompletionHandler handler) {
        final JSONObject localFile = getStaticLocalFileJSONObject(folder, fileName);
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

    default String getLocalFileString(Folder folder, String fileName, String extension) {
        return getStaticLocalFileString(folder, fileName, extension);
    }
    default JSONArray getLocalFileJSONArray(Folder folder, String fileName) {
        return getStaticFileJSONArray(folder, fileName);
    }
    default void getJSONObject(Folder folder, String fileName, CompletionHandler handler) {
        getStaticJSONObject(folder, fileName, handler);
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
    default void setFileJSONObject(Folder folder, String fileName, JSONObject json) {
        setFileJSON(folder, fileName, json);
    }
    default void setFileJSONArray(Folder folder, String fileName, JSONArray array) {
        setFileJSON(folder, fileName, array);
    }
    default void setFileJSON(Folder folder, String fileName, String value) {
        setFileJSON(folder, fileName, (Object) value);
    }
    private void setFileJSON(Folder folder, String fileName, Object value) {
        writeFile(null, folder, fileName, value, "json", true);
    }
    static void saveFileJSON(Folder folder, String fileName, String value) {
        saveFile(folder, fileName, value, "json");
    }
    static void saveFile(Folder folder, String fileName, String value, String extension) {
        saveFile(null, folder, fileName, value, extension);
    }
    static void saveFile(String sender, Folder folder, String fileName, String value, String extension) {
        writeFile(sender, folder, fileName, value, extension, false);
    }
    private static void writeFile(String sender, Folder folder, String fileName, Object value, String extension, boolean canExist) {
        if(value != null) {
            final String directory = getFilePath(folder, fileName, extension);
            final Path path = Paths.get(directory);
            sender = sender != null ? "[" + sender + "] " : "";
            final boolean alreadyExists = Files.exists(path);
            if(alreadyExists) {
                if(!canExist) {
                    WLLogger.logError("Jsonable", sender + "Jsonable - writeFile(" + fileName + ") - already exists at " + directory + " (folder=" + folder.name() + ")!");
                } else {
                    WLLogger.logInfo(sender + "Jsonable - overriding file with folder " + folder.name() + " at " + path.toAbsolutePath().toString());
                    write(path, value);
                }
            } else {
                WLLogger.logInfo(sender + "Jsonable - creating file with folder " + folder.name() + " at " + path.toAbsolutePath().toString());
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
