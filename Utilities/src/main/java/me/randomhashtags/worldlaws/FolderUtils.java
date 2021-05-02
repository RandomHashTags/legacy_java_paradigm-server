package me.randomhashtags.worldlaws;

import java.io.File;

public interface FolderUtils {
    static String getFolder(FileType type) {
        final String folderName = type.getFolderName(true);
        return "downloaded_pages" + (folderName != null ? File.separator + folderName : "");
    }
}
