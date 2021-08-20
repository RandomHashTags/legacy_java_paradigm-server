package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.LocalServer;
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

public enum Washington implements LawSubdivisionController {
    INSTANCE(
            "https://apps.leg.wa.gov/rcw/",
            "https://apps.leg.wa.gov/rcw/default.aspx?Cite=%index%",
            "https://app.leg.wa.gov/RCW/default.aspx?cite=%index%.%chapter%",
            "https://app.leg.wa.gov/RCW/default.aspx?cite=%index%.%chapter%.%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private HashMap<String, String> statutes;

    Washington(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div.noscroll div table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = values[0] + " " + values[1], value = text.split(title + " ")[1];
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
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.noscroll div table tbody tr")) {
                final String text = element.text();
                final String[] values = text.split(" ");

                final String chapter = values[0], value = text.split(chapter + " ")[1];
                final String chapterValue = chapter.split("\\.")[1], correctedChapter = chapterValue.substring(chapterValue.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }

    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        chapter = prefixZeros(chapter, 2);
        final String url = statutesListURL.replace("%index%", title).replace("%chapter%", chapter);
        final Document doc = getDocument(url);
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.noscroll div table tbody tr")) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1];
                final String statuteValue = statute.split("\\.")[2], correctedStatute = statuteValue.substring(statuteValue.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        chapter = prefixZeros(chapter, 2);
        section = prefixZeros(section, 3);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements noscroll = doc.select("div.noscroll div div");
                final String topic = LocalServer.fixEscapeValues(noscroll.select("h3").get(1).text());
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : noscroll.select("div")) {
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
