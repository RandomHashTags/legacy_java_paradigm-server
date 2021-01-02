package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.law.StateIndex;
import me.randomhashtags.worldlaws.law.StateReference;
import me.randomhashtags.worldlaws.law.StateStatute;
import me.randomhashtags.worldlaws.law.State;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Pennsylvania implements State {
    INSTANCE(
            "https://www.legis.state.pa.us/cfdocs/legis/LI/Public/cons_index.cfm",
            "https://www.legis.state.pa.us//WU01/LI/LI/CT/HTM/%index%/%index%.HTM",
            "https://www.legis.state.pa.us/cfdocs/legis/LI/consCheck.cfm?txtType=HTM&ttl=%index%&div=0&chpt=%chapter%",
            "https://www.legis.state.pa.us//WU01/LI/LI/CT/HTM/%index%/00.%chapter%.%section%.000..HTM"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    private HashMap<String, HashMap<String, List<String>>> chapterStatutes;

    Pennsylvania(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

        tableOfChaptersJSON = new HashMap<>();
        statutesJSON = new HashMap<>();
        statutes = new HashMap<>();
        chapterStatutes = new HashMap<>();
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
            final Elements table = doc.select("table.DataTable tbody tr");
            table.remove(0);
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = values[0], value = text.split(title + " ")[1].split(" History")[0], correctedTitle = "Title " + title;
                list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
        }
        return chapters;
    }

    private int getIndexOf(String text, Elements array) {
        int index = 0;
        for(Element element : array) {
            if(element.text().equals(text)) {
                return index;
            }
            index++;
        }
        return -1;
    }
    @Override
    public String getTableOfChapters(String title) {
        final String originalTitle = title;
        title = prefixZeros(title, 2);
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final HashMap<String, List<String>> map = new HashMap<>();
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                final List<Element> list = new ArrayList<>();
                String targetChapter = "";
                int textIndex = 0, statuteIndex = 0;
                final Elements test = doc.select("div.BodyContainer p");
                for(Element element : test.select("a[href]")) {
                    final String text = element.text();
                    final boolean isChapter = text.startsWith("Chapter ");
                    if(isChapter) {
                        statuteIndex = 0;
                        textIndex = getIndexOf(text, test);
                        final String[] values = text.split("\\.");
                        final String chapter = values[0], value = text.split(chapter + "\\. ")[1];
                        list.add(new Element(chapter).appendChild(new TextNode(chapter)));
                        list.add(new Element(value).appendChild(new TextNode(value)));
                        targetChapter = chapter.split(" ")[1];
                        map.put(targetChapter, new ArrayList<>());
                    } else {
                        final Element target = test.get(textIndex+2+statuteIndex);
                        map.get(targetChapter).add(target.text());
                        statuteIndex++;
                    }
                }
                iterateThroughChapterTable(new Elements(list), builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            chapterStatutes.put(originalTitle, map);
            return string;
        }
    }
    @Override
    public String getStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final int substring = 2+chapter.length();
            final StringBuilder builder = new StringBuilder("[");
            final List<Element> list = new ArrayList<>();
            for(String string : chapterStatutes.get(title).get(chapter)) {
                final String[] values = string.split("\\.");
                final String statute = values[0], value = string.split(statute + "\\. ")[1], statuteValue = statute.substring(substring);
                final String correctedStatute = statuteValue.substring(statuteValue.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(new Elements(list), builder, false);
            builder.append("]");
            final String string = builder.toString();
            statutesJSON.put(path, string);
            return string;
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        title = prefixZeros(title, 2);
        chapter = prefixZeros(chapter, 3);
        section = prefixZeros(section, 3);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replaceFirst("%index%", title).replace("%index%", prefixZeros(title, 3)).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements container = doc.select("div.BodyContainer"), commentTitles = container.select("div.Comment + p");
                final Elements elements = container.select("div.Comment + p ~ p");
                boolean foundTopic = false, isFirst = true;
                String topic = "";
                final StringBuilder description = new StringBuilder();
                for(Element element : elements) {
                    final String text = element.text();
                    if(text.startsWith("§")) {
                        topic = text;
                        foundTopic = true;
                    } else if(foundTopic) {
                        if(text.isEmpty()) {
                            break;
                        } else {
                            description.append(isFirst ? "" : "\n").append(text);
                            isFirst = false;
                        }
                    }
                }
                if(!foundTopic) {
                    topic = commentTitles.size() > 0 ? commentTitles.get(0).text() : null;
                    description.append(elements.get(0).text());
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
