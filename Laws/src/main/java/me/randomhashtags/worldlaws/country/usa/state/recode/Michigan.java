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

public final class Michigan extends TestLawSubdivisionController {
    public static final Michigan INSTANCE = new Michigan (
            "http://www.legislature.mi.gov/(S(51fchyizumt3j1zpocuttlkt))/mileg.aspx?page=ChapterIndex",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=GetObject&objectname=mcl-chap%index%",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=getObject&objectName=mcl-%chapter%",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=getObject&objectName=mcl-%index%-%section%"
    );

    Michigan(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.MICHIGAN;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = "article table tbody tr";
            final Elements table = doc.select(tableID);
            if(!table.isEmpty()) {
                table.remove(0);
                final String key = isIndex ? "Chapter " : isChapter ? "" : "Section " + index + ".";
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    final Elements tds = row.select("td");
                    if(tds.size() == 3) {
                        final Element ahref = tds.get(0).selectFirst("a[href]");
                        if(ahref != null) {
                            String id = ahref.text().substring(key.length());
                            if(isIndex) {
                                id = id.replace(" ", "");
                            } else if(isChapter) {
                                id = id.replace(" ", "-").replace(".", "");
                            }
                            String title = tds.get(2).text();
                            if(isChapter) {
                                title = title.split(" \\(" + index + "\\.")[0];
                            }
                            if(!title.toLowerCase().contains("repealed")) {
                                final TestStatuteAbstract statuteAbstract = isIndex ? new TestStatuteIndex(id, title) : isChapter ? new TestStatuteChapter(id, title) : isStatute ? new TestStatuteStatute(id, title) : null;
                                if(statuteAbstract != null) {
                                    values.add(statuteAbstract);
                                }
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
    public void loadStatute(String index, String chapter, String section, CompletionHandlerLaws handler) {
        final String url = statuteURL.replace("%index%", index).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements span = doc.select("div form div article span"), elements = span.select("div");
            final String topic = span.select("center ~ b").get(0).text().substring(2+index.length()+section.length());
            final StringBuilder description = new StringBuilder();
            boolean isFirst = true;
            for(Element element : elements) {
                description.append(isFirst ? "" : "\n").append(element.text());
                isFirst = false;
            }
            final EventSources sources = new EventSources();
            sources.add(new EventSource("Michigan Legislature: Statute Page", url));
            statute = new TestStatute(topic, description.toString(), null, sources);
        }
        handler.handleStatute(statute);
    }
}
