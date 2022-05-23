package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public enum ServerStatuses {
    ;

    public static void shutdownServers() {
        final long started = System.currentTimeMillis();
        WLLogger.logInfo("Proxy - shutting down Paradigm Servers...");
        sendResponseToServers(started, "stop", "shutdown");
    }
    public static void shutdownServers(List<TargetServer> servers) {
        final long started = System.currentTimeMillis();
        final List<String> names = servers.stream().map(TargetServer::getName).toList();
        WLLogger.logInfo("Proxy - shutting down Paradigm Servers " + names.toString() + "...");
        sendResponseToServers(started, "stop", "shutdown");
    }
    public static void refreshServers() {
        final long started = System.currentTimeMillis();
        Settings.refresh();
        sendResponseToServers(started, "refresh", "refreshed");
    }
    private static void sendResponseToServers(long started, String path, String actionPastTense) {
        final List<TargetServer> servers = Arrays.asList(TargetServer.values());
        sendResponseToServers(started, servers, path, actionPastTense);
    }
    private static void sendResponseToServers(long started, List<TargetServer> servers, String path, String actionPastTense) {
        final APIVersion apiVersion = APIVersion.getLatest();
        final String uuid = Settings.Server.getUUID();
        new CompletableFutures<TargetServer>().stream(servers, server -> {
            if(server.isRealServer()) {
                final String string = sendServerResponse(server, apiVersion, uuid, path);
            }
        });
        WLLogger.logInfo("Proxy - " + actionPastTense + " Paradigm Servers (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private static String sendServerResponse(TargetServer server, APIVersion version, String uuid, String path) {
        return server.sendResponse(uuid, "***REMOVED***" + uuid, "***REMOVED***", version, path, false);
    }
    public static void spinUpServers() {
        final long started = System.currentTimeMillis();
        final String command = Settings.Server.getRunServersCommand();
        WLUtilities.executeCommand(command, false);
        WLLogger.logInfo("Proxy - spun up servers (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    public static void rebootServers() {
        Proxy.startMaintenanceMode("Servers are rebooting, and should be back up in a few minutes :)");
        shutdownServers();
        Settings.refresh();
        spinUpServersWithHomeResponse();
        Proxy.endMaintenanceMode();
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
    public static HashSet<Path> getUpdateFiles() {
        return Folder.UPDATES_FILES.getAllFilePaths(null);
    }

    public static boolean updateServers(long started, HashSet<Path> files) {
        final boolean updatesAreAvailable = !files.isEmpty();
        if(updatesAreAvailable) {
            Proxy.startMaintenanceMode("Servers are updating, and should be back up in a few minutes :)");
            final List<TargetServer> servers = new ArrayList<>();
            for(Path path : files) {
                final String fileName = path.getFileName().toString();
                if(fileName.endsWith(".jar")) {
                    final TargetServer server = TargetServer.valueOfInput(fileName.split("\\.jar")[0]);
                    if(server != null) {
                        servers.add(server);
                    }
                }
            }
            shutdownServers(servers);
            applyUpdate(files);
            final int serversUpdated = servers.size();
            WLLogger.logInfo("Proxy - updated " + serversUpdated + " server" + (serversUpdated > 1 ? "s" : "") + " (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return updatesAreAvailable;
    }
    public static void applyUpdate() {
        final HashSet<Path> files = getUpdateFiles();
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
        final AtomicInteger updated = new AtomicInteger(0);
        final String separator = File.separator;
        new CompletableFutures<Path>().stream(files, path -> {
            final String fullFileName = path.getFileName().toString();
            if(fileFolders.containsKey(fullFileName)) {
                final String fileName = fullFileName.split("\\.")[0];
                final String filePath = fileFolders.get(fullFileName).replace("/", separator);
                sourceFolder.setCustomFolderName(fileName, filePath);
                updatedFilesFolder.setCustomFolderName(fileName, null);
                final String oldURI = sourceFolder.getFullFolderPath(fileName) + separator + fullFileName, newURI = updatedFilesFolder.getFullFolderPath(fileName) + separator + fullFileName;
                final Path sourcePath = Paths.get(newURI), targetPath = Paths.get(oldURI);
                try {
                    if(Files.exists(targetPath)) {
                        Files.delete(targetPath);
                    }
                    Jsonable.tryCreatingParentFolders(targetPath);
                    Files.move(sourcePath, targetPath);
                    updated.addAndGet(1);
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
                sourceFolder.removeCustomFolderName(fileName);
                updatedFilesFolder.removeCustomFolderName(fileName);
            } else {
                WLLogger.logWarning("Proxy - failed updating file \"" + fullFileName + "\" for path \"" + path.toAbsolutePath().toString() + "\". Not found in fileFolders!");
            }
        });
        final int amountUpdated = updated.get();
        WLLogger.logInfo("Proxy - updated " + amountUpdated + " file" + (amountUpdated > 1 ? "s" : "") + " (took " + WLUtilities.getElapsedTime(started) + ")");
    }
}
