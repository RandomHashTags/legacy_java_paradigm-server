package me.randomhashtags.worldlaws.country.usa.state.unfinished;

import me.randomhashtags.worldlaws.country.State;
import me.randomhashtags.worldlaws.country.StateIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Oregon implements State {
    INSTANCE(
            "https://www.oregonlegislature.gov/bills_laws/Pages/ORS.aspx",
            "http://www.wvlegislature.gov/WVCODE/code.cfm?chap=%index%&art=1",
            "http://www.wvlegislature.gov/WVCODE/code.cfm?chap=%index%&art=%chapter%#01",
            ""
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Oregon(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div div div div div div div div div div div div div div div table tr tr td");
            System.out.println("table=" + table.toString());
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                if(text.startsWith("Title Number :")) {
                    final String[] values = text.split("\\."), spaces = values[0].split(" ");
                    final String number = spaces[spaces.length-1], title = text.split(values[0] + "\\. ")[1].split(" - Chapters")[0];
                    list.add(new Element(number).appendChild(new TextNode(number)));
                    list.add(new Element(title).appendChild(new TextNode(title)));
                }
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
                final Elements articles = doc.select("div div ~ a[href]");
                final List<Element> list = new ArrayList<>();
                for(Element element : articles) {
                    final String text = element.text().replaceFirst("ARTICLE", "Chapter");
                    final String[] values = text.split("\\.");
                    final String article = values[0], value = text.split(article + "\\. ")[1];
                    list.add(new Element(article).appendChild(new TextNode(article)));
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
                for(Element element : doc.select("div div div a[href]")) {
                    final String text = element.text();
                    final String[] values = text.split("\\.");
                    final String statute = values[0];
                    final String[] split = text.split(statute + "\\. ");
                    final String statuteTitle = split.length == 1 ? "[REPEALED]" : split[1];
                    list.add(new Element(statute).appendChild(new TextNode(statute)));
                    list.add(new Element(statuteTitle).appendChild(new TextNode(statuteTitle)));
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
        final String path = chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final Document doc = getDocument(statuteURL.replace("%chapter%", chapter).replace("%section%", section));
            if(doc != null) {
                final Elements desc = doc.select("div.content h2 ~ p");
                //final StateStatute statute = new StateStatute(null, element, chapter, section);
                //final String string = statute.toString();
                //statutes.put(path, string);
                //return string;
            }
            return null;
        }
    }
}