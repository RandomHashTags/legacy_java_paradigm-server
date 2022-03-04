package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public enum ServerStatuses {
    ;

    public static void shutdownServers() {
        final long started = System.currentTimeMillis();
        WLLogger.logInfo("ServerHandler - shutting down Paradigm Servers...");
        final List<TargetServer> servers = Arrays.asList(TargetServer.values());
        final APIVersion apiVersion = APIVersion.getLatest();
        final String uuid = Settings.Server.getUUID();
        new CompletableFutures<TargetServer>().stream(servers, server -> {
            if(server.isRealServer()) {
                final String string = shutdownServer(server, apiVersion, uuid);
            }
        });
        WLLogger.logInfo("ServerHandler - shutdown Paradigm Servers (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public static String shutdownServer(TargetServer server, APIVersion version, String uuid) {
        return server.handleResponse(version, uuid, "stop", null);
    }
    public static void spinUpServers() {
        final long started = System.currentTimeMillis();
        final String command = Settings.Server.getRunServersCommand();
        WLUtilities.executeCommand(command);
        WLLogger.logInfo("ServerHandler - spun up servers (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public static void rebootServers() {
        final boolean updated = updateServersIfAvailable();
        if(!updated) {
            ServerHandler.setMaintenanceMode(true, "Servers are rebooting, and should be back up in a few minutes :)");
            rebootServersWithHomeResponse();
        } else {
            spinUpServersWithHomeResponse();
        }
        ServerHandler.setMaintenanceMode(false, null);
    }
    private static void rebootServersWithHomeResponse() {
        shutdownServers();
        Settings.refresh();
        spinUpServersWithHomeResponse();
    }
    private static void spinUpServersWithHomeResponse() {
        spinUpServers();
        try {
            Thread.sleep(10*1000);
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
        final String string = ServerHandler.updateHomeResponse();
    }

    public static void tryUpdatingServersIfAvailable() {
        final boolean updated = updateServersIfAvailable();
        if(updated) {
            ServerHandler.setMaintenanceMode(false, null);
            spinUpServersWithHomeResponse();
        } else {
            WLLogger.logInfo("ServerHandler - no server updates available");
        }
    }
    private static boolean updateServersIfAvailable() {
        final long started = System.currentTimeMillis();
        final Folder sourceFolder = Folder.UPDATES, updatedFilesFolder = Folder.UPDATES_FILES;
        final HashSet<Path> files = updatedFilesFolder.getAllFilePaths(null);
        final boolean updatesAreAvailable = files != null;
        if(updatesAreAvailable) {
            ServerHandler.setMaintenanceMode(true, "Servers are updating, and should be back up in a few minutes :)");
            shutdownServers();

            final JSONObject updateJSON = Jsonable.getStaticLocalFileJSONObject(Folder.UPDATES, "update");
            final JSONArray updateNotes = updateJSON.getJSONArray("update notes");

            final JSONObject actions = updateJSON.getJSONObject("actions");

            final JSONObject replace = actions.getJSONObject("replace");
            final HashMap<String, String> fileFolders = getFileFolders(replace);
            replaceFiles(files, fileFolders, sourceFolder, updatedFilesFolder);

            WLLogger.logInfo("ServerHandler - updated servers (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return updatesAreAvailable;
    }
    private static HashMap<String, String> getFileFolders(JSONObject json) {
        final HashMap<String, String> fileFolders = new HashMap<>();
        for(String folder : json.keySet()) {
            final JSONObject extensionsJSON = json.getJSONObject(folder);
            for(String extension : extensionsJSON.keySet()) {
                final JSONArray array = extensionsJSON.getJSONArray(extension);
                for(Object fileName : array) {
                    final String fullFileName = fileName + "." + extension;
                    fileFolders.put(fullFileName, folder);
                }
            }
        }
        return fileFolders;
    }
    private static void replaceFiles(HashSet<Path> files, HashMap<String, String> fileFolders, Folder sourceFolder, Folder updatedFilesFolder) {
        final long started = System.currentTimeMillis();
        int updated = 0;
        final String separator = File.separator;
        for(Path path : files) {
            final String fullFileName = path.getFileName().toString();
            if(fileFolders.containsKey(fullFileName)) {
                final String filePath = fileFolders.get(fullFileName).replace("/", separator);
                final String fileName = fullFileName.split("\\.")[0];
                sourceFolder.setCustomFolderName(fileName, filePath);
                updatedFilesFolder.setCustomFolderName(fileName, null);
                final File newFile = updatedFilesFolder.literalFileExists(fullFileName);
                if(newFile != null) {
                    final File oldFile = sourceFolder.literalFileExists(fileName, fullFileName);
                    if(oldFile != null) {
                        final boolean deleted = oldFile.delete();
                    }

                    final String oldURI = sourceFolder.getFullFolderPath(fileName) + separator + fullFileName, newURI = updatedFilesFolder.getFullFolderPath(fileName) + separator + fullFileName;
                    final Path sourcePath = Paths.get(newURI), targetPath = Paths.get(oldURI);
                    try {
                        Files.move(sourcePath, targetPath);
                        updated += 1;
                    } catch (Exception e) {
                        WLUtilities.saveException(e);
                    }
                }
                sourceFolder.removeCustomFolderName(fileName);
                updatedFilesFolder.removeCustomFolderName(fileName);
            }
        }
        WLLogger.logInfo("ServerHandler - updated " + updated + " file(s) (took " + WLUtilities.getElapsedTime(started) + ")");
    }
}
