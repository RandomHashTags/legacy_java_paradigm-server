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

public enum NorthDakota implements LawSubdivisionController {
    INSTANCE(
            "https://www.legis.nd.gov/general-information/north-dakota-century-code",
            "https://www.legis.nd.gov/cencode/t%index%.html",
            "https://www.legis.nd.gov/cencode/t%index%c%chapter%.html",
            "https://www.legis.nd.gov/cencode/t%index%c%chapter%.pdf#nameddest=%index%-%chapter%-%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    NorthDakota(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements column = doc.select("div.three-column"), titles = column.select("a[href]");
            final List<Element> list = new ArrayList<>();
            for(Element element : column) {
                final String text = element.text();
                final String[] values = text.split(" ");
                String title = "", previousTitle = "";
                boolean isFirst = true, isFirstTitleIndex = true;
                for(String value : values) {
                    final String key = value.split("\\.")[0];
                    if(key.matches("[0-9]+")) {
                        if(isFirst) {
                            isFirst = false;
                        } else {
                            list.add(new Element(title).appendChild(new TextNode(title)));
                            list.add(new Element(previousTitle).appendChild(new TextNode(previousTitle)));
                        }
                        title = value;
                        previousTitle = "";
                        isFirstTitleIndex = true;
                    } else {
                        previousTitle = previousTitle.concat((isFirstTitleIndex ? "" : " ") + value);
                        isFirstTitleIndex = false;
                    }
                }
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
        }
        return chapters;
    }
    @Override
    public String getTableOfChapters(String title) {
        final int substring = title.length()+1;
        title = prefixZeros(title.replace(".", "-"), 2+(title.contains(".") ? 2 : 0));
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                final Elements elements = doc.select("table.simple-table tbody tr");
                final List<Element> list = new ArrayList<>();
                for(Element element : elements) {
                    final String text = element.text().substring(substring);
                    final String[] values = text.split(" ");
                    final String chapter = values[0], value = text.split(chapter + " " + values[1] + " " + (text.contains("Sections") ? values[2] + " " : ""))[1], correctedChapter = chapter.substring(chapter.startsWith("0") ? 1 : 0);
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
        int substring = 2+title.length();
        title = prefixZeros(title.replace(".", "-"), 2+(title.contains(".") ? 2 : 0));
        chapter = prefixZeros(chapter.replace(".", "-"), 2+(chapter.contains(".") ? 2 : 0));
        substring += chapter.length();
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%index%", title).replaceFirst("%chapter%", chapter));
            if(doc != null) {
                final Elements elements = doc.select("table.simple-table tbody tr");
                final List<Element> list = new ArrayList<>();
                for(Element element : elements) {
                    final String text = element.text().substring(substring);
                    final String[] values = text.split(" ");
                    final String statute = values[0], value = text.split(statute + " ")[1], correctedStatute = statute.substring(statute.startsWith("0") ? 1 : 0);
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
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements elements = doc.select("div.Results div div p");
                final String topic = elements.get(0).text().split(title + "-" + chapter + "-" + section + "\\. ")[1];
                elements.remove(0);
                for(int i = 1; i <= 2; i++) {
                    elements.remove(elements.last());
                }
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : elements) {
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
