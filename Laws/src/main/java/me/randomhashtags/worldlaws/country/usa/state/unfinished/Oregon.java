package me.randomhashtags.worldlaws.country.usa.state.unfinished;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Oregon implements LawSubdivisionController {
    INSTANCE(
            "https://www.oregonlegislature.gov/bills_laws/Pages/ORS.aspx",
            "http://www.wvlegislature.gov/WVCODE/code.cfm?chap=%index%&art=1",
            "http://www.wvlegislature.gov/WVCODE/code.cfm?chap=%index%&art=%chapter%#01",
            ""
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private HashMap<String, String> statutesJSON, statutes;

    Oregon(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

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
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
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
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
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
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
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
            iterateThroughStatuteTable(path, new Elements(list));
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
