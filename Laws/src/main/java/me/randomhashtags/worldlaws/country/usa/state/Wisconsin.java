package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.country.StateIndex;
import me.randomhashtags.worldlaws.country.State;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Wisconsin implements State {
    INSTANCE(
            "https://docs.legis.wisconsin.gov/statutes/prefaces/toc",
            "https://docs.legis.wisconsin.gov/statutes/statutes/%index%",
            "https://docs.legis.wisconsin.gov/statutes/statutes/%index%",
            "https://docs.legis.wisconsin.gov/document/statutes/%index%/%chapter%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;
    private HashMap<Integer, List<String>> chapters;

    Wisconsin(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div.statutes div.qs_toc_head_bold_");
            int index = 1;
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text(), title = "Title " + index;
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(text).appendChild(new TextNode(text)));
                index++;
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
            loadChapters(doc);
        }
        return chapters;
    }

    private void loadChapters(Document doc) {
        chapters = new HashMap<>();
        int index = 1;
        for(Element element : doc.select("div.statutes div.qs_toc_head_bold_ ~ div")) {
            final String text = element.text();
            if(!text.isEmpty()) {
                final boolean isChapter = Character.isDigit(text.charAt(0));
                if(isChapter) {
                    if(!chapters.containsKey(index)) {
                        chapters.put(index, new ArrayList<>());
                    }
                    final String[] values = text.split("\\.");
                    chapters.get(index).add(text.split(values[0] + "\\. ")[1].split(" \\(PDF")[0]);
                } else {
                    index++;
                }
            }
        }
    }
    @Override
    public String getTableOfChapters(String title) {
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final int titleInt = Integer.parseInt(title);
            final List<String> list = chapters.get(titleInt);
            int startingChapter = 1;
            for(int i = 1; i < titleInt; i++) {
                startingChapter += chapters.get(i).size();
            }
            final List<Element> elements = new ArrayList<>();
            for(String string : list) {
                final String chapter = Integer.toString(startingChapter);
                elements.add(new Element(chapter).appendChild(new TextNode(chapter)));
                elements.add(new Element(string).appendChild(new TextNode(string)));
                startingChapter += 1;
            }
            iterateThroughChapterTable(new Elements(elements), builder, false);
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
            final Document doc = getDocument(statutesListURL.replace("%index%", chapter));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("div.statutes div.qs_toc_entry_")) {
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
            statutesJSON.put(chapter, string);
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
