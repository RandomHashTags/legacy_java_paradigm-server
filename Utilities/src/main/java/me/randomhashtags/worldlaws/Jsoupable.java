package me.randomhashtags.worldlaws;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
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
            /*final Path path = file.toPath();
            final List<String> lines = Files.readAllLines(path);
            final StringBuilder builder = new StringBuilder();
            for(String line : lines) {
                builder.append(line);
            }*/
            return Jsoup.parse(file, null);
        }
        return null;
    }
    private static void createDocument(String url, String html) throws Exception {
        final String currentPath = System.getProperty("user.dir") + FILE_SEPARATOR;

        final String folder = currentPath + getFolder();
        final String directory = folder + FILE_SEPARATOR + fixURL(url) + ".txt";
        final File file = new File(directory);
        if(!file.exists()) {
            final Path path = file.toPath();
            WLLogger.log(Level.INFO, "Jsoupable - creating document at path " + path.toAbsolutePath().toString());
            Files.writeString(path, html, StandardCharsets.UTF_8);
        } else {
            WLLogger.log(Level.WARNING, "Jsoupable - createDocument(" + url + ") - already exists at " + directory + "!");
        }
    }

    default Document getDocument(String url) {
        return getStaticDocument(url);
    }
    default Elements getDocumentElements(String url, String targetElements) {
        return Jsoupable.getStaticDocumentElements(url, targetElements, -1);
    }
    default Elements getDocumentElements(String url, String targetElements, int index) {
        return Jsoupable.getStaticDocumentElements(url, targetElements, index);
    }
    static Elements getStaticDocumentElements(String url, String targetElements) {
        return getStaticDocumentElements(url, targetElements, -1);
    }
    static Elements getStaticDocumentElements(String url, String targetElements, int index) {
        try {
            final Document local = getLocalDocument(url);
            final boolean isList = targetElements.endsWith(" li"), isTR = targetElements.endsWith(" tr");
            if(local != null) {
                final boolean isUL = targetElements.endsWith(" ul"), isTableWikitable = targetElements.endsWith(" table.wikitable"), isTableSortable = targetElements.endsWith(" table.sortable");
                return isList ? local.select("li")
                        : isUL ? local.select("ul")
                        : isTableWikitable ? local.select("table.wikitable")
                        : isTableSortable ? local.select("table.sortable")
                        : isTR ? local.select("tr")
                        : local.getAllElements();
            }
            final boolean hasIndex = index != -1;
            final Document doc = Jsoup.connect(url).get();
            final Elements elements = doc.select(targetElements);
            final String html;
            Element element = hasIndex ? elements.get(index) : null;
            if(!hasIndex) {
                final String outerHtml = elements.outerHtml();
                final String prefix = isList ? "<ul>\n"
                        : isTR ? "<table>\n<tbody>\n"
                        : "";
                final String suffix = isList ? "\n</ul>"
                        : isTR ? "\n</tbody>\n</table>"
                        : "";
                html = prefix + outerHtml + suffix;
            } else {
                html = element.outerHtml();
            }
            createDocument(url, html);
            return hasIndex ? element.getAllElements() : elements;
        } catch (Exception e) {
            WLLogger.log(Level.WARNING, "Jsoupable - getStaticDocumentElements(" + url + ") - error getting document elements! (" + e.getLocalizedMessage() + ")");
            return null;
        }
    }
    static Document getStaticDocument(String url) {
        try {
            final Document local = getLocalDocument(url);
            if(local != null) {
                return local;
            }
            final Document doc = Jsoup.connect(url).get();
            final String html = doc.html();
            createDocument(url, html);
            return doc;
        } catch (Exception e) {
            WLLogger.log(Level.WARNING, "Jsoupable - getStaticDocument(" + url + ") - error getting document!");
            return null;
        }
    }
}
