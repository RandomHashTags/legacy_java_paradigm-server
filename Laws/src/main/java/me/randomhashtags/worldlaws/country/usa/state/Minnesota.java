package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.Subdivision;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class Minnesota extends LawSubdivisionController {
    public static final Minnesota INSTANCE = new Minnesota(
            "https://www.revisor.mn.gov/statutes/",
            "https://www.revisor.mn.gov/statutes/part/%index%",
            "https://www.revisor.mn.gov/statutes/cite/%chapter%",
            "https://www.revisor.mn.gov/statutes/cite/%index%.%section%",
            "https://www.house.leg.state.mn.us/members/list",
            "https://www.senate.mn/members/index.html"
    );

    private String title, description;

    Minnesota(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL, String houseMembersURL, String senateMembersURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("td");
            iterateThroughIndexTable(table);
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        title = title.replace("_", "+").replace(" ", "+");
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final Elements table = doc.select("td");
            iterateThroughChapterTable(title, table);
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
        if(doc != null) {
            final Elements table = doc.select("td");
            iterateThroughStatuteTable(path, table);
        }
    }

    private List<Subdivision> getSubdivisions(String chapter, String section, Element sectionElement) {
        final List<Subdivision> subdivisions = new ArrayList<>();
        // TODO: support terms (like in https://www.revisor.mn.gov/statutes/cite/624.7181)

        final Elements header = sectionElement.select("h1");
        final String[] splits = header.text().split(chapter + "\\." + section + " ");
        title = splits[splits.length-1];

        final Elements keys = sectionElement.select("h2"), values = sectionElement.select("h2 + p");
        if(!values.isEmpty()) {
            int test = 0;
            for(Element element : values) {
                final String desc = element.text();
                final Subdivision subdivision = new Subdivision(keys.get(test).text(), desc);
                subdivisions.add(subdivision);
                test++;
            }
        } else {
            final Elements target = sectionElement.select("h1 + p");
            description = target != null && !target.isEmpty() ? target.get(0).text() : null;
        }
        return subdivisions;
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        section = prefixZeros(section, 2);
        final String path = title + "." + chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final String docTitle = doc.title();
                final Elements breadcrumb = doc.select("div.mb-3 a[href]");
                final Elements sections = doc.select("div.section");
                if(sections.isEmpty()) {
                    System.out.println(docTitle + ": sections are empty!");
                } else {
                    final int size = sections.size();
                    System.out.println(docTitle + ": section size=" + size);
                    if(size == 1) {
                        final Element element = sections.get(0);
                        final List<Subdivision> subdivisions = getSubdivisions(chapter, section, element);
                        final String topic = breadcrumb.get(1).text();

                        final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), title, description, subdivisions);
                        final String string = statute.toString();
                        statutes.put(path, string);
                        return string;
                    }
                }
            }
            return null;
        }
    }
}
