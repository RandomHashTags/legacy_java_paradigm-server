package me.randomhashtags.worldlaws.country.usa.state.unfinished;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class NorthCarolina extends LawSubdivisionController {
    public static final NorthCarolina INSTANCE = new NorthCarolina(
            "https://www.ncleg.gov/Laws/GeneralStatutesTOC",
            "https://www.ncleg.gov/Laws/GeneralStatuteSections/Chapter%index%",
            "",
            "https://www.ncleg.gov/enactedlegislation/statutes/pdf/bysection/chapter_%chapter%/gs_%chapter%-%section%.pdf"
    );

    NorthCarolina(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div.col-9 a[href]");

            final List<Element> list = new ArrayList<>();
            int index = 1;
            String chapter = "";
            for(Element element : table) {
                if(index % 2 == 0) {
                    final String text = chapter + " - " + element.text();
                    final Element target = new Element(text);
                    target.appendChild(new TextNode(text));
                    list.add(target);
                } else {
                    chapter = element.text();
                }
                index++;
            }
            iterateIndexTable(new Elements(list), true);
        }
        return chapters;
    }

    @Override
    public void loadTableOfChapters(String title) {
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            // pdf decoder
        }
    }

    @Override
    public void loadStatuteList(String title, String chapter) {
    }

    @Override
    public String getStatute(String title, String chapter, String section) {
        return null;
    }
}
