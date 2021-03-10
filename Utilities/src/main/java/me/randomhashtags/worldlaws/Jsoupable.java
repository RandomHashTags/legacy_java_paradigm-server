package me.randomhashtags.worldlaws;

import org.apache.logging.log4j.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public interface Jsoupable {
    String FILE_SEPARATOR = File.separator;

    private static String fixURL(String url) {
        return url.replace("/", "-").replace(".", "_").replace(":", "-");
    }
    private static String getFolder(FileType type) {
        final String folderName = type.getFolderName();
        return "downloaded_pages" + (folderName != null ? FILE_SEPARATOR + folderName : "");
    }
    public static Document getLocalDocument(FileType type, String url) throws Exception {
        final String currentPath = System.getProperty("user.dir") + FILE_SEPARATOR;

        final String folder = currentPath + getFolder(type);
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
    private static void createDocument(FileType type, String url, String html) throws Exception {
        final String currentPath = System.getProperty("user.dir") + FILE_SEPARATOR;

        final String folder = currentPath + getFolder(type);
        final String directory = folder + FILE_SEPARATOR + fixURL(url) + ".txt";
        final File file = new File(directory);
        if(!file.exists()) {
            final Path path = file.toPath();
            WLLogger.log(Level.INFO, "Jsoupable - creating document at path " + path.toAbsolutePath().toString());
            Files.writeString(path, html, StandardCharsets.UTF_8);
        } else {
            WLLogger.log(Level.WARN, "Jsoupable - createDocument(" + url + ") - already exists at " + directory + "!");
        }
    }

    default String removeReferences(String string) {
        if(string != null && !string.isEmpty()) {
            string = string.replaceAll("\\[.*?]", "");
        }
        return string;
    }

    default Document getDocument(String url) {
        return getDocument(null, url, false);
    }
    default Document getDocument(FileType type, String url) {
        return getDocument(type, url, false);
    }
    default Document getDocument(FileType type, String url, boolean download) {
        return getStaticDocument(type, url, download);
    }
    default Elements getDocumentElements(FileType type, String url, String targetElements) {
        return Jsoupable.getStaticDocumentElements(type, url, false, targetElements, -1);
    }
    default Elements getDocumentElements(FileType type, String url, String targetElements, int index) {
        return Jsoupable.getStaticDocumentElements(type, url, false, targetElements, index);
    }
    default Elements getDocumentElements(FileType type, String url, boolean download, String targetElements) {
        return Jsoupable.getStaticDocumentElements(type, url, download, targetElements, -1);
    }
    default Elements getDocumentElements(FileType type, String url, boolean download, String targetElements, int index) {
        return Jsoupable.getStaticDocumentElements(type, url, download, targetElements, index);
    }
    static Elements getStaticDocumentElements(FileType type, String url, boolean download, String targetElements) {
        return getStaticDocumentElements(type, url, download, targetElements, -1);
    }
    static Elements getStaticDocumentElements(FileType type, String url, boolean download, String targetElements, int index) {
        try {
            final Document local = getLocalDocument(type, url);
            final boolean isList = targetElements.endsWith(" li"), isTR = targetElements.endsWith(" tr");
            if(local != null) {
                final boolean isUL = targetElements.endsWith(" ul");
                final boolean isTableWikitable = targetElements.equals("table.wikitable") || targetElements.endsWith(" table.wikitable");
                final boolean isTableSortable = targetElements.equals("table.sortable") || targetElements.endsWith(" table.sortable");
                return isList ? local.select("li")
                        : isUL ? local.select("ul")
                        : isTableWikitable ? local.select("table.wikitable")
                        : isTableSortable ? local.select("table.sortable")
                        : isTR ? local.select("tr")
                        : local.getAllElements();
            }
            final boolean hasIndex = index != -1;
            final Document doc = requestDocument(url);
            final Elements elements = doc.select(targetElements);
            Element element = hasIndex ? elements.get(index) : null;
            if(download) {
                final String html;
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
                createDocument(type, url, html);
            }
            return hasIndex ? element.getAllElements() : elements;
        } catch (Exception e) {
            WLLogger.log(Level.WARN, "Jsoupable - getStaticDocumentElements(" + url + ") - error getting document elements! (" + e.getLocalizedMessage() + ")");
            return null;
        }
    }
    static Document getStaticDocument(FileType type, String url, boolean download) {
        try {
            final Document local = getLocalDocument(type, url);
            if(local != null) {
                return local;
            }
            final Document doc = requestDocument(url);
            if(download) {
                final String html = doc.html();
                createDocument(type, url, html);
            }
            return doc;
        } catch (Exception e) {
            WLLogger.log(Level.WARN, "Jsoupable - getStaticDocument(" + url + ") - download=" + download + " - error getting document!");
            return null;
        }
    }
    private static Document requestDocument(String url) throws Exception {
        WLLogger.log(Level.INFO, "Jsoupable - making request to \"" + url + "\"");
        return Jsoup.connect(url).get();
    }
}
