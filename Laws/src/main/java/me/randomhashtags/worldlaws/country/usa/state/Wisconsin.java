package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Wisconsin extends LawSubdivisionController {
    public static final Wisconsin INSTANCE = new Wisconsin(
            "https://docs.legis.wisconsin.gov/statutes/prefaces/toc",
            "https://docs.legis.wisconsin.gov/statutes/statutes/%index%",
            "https://docs.legis.wisconsin.gov/statutes/statutes/%index%",
            "https://docs.legis.wisconsin.gov/document/statutes/%index%/%chapter%"
    );

    private HashMap<Integer, List<String>> chapters;

    Wisconsin(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div.statutes div.qs_toc_head_bold_");
            int index = 1;
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text(), title = "Title " + index;
                list.add(new Element(title).appendChild(new TextNode(title)));
                list.add(new Element(text).appendChild(new TextNode(text)));
                index++;
            }
            iterateThroughIndexTable(new Elements(list));
            loadChapters(doc);
        }
        return chapters;
    }

    private void loadChapters(Document doc) {
        chapters = new HashMap<>();
        int index = 1;
        for(Element element : doc.select("div.statutes div.qs_toc_head_bold_ ~ div")) {
            final String text = element.text();
            if(!text.isEmpty()) {
                final boolean isChapter = Character.isDigit(text.charAt(0));
                if(isChapter) {
                    if(!chapters.containsKey(index)) {
                        chapters.put(index, new ArrayList<>());
                    }
                    final String[] values = text.split("\\.");
                    chapters.get(index).add(text.split(values[0] + "\\. ")[1].split(" \\(PDF")[0]);
                } else {
                    index++;
                }
            }
        }
    }
    @Override
    public void loadTableOfChapters(String title) {
        final int titleInt = Integer.parseInt(title);
        final List<String> list = chapters.get(titleInt);
        int startingChapter = 1;
        for(int i = 1; i < titleInt; i++) {
            startingChapter += chapters.get(i).size();
        }
        final List<Element> elements = new ArrayList<>();
        for(String string : list) {
            final String chapter = Integer.toString(startingChapter);
            elements.add(new Element(chapter).appendChild(new TextNode(chapter)));
            elements.add(new Element(string).appendChild(new TextNode(string)));
            startingChapter += 1;
        }
        iterateThroughChapterTable(title, new Elements(elements));
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        final Document doc = getDocument(statutesListURL.replace("%index%", chapter));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.statutes div.qs_toc_entry_")) {
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
