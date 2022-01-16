package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public final class Washington extends TestLawSubdivisionController {
    public static final Washington INSTANCE = new Washington(
            "https://apps.leg.wa.gov/rcw/",
            "https://apps.leg.wa.gov/rcw/default.aspx?Cite=%index%",
            "https://app.leg.wa.gov/RCW/default.aspx?cite=%index%.%chapter%",
            "https://app.leg.wa.gov/RCW/default.aspx?cite=%index%.%chapter%.%section%"
    );

    Washington(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.WASHINGTON;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public HashSet<? extends TestStatuteStatute> loadStatutesList(String index, String chapter) {
        chapter = prefixZeros(chapter, 2);
        return super.loadStatutesList(index, chapter);
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final Elements table = doc.select("div.noscroll div table tbody tr");
            if(!table.isEmpty()) {
                final String key = isIndex ? "Title " : isChapter ? index + "." : index + "." + chapter + ".";
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    final Elements tds = row.select("td");
                    if(tds.size() == 2) {
                        String id = tds.get(0).text().substring(key.length());
                        for(int i = 1; i <= 2; i++) {
                            if(id.startsWith("0") && (isChapter || isStatute)) {
                                id = id.substring(1);
                            }
                        }
                        final String title = tds.get(1).text();
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
    public TestStatute loadStatute(String index, String chapter, String section) {
        chapter = prefixZeros(chapter, 2);
        section = prefixZeros(section, 3);
        final String url = statuteURL.replace("%index%", index).replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements noscroll = doc.select("div.noscroll div");
            for(Element element : noscroll) {
                if(element.hasAttr("id") && element.attr("id").equals("contentWrapper")) {
                    final String topic = LocalServer.fixEscapeValues(element.select("h3").get(1).text());
                    final Elements targetElements = element.select("div");
                    for(int i = 1; i <= 3; i++) {
                        targetElements.remove(0);
                    }
                    final StringBuilder description = new StringBuilder();
                    boolean isFirst = true;
                    for(Element e : targetElements) {
                        description.append(isFirst ? "" : "\n").append(e.text());
                        isFirst = false;
                    }
                    final EventSources sources = new EventSources();
                    sources.add(new EventSource("Washington Legislature: Statute Page", url));
                    statute = new TestStatute(topic, description.toString(), null, sources);
                    break;
                }
            }
        }
        return statute;
    }

    /*
    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div.noscroll div table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = values[0] + " " + values[1], value = text.split(title + " ")[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.noscroll div table tbody tr")) {
                final String text = element.text();
                final String[] values = text.split(" ");

                final String chapter = values[0], value = text.split(chapter + " ")[1];
                final String chapterValue = chapter.split("\\.")[1], correctedChapter = chapterValue.substring(chapterValue.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }

    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        chapter = prefixZeros(chapter, 2);
        final String url = statutesListURL.replace("%index%", title).replace("%chapter%", chapter);
        final Document doc = getDocument(url);
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.noscroll div table tbody tr")) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1];
                final String statuteValue = statute.split("\\.")[2], correctedStatute = statuteValue.substring(statuteValue.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        chapter = prefixZeros(chapter, 2);
        section = prefixZeros(section, 3);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements noscroll = doc.select("div.noscroll div div");
                final String topic = LocalServer.fixEscapeValues(noscroll.select("h3").get(1).text());
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : noscroll.select("div")) {
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
