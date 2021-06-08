package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LocalServer;
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

public enum Washington implements State {
    INSTANCE(
            "https://apps.leg.wa.gov/rcw/",
            "https://apps.leg.wa.gov/rcw/default.aspx?Cite=%index%",
            "https://app.leg.wa.gov/RCW/default.aspx?cite=%index%.%chapter%",
            "https://app.leg.wa.gov/RCW/default.aspx?cite=%index%.%chapter%.%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Washington(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div.noscroll div table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = values[0] + " " + values[1], value = text.split(title + " ")[1];
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
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("div.noscroll div table tbody tr")) {
                    final String text = element.text();
                    final String[] values = text.split(" ");

                    final String chapter = values[0], value = text.split(chapter + " ")[1];
                    final String chapterValue = chapter.split("\\.")[1], correctedChapter = chapterValue.substring(chapterValue.startsWith("0") ? 1 : 0);
                    list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
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
        chapter = prefixZeros(chapter, 2);
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
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
                final StateStatute statute = new StateStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
