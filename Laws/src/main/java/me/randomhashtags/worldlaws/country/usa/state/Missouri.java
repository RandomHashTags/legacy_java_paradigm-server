package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Missouri implements LawSubdivisionController {
    INSTANCE(
            "http://revisor.mo.gov/main/home.aspx",
            "http://revisor.mo.gov/main/home.aspx",
            "https://revisor.mo.gov/main/OneChapter.aspx?chapter=%chapter%",
            "https://revisor.mo.gov/main/OneSection.aspx?section=%chapter%.%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Missouri(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            setupTableOfChapters(doc);
            final Elements table = doc.select("div.lr-fl-s-all summary span + span");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = "Title " + values[0], value = values[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
        }
        return chapters;
    }
    private void setupTableOfChapters(Document doc) {
        final Elements div = doc.select("div.lr-fl-s-all"), chapterList = div.select("summary span"), chapterTitles = div.select("details div a[href]");
        int index = 0, chapterIndex = 0;
        for(Element element : chapterList) {
            if(index % 2 == 0) {
                final String text = element.text();
                final String[] range = text.split(" ")[1].split(" ")[1].split("‑");
                final int min = Integer.parseInt(range[0]), max = Integer.parseInt(range[1]);
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(int i = min; i <= max; i++) {
                    final String chapterText = chapterTitles.get(chapterIndex).text();
                    final String[] values = chapterText.split(" ");
                    final int targetChapter = Integer.parseInt(values[2]);
                    if(i == targetChapter) {
                        final String title = values[3], chapter = Integer.toString(i);
                        final SubdivisionStatuteChapter stateChapter = new SubdivisionStatuteChapter(chapter);
                        stateChapter.setTitle(title);
                        builder.append(isFirst ? "" : ",").append(stateChapter.toString());
                        isFirst = false;
                        chapterIndex++;
                    }
                }
                builder.append("]");
                final String string = builder.toString();
                tableOfChaptersJSON.put(Integer.toString(index+1), string);
            }
            index++;
        }
    }
    @Override
    public String getTableOfChapters(String title) {
        return tableOfChaptersJSON.get(title);
    }
    @Override
    public String getStatuteList(String title, String chapter) {
        if(statutesJSON.containsKey(chapter)) {
            return statutesJSON.get(chapter);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
            if(doc != null) {
                final int substring = 1+chapter.length();
                final Elements elements = doc.select("div.lr-fl-s-all table tbody tr");
                final List<Element> list = new ArrayList<>();
                for(Element element : elements) {
                    final String text = element.text();
                    if(text.startsWith(chapter + ".")) {
                        final String separator = "   ";
                        final String[] values = text.split(separator);
                        final String statute = values[0], value = text.split(statute + separator)[1], statuteValue = statute.substring(substring);
                        final String correctedStatute = statuteValue.substring(statuteValue.startsWith("00") ? 2 : statuteValue.startsWith("0") ? 1 : 0);
                        list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                        list.add(new Element(value).appendChild(new TextNode(value)));
                    }
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
        section = prefixZeros(section, 3);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements div = doc.select("div.norm p.norm");
                final String topic = div.select("span.bold").get(0).text();
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : div) {
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
