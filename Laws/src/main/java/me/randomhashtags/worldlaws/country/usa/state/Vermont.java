package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Vermont implements LawSubdivisionController {
    INSTANCE(
            "https://legislature.vermont.gov/statutes/",
            "https://legislature.vermont.gov/statutes/title/%index%",
            "https://legislature.vermont.gov/statutes/chapter/%index%/%chapter%",
            "https://legislature.vermont.gov/statutes/section/%index%/%chapter%/%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private HashMap<String, String> statutes;

    Vermont(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

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

    private void addElement(List<Element> list, String text) {
        final String[] values = text.split(":");
        final String title = values[0], value = text.split(title + ": ")[1];
        list.add(new Element(title).appendChild(new TextNode(title)));
        list.add(new Element(value).appendChild(new TextNode(value)));
    }
    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements ul = doc.select("ul.item-list");
            final List<Element> list = new ArrayList<>();

            final Element firstTitle = ul.select("a[href]").get(0);
            addElement(list, firstTitle.text());
            final Elements table = ul.select("li");
            for(Element element : table) {
                addElement(list, element.text());
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        title = prefixZeros(title, 2);
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("ul.item-list li a[href]")) {
                final String text = element.text().substring(8);
                final String[] values = text.split(":");
                final String chapter = values[0], value = text.split(chapter + ": ")[1], correctedChapter = chapter.substring(chapter.startsWith("00") ? 2 : chapter.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        title = prefixZeros(title, 2);
        chapter = prefixZeros(chapter, 3);
        final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.main ul li + li")) {
                final String text = element.text();
                final String[] values = text.substring(2+(text.startsWith("§§") ? 1 : 0)).split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1];
                list.add(new Element(statute).appendChild(new TextNode(statute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        final int substring = section.length();
        title = prefixZeros(title, 2);
        chapter = prefixZeros(chapter, 3);
        section = prefixZeros(section, 5);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements elements = doc.select("ul.item-list p.MsoPlainText");
                final String topic = elements.get(0).text().substring(4+substring);
                elements.remove(0);
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
