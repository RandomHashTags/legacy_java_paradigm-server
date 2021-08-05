package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Arizona implements LawSubdivisionController {
    INSTANCE(
            "https://www.azleg.gov/ARStitle/",
            "https://www.azleg.gov/arsDetail/?title=%index%",
            "https://www.azleg.gov/arsDetail/?title=%chapter%",
            "https://www.azleg.gov/ars/%title%/%section%.htm"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Arizona(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

        tableOfChaptersJSON = new HashMap<>();
        statutesJSON = new HashMap<>();
        statutes = new HashMap<>();
    }

    @Override
    public String getIndexesURL() {
        return indexesURL;
    }
    @Override
    public String getTableOfChaptersURL() {
        return tableOfChaptersURL;
    }
    @Override
    public String getStatutesListURL() {
        return statutesListURL;
    }
    @Override
    public String getStatuteURL() {
        return statuteURL;
    }

    @Override
    public String getIndexesJSON() {
        if(indexesJSON == null) {
            indexesJSON = new StringBuilder("[");
            getIndexes();
            indexesJSON.append("]");
        }
        return indexesJSON.toString();
    }
    @Override
    public String getTableOfChaptersJSON() {
        return tableOfChaptersJSON.toString();
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("tr ~ tr");
            iterateIndexTable(table, indexesJSON, true);
        }
        return chapters;
    }
    @Override
    public String getTableOfChapters(String title) {
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                final Elements inner = doc.select("div.site-inner"), table = inner.select("h5");
                final List<Element> list = new ArrayList<>();
                for(Element element : table) {
                    final String text = element.text(), string = text.substring("Chapter ".length());
                    list.add(new Element(string).appendChild(new TextNode(string)));
                }
                iterateIndexTable(new Elements(table), builder, false);
                new Thread(() -> loadStatuteList(inner, table)).start();
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            return string;
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
                final StringBuilder statuteBuilder = new StringBuilder("[");

                final List<Element> elements = new ArrayList<>();
                for(Element element : test) {
                    final String  string = element.text();
                    final String[] values = string.split(" ");
                    final String topic = values[0], value = string.split(topic + " ")[1];
                    elements.add(new Element(topic).appendChild(new TextNode(topic)));
                    elements.add(new Element(value).appendChild(new TextNode(value)));
                }
                iterateThroughChapterTable(new Elements(elements), statuteBuilder, false);
                statuteBuilder.append("]");
                statutesJSON.put(chapter, statuteBuilder.toString());
            }
        }
    }
    @Override
    public String getStatuteList(String title, String chapter) {
        return statutesJSON.get(chapter);
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
