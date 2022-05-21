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
        WLLogger.logInfo("Proxy - shutting down Paradigm Servers...");
        final List<TargetServer> servers = Arrays.asList(TargetServer.values());
        final APIVersion apiVersion = APIVersion.getLatest();
        final String uuid = Settings.Server.getUUID();
        new CompletableFutures<TargetServer>().stream(servers, server -> {
            if(server.isRealServer()) {
                final String string = shutdownServer(server, apiVersion, uuid);
            }
        });
        WLLogger.logInfo("Proxy - shutdown Paradigm Servers (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public static String shutdownServer(TargetServer server, APIVersion version, String uuid) {
        return server.sendResponse(uuid, "***REMOVED***" + uuid, "***REMOVED***", version, "stop", false);
    }
    public static void spinUpServers() {
        final long started = System.currentTimeMillis();
        final String command = Settings.Server.getRunServersCommand();
        WLUtilities.executeCommand(command, false);
        WLLogger.logInfo("Proxy - spun up servers (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public static void rebootServers() {
        final boolean updated = updateServersIfAvailable();
        if(!updated) {
            Proxy.startMaintenanceMode("Servers are rebooting, and should be back up in a few minutes :)");
            rebootServersWithHomeResponse();
        } else {
            spinUpServersWithHomeResponse();
        }
        Proxy.endMaintenanceMode();
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
        final String string = Proxy.updateHomeResponse();
    }

    public static boolean tryUpdatingServersIfAvailable() {
        final boolean updated = updateServersIfAvailable();
        if(updated) {
            spinUpServersWithHomeResponse();
            Proxy.endMaintenanceMode();
            return true;
        } else {
            WLLogger.logInfo("Proxy - no server updates available");
            return false;
        }
    }
    private static boolean updateServersIfAvailable() {
        final long started = System.currentTimeMillis();
        final HashSet<Path> files = Folder.UPDATES_FILES.getAllFilePaths(null);
        final boolean updatesAreAvailable = !files.isEmpty();
        if(updatesAreAvailable) {
            Proxy.startMaintenanceMode("Servers are updating, and should be back up in a few minutes :)");
            shutdownServers();

            applyUpdate(files);

            WLLogger.logInfo("Proxy - updated servers (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return updatesAreAvailable;
    }
    public static void applyUpdate() {
        final HashSet<Path> files = Folder.UPDATES_FILES.getAllFilePaths(null);
        final boolean updatesAreAvailable = !files.isEmpty();
        if(updatesAreAvailable) {
            applyUpdate(files);
        }
    }
    private static void applyUpdate(HashSet<Path> files) {
        final Folder sourceFolder = Folder.UPDATES, updatedFilesFolder = Folder.UPDATES_FILES;
        final JSONObject updateJSON = Jsonable.getStaticLocalFileJSONObject(sourceFolder, "update");
        final JSONArray updateNotes = updateJSON.getJSONArray("update notes");

        final JSONObject actions = updateJSON.getJSONObject("actions");

        final JSONObject replace = actions.getJSONObject("replace");
        final HashMap<String, String> fileFolders = getFileFolders(replace);
        replaceFiles(files, fileFolders, sourceFolder, updatedFilesFolder);
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
                final String oldURI = sourceFolder.getFullFolderPath(fileName) + separator + fullFileName, newURI = updatedFilesFolder.getFullFolderPath(fileName) + separator + fullFileName;
                final Path sourcePath = Paths.get(newURI), targetPath = Paths.get(oldURI);
                try {
                    if(Files.exists(targetPath)) {
                        Files.delete(targetPath);
                    }
                    Files.move(sourcePath, targetPath);
                    updated += 1;
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
                sourceFolder.removeCustomFolderName(fileName);
                updatedFilesFolder.removeCustomFolderName(fileName);
            } else {
                WLLogger.logWarning("Proxy - failed updating file \"" + fullFileName + "\" for path \"" + path.toAbsolutePath().toString() + "\". Not found in fileFolders!");
            }
        }
        WLLogger.logInfo("Proxy - updated " + updated + " file" + (updated > 1 ? "s" : "") + " (took " + WLUtilities.getElapsedTime(started) + ")");
    }
}
