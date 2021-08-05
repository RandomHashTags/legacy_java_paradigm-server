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

public enum Kansas implements LawSubdivisionController {
    INSTANCE(
            "http://www.kslegislature.org/li_2020s/b2020s/statute/",
            "http://www.kslegislature.org/li_2020s/b2020s/statute/%index%_000_0000_chapter/",
            "http://www.kslegislature.org/li_2020s/b2020s/statute/%index%_000_0000_chapter/%index%_%chapter%_0000_article/",
            "http://www.kslegislature.org/li_2020s/b2020s/statute/%index%_000_0000_chapter/%index%_%chapter%_0000_article/%index%_%chapter%_%section%_section/%index%_%chapter%_%section%_k/"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Kansas(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("table tbody tr");
            table.remove(0);
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text().replaceFirst("Chapter", "Title");
                final String[] values = text.split("\\. - ");
                final String title = values[0], value = text.split(title + "\\. - ")[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
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
                for(Element element : doc.select("table tbody tr")) {
                    final String text = element.text();
                    final String[] values = text.split("\\. - ");
                    final String chapter = values[0], value = text.split(chapter + "\\. - ")[1];
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
        final int titleLength = title.length();
        title = prefixZeros(title, 3);
        chapter = prefixZeros(chapter, 3);
        final String path = title + "." + chapter;
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("table tbody tr")) {
                    final String text = element.text().substring(titleLength+2);
                    final String[] values = text.split(" - ");
                    final String statute = values[0], value = text.split(statute + " - ")[1];
                    final String correctedStatute = statute.substring(statute.startsWith("0") ? 1 : 0);
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
        title = prefixZeros(title, 3);
        chapter = prefixZeros(chapter, 3);
        section = prefixZeros(section, 4+(String.valueOf(section.charAt(section.length()-1)).matches("[0-9]+") ? 0 : 1));
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements table = doc.select("table tbody tr td p"), topics = table.select("span");
                final String topic = topics.get(0).text() + " " + topics.get(1).text();
                final Element last = table.last();
                final String history = last.text();
                if(history.startsWith("History")) {
                    table.remove(last);
                }
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : table) {
                    final String text = element.text().replace(topic + " ", "");
                    description.append(isFirst ? "" : "\n").append(text);
                    isFirst = false;
                }

                final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic.split(" ")[1], description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
