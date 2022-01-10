package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
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
        return url.split("/wiki/")[1].replace("_", " ").replace("%27", "'");
    }
    public EventSource getEventSource() {
        return new EventSource("Wikipedia: " + getPageName(), url);
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
            final Elements images = element.select("a[href] img");
            return !images.isEmpty() ? "https:" + WikipediaService.getPictureThumbnailImageURL(images.get(0)) : null;
        }
        return null;
    }

    public EventSources getExternalLinks() {
        final EventSources sources = new EventSources();
        final Elements elements = document.select("div.mw-parser-output > *");
        final Elements headlines = elements.select("h2");
        headlines.removeIf(headline -> !headline.select("span.mw-headline").text().equalsIgnoreCase("External links"));
        if(!headlines.isEmpty()) {
            final Element headline = headlines.get(0);
            final int indexOfExternalLinks = headline.elementSiblingIndex();
            if(indexOfExternalLinks != -1) {
                for(int i = 1; i <= indexOfExternalLinks; i++) {
                    elements.remove(0);
                }
                final Elements uls = elements.select("ul");
                int index = 0;
                for(Element ul : uls) {
                    if(!ul.hasAttr("style")) {
                        break;
                    }
                    index += 1;
                }
                final Element ul = uls.get(index);
                final Elements lists = ul.select("li");
                for(Element list : lists) {
                    final Elements hrefs = list.select("a[href]");
                    hrefs.removeIf(href -> href.attr("href").startsWith("https://www.wikidata.org/wiki/"));

                    if(!hrefs.isEmpty()) {
                        final String listText = list.text();
                        final int totalLinks = hrefs.size();
                        final boolean hasAt = listText.contains(" at ");
                        EventSource externalSource = null;
                        final Element href = hrefs.get(0);
                        String hrefText = href.text(), hrefTextLowercase = hrefText.toLowerCase();
                        switch (hrefTextLowercase) {
                            case "official website":
                            case "official uk website":
                            case "linkedin page":
                                externalSource = new EventSource(hasAt ? listText : hrefText, href.attr("href"));
                                break;
                            default:
                                if(totalLinks >= 2) {
                                    hrefText = hrefs.get(1).text();
                                    hrefTextLowercase = hrefText.toLowerCase();
                                    switch (hrefTextLowercase) {
                                        case "adult swim":
                                        case "imdb":
                                        case "disney+":
                                        case "facebook":
                                        case "allmovie":
                                        case "history vs. hollywood":
                                        case "netflix":
                                        case "rotten tomatoes":
                                        case "metacritic":
                                        case "box office mojo":
                                        case "the big cartoon database":
                                        case "twitter":
                                        case "youtube":
                                        case "instagram":
                                            externalSource = new EventSource(hrefText + ": " + href.text(), href.attr("href"));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                break;
                        }
                        if(externalSource != null) {
                            sources.add(externalSource);
                        }
                    }
                }
            }
        }
        return sources;
    }
}
