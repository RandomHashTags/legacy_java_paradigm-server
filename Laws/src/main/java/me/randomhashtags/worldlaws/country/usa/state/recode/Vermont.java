package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.CompletionHandlerLaws;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public final class Vermont extends TestLawSubdivisionController {
    public static final Vermont INSTANCE = new Vermont(
            "https://legislature.vermont.gov/statutes/",
            "https://legislature.vermont.gov/statutes/title/%index%",
            "https://legislature.vermont.gov/statutes/chapter/%index%/%chapter%",
            "https://legislature.vermont.gov/statutes/section/%index%/%chapter%/%section%"
    );

    Vermont(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.VERMONT;
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
            final String tableID = "div ul.item-list li";
            final Elements table = doc.select(tableID);
            if(!table.isEmpty()) {
                String realChapter = chapter;
                if(isStatute) {
                    for(int i = 1; i <= 2; i++) {
                        if(realChapter.startsWith("0")) {
                            realChapter = realChapter.substring(1);
                        }
                    }
                }
                final String key = isIndex ? "Title " : isChapter ? "Chapter " : "App. § " + realChapter + "-";
                final String splitRegex = isIndex || isChapter ? ": " : "\\. ";
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                if(isStatute) {
                    table.remove(0);
                }
                for(Element row : table) {
                    final Element href = row.selectFirst("a[href]");
                    if(href != null) {
                        final String text = href.text();
                        final String[] textValues = text.split(splitRegex);
                        final String zero = textValues[0];
                        String id = null;
                        final String title;
                        if(isIndex || isChapter) {
                            id = zero.substring(key.length()).replace(" ", "").toUpperCase();
                            if(isChapter) {
                                for(int i = 1; i <= 2; i++) {
                                    if(id.startsWith("0")) {
                                        id = id.substring(1);
                                    }
                                }
                            }
                            title = text.substring(zero.length()+2);
                        } else {
                            final String one = textValues[1];
                            id = one.substring(realChapter.length()+3);
                            title = text.substring(zero.length()+one.length()+splitRegex.length());
                        }
                        final TestStatuteAbstract statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : isChapter ? new TestStatuteChapter(id, title) : isStatute ? new TestStatuteStatute(id, title) : null;
                        if(statuteAbstract != null) {
                            values.add(statuteAbstract);
                        }
                    }
                }
                return values;
            }
        }
        return null;
    }

    @Override
    public void loadStatute(String index, String chapter, String section, CompletionHandlerLaws handler) {
        final String originalChapter = chapter;
        chapter = prefixZeros(chapter, 3);
        section = prefixZeros(section, 5);
        final String url = statuteURL.replace("%index%", index).replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements elements = doc.select("ul.item-list p.MsoPlainText");
            final String topic = elements.get(0).text().substring(originalChapter.length()+5);
            elements.remove(0);
            final StringBuilder description = new StringBuilder();
            boolean isFirst = true;
            for(Element element : elements) {
                description.append(isFirst ? "" : "\n").append(element.text());
                isFirst = false;
            }

            final EventSources sources = new EventSources();
            sources.add(new EventSource("Vermont Legislature: Statute Page", url));
            statute = new TestStatute(topic, description.toString(), null, sources);
        }
        handler.handleStatute(statute);
    }

    /*
    private void addElement(List<Element> list, String text) {
        final String[] values = text.split(":");
        final String title = values[0], value = text.split(title + ": ")[1];
        list.add(new Element(title).appendChild(new TextNode(title)));
        list.add(new Element(value).appendChild(new TextNode(value)));
    }
    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements ul = doc.select("ul.item-list");
            final List<Element> list = new ArrayList<>();

            final Element firstTitle = ul.select("a[href]").get(0);
            addElement(list, firstTitle.text());
            final Elements table = ul.select("li");
            for(Element element : table) {
                addElement(list, element.text());
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        title = prefixZeros(title, 2);
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("ul.item-list li a[href]")) {
                final String text = element.text().substring(8);
                final String[] values = text.split(":");
                final String chapter = values[0], value = text.split(chapter + ": ")[1], correctedChapter = chapter.substring(chapter.startsWith("00") ? 2 : chapter.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        title = prefixZeros(title, 2);
        chapter = prefixZeros(chapter, 3);
        final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.main ul li + li")) {
                final String text = element.text();
                final String[] values = text.substring(2+(text.startsWith("§§") ? 1 : 0)).split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1];
                list.add(new Element(statute).appendChild(new TextNode(statute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        final int substring = section.length();
        title = prefixZeros(title, 2);
        chapter = prefixZeros(chapter, 3);
        section = prefixZeros(section, 5);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements elements = doc.select("ul.item-list p.MsoPlainText");
                final String topic = elements.get(0).text().substring(4+substring);
                elements.remove(0);
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : elements) {
                    description.append(isFirst ? "" : "\n").append(element.text());
                    isFirst = false;
                }
                final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }*/
}
