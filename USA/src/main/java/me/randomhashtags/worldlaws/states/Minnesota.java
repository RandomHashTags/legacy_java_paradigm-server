package me.randomhashtags.worldlaws.states;

import me.randomhashtags.worldlaws.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public enum Minnesota implements State {
    INSTANCE(
            "https://www.revisor.mn.gov/statutes/",
            "https://www.revisor.mn.gov/statutes/part/%index%",
            "https://www.revisor.mn.gov/statutes/cite/%chapter%",
            "https://www.revisor.mn.gov/statutes/cite/%index%.%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL, title, description;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Minnesota(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
    public List<StateIndex> getIndexes() {
        final List<StateIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("td");
            iterateThroughChapterTable(table, indexesJSON, true);
        }
        return chapters;
    }
    @Override
    public String getTableOfChapters(String title) {
        title = title.replace("_", "+").replace(" ", "+");
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                final Elements table = doc.select("td");
                iterateThroughChapterTable(table, builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            return string;
        }
    }
    @Override
    public String getStatuteList(String title, String chapter) {
        if(statutesJSON.containsKey(chapter)) {
            return statutesJSON.get(chapter);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
            if(doc != null) {
                final Elements table = doc.select("td");
                iterateThroughChapterTable(table, builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            statutesJSON.put(chapter, string);
            return string;
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

                        final StateStatute statute = new StateStatute(StateReference.build(title, chapter, section, url), title, description, subdivisions);
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
