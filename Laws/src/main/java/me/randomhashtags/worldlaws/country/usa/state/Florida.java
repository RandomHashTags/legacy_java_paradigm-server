package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.country.StateIndex;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.StateStatute;
import me.randomhashtags.worldlaws.country.State;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Florida implements State {
    INSTANCE(
            "https://www.flsenate.gov/Laws/Statutes/2019",
            "https://www.flsenate.gov/Laws/Statutes/2019/Title%index%/#Title%index%",
            "https://www.flsenate.gov/Laws/Statutes/2019/Chapter%chapter%",
            "https://www.flsenate.gov/Laws/Statutes/2019/%chapter%.%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Florida(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div.statutesTOC li span");
            iterateThroughChapterTable(table, indexesJSON, true, 3);
        }
        return chapters;
    }
    @Override
    public String getTableOfChapters(String title) {
        title = title.replace("_", "+").replace(" ", "+");
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("ol.chapter a[href] span")) {
                    final String text = element.text();
                    if(!text.startsWith("(ss.") && !text.startsWith("(s.")) {
                        list.add(element);
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
        if(statutesJSON.containsKey(chapter)) {
            return statutesJSON.get(chapter);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
            if(doc != null) {
                final Elements table = doc.select("div.CatchlineIndex div.IndexItem");
                final List<Element> list = new ArrayList<>();
                for(Element element : table) {
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
        chapter = prefixZeros(chapter, 4);
        section = section.split("\\.")[1];
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements table = doc.select("div.Section div span");
                final int size = table.size();
                final List<Element> history = Arrays.asList(table.get(size-3), table.get(size-2), table.get(size-1));
                table.removeIf(history::contains);

                final StringBuilder description = new StringBuilder();
                final String topic = doc.select("span.Catchline").get(0).text();
                final Elements testIntro = doc.select("div.Section span.SectionBody span.Text");
                if(!testIntro.isEmpty()) {
                    description.append(testIntro.get(0).text());
                } else {
                    String previousString = "";
                    int index = 1;
                    for(Element element : table) {
                        final String text = element.text();
                        if(index % 2 == 0) {
                            description.append(previousString).append(text);
                        } else {
                            description.append(index == 1 ? "" : "\n");
                            previousString = text;
                        }
                        index++;
                    }
                }
                final StateStatute statute = new StateStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
                final String string = statute.toString();
                statutesJSON.put(section, string);
                return string;
            }
            return null;
        }
    }
}
