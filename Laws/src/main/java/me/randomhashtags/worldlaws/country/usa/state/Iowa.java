package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class Iowa extends LawSubdivisionController {
    public static final Iowa INSTANCE = new Iowa(
            "https://www.legis.iowa.gov/law/iowaCode",
            "https://www.legis.iowa.gov/law/iowaCode/chapters?title=%index%&year=2020",
            "https://www.legis.iowa.gov/law/iowaCode/sections?codeChapter=%chapter%&year=2020",
            ""
    );

    Iowa(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("table.standard tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" - ");
                final String title = values[0], value = text.split(title + " - ")[1].split(" \\(Ch")[0];
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
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("table.standard tbody tr")) {
                final String text = element.text();
                final String[] values = text.split(" - ");
                final String chapter = values[0], value = text.split(chapter + " - ")[1].split(" Sections PDF RTF")[0];
                list.add(new Element(chapter).appendChild(new TextNode(chapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            final String suffix = " PDF RTF";
            for(Element element : doc.select("table.standard tbody tr")) {
                final String text = element.text();
                final String[] values = text.split(" - ");
                final boolean isReserved = values.length == 1;
                final String statute = isReserved ? values[0].split(suffix)[0] : values[0].substring(1);
                final String value = !isReserved ? text.split(statute + " - ")[1].split(suffix)[0] : "RESERVED";
                list.add(new Element(statute).appendChild(new TextNode(statute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final Document doc = getDocument(statuteURL.replace("%chapter%", chapter).replace("%section%", section));
            if(doc != null) {
                final Elements desc = doc.select("div.content h2 ~ p");
                //final StateStatute statute = new StateStatute(null, element, chapter, section);
                //final String string = statute.toString();
                //statutes.put(path, string);
                //return string;
            }
            return null;
        }
    }
}
