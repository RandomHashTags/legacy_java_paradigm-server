package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class WikipediaDocument {

    private final String url;
    private final Document document;

    public WikipediaDocument(String url) {
        this.url = url;
        document = Jsoupable.getStaticDocument(Folder.OTHER, url, false);
    }

    public String getPageName() {
        return url.split("/wiki/")[1].replace("_", " ");
    }
    public Document getDocument() {
        return document;
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

    public List<Node> getAllParagraphs() {
        final List<Node> paragraphs = getFirstElementsOfTagName("p");
        if(paragraphs != null) {
            paragraphs.removeIf(paragraph -> paragraph.hasAttr("class") && paragraph.attr("class").equals("mw-empty-elt"));
        }
        return paragraphs;
    }
    public List<Node> getConsecutiveParagraphs() {
        return getConsecutiveNodes(paragraph -> paragraph.hasAttr("class") && paragraph.attr("class").equals("mw-empty-elt"), "p");
    }

    private List<Node> getConsecutiveNodes(Predicate<? super Node> removeIf, String tagName) {
        final List<Node> targetNodes = getFirstElementsOfTagName(tagName);
        if(targetNodes != null) {
            targetNodes.removeIf(removeIf);
            final List<Node> nodes = new ArrayList<>();
            int previousIndex = -1;
            for(Node node : targetNodes) {
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

    public List<Node> getFirstElementsOfTagName(String tagName) {
        final Element output = selectFirst("body div div div div.mw-parser-output");
        if(output != null) {
            final List<Node> children = output.childNodes();
            final List<Node> wikiElements = new ArrayList<>(children);
            wikiElements.removeIf(node -> {
                if(node instanceof Element) {
                    final Element wikiElement = (Element) node;
                    return !wikiElement.tagName().equals(tagName);
                }
                return true;
            });
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
        return images.isEmpty() ? null : images;
    }
    private String getImageURLFrom(Element element) {
        final Elements images = element.select("a[href] img");
        return !images.isEmpty() ? "https:" + WikipediaService.getPictureThumbnailImageURL(images.get(0)) : null;
    }
}
