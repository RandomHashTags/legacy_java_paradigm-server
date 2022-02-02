package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class Montana extends LawSubdivisionController {
    public static final Montana INSTANCE = new Montana(
            "https://leg.mt.gov/bills/mca/index.html",
            "https://leg.mt.gov/bills/mca/title_%index%0/chapters_index.html",
            "https://leg.mt.gov/bills/mca/title_%index%/chapter_%chapter%/parts_index.html",
            ""
    );

    Montana(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div.title-toc-content ul li");
            table.remove(0);
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split("\\.");
                final String title = values[0], value = text.split(title + "\\. ")[1], correctedTitle = title.replace("TITLES", "Title").replace("TITLE", "Title").replace("AND", "and");
                list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        title = prefixZeros(title, 3);
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.chapter-toc-content ul li")) {
                final String text = element.text();
                final String[] values = text.split("\\."), spaces = text.split(" ");
                final boolean isChapter = values.length > 1, containsThrough = text.contains(" THROUGH ");
                final String chapter = isChapter ? values[0] : spaces[0] + " " + spaces[1] + (containsThrough ? " " + spaces[2] + " " + spaces[3] : ""), value = text.split(chapter + (isChapter ? "\\." : " "))[1];
                final String correctedTitle = chapter.replace("CHAPTERS ", "").replace("CHAPTER ", "").replace(" THROUGH ", "-");
                if(containsThrough) {
                    final String[] chapterValues = correctedTitle.split("-");
                    final int starting = Integer.parseInt(chapterValues[0]), max = Integer.parseInt(chapterValues[1]);
                    for(int i = starting; i <= max; i++) {
                        final String number = Integer.toString(i);
                        list.add(new Element(number).appendChild(new TextNode(number)));
                        list.add(new Element(value).appendChild(new TextNode(value)));
                    }
                } else {
                    list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                    list.add(new Element(value).appendChild(new TextNode(value)));
                }
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
