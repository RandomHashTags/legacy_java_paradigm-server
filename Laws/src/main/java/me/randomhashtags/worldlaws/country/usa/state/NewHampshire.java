package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum NewHampshire implements LawSubdivisionController {
    INSTANCE(
            "http://www.gencourt.state.nh.us/rsa/html/nhtoc.htm",
            "http://www.gencourt.state.nh.us/rsa/html/NHTOC/NHTOC-%index%.htm",
            "http://www.gencourt.state.nh.us/rsa/html/NHTOC/NHTOC-%index%-%chapter%.htm",
            "http://www.gencourt.state.nh.us/rsa/html/%index%/%chapter%/%chapter%-%section%.htm"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    NewHampshire(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("ul li");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(":");
                final String title = values[0], value = text.split(title + ": ")[1], correctedTitle = title.replace("TITLE", "Title");
                list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
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
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("ul li")) {
                    final String text = element.text().replaceFirst("CHAPTER ", "");
                    final String[] values = text.split(":");
                    final String chapter = values[0], value = text.split(chapter + ": ")[1];
                    list.add(new Element(chapter).appendChild(new TextNode(chapter)));
                    list.add(new Element(value).appendChild(new TextNode(value)));
                }
                iterateThroughChapterTable(new Elements(list), builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            return string;
        }
    }
    @Override
    public String getStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("ul li")) {
                    final String text = element.text();
                    final String[] values = text.split(":");
                    final String key = values[0] + ":" + values[1], statute = values[2].split(" ")[0], value = text.split(key + ":" + statute + " ")[1];
                    list.add(new Element(statute).appendChild(new TextNode(statute)));
                    list.add(new Element(value).appendChild(new TextNode(value)));
                }
                iterateThroughChapterTable(new Elements(list), builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            statutesJSON.put(path, string);
            return string;
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements titles = doc.select("body b"), titleDescriptions = doc.select("body codesect");
                titles.remove(titles.size()-1);
                final StateReference reference = StateReference.build(title, chapter, section, url);
                final SubdivisionStatute statute;
                if(titles.size() == 1) {
                    statute = new SubdivisionStatute(reference, titles.get(0).text(), titleDescriptions.get(0).text(), null);
                } else {
                    statute = new SubdivisionStatute(reference, null, null, null);
                }
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
