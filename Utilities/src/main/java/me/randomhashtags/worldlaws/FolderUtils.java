package me.randomhashtags.worldlaws;

import java.io.File;

public interface FolderUtils {
    static String getFolder(Folder type) {
        final String folderName = type.getFolderName(true);
        return "downloaded_pages" + (folderName != null ? File.separator + folderName : "");
    }
}
