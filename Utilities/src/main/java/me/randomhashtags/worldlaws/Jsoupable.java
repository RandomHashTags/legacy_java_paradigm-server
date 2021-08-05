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

    private static String fixURL(String url) {
        return url.replace("/", "-").replace(".", "_").replace(":", "-");
    }
    static Document getLocalDocument(Folder folder, String url) {
        final String fileSeparator = File.separator;
        final String folderPath = folder.getFolderPath();
        final File folderFile = new File(folderPath);
        if(!folderFile.exists()) {
            try {
                Files.createDirectory(folderFile.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final String directory = folderPath + fileSeparator + fixURL(url) + ".txt";
        final File file = new File(directory);
        if(file.exists()) {
            try {
                return Jsoup.parse(file, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private static void createDocument(Folder folder, String url, String html) throws Exception {
        final String fileSeparator = File.separator;
        final String folderPath = folder.getFolderPath();
        final String directory = folderPath + fileSeparator + fixURL(url) + ".txt";
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
        return string != null && !string.isEmpty() ? string.replaceAll("\\[.*?]", "") : string;
    }

    default Document getDocument(String url) {
        return getDocument(Folder.OTHER, url, false);
    }
    default Document getDocument(Folder folder, String url) {
        return getDocument(folder, url, false);
    }
    default Document getDocument(Folder folder, String url, boolean download) {
        return getStaticDocument(folder, url, download);
    }
    default Document getDocument(Folder folder, String fileName, String url, boolean download) {
        return getStaticDocument(folder, fileName, url, download);
    }
    default Elements getDocumentElements(Folder folder, String url, String targetElements) {
        return Jsoupable.getStaticDocumentElements(folder, url, false, targetElements, -1);
    }
    default Elements getDocumentElements(Folder folder, String url, String targetElements, int index) {
        return Jsoupable.getStaticDocumentElements(folder, url, false, targetElements, index);
    }
    default Elements getDocumentElements(Folder folder, String url, boolean download, String targetElements) {
        return Jsoupable.getStaticDocumentElements(folder, url, download, targetElements, -1);
    }
    default Elements getDocumentElements(Folder folder, String fileName, String url, boolean download, String targetElements) {
        return Jsoupable.getStaticDocumentElements(folder, fileName, url, download, targetElements, -1);
    }
    default Elements getDocumentElements(Folder folder, String url, boolean download, String targetElements, int index) {
        return Jsoupable.getStaticDocumentElements(folder, url, download, targetElements, index);
    }
    default Elements getDocumentElements(Folder folder, String fileName, String url, boolean download, String targetElements, int index) {
        return Jsoupable.getStaticDocumentElements(folder, fileName, url, download, targetElements, index);
    }
    static Elements getStaticDocumentElements(Folder folder, String url, boolean download, String targetElements) {
        return getStaticDocumentElements(folder, url, download, targetElements, -1);
    }
    static Elements getStaticDocumentElements(Folder folder, String url, boolean download, String targetElements, int index) {
        return getStaticDocumentElements(folder, null, url, download, targetElements, index);
    }
    static Elements getStaticDocumentElements(Folder folder, String fileName, String url, boolean download, String targetElements, int index) {
        final Document local = getLocalDocument(folder, fileName != null ? fileName : url);
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
        try {
            final boolean hasIndex = index != -1;
            final Document doc = requestDocument(url);
            if(doc != null) {
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
                    createDocument(folder, fileName != null ? fileName : url, html);
                }
                return hasIndex ? element.getAllElements() : elements;
            }
            return null;
        } catch (Exception e) {
            WLLogger.log(Level.WARN, "Jsoupable - fileName=\"" + fileName + "\", getStaticDocumentElements(" + url + ") - error getting document elements! (" + e.getLocalizedMessage() + ")");
            return null;
        }
    }
    static Document getStaticDocument(Folder folder, String url, boolean download) {
        return getStaticDocument(folder, null, url, download);
    }
    static Document getStaticDocument(Folder folder, String fileName, String url, boolean download) {
        final Document local = getLocalDocument(folder, fileName != null ? fileName : url);
        if(local != null) {
            return local;
        }
        try {
            final Document doc = requestDocument(url);
            if(download) {
                final String html = doc.html();
                createDocument(folder, fileName != null ? fileName : url, html);
            }
            return doc;
        } catch (Exception e) {
            WLLogger.log(Level.WARN, "Jsoupable - fileName=\"" + fileName + "\", getStaticDocument(" + url + ") - download=" + download + " - error getting document! (" + e.getLocalizedMessage() + ")");
            return null;
        }
    }
    private static Document requestDocument(String url) throws Exception {
        WLLogger.log(Level.INFO, "Jsoupable - making request to \"" + url + "\"");
        return Jsoup.connect(url).get();
    }
}
