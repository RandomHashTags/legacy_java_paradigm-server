package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.filetransfer.TransferredFile;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

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
            final long started = System.currentTimeMillis();
            final TransferredFile transferredFile = httpExchange.parseTransferredFile();
            boolean success = false;
            if(transferredFile != null) {
                final String fileName = transferredFile.getFileName();
                final byte[] contentBytes = transferredFile.getBytes();
                final Folder updateFilesFolder = Folder.UPDATES_FILES;
                final String folder = updateFilesFolder.getFullFolderPath(null) + File.separator;
                try {
                    final Path path = Path.of(folder + fileName);
                    final boolean hadToDelete = Files.exists(path);
                    if(hadToDelete) {
                        Files.delete(path);
                    }
                    Files.write(path, contentBytes);
                    success = true;
                    WLLogger.logInfo("FileTransferServer - successfully " + (hadToDelete ? "replaced" : "downloaded") + " file \"" + fileName + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
            } else {
                WLLogger.logWarning("FileTransferServer - failed to receive file");
            }
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            json.put("completed", success);
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
