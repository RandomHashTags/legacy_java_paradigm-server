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

public final class SouthDakota extends LawSubdivisionController {
    public static final SouthDakota INSTANCE = new SouthDakota(
            "https://sdlegislature.gov/statutes/Codified_Laws/",
            "https://sdlegislature.gov/Statutes/Codified_Laws/DisplayStatute.aspx?Type=Statute&Statute=%index%",
            "https://sdlegislature.gov/Statutes/Codified_Laws/DisplayStatute.aspx?Type=Statute&Statute=%index%-%chapter%",
            "https://sdlegislature.gov/Statutes/Codified_Laws/DisplayStatute.aspx?Type=Statute&Statute=%index%-%chapter%-%section%"
    );

    SouthDakota(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("table.table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = values[0], value = text.split(title + " ")[1];
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
            final Elements elements = doc.select("div.Results div div p");
            for(int i = 1; i <= 5; i++) {
                elements.remove(0);
            }
            final List<Element> list = new ArrayList<>();
            for(Element element : elements) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String chapter = values[0], value = text.split(chapter + " ")[1], correctedChapter = chapter.substring(chapter.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
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
            final int substring = 2+title.length()+chapter.length();
            final Elements elements = doc.select("div.Results div div p");
            final List<Element> list = new ArrayList<>();
            if(elements.size() == 2) {
                final String text = elements.get(0).text();
                final Elements statutes = elements.select("a[href]");
            } else {
                for(int i = 1; i <= 3; i++) {
                    elements.remove(0);
                }
                for(Element element : elements) {
                    final String text = element.text().substring(substring);
                    final String[] values = text.split(" ");
                    final String statute = values[0], value = text.split(statute + " ")[1];
                    list.add(new Element(statute).appendChild(new TextNode(statute)));
                    list.add(new Element(value).appendChild(new TextNode(value)));
                }
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public SubdivisionStatute loadStatute(String title, String chapter, String section) {
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
            return new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
        }
        return null;
    }
}
