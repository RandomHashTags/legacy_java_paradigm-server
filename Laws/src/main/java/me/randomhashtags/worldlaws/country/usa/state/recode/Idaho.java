package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public final class Idaho extends TestLawSubdivisionController {
    public static final Idaho INSTANCE = new Idaho(
            "https://legislature.idaho.gov/statutesrules/idstat/",
            "https://legislature.idaho.gov/statutesrules/idstat/Title%index%",
            "https://legislature.idaho.gov/statutesrules/idstat/Title%index%/T%index%CH%chapter%/",
            "https://legislature.idaho.gov/statutesrules/idstat/Title%index%/T%index%CH%chapter%/SECT%index%-%section%/"
    );

    Idaho(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.IDAHO;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = "div.vc-column-innner-wrapper table tbody tr";
            final Elements table = doc.select(tableID);
            if(!table.isEmpty()) {
                final String key = isIndex ? "Title " : isChapter ? "Chapter " : chapter + "-";
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    final Elements tds = row.select("td");
                    if(tds.size() >= 3) {
                        final Element link = tds.get(0).selectFirst("a[href]");
                        if(link != null) {
                            final String id = link.text().substring(key.length());
                            final String title = tds.get(2).text();
                            final TestStatuteAbstract statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : isChapter ? new TestStatuteChapter(id, title) : isStatute ? new TestStatuteStatute(id, title) : null;
                            if(statuteAbstract != null) {
                                values.add(statuteAbstract);
                            }
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
        final String url = statuteURL.replace("%index%", index).replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final String key = index + "-" + section + ". ";
            final Elements elements = doc.select("div.pgbrk div + div");
            final String topic = elements.get(2).text();
            for(int i = 1; i <= 3; i++) {
                elements.remove(0);
            }
            elements.remove(elements.last());

            final StringBuilder description = new StringBuilder();
            boolean isFirst = true;
            for(Element element : elements) {
                String text = element.text();
                if(text.startsWith(key)) {
                    text = text.substring(key.length());
                }
                description.append(isFirst ? "" : "\n").append(text);
                isFirst = false;
            }
            final EventSources sources = new EventSources();
            sources.add(new EventSource("Idaho Legislature: Statute Page", url));
            statute = new TestStatute(topic, description.toString(), null, sources);
        }
        return statute;
    }
}
