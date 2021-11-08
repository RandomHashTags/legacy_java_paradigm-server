package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.CompletionHandlerLaws;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class Delaware extends TestLawSubdivisionController {
    public static final Delaware INSTANCE = new Delaware(
            "https://delcode.delaware.gov",
            "https://delcode.delaware.gov/title%index%/index.html",
            "https://delcode.delaware.gov/title%index%/c%chapter%/index.html",
            "https://delcode.delaware.gov/title%index%/c%chapter%/index.html"
    );

    Delaware(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.DELAWARE;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public void loadStatutesList(String index, String chapter, CompletionHandlerLaws handler) {
        chapter = prefixZeros(chapter, 3);
        super.loadStatutesList(index, chapter, handler);
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = isIndex || isChapter ? "div.title-links" : "div div.SectionHead";
            final Elements table = doc.select(tableID);
            if(!table.isEmpty()) {
                if(isIndex) {
                    table.remove(0);
                }
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                final String key = isIndex ? "Title " : isChapter ? "Chapter " : null;
                for(Element row : table) {
                    final TestStatuteAbstract statuteAbstract;
                    if(isStatute) {
                        final String id = row.attr("id").replace(".", "");
                        final String title = row.text().split(id + "\\. ")[1];
                        statuteAbstract = new TestStatuteStatute(id, title);
                    } else {
                        final Element href = row.selectFirst("a[href]");
                        final String[] splitValues = href.text().split("\\. ");
                        final String id = splitValues[0].substring(key.length()), title = splitValues[1];
                        statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : isChapter ? new TestStatuteChapter(id, title) : null;
                    }
                    if(statuteAbstract != null) {
                        values.add(statuteAbstract);
                    }
                }
                return values;
            }
        }
        return null;
    }

    @Override
    public void loadStatute(String index, String chapter, String section, CompletionHandlerLaws handler) {
        chapter = prefixZeros(chapter, 3);
        final String url = statuteURL.replace("%index%", index).replace("%chapter%", chapter);
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements allElements = doc.getAllElements();
            final Elements elements = doc.select("div.SectionHead"), elementsCopy = new Elements(elements), paragraphs = doc.select("p");
            elements.removeIf(element -> !element.attr("id").replace(".", "").equals(section));
            final Element element = elements.get(0);
            int indexOfTargetElement = elementsCopy.indexOf(element);
            final int nextElementIndex = allElements.indexOf(elementsCopy.get(indexOfTargetElement+1));
            indexOfTargetElement = allElements.indexOf(element);
            WLLogger.logInfo("Delaware;indexOfTargetElement=" + indexOfTargetElement + ";nextElementIndex=" + nextElementIndex);

            final List<Element> subsections = new ArrayList<>();
            for(Element paragraph : paragraphs) {
                final int indexOfParagraph = allElements.indexOf(paragraph);
                WLLogger.logInfo("Delaware;indexOfParagraph=" + indexOfParagraph);
                if(indexOfParagraph > indexOfTargetElement && indexOfParagraph < nextElementIndex) {
                    subsections.add(paragraph);
                }
            }
            String description = null;
            List<TestSubdivision> subdivisions = null;
            if(subsections.size() == 1) {
                description = subsections.get(0).text();
            } else {
                subdivisions = new ArrayList<>();
                TestSubdivision previousSubdivision = null;
                for(Element subsection : subsections) {
                    final String text = subsection.text();
                    if(previousSubdivision != null && text.substring(0, 3).matches("\\([0-9]\\)")) {
                        final TestSubdivision subdivision = new TestSubdivision(text.substring(1, 2), text.substring(4));
                        previousSubdivision.addSubdivision(subdivision);
                    } else {
                        if(previousSubdivision != null) {
                            subdivisions.add(previousSubdivision);
                        }
                        previousSubdivision = new TestSubdivision(text.substring(1, 2), text.substring(4));
                    }
                }
                if(previousSubdivision != null && !subdivisions.contains(previousSubdivision)) {
                    subdivisions.add(previousSubdivision);
                }
            }

            final EventSources sources = new EventSources();
            sources.append(new EventSource("Delaware Legislature: Statute Page", url));

            final TestStatute statute = new TestStatute(element.text().substring(section.length()+4), description, subdivisions, sources);
            handler.handleStatute(statute);
            return;
        }
        handler.handleStatute(null);
    }

    /*
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        chapter = prefixZeros(chapter, 3);
        final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("article p.section-label")) {
                final String text = element.text().substring(2);
                final String[] values = text.split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1];
                list.add(new Element(statute).appendChild(new TextNode(statute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = chapter + "." + section;
        chapter = prefixZeros(chapter, 3);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements article = doc.select("article"), labels = article.select("p.section-label");
                final HashSet<String> set = new HashSet<>();
                for(Element label : labels) {
                    set.add(label.text());
                }
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true, foundTitle = false;
                for(Element element : article.select("p")) {
                    final String text = element.text();
                    if(set.contains(text)) {
                        if(foundTitle) {
                            break;
                        }
                        foundTitle = text.substring(2).startsWith(section);
                    } else if(foundTitle) {
                        description.append(isFirst ? "" : "\n").append(text);
                        isFirst = false;
                    }
                }
                final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), null, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }*/
}
