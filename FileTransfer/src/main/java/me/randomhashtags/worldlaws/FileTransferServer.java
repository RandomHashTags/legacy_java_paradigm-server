package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileTransferServer implements WLServer {

    public static void main(String[] args) {
        new FileTransferServer();
    }

    FileTransferServer() {
        load();
    }

    @Override
    public WLHttpHandler getDefaultHandler() {
        return httpExchange -> {
            final JSONObject requestBody = httpExchange.getRequestBodyJSON();
            boolean completedSuccessfully = false;
            int amountOfFiles = 0;
            if(requestBody != null && requestBody.has("files")) {
                final JSONArray array = requestBody.getJSONArray("files");
                amountOfFiles = array.length();
                final Folder updateFilesFolder = Folder.UPDATES_FILES;
                for(Path path : updateFilesFolder.getAllFilePaths(null)) {
                    try {
                        Files.delete(path);
                    } catch (Exception e) {
                        WLUtilities.saveException(e);
                    }
                }
                final String folder = updateFilesFolder.getFullFolderPath(null) + File.separator;
                for(Object fileObj : array) {
                    final JSONObject fileJSON = (JSONObject) fileObj;
                    final String fileName = fileJSON.getString("name");
                    try {
                        final JSONArray contentBytesArray = fileJSON.getJSONArray("contentBytes");
                        final int length = contentBytesArray.length();
                        final byte[] contentBytes = new byte[length];
                        for(int i = 0; i < length; i++) {
                            contentBytes[i] = (byte) contentBytesArray.getInt(i);
                        }
                        Files.write(Path.of(folder + fileName), contentBytes);
                        completedSuccessfully = true;
                    } catch (Exception e) {
                        WLUtilities.saveException(e);
                    }
                }
            }
            WLLogger.logInfo("FileTransferServer - " + (completedSuccessfully ? "successfully received" : "failed to receive") + " " + amountOfFiles + " file" + (amountOfFiles > 1 ? "s" : ""));
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            json.put("completed", completedSuccessfully);
            return json;
        };
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.FILE_TRANSFER;
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return null;
    }
}
