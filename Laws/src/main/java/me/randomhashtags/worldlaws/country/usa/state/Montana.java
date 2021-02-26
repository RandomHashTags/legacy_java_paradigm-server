package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.country.State;
import me.randomhashtags.worldlaws.country.StateIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Montana implements State {
    INSTANCE(
            "https://leg.mt.gov/bills/mca/index.html",
            "https://leg.mt.gov/bills/mca/title_%index%0/chapters_index.html",
            "https://leg.mt.gov/bills/mca/title_%index%/chapter_%chapter%/parts_index.html",
            ""
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Montana(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div.title-toc-content ul li");
            table.remove(0);
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split("\\.");
                final String title = values[0], value = text.split(title + "\\. ")[1], correctedTitle = title.replace("TITLES", "Title").replace("TITLE", "Title").replace("AND", "and");
                list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
        }
        return chapters;
    }
    @Override
    public String getTableOfChapters(String title) {
        title = prefixZeros(title, 3);
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("div.chapter-toc-content ul li")) {
                    final String text = element.text();
                    final String[] values = text.split("\\."), spaces = text.split(" ");
                    final boolean isChapter = values.length > 1, containsThrough = text.contains(" THROUGH ");
                    final String chapter = isChapter ? values[0] : spaces[0] + " " + spaces[1] + (containsThrough ? " " + spaces[2] + " " + spaces[3] : ""), value = text.split(chapter + (isChapter ? "\\." : " "))[1];
                    final String correctedTitle = chapter.replace("CHAPTERS ", "").replace("CHAPTER ", "").replace(" THROUGH ", "-");
                    if(containsThrough) {
                        final String[] chapterValues = correctedTitle.split("-");
                        final int starting = Integer.parseInt(chapterValues[0]), max = Integer.parseInt(chapterValues[1]);
                        for(int i = starting; i <= max; i++) {
                            final String number = Integer.toString(i);
                            list.add(new Element(number).appendChild(new TextNode(number)));
                            list.add(new Element(value).appendChild(new TextNode(value)));
                        }
                    } else {
                        list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                        list.add(new Element(value).appendChild(new TextNode(value)));
                    }
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
                for(Element element : doc.select("div.vc-column-innner-wrapper tr")) {
                    final String text = element.text();
                    final String[] values = text.split(" ");
                    final String statute = values[0], value = text.split(statute + " ")[1];
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
