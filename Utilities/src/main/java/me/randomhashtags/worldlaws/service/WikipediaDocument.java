package me.randomhashtags.worldlaws.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class WikipediaDocument {

    private final Document document;

    public WikipediaDocument(Document document) {
        this.document = document;
    }

    public Elements select(String cssQuery) {
        return document.select(cssQuery);
    }
    public Element selectFirst(String cssQuery) {
        final Elements elements = select(cssQuery);
        return elements.first();
    }

    public Element getInfobox() {
        return selectFirst("table.infobox");
    }
    public Element getSideBar() {
        return selectFirst("table.sidebar");
    }

    public List<Node> getFirstElementsOfTagName(String tagName) {
        final Element output = selectFirst("body div div div div.mw-parser-output");
        if(output != null) {
            final List<Node> wikiElements = new ArrayList<>(output.childNodes());
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
}
