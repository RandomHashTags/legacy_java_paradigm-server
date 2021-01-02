package me.randomhashtags.worldlaws;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

public interface Jsoupable {
    String FILE_SEPARATOR = File.separator;

    private static String fixURL(String url) {
        return url.replace("/", "-").replace(".", "_").replace(":", "-");
    }
    private static String getFolder() {
        return "downloaded_pages";
    }
    private static Document getLocalDocument(String url) throws Exception {
        final String currentPath = System.getProperty("user.dir") + FILE_SEPARATOR;

        final String folder = currentPath + getFolder();
        final File folderFile = new File(folder);
        if(!folderFile.exists()) {
            Files.createDirectory(folderFile.toPath());
        }

        final String directory = folder + FILE_SEPARATOR + fixURL(url) + ".txt";
        final File file = new File(directory);
        if(file.exists()) {
            final Path path = file.toPath();
            final List<String> lines = Files.readAllLines(path);
            final StringBuilder builder = new StringBuilder();
            for(String line : lines) {
                builder.append(line);
            }
            return Jsoup.parse(builder.toString());
        }
        return null;
    }
    private static void createDocument(String url, Document doc) throws Exception {
        final String currentPath = System.getProperty("user.dir") + FILE_SEPARATOR;

        final String folder = currentPath + getFolder();
        final String directory = folder + FILE_SEPARATOR + fixURL(url) + ".txt";
        final File file = new File(directory);
        if(!file.exists()) {
            final Path path = file.toPath();
            WLLogger.log(Level.INFO, "Jsoupable - creating document at path " + path.toAbsolutePath().toString());
            final String html = doc.html();
            Files.writeString(path, html, StandardCharsets.UTF_8);
        } else {
            WLLogger.log(Level.WARNING, "Jsoupable - createDocument(" + url + ") - already exists at " + directory + "!");
        }
    }

    default Document getDocument(String url) {
        return getStaticDocument(url);
    }
    static Document getStaticDocument(String url) {
        try {
            final Document local = getLocalDocument(url);
            if(local != null) {
                return local;
            }
            final Document doc = Jsoup.connect(url).get();
            createDocument(url, doc);
            return doc;
        } catch (Exception e) {
            WLLogger.log(Level.WARNING, "Jsoupable - getStaticDocument(" + url + ") - error getting document!");
            return null;
        }
    }
}
