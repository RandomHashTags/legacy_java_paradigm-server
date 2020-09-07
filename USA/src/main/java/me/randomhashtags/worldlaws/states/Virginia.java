package me.randomhashtags.worldlaws.states;

import me.randomhashtags.worldlaws.StateIndex;
import me.randomhashtags.worldlaws.StateReference;
import me.randomhashtags.worldlaws.StateStatute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Virginia implements State {
    INSTANCE(
            "https://law.lis.virginia.gov/vacode/",
            "https://law.lis.virginia.gov/vacode/title%index%/",
            "https://law.lis.virginia.gov/vacode/title%index%/chapter%chapter%/",
            "https://law.lis.virginia.gov/vacode/title%index%/chapter%chapter%/section%index%-%section%/"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    Virginia(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
            int index = 0;
            final Elements titles = doc.select("dl.number-descrip-list dt"), chapterTitles = doc.select("dl.number-descrip-list dd");
            final List<Element> list = new ArrayList<>();
            for(Element element : titles) {
                list.add(element);
                list.add(chapterTitles.get(index));
                index++;
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
                int index = 0;
                final Elements chapters = doc.select("dl.number-descrip-list dt"), titles = doc.select("dl.number-descrip-list dd");
                final List<Element> list = new ArrayList<>();
                for(Element element : chapters) {
                    list.add(element);

                    final Element chapterTitle = titles.get(index);
                    final String text = chapterTitle.text();
                    final String value = text.split(" \\(§")[0];
                    chapterTitle.text(value);
                    list.add(chapterTitle);
                    index++;
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
                final Elements articles = doc.select("ul.outline");
                final String suffix = articles == null || articles.isEmpty() ? "" : "-outline";
                final Elements sections = doc.select("dl.number-descrip-list" + suffix + " dt"), titles = doc.select("dl.number-descrip-list" + suffix + " dd");
                int index = 0;
                final int substring = 3+title.length();
                final List<Element> list = new ArrayList<>();
                for(Element element : sections) {
                    final String text = element.text().substring(substring);
                    list.add(new Element(text).appendChild(new TextNode(text)));
                    list.add(titles.get(index));
                    index++;
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
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements span = doc.select("article.content span"), elements = span.select("section p");
                final String topic = span.select("h2").get(0).text();
                final StringBuilder description = new StringBuilder();
                final Element last = elements.last();
                if(last.text().startsWith("Code")) {
                    elements.remove(last);
                }

                boolean isFirst = true;
                for(Element element : elements) {
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
