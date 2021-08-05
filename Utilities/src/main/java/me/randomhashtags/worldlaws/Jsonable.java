package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public interface Jsonable {
    String USER_DIR = System.getProperty("user.dir") + File.separator;

    private static String getJSONFilePath(Folder folder, String fileName) {
        return folder.getFolderPath() + File.separator + fileName + ".json";
    }
    private String getLocalFileString(Folder type, String fileName) {
        final List<String> parentFolders = type.getParentFolders();
        if(parentFolders != null) {
            for(String folder : parentFolders) {
                tryCreatingFolder(folder);
            }
        }

        final String folder = type.getFolderPath();
        tryCreatingFolder(folder);

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
    default void tryCreatingFolder(String folderPath) {
        final File folderFile = new File(folderPath);
        if(!folderFile.exists()) {
            WLLogger.log(Level.INFO, "Jsonable - creating folder at \"" + folderPath + "\"!");
            try {
                Files.createDirectory(folderFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private JSONObject getLocalFileJSONObject(Folder folder, String fileName) {
        final String string = getLocalFileString(folder, fileName);
        return string != null ? new JSONObject(string) : null;
    }
    private JSONArray getLocalFileJSONArray(Folder type, String fileName) {
        final String string = getLocalFileString(type, fileName);
        return string != null ? new JSONArray(string) : null;
    }
    default void getJSONObject(Folder folder, String fileName, CompletionHandler handler) {
        final JSONObject localFile = getLocalFileJSONObject(folder, fileName);
        if(localFile != null) {
            handler.handleJSONObject(localFile);
        } else {
            handler.load(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    JSONObject json = null;
                    if(string != null) {
                        saveFileJSON(folder, fileName, string);
                        json = new JSONObject(string);
                    }
                    handler.handleJSONObject(json);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json != null) {
                        saveFileJSON(folder, fileName, json.toString());
                    }
                    folder.resetCustomFolderName();
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
    default void setFileJSON(Folder type, String fileName, String value) {
        setFileJSON(type, fileName, (Object) value);
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
