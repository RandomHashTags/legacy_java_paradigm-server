package me.randomhashtags.worldlaws.country.usa.state.unfinished;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class Connecticut extends LawSubdivisionController {
    public static final Connecticut INSTANCE = new Connecticut(
            "https://www.cga.ct.gov/2019/pub/titles.htm",
            "https://www.cga.ct.gov/current/pub/title_%index%.htm",
            "https://www.cga.ct.gov/current/pub/chap_%chapter%.htm",
            ""
    );

    Connecticut(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        System.out.println("doc=" + doc);
        if(doc != null) {
            final Elements table = doc.select("table tbody tr td a[href]");
            iterateThroughIndexTable(table);
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.vc-column-innner-wrapper tr")) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String chapter = values[0] + " " + values[1], value = text.split(chapter + " ")[1].split(" Download Entire Chapter \\(PDF\\)")[0];
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
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.vc-column-innner-wrapper tr")) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1];
                list.add(new Element(statute).appendChild(new TextNode(statute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public SubdivisionStatute loadStatute(String title, String chapter, String section) {
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
