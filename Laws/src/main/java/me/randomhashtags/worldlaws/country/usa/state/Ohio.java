package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.law.StateIndex;
import me.randomhashtags.worldlaws.law.StateReference;
import me.randomhashtags.worldlaws.law.StateStatute;
import me.randomhashtags.worldlaws.law.Subdivision;
import me.randomhashtags.worldlaws.law.State;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Ohio implements State {
    INSTANCE(
            "http://codes.ohio.gov/orc/",
            "http://codes.ohio.gov/orc/%index%",
            "http://codes.ohio.gov/orc/%chapter%",
            "http://codes.ohio.gov/orc/%chapter%.%section%v1"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Ohio(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            final Elements table = doc.select("div.content h2");
            table.remove(0);
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String bracketValue = " [" + text.split("\\[")[1].split("]")[0] + "]";
                final String string = text.replace(bracketValue, "");
                final String[] values = string.split(" ");
                final String title = values[0] + " " + values[1], value = string.split(title + " ")[1];
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
                for(Element element : doc.select("div.content h2")) {
                    final String text = element.text();
                    final String[] values = text.split(" ");
                    final String chapter = values[0] + " " + values[1], value = text.split(chapter + " ")[1];
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
        if(statutesJSON.containsKey(chapter)) {
            return statutesJSON.get(chapter);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("div.content h2")) {
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
        section = section.split("\\.")[1];
        final String path = chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements table = doc.select("div.content");
                final String topic = table.select("h2").get(0).text();
                final StringBuilder description = new StringBuilder();

                final Elements elements = table.select("div.content p + p + p");
                final Element last = elements.last();
                if(last.text().isEmpty()) {
                    elements.remove(last);
                }
                boolean isFirst = true;
                Subdivision previousSubdivision = null;
                final List<Subdivision> subdivisions = new ArrayList<>();
                for(Element element : elements) {
                    final String string = element.text(), character = string.length() > 1 ? String.valueOf(string.charAt(1)) : "";
                    if(string.startsWith("(") && character.matches("[A-Z]+")) {
                        if(previousSubdivision != null) {
                            subdivisions.add(previousSubdivision);
                        }
                        previousSubdivision = new Subdivision(character, string);
                    } else if(previousSubdivision != null) {
                        previousSubdivision.setDescription(previousSubdivision.getDescription() + "\n" + string);
                    } else {
                        description.append(isFirst ? "" : "\n").append(string);
                    }
                    isFirst = false;
                }
                if(previousSubdivision != null) {
                    subdivisions.add(previousSubdivision);
                }
                final StateStatute statute = new StateStatute(StateReference.build(title, chapter, section, url), topic, description.toString(), subdivisions);
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
