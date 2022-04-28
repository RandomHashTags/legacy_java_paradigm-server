package me.randomhashtags.worldlaws.service.wikipedia;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public final class WikipediaDocument {

    private final String url;
    private final Document document;

    public WikipediaDocument(String url) {
        this(Folder.OTHER, url, false);
    }
    public WikipediaDocument(Folder folder, String url, boolean download) {
        this.url = url;
        document = Jsoupable.getStaticDocument(folder, url, download);
    }
    public WikipediaDocument(String url, Document document) {
        this.url = url;
        this.document = document;
    }

    public String getPageName() {
        final String[] values = url.split("/wiki/");
        return values.length > 1 ? values[1].replace("_", " ").replace("%27", "'") : null;
    }
    public EventSource getEventSource() {
        return new EventSource("Wikipedia: " + getPageName(), url);
    }
    public Document getDocument() {
        return document;
    }
    public boolean exists() {
        return document != null;
    }

    public Elements select(String cssQuery) {
        return document.select(cssQuery);
    }
    public Element selectFirst(String cssQuery) {
        return select(cssQuery).first();
    }

    public Element getInfobox() {
        return selectFirst("table.infobox");
    }
    public Element getSideBar() {
        return selectFirst("table.sidebar");
    }

    public List<Element> getAllParagraphs() {
        final List<Element> paragraphs = getFirstElementsOfTagName("p");
        if(paragraphs != null) {
            paragraphs.removeIf(paragraph -> paragraph.hasAttr("class") && paragraph.attr("class").equals("mw-empty-elt"));
        }
        return paragraphs;
    }
    public List<Element> getConsecutiveParagraphs() {
        return getConsecutiveNodes(paragraph -> {
            final Elements span = paragraph.select("span span.plainlinks span.geo-nondefault");
            return paragraph.hasAttr("class") && paragraph.attr("class").equals("mw-empty-elt") || !span.isEmpty();
        }, "p");
    }
    public String getDescription() {
        final List<Element> paragraphs = getConsecutiveParagraphs();
        return !paragraphs.isEmpty() ? paragraphs.get(0).text() : null;
    }

    private List<Element> getConsecutiveNodes(Predicate<? super Element> removeIf, String tagName) {
        final List<Element> targetNodes = getFirstElementsOfTagName(tagName);
        if(targetNodes != null) {
            targetNodes.removeIf(removeIf);
            final List<Element> nodes = new ArrayList<>();
            int previousIndex = -1;
            for(Element node : targetNodes) {
                if(previousIndex == -1) {
                    previousIndex = node.siblingIndex();
                    nodes.add(node);
                } else {
                    final int index = node.siblingIndex();
                    if(previousIndex+1 == index) {
                        nodes.add(node);
                        previousIndex = index;
                    } else {
                        break;
                    }
                }
            }
            return nodes;
        }
        return null;
    }

    public List<Element> getFirstElementsOfTagName(String tagName) {
        final Element output = selectFirst("body div div div div.mw-parser-output");
        if(output != null) {
            final List<Node> children = new ArrayList<>(output.childNodes());
            children.removeIf(child -> !(child instanceof Element));
            final List<Element> wikiElements = new ArrayList<>();
            for(Node node : children) {
                final Element wikiElement = (Element) node;
                if(wikiElement.tagName().equals(tagName)) {
                    wikiElements.add(wikiElement);
                }
            }
            return wikiElements;
        }
        return null;
    }

    public List<String> getImages() {
        final List<String> images = new ArrayList<>();
        final Element infobox = getInfobox();
        final String infoboxURL = getImageURLFrom(infobox);
        if(infoboxURL != null) {
            images.add(infoboxURL);
        }

        final Element sidebar = getSideBar();
        final String sidebarURL = getImageURLFrom(sidebar);
        if(sidebarURL != null && !images.contains(sidebarURL)) {
            images.add(sidebarURL);
        }

        final String documentImageURL = getImageURLFrom(document);
        if(documentImageURL != null && !images.contains(documentImageURL)) {
            images.add(documentImageURL);
        }
        images.removeIf(img -> {
            return img.endsWith("static/images/footer/wikimedia-button.png")
                    || img.contains("static/images/footer/poweredby_mediawiki_")
                    || img.endsWith("px-Question_book-new.svg.png")
                    || img.endsWith("px-OOjs_UI_icon_edit-ltr-progressive.svg.png")
                    ;
        });
        return images;
    }
    private String getImageURLFrom(Element element) {
        if(element != null) {
            Element ahref = element.selectFirst("a[href]");
            if(ahref != null) {
                final Element parent = ahref.parent();
                ahref = parent != null && parent.tagName().equals("div") && parent.hasClass("frb") ? null : ahref;
            }
            if(ahref != null) {
                final Elements images = ahref.select("img");
                return !images.isEmpty() ? "https:" + WikipediaService.getPictureThumbnailImageURL(images.get(0)) : null;
            }
        }
        return null;
    }

    public HashMap<String, EventSource> getReferences(String identifier) {
        if(!exists()) {
            return null;
        }
        final Element reflistElement = document.selectFirst("div.reflist");;
        final HashMap<String, EventSource> references = new HashMap<>();
        if(reflistElement != null) {
            final Elements listElements = reflistElement.select("ol.references li");
            new CompletableFutures<Element>().stream(listElements, listElement -> {
                final String[] numberValues = listElement.attr("id").split("-");
                final String number = numberValues[numberValues.length-1];
                final Element referenceTextElement = listElement.selectFirst("span.reference-text");
                Element sourceElement = referenceTextElement.selectFirst("cite.citation");
                if(sourceElement == null) {
                    sourceElement = referenceTextElement;
                }
                final Element ahref = sourceElement.selectFirst("a.external");
                if(ahref != null) {
                    final String url = ahref.attr("href");
                    String title = ahref.text();
                    if(sourceElement.text().toLowerCase().startsWith("clinical trial number")) {
                        title = "Clinical Trial Number " + title;
                    }
                    final Element italicElement = sourceElement.selectFirst("i");
                    String siteName = italicElement != null ? italicElement.text() : "Unknown Publisher";
                    if(italicElement == null) {
                        final String[] values = sourceElement.text().split("\\. ");
                        if(values.length >= 2) {
                            if(values[1].matches("[0-9]+ [a-zA-Z]+ [0-9]+")) {
                                if(values[0].contains(":")) {
                                    siteName = null;
                                } else if(values[0].contains(" - ")) {
                                    final String[] test = values[0].split(" - ");
                                    siteName = test[test.length-1];
                                }
                                if(siteName != null) {
                                    siteName = siteName.replace("\"", "");
                                }
                                WLLogger.logInfo("WikipediaDocument;getReferences;italicElement == null;identifier=" + identifier + ";number=" + number + ";values[1]=" + values[1] + ";siteName=" + siteName);
                            } else {
                                siteName = values[1];
                            }
                        }
                    }
                    final String realSiteName = (siteName != null ? siteName + ": " : "") + title;
                    final EventSource source = new EventSource(realSiteName, url);
                    references.put(number, source);
                } else {
                    WLLogger.logInfo("WikipediaDocument;getReferences;ahref == null;identifier=" + identifier + ";number=" + number);
                }
            });
        }
        return references;
    }

    public EventSources getExternalLinks() {
        if(!exists()) {
            return null;
        }
        final Element targetElement = document.selectFirst("div.mw-parser-output");
        if(targetElement == null) {
            return null;
        }
        final Elements children = targetElement.children();
        final Elements uls = children.select("ul");
        final EventSources sources = new EventSources();
        new CompletableFutures<Element>().stream(uls, lastList -> {
            final Elements elements = lastList.select("li");
            for(Element list : elements) {
                final Elements hrefs = list.select("a[href]");
                hrefs.removeIf(href -> href.attr("href").startsWith("https://www.wikidata.org/wiki/"));
                if(!hrefs.isEmpty()) {
                    final String listText = list.text();
                    final int totalLinks = hrefs.size();
                    final boolean hasAt = listText.contains(" at ");
                    final Element href = hrefs.get(0);
                    String hrefText = href.text(), hrefTextLowercase = hrefText.toLowerCase(), siteName = null;
                    switch (hrefTextLowercase) {
                        case "linkedin page":
                            siteName = hasAt ? listText : hrefText;
                            break;
                        default:
                            if(totalLinks >= 2) {
                                final String targetText = hrefs.get(1).text();
                                final String targetTextLowercase = targetText.toLowerCase();
                                final List<String> supportedSources = Settings.ServerValues.getWikipediaSupportedExternalLinkSources();
                                if(supportedSources.contains(targetTextLowercase)) {
                                    siteName = targetText + ": " + href.text();
                                }
                            }
                            if(siteName == null && (hrefTextLowercase.contains("official ")
                                    && (hrefTextLowercase.contains(" website") || hrefTextLowercase.contains(" app") || hrefTextLowercase.contains(" wiki ") || hrefTextLowercase.contains(" site") || hrefTextLowercase.contains(" webpage"))
                                    || hrefTextLowercase.contains("website"))
                            ) {
                                siteName = hrefText;
                            }
                            break;
                    }
                    if(siteName != null) {
                        final EventSource externalSource = new EventSource(siteName, href.attr("href"));
                        sources.add(externalSource);
                    }
                }
            }
        });
        return sources;
    }
}
