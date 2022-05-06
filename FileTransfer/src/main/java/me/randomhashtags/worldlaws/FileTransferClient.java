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
        sendLocalToServer();
    }

    private static void sendLocalToServer() {
        final long started = System.currentTimeMillis();
        final int port = Settings.Server.getServerHandlerPort();
        final String localIP = "http://" + (false ? "localhost" : "localhost") + "/transfer_files";
        final String localURL = localIP + port;

        final HashSet<File> filesToSend = new HashSet<>() {{
            addAll(getServers());
        }};

        final int amount = filesToSend.size();
        if(amount == 0) {
            WLLogger.logInfo("FileTransferClient - no files to send");
        } else {
            WLLogger.logInfo("FileTransferClient - sending " + amount + " file" + (amount > 1 ? "s" : ""));
            final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
            postData.put("files", toPostJSONArray(filesToSend));

            final String serverUUID = Settings.Server.getUUID();
            final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("***REMOVED***", serverUUID);
            headers.put("***REMOVED***", "***REMOVED***" + serverUUID);
            headers.put("***REMOVED***", "1");
            final JSONObject json = RestAPI.postStaticJSONObject(localURL, postData, true, headers);
            final boolean success = json != null && json.optBoolean("completed", false);
            if(success) {
                for(File file : filesToSend) {
                    file.delete();
                }
            }
            WLLogger.logInfo("FileTransferClient - " + (success ? "successfully sent" : "failed sending") + " " + amount + " file" + (amount > 1 ? "s" : "") + " (took " + WLUtilities.getElapsedTime(started) + ")");
        }
    }
    private static JSONArray toPostJSONArray(HashSet<File> files) {
        final JSONArray array = new JSONArray();
        for(File file : files) {
            final byte[] fileContentBytes = new byte[(int) file.length()];

            final JSONObject json = new JSONObject();
            json.put("name", file.getName());
            json.put("contentLength", fileContentBytes.length);
            json.put("contentBytes", fileContentBytes);
            array.put(json);
        }
        return array;
    }
    private static HashSet<File> getServers() {
        final HashSet<File> filesToSend = new HashSet<>();
        final String artifactPrefix = "/Users/randomhashtags/IdeaProjects/World Laws/out/artifacts" + File.separator;
        final HashSet<String> servers = new HashSet<>() {{
            add("ServerHandler");
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
