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

public final class Indiana extends LawSubdivisionController {
    public static final Indiana INSTANCE = new Indiana(
            "http://iga.in.gov/legislative/laws/2020/ic/titles/001",
            "http://iga.in.gov/legislative/laws/2020/ic/titles/%index%",
            "http://iga.in.gov/legislative/laws/2020/ic/titles/%index%#%index%-%chapter%",
            "http://iga.in.gov/legislative/laws/2020/ic/titles/%index%#%index%-%article%-%chapter%-%section%"
    );

    Indiana(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div.tabable ul.nav li.title_text");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split("\\.");
                final String title = values[0] + (values.length == 3 ? "." + values[1] : ""), value = text.split(title + "\\. ")[1];
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }

    @Override
    public void loadTableOfChapters(String title) {
        title = prefixZeros(title, 3);
        final String url = tableOfChaptersURL.replace("%index%", title);
        final Document doc = getDocument(url);
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.tabable ul.nav li.title_text + div ul div div li")) {
                final String text = element.text();
                final String[] values = text.split("\\.");
                final String chapter = values[0], value = text.split(chapter + "\\. ")[1];
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
        final String path = chapter + "." + section;
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
