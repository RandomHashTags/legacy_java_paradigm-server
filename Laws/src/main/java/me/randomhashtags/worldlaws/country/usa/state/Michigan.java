package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.country.State;
import me.randomhashtags.worldlaws.country.StateIndex;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.StateStatute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Michigan implements State {
    INSTANCE(
            "http://www.legislature.mi.gov/(S(51fchyizumt3j1zpocuttlkt))/mileg.aspx?page=ChapterIndex",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=GetObject&objectname=mcl-chap%index%",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=getObject&objectName=mcl-%chapter%",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=getObject&objectName=mcl-%index%-%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Michigan(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div form div article table tbody tr");
            for(int i = 1; i <= 3; i++) {
                table.remove(0);
            }
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" Chapter ");
                final String title = values[0].replace("Chapters", "").replace("Chapter", ""), value = values[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
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
                final Elements table = doc.select("div form div article table tbody tr");
                for(int i = 1; i <= 7; i++) {
                    table.remove(0);
                }
                final List<Element> list = new ArrayList<>();
                for(Element element : table) {
                    final String text = element.text();
                    final String[] values = text.split(" Statute ");
                    final String chapter = values[0], value = values[1].split(" \\(")[0];
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
                final int substring = 1+title.length();
                final Elements table = doc.select("div form div article table tbody tr");
                table.removeIf(element -> !element.text().startsWith("Section "));
                final List<Element> list = new ArrayList<>();
                for(Element element : table) {
                    final String text = element.text();
                    final String[] values = text.split(" Section ");
                    final String statute = values[0].split("Section ")[1].substring(substring), value = values[1];
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
        final String path = title + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements span = doc.select("div form div article span"), elements = span.select("div");
                final String topic = span.select("center ~ b").get(0).text().substring(2+title.length()+section.length());
                final Element history = span.select("font").get(0);
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : elements) {
                    description.append(isFirst ? "" : "\n").append(element.text());
                    isFirst = false;
                }
                final StateStatute statute = new StateStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
