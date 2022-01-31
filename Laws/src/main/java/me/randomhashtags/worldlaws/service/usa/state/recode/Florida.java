package me.randomhashtags.worldlaws.service.usa.state.recode;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class Florida extends TestLawSubdivisionController {
    public static Florida INSTANCE = new Florida(
            "https://www.flsenate.gov/Laws/Statutes/2021",
            "https://www.flsenate.gov/Laws/Statutes/2021/Title%index%/#Title%index%",
            "https://www.flsenate.gov/Laws/Statutes/2021/Chapter%chapter%",
            "https://www.flsenate.gov/Laws/Statutes/2021/%chapter%.%section%"
    );

    Florida(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.FLORIDA;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String targetPrefix = "div.statutesTOC ol li ", target = isIndex ? targetPrefix + "a[href]" : isChapter ? targetPrefix + "ol.chapter li a[href]" : "div.CatchlineIndex div.IndexItem";
            final Elements table = doc.select(target);
            if(!table.isEmpty()) {
                final String key = isIndex ? "Title " : isChapter ? "Chapter " : null;
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    final Elements span = row.select("span");
                    String targetID = isIndex ? span.get(0).attr("id").substring(key.length()-1) : isStatute ? row.selectFirst("a[href]").text() : span.get(0).text();
                    String title = isStatute ? row.selectFirst("div.Catchline").text() : span.get(1).text();
                    final String id;
                    if(isChapter) {
                        id = targetID.substring(key.length());
                        if(title.startsWith("- ")) {
                            title = title.substring(2);
                        }
                    } else if(isStatute) {
                        id = targetID.split("\\.")[1];
                    } else {
                        id = targetID;
                    }
                    final TestStatuteAbstract statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : isChapter ? new TestStatuteChapter(id, title) : isStatute ? new TestStatuteStatute(id, title) : null;
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
    public TestStatute loadStatute(String index, String chapter, String section) {
        chapter = prefixZeros(chapter, 4);
        final String url = statuteURL.replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements table = doc.select("div.Section div span");
            final int size = table.size();
            final List<Element> history = Arrays.asList(table.get(size-3), table.get(size-2), table.get(size-1));
            table.removeIf(history::contains);

            final StringBuilder description = new StringBuilder();
            final String topic = doc.select("span.Catchline").get(0).text();
            final Elements testIntro = doc.select("div.Section span.SectionBody span.Text");
            if(!testIntro.isEmpty()) {
                description.append(testIntro.get(0).text());
            } else {
                String previousString = "";
                int targetIndex = 1;
                for(Element element : table) {
                    final String text = element.text();
                    if(targetIndex % 2 == 0) {
                        description.append(previousString).append(text);
                    } else {
                        description.append(targetIndex == 1 ? "" : "\n");
                        previousString = text;
                    }
                    targetIndex++;
                }
            }

            final EventSources sources = new EventSources();
            sources.add(new EventSource("Florida Legislature: Statute Page", url));
            statute = new TestStatute(topic, description.toString(), null, sources);
        }
        return statute;
    }
}
