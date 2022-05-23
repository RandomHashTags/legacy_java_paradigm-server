package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.filetransfer.TransferredFile;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
            WLLogger.logWarning("FileTransferClient - no files to send");
        } else {
            sendFilesToServer(started, filesToSend);
        }
    }
    private void sendFilesToServer(long started, HashSet<File> files) {
        final int amount = files.size();
        final String serverUUID = Settings.Server.getUUID();
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("***REMOVED***", serverUUID);
        headers.put("***REMOVED***", "***REMOVED***" + serverUUID);
        headers.put("***REMOVED***", "1");
        final int port = TargetServer.FILE_TRANSFER.getPort();
        final String ip = true ? Settings.Server.getProxyLocalAddress() : "localhost";
        final String targetURL = "http://" + ip + ":" + port + "/transfer_files";
        WLLogger.logInfo("FileTransferClient - sending " + amount + " file" + (amount > 1 ? "s" : ""));

        final ConcurrentLinkedQueue<File> successfulTransfers = new ConcurrentLinkedQueue<>();
        new CompletableFutures<File>().stream(files, file -> {
            try {
                final TransferredFile transferredFile = new TransferredFile(file);
                final String fileName = transferredFile.getFileName();
                final int fileNameLength = fileName.length();
                final StringBuilder builder = new StringBuilder();
                builder.append(fileNameLength < 10 ? "0" : "").append(fileNameLength);
                builder.append(fileName);
                builder.append(Arrays.toString(transferredFile.getBytes()));
                final JSONObject json = RestAPI.postStaticJSONObject(targetURL, builder.toString(), headers);
                if(json != null && json.optBoolean("completed", false)) {
                    successfulTransfers.add(file);
                }
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        });
        for(File file : successfulTransfers) {
            try {
                final boolean success = file.delete();
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
        }
        final int successfulTransfersTotal = successfulTransfers.size();
        final boolean successful = successfulTransfersTotal > 0;
        WLLogger.logInfo("FileTransferClient - " + (successful ? "successfully sent" : "failed to send") + " " + successfulTransfersTotal + " file" + (successfulTransfersTotal > 1 ? "s" : "") + " to server (took " + WLUtilities.getElapsedTime(started) + ")");
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
