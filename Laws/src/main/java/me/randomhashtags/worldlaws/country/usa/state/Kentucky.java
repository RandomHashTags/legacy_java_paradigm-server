package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Kentucky implements LawSubdivisionController {
    INSTANCE(
            "https://apps.legislature.ky.gov/law/kar/titles.htm",
            "https://apps.legislature.ky.gov/law/kar/TITLE%index%.HTM",
            "https://apps.legislature.ky.gov/law/kar/TITLE%chapter%.HTM",
            "https://www.revisor.mn.gov/statutes/cite/%chapter%.%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private int chapters, sections;
    private HashMap<String, String> statutes;

    Kentucky(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("div.ContentAreaNote li")) {
                final String text = element.text(), key = text.split(" - ")[0], correctedKey = key.replace("TITLE", "Title");
                list.add(new Element(correctedKey).appendChild(new TextNode(correctedKey)));
                final String value = text.split(key + " - ")[1];
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
            for(Element element : doc.select("div.ContentAreaNote4 p")) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String chapter = values[0] + " " + values[1], chapterTitle = text.split(chapter + " ")[1];
                list.add(new Element(chapter).appendChild(new TextNode(chapter)));
                list.add(new Element(chapterTitle).appendChild(new TextNode(chapterTitle)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        final Document doc = getDocument(statutesListURL.replace("%chapter%", chapter));
        if(doc != null) {
            final Elements table = doc.select("td");
            iterateThroughStatuteTable(path, table);
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
                final String docTitle = doc.title();
                final Elements breadcrumb = doc.select("div.mb-3 a[href]");
                final Elements sections = doc.select("div.section");
                if(sections.isEmpty()) {
                    System.out.println(docTitle + ": sections are empty!");
                } else {
                    final int size = sections.size();
                    System.out.println(docTitle + ": section size=" + size);
                    if(size == 1) {
                        final Element element = sections.get(0);
                        //final StateStatute statute = new StateStatute(breadcrumb, element, chapter, section);
                        //final String string = statute.toString();
                        //statutes.put(path, string);
                        //return string;
                    }
                }
            }
            return null;
        }
    }
}
