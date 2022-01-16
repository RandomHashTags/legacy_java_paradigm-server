package me.randomhashtags.worldlaws.country.usa.state.recode;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.recode.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class Minnesota extends TestLawSubdivisionController {
    public static final Minnesota INSTANCE = new Minnesota(
            "https://www.revisor.mn.gov/statutes/",
            "https://www.revisor.mn.gov/statutes/part/%index%",
            "https://www.revisor.mn.gov/statutes/cite/%chapter%",
            "https://www.revisor.mn.gov/statutes/cite/%section%",
            "https://www.house.leg.state.mn.us/members/list",
            "https://www.senate.mn/members/index.html"
    );

    private String description;

    Minnesota(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL, String houseMembersURL, String senateMembersURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.MINNESOTA;
    }

    @Override
    public int getPublishedDataYear() {
        return 2021;
    }

    @Override
    public String getTableOfChapters(String index) {
        index = index.replace("$", "+");
        return super.getTableOfChapters(index);
    }

    @Override
    public HashSet<? extends TestStatuteAbstract> getStatutesAbstract(Document doc, String index, String chapter, SubdivisionLegislationType type) {
        if(doc != null) {
            final boolean isIndex = type == SubdivisionLegislationType.INDEX, isChapter = type == SubdivisionLegislationType.CHAPTER, isStatute = type == SubdivisionLegislationType.STATUTE;
            final String tableID = "table" + (isIndex || isChapter ? ".table" : "");
            final Elements table = doc.select("div.col-lg " + tableID + " tbody tr");
            if(!table.isEmpty()) {
                final HashSet<TestStatuteAbstract> values = new HashSet<>();
                for(Element row : table) {
                    final Elements tds = row.select("td");
                    if(tds.size() >= 2) {
                        String id = tds.get(0).text();
                        if(isStatute) {
                            final String[] sectionValues = id.split("\\.");
                            id = id.substring(sectionValues[0].length()+1);
                        }
                        final String title = tds.get(1).text();
                        if(isIndex) {
                            id = title.replace(" ", "$")
                                    .replace(":", "%253A")
                                    .replace(";", "%253B")
                                    .replace(",", "%252C");
                        }
                        final String titleLowercase = title.toLowerCase();
                        if(titleLowercase.contains("repealed")) {
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
    public TestStatute loadStatute(String index, String chapter, String section) {
        final String originalSection = section;
        section = chapter + "." + section;
        final String url = statuteURL.replace("%section%", section);
        final Document doc = getDocument(url);
        TestStatute statute = null;
        if(doc != null) {
            final Elements breadcrumb = doc.select("div.mb-3 a[href]");
            final Elements sections = doc.select("div.section");
            if(sections.isEmpty()) {
                WLLogger.logError(this, "loadStatute;index=" + index + ";chapter=" + chapter + ";section=" + section + ";sections.isEmpty !");
            } else {
                final EventSources sources = new EventSources();
                sources.add(new EventSource("Minnesota Legislature: Statute Publication", url));
                final int size = sections.size();
                if(size == 1) {
                    final Element element = sections.get(0);
                    final List<TestSubdivision> subdivisions = getSubdivisions(element);
                    String title = doc.select("div.col-lg div.section h1.shn").text().substring(originalSection.length()+1);
                    if(title.endsWith(".")) {
                        title = title.substring(0, title.length()-1);
                    }
                    statute = new TestStatute(title, description, subdivisions, sources);
                }
            }
        }
        return statute;
    }

    private List<TestSubdivision> getSubdivisions(Element sectionElement) {
        // TODO: support terms (like in https://www.revisor.mn.gov/statutes/cite/624.7181)
        final List<TestSubdivision> subdivisions = new ArrayList<>();
        final Elements keys = sectionElement.select("h2"), values = sectionElement.select("h2 + p");
        if(!values.isEmpty()) {
            int test = 0;
            for(Element element : values) {
                final String desc = element.text();
                final TestSubdivision subdivision = new TestSubdivision(keys.get(test).text(), desc);
                subdivisions.add(subdivision);
                test++;
            }
        } else {
            final Elements target = sectionElement.select("h1 + p");
            description = target != null && !target.isEmpty() ? target.get(0).text() : null;
        }
        return subdivisions;
    }
}
