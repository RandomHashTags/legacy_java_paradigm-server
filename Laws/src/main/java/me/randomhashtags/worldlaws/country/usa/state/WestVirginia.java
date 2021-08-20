package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum WestVirginia implements LawSubdivisionController {
    INSTANCE(
            "http://www.wvlegislature.gov/WVCODE/code.cfm",
            "http://www.wvlegislature.gov/WVCODE/code.cfm?chap=%index%&art=1",
            "http://www.wvlegislature.gov/WVCODE/code.cfm?chap=%index%&art=%chapter%#01",
            "http://www.wvlegislature.gov/WVCODE/ChapterEntire.cfm?chap=%index%&art=%chapter%&section=%section%#1"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private HashMap<String, String> statutes;

    WestVirginia(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

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
            final Elements table = doc.select("select.btn option");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text().replaceFirst("CHAPTER", "Title");
                final String[] values = text.split("\\.");
                final String title = values[0], value = text.split(title + "\\. ")[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(value).appendChild(new TextNode(value)));
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
            final int substring = 3+title.length()+chapter.length();
            for(Element element : doc.select("div div div a[href]")) {
                final String text = element.text();
                final String[] values = text.split("\\.");
                final String statute = values[0];
                final String[] split = text.split(statute + "\\. ");
                final String value = split.length == 1 ? "[REPEALED]" : split[1], correctedStatute = statute.substring(substring);
                list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
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
                final Elements div = doc.select("div div");
                final String topic = div.select("h4").get(0).text();
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : div.select("p")) {
                    description.append(isFirst ? "" : "\n").append(element.text());
                    isFirst = false;
                }
                final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
