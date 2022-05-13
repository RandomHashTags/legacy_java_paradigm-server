package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;

public final class FileTransferClient implements RestAPI {

    public static void main(String[] args) {
        new FileTransferClient().sendLocalToServer();
    }

    private void sendLocalToServer() {
        final long started = System.currentTimeMillis();
        final HashSet<File> filesToSend = new HashSet<>() {{
            addAll(getServers());
        }};

        final Folder updateFilesFolder = Folder.UPDATES_FILES;
        for(Path path : updateFilesFolder.getAllFilePaths(null)) {
            final String totalPath = path.toAbsolutePath().toString();
            if(Files.exists(path)) {
                final File file =  new File(totalPath);
                filesToSend.add(file);
            }
        }

        final int amount = filesToSend.size();
        if(amount == 0) {
            WLLogger.logInfo("FileTransferClient - no files to send");
        } else {
            WLLogger.logInfo("FileTransferClient - sending " + amount + " file" + (amount > 1 ? "s" : ""));
            final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
            try {
                postData.put("files", toPostJSONArray(filesToSend));
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }

            final String serverUUID = Settings.Server.getUUID();
            final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("***REMOVED***", serverUUID);
            headers.put("***REMOVED***", "***REMOVED***" + serverUUID);
            headers.put("***REMOVED***", "1");
            final int port = TargetServer.FILE_TRANSFER.getPort();
            final String ip = true ? Settings.Server.getProxyLocalAddress() : "localhost";
            final String targetURL = "http://" + ip + ":" + port + "/transfer_files";
            final JSONObject json = RestAPI.postStaticJSONObject(targetURL, postData, true, headers);
            final boolean success = json != null && json.optBoolean("completed", false);
            if(success) {
                for(File file : filesToSend) {
                    file.delete();
                }
            }
            WLLogger.logInfo("FileTransferClient - " + (success ? "successfully sent" : "failed sending") + " " + amount + " file" + (amount > 1 ? "s" : "") + " (took " + WLUtilities.getElapsedTime(started) + ")");
        }
    }
    private JSONArray toPostJSONArray(HashSet<File> files) throws Exception {
        final JSONArray array = new JSONArray();
        for(File file : files) {
            final byte[] fileContentBytes = Files.readAllBytes(file.toPath());

            final JSONObject json = new JSONObject();
            json.put("name", file.getName());
            json.put("contentBytes", fileContentBytes);
            array.put(json);
        }
        return array;
    }
    private HashSet<File> getServers() {
        final HashSet<File> filesToSend = new HashSet<>();
        final String artifactPrefix = "/Users/randomhashtags/IdeaProjects/World Laws/out/artifacts" + File.separator;
        final HashSet<String> servers = new HashSet<>() {{
            add("Proxy");
        }};
        for(TargetServer server : TargetServer.values()) {
            final String serverName = server.getName();
            servers.add(serverName);
        }
        for(String serverName : servers) {
            final String totalPath = artifactPrefix + serverName + File.separator + serverName + ".jar";
            final Path path = Path.of(totalPath);
            if(Files.exists(path)) {
                final File file =  new File(totalPath);
                filesToSend.add(file);
            }
        }
        return filesToSend;
    }
}
