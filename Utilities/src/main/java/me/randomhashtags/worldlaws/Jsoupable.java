package me.randomhashtags.worldlaws;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface Jsoupable {
    static String fixURL(String url) {
        return url.replace("/", "-").replace(".", "_").replace(":", "-");
    }
    static Document getLocalDocument(Folder folder, String url) {
        if(folder != Folder.OTHER) {
            final String folderPath = folder.getFullFolderPath(url);
            final String directory = folderPath + File.separator + fixURL(url) + ".txt";
            final Path path = Paths.get(directory);
            Jsonable.tryCreatingParentFolders(path);

            final File file = new File(directory);
            if(Files.exists(path)) {
                try {
                    return Jsoup.parse(file, null);
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
            }
        }
        return null;
    }
    private static void createDocument(Folder folder, String fileName, String html) {
        final String fileSeparator = File.separator;
        final String folderPath = folder.getFullFolderPath(fileName);
        final String directory = folderPath + fileSeparator + fixURL(fileName) + ".txt";
        final Path path = Paths.get(directory);
        if(!Files.exists(path)) {
            WLLogger.logInfo("Jsoupable - creating document at path " + path.toAbsolutePath().toString());
            Jsonable.tryCreatingParentFolders(path);
            try {
                Files.writeString(path, html, StandardCharsets.UTF_8);
            } catch (Exception e) {
                WLLogger.logError("Jsoupable", "createDocument - error writing to file \"" + fileName + "\" at \"" + directory + "\"!");
                WLUtilities.saveException(e);
            }
        } else {
            WLLogger.logError("Jsoupable", "createDocument - file \"" + fileName + "\" already exists at " + directory + "!");
        }
    }

    default Document getDocument(String url) {
        return getDocument(Folder.OTHER, url, false);
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
    }
    static Document getStaticDocument(Folder folder, String url, boolean download) {
        return getStaticDocument(folder, null, url, download);
    }
    static Document getStaticDocument(Folder folder, String fileName, String url, boolean download) {
        final String realFileName = fileName != null ? fileName : url;
        final Document local = getLocalDocument(folder, realFileName);
        if(local != null) {
            return local;
        }
        final Document doc = requestDocument(url);
        if(doc != null && download) {
            final String html = doc.html();
            createDocument(folder, realFileName, html);
        }
        return doc;
    }
    private static Document requestDocument(String url) {
        try {
            //WLLogger.logInfo("Jsoupable - requesting document for url \"" + url + "\"");
            return WLUtilities.getJsoupDocumentFrom(url);
        } catch (Exception e) {
            WLLogger.logWarning("Jsoupable - requestDocument(" + url + ") - error getting document! (" + e.getClass().getSimpleName() + ", " + e.getLocalizedMessage() + ")");
            WLUtilities.saveException(e);
            return null;
        }
    }
}
