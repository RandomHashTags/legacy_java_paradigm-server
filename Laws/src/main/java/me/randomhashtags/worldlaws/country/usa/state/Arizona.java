package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.Subdivision;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Arizona extends LawSubdivisionController {
    public static final Arizona INSTANCE = new Arizona(
            "https://www.azleg.gov/ARStitle/",
            "https://www.azleg.gov/arsDetail/?title=%index%",
            "https://www.azleg.gov/arsDetail/?title=%chapter%",
            "https://www.azleg.gov/ars/%title%/%section%.htm"
    );

    private HashMap<String, String> statutesJSON;

    Arizona(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
        statutesJSON = new HashMap<>();
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("tr ~ tr");
            iterateIndexTable(table, true);
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final Elements inner = doc.select("div.site-inner"), table = inner.select("h5");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text(), string = text.substring("Chapter ".length());
                list.add(new Element(string).appendChild(new TextNode(string)));
            }

            iterateChapterTable(title, new Elements(table), false, null);
            new Thread(() -> loadStatuteList(inner, table)).start();
        }
    }

    private void loadStatuteList(Elements inner, Elements table) {
        final List<Element> statutes = new ArrayList<>();
        for(Element statute : inner.select("h5 + div div div ul")) {
            final String text = statute.text(), string = text.substring(text.split("-")[0].length()+1);
            statutes.add(new Element(string).appendChild(new TextNode(string)));
        }
        for(int i = 1; i <= table.size(); i++) {
            final String chapter = Integer.toString(i);
            final List<Element> test = new ArrayList<>(statutes);
            test.removeIf(string -> !string.text().startsWith(chapter));
            if(!test.isEmpty()) {
                final List<Element> elements = new ArrayList<>();
                for(Element element : test) {
                    final String  string = element.text();
                    final String[] values = string.split(" ");
                    final String topic = values[0], value = string.split(topic + " ")[1];
                    elements.add(new Element(topic).appendChild(new TextNode(topic)));
                    elements.add(new Element(value).appendChild(new TextNode(value)));
                }
                iterateThroughStatuteTable(chapter, new Elements(elements));
            }
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            section = prefixZeros(section, 5);
            final String url = statuteURL.replace("%title%", title).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements sections = doc.select("p");
                if(!sections.isEmpty()) {
                    final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), null, null);
                    final List<Subdivision> subdivisions = new ArrayList<>();
                    Subdivision previousSubdivision = null;
                    for(Element paragraph : sections) {
                        final String text = paragraph.text();
                        if(text.startsWith("A.") || text.startsWith("B.") || text.startsWith("C.") || text.startsWith("D.") || text.startsWith("E.")) {
                            if(previousSubdivision != null) {
                                subdivisions.add(previousSubdivision);
                            }
                            final Subdivision subdivision = new Subdivision(text, "");
                            previousSubdivision = subdivision;
                        } else if(previousSubdivision != null) {
                            for(int i = 1; i <= 100; i++) {
                                if(text.startsWith(i + ".")) {
                                    previousSubdivision.setDescription(previousSubdivision.getDescription() + "\n" + text);
                                }
                            }
                        }
                    }
                    if(previousSubdivision != null && !subdivisions.contains(previousSubdivision)) {
                        subdivisions.add(previousSubdivision);
                    }
                    statute.setSubdivisions(subdivisions);
                    final String string = statute.toString();
                    statutes.put(path, string);
                    return string;
                }
            }
            return null;
        }
    }
}
