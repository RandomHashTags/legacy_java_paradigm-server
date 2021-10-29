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
import java.util.List;

public final class Michigan extends LawSubdivisionController {
    public static final Michigan INSTANCE = new Michigan (
            "http://www.legislature.mi.gov/(S(51fchyizumt3j1zpocuttlkt))/mileg.aspx?page=ChapterIndex",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=GetObject&objectname=mcl-chap%index%",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=getObject&objectName=mcl-%chapter%",
            "http://www.legislature.mi.gov/(S(5jwm0hrcibdqcxn2prf5wfgq))/mileg.aspx?page=getObject&objectName=mcl-%index%-%section%"
    );

    Michigan(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div form div article table tbody tr");
            for(int i = 1; i <= 3; i++) {
                table.remove(0);
            }
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" Chapter ");
                final String title = values[0].replace("Chapters", "").replace("Chapter", ""), value = values[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final Elements table = doc.select("div form div article table tbody tr");
            for(int i = 1; i <= 7; i++) {
                table.remove(0);
            }
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" Statute ");
                final String chapter = values[0], value = values[1].split(" \\(")[0];
                list.add(new Element(chapter).appendChild(new TextNode(chapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
        if(doc != null) {
            final int substring = 1+title.length();
            final Elements table = doc.select("div form div article table tbody tr");
            table.removeIf(element -> !element.text().startsWith("Section "));
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" Section ");
                final String statute = values[0].split("Section ")[1].substring(substring), value = values[1];
                list.add(new Element(statute).appendChild(new TextNode(statute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
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
                final Elements span = doc.select("div form div article span"), elements = span.select("div");
                final String topic = span.select("center ~ b").get(0).text().substring(2+title.length()+section.length());
                final Element history = span.select("font").get(0);
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
