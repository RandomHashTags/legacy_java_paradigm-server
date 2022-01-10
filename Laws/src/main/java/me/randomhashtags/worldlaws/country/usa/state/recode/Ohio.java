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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class Ohio extends TestLawSubdivisionController {
    public static Ohio INSTANCE = new Ohio(
            "https://codes.ohio.gov/ohio-revised-code",
            "https://codes.ohio.gov/ohio-revised-code/title-%index%",
            "https://codes.ohio.gov/ohio-revised-code/chapter-%chapter%",
            "https://codes.ohio.gov/ohio-revised-code/section-%chapter%.%section%"
    );

    Ohio(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.OHIO;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = "table.data-grid";
            final Elements table = doc.select(tableID + " tbody tr");
            if(!table.isEmpty()) {
                final String key = isIndex ? "Title " : isChapter ? "Chapter " : "Section ";
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    final Elements tds = row.select("td");
                    if(tds.size() >= 1) {
                        final String[] textValues = tds.get(0).text().split(" \\| ");
                        final String titleText = textValues[0];
                        if(titleText.startsWith(key)) {
                            String id = textValues[0].substring(key.length());
                            if(isStatute) {
                                id = id.substring(id.split("\\.")[0].length()+1);
                            }
                            final String title = textValues[1];
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
    public void loadStatute(String index, String chapter, String section, CompletionHandlerLaws handler) {
        final String url = statuteURL.replace("%chapter%", chapter).replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements table = doc.select("div.content");
            final String topic = table.select("h2").get(0).text();
            final StringBuilder description = new StringBuilder();

            final Elements elements = table.select("div.content p + p + p");
            final Element last = elements.last();
            if(last.text().isEmpty()) {
                elements.remove(last);
            }
            boolean isFirst = true;
            TestSubdivision previousSubdivision = null;
            final List<TestSubdivision> subdivisions = new ArrayList<>();
            for(Element element : elements) {
                final String string = element.text(), character = string.length() > 1 ? String.valueOf(string.charAt(1)) : "";
                if(string.startsWith("(") && character.matches("[A-Z]+")) {
                    if(previousSubdivision != null) {
                        subdivisions.add(previousSubdivision);
                    }
                    previousSubdivision = new TestSubdivision(character, string);
                } else if(previousSubdivision != null) {
                    previousSubdivision.setDescription(previousSubdivision.getDescription() + "\n" + string);
                } else {
                    description.append(isFirst ? "" : "\n").append(string);
                }
                isFirst = false;
            }
            if(previousSubdivision != null) {
                subdivisions.add(previousSubdivision);
            }

            final EventSources sources = new EventSources();
            sources.add(new EventSource("Ohio Legislature: Statute Page", url));
            statute = new TestStatute(topic, description.toString(), subdivisions, sources);
        }
        handler.handleStatute(statute);
    }
}
