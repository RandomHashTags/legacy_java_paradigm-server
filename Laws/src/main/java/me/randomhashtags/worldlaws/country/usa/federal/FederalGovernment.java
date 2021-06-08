package me.randomhashtags.worldlaws.country.usa.federal;

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
import java.util.HashSet;
import java.util.List;

public enum FederalGovernment implements State {
    INSTANCE(
            "https://uscode.house.gov",
            "https://uscode.house.gov/browse/prelim@title%index%&edition=prelim",
            "https://uscode.house.gov/browse/prelim@title%index%/chapter%chapter%&edition=prelim",
            "https://uscode.house.gov/view.xhtml?req=granuleid:USC-prelim-title%index%-section%section%&num=0&edition=prelim"
    );

    private final String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private final HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    FederalGovernment(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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

    private void addIndex(List<Element> list, Element element) {
        final String text = element.text();
        final String[] values = text.split(" ");
        final String title = values[0] + " " + values[1], value = text.split(title + " - ")[1].split("Authenticated PDF")[0];
        list.add(new Element(title).appendChild(new TextNode(title)));
        list.add(new Element(value).appendChild(new TextNode(value)));
    }
    @Override
    public List<StateIndex> getIndexes() {
        final List<StateIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("table.table tbody tr td");
            table.remove(0);
            int index = 1;
            final List<Element> list = new ArrayList<>(), after = new ArrayList<>();
            for(Element element : table) {
                addIndex(index % 2 == 0 ? list : after, element);
                index++;
            }
            list.addAll(after);
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
                for(Element element : doc.select("section ul")) {
                    final String text = element.text();
                    final String[] values = text.split("\\.");
                    final String chapter = values[0], value = text.split(chapter + "\\. ")[1];
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
        final String path = title + "." + chapter;
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("article p.section-label")) {
                    final String text = element.text().substring(2);
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
        final String path = title + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements article = doc.select("article"), labels = article.select("p.section-label");
                final HashSet<String> set = new HashSet<>();
                for(Element label : labels) {
                    set.add(label.text());
                }
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true, foundTitle = false;
                for(Element element : article.select("p")) {
                    final String text = element.text();
                    if(set.contains(text)) {
                        if(foundTitle) {
                            break;
                        }
                        foundTitle = text.substring(2).startsWith(section);
                    } else if(foundTitle) {
                        description.append(isFirst ? "" : "\n").append(text);
                        isFirst = false;
                    }
                }
                final StateStatute statute = new StateStatute(StateReference.build(title, chapter, section, url), null, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
