package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public interface Jsonable {
    String USER_DIR = System.getProperty("user.dir") + File.separator;

    private static String getFolder(Folder type) {
        return FolderUtils.getFolder(type);
    }
    private static String getJSONFilePath(Folder fileType, String fileName) {
        final String folder = USER_DIR + getFolder(fileType);
        return folder + File.separator + fileName + ".json";
    }
    private String getLocalFileString(Folder type, String fileName) {
        final String folder = USER_DIR + getFolder(type);
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
            try {
                return Files.lines(path).collect(Collectors.joining(System.lineSeparator()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
    private JSONObject getLocalFileJSONObject(Folder type, String fileName) {
        final String string = getLocalFileString(type, fileName);
        return string != null ? new JSONObject(string) : null;
    }
    private JSONArray getLocalFileJSONArray(Folder type, String fileName) {
        final String string = getLocalFileString(type, fileName);
        return string != null ? new JSONArray(string) : null;
    }
    default void getJSONObject(Folder type, String fileName, CompletionHandler handler) {
        final JSONObject localFile = getLocalFileJSONObject(type, fileName);
        if(localFile != null) {
            handler.handleJSONObject(localFile);
        } else {
            handler.load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    JSONObject json = null;
                    if(string != null) {
                        saveFileJSON(type, fileName, string);
                        json = new JSONObject(string);
                    }
                    handler.handleJSONObject(json);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json != null) {
                        saveFileJSON(type, fileName, json.toString());
                    }
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
            handler.load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    saveFileJSON(type, fileName, string);
                    handler.handleJSONArray(new JSONArray(string));
                }

                @Override
                public void handleJSONArray(JSONArray array) {
                    saveFileJSON(type, fileName, array.toString());
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
    private void setFileJSON(Folder type, String fileName, Object value) {
        final String path = getJSONFilePath(type, fileName);
        try {
            final FileWriter fileWriter = new FileWriter(path, false);
            fileWriter.write(value.toString());
            fileWriter.close();
            WLLogger.log(Level.INFO, "Jsonable - setting json file contents at path \"" + path + "\"");
        } catch (Exception e) {
            e.printStackTrace();
            WLLogger.log(Level.ERROR, "Jsonable - failed setting json file contents at path \"" + path + "\" (" + e.getMessage() + ")!");
        }
    }
    private void saveFileJSON(Folder type, String fileName, String value) {
        if(value != null) {
            final String directory = getJSONFilePath(type, fileName);
            final File file = new File(directory);
            if(!file.exists()) {
                final Path path = file.toPath();
                WLLogger.log(Level.INFO, "Jsonable - creating file at path " + path.toAbsolutePath().toString());
                try {
                    Files.writeString(path, value, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                WLLogger.log(Level.WARN, "Jsonable - saveFileJSON(" + fileName + ") - already exists at " + directory + "!");
            }
        }
    }
}
