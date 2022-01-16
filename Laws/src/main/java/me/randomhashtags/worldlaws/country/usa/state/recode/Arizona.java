package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class Arizona extends TestLawSubdivisionController {
    public static final Arizona INSTANCE = new Arizona(
            "https://www.azleg.gov/ARStitle/",
            "https://www.azleg.gov/arsDetail/?title=%index%",
            "https://www.azleg.gov/arsDetail/?title=%index%",
            "https://www.azleg.gov/ars/%index%/%section%.htm"
    );

    Arizona(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.ARIZONA;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = isIndex ? "div.content-sidebar-wrap table tbody tr" : "div.site-inner div.accordion";
            final Elements table = doc.select(tableID);
            if(!table.isEmpty()) {
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    String id = null, title = null;
                    if(isIndex) {
                        final Elements tds = row.select("td"), ahrefs = row.select("a[href]");
                        if(tds.size() >= 3 && ahrefs.size() > 0) {
                            id = ahrefs.get(0).text().substring("Title ".length());
                            title = tds.get(2).text();
                            final TestStatuteAbstract statuteAbstract = new TestStatuteIndex(id, title);
                            values.add(statuteAbstract);
                        }
                    } else if(isChapter) {
                        final Element chapterElement = row.selectFirst("h5");
                        id = chapterElement.select("a.one-sixth").get(0).text().substring("Chapter ".length());
                        title = chapterElement.select("div.five-sixth").get(0).text();
                    } else if(isStatute) {
                        final String targetChapter = row.selectFirst("a.one-sixth").text().substring("Chapter ".length());
                        if(targetChapter.equals(chapter)) {
                            final Elements statutes = row.select("div.article div ul");
                            for(Element statute : statutes) {
                                id = statute.selectFirst("li.colleft a[href]").text().substring(index.length()+1);
                                title = statute.selectFirst("li.colright").text();
                                if(title.startsWith(" ")) {
                                    title = title.substring(1);
                                }
                                final TestStatuteAbstract statuteAbstract = new TestStatuteStatute(id, title);
                                values.add(statuteAbstract);
                            }
                            id = null;
                            title = null;
                            break;
                        }
                    }
                    if(id != null && title != null) {
                        final TestStatuteAbstract statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : new TestStatuteChapter(id, title);
                        values.add(statuteAbstract);
                    }
                }
                return values;
            }
        }
        return null;
    }

    @Override
    public TestStatute loadStatute(String index, String chapter, String section) {
        section = prefixZeros(section, 5);
        final String url = statuteURL.replace("%index%", index).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements sections = doc.select("p");
            sections.remove(0);
            if(!sections.isEmpty()) {
                final List<TestSubdivision> subdivisions = new ArrayList<>();
                TestSubdivision previousSubdivision = null; // TODO: support subdivisions from 2 parent subdivisions (like https://www.azleg.gov/ars/1/00271.htm)
                String description = null;
                for(Element paragraph : sections) {
                    final String text = paragraph.text();
                    final boolean isEmpty = text.isEmpty();
                    if(!isEmpty && text.substring(0, 2).matches("[A-Z]\\.")) {
                        if(previousSubdivision != null) {
                            subdivisions.add(previousSubdivision);
                        }
                        previousSubdivision = new TestSubdivision(text.substring(0, 1), text.substring(3));
                    } else {
                        boolean found = false;
                        for(int i = 1; i <= 100; i++) {
                            if(text.startsWith(i + ".")) {
                                found = true;
                                final TestSubdivision subdivision = new TestSubdivision(Integer.toString(i), text.substring(3));
                                if(previousSubdivision != null) {
                                    previousSubdivision.addSubdivision(subdivision);
                                }
                                break;
                            }
                        }
                        if(!found && !isEmpty) {
                            description = (description != null ? description + "\\n" : "") + text;
                        }
                    }
                }
                if(previousSubdivision != null && !subdivisions.contains(previousSubdivision)) {
                    subdivisions.add(previousSubdivision);
                }
                final String topic = doc.select("p font u").get(0).text();
                final EventSources sources = new EventSources();
                sources.add(new EventSource("Arizona Legislature: Statute Page", url));
                statute = new TestStatute(topic, description, subdivisions, sources);
            }
        }
        return statute;
    }
}
