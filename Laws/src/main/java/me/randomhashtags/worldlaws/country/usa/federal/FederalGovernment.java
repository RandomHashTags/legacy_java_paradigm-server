package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class FederalGovernment extends LawSubdivisionController {
    public static final FederalGovernment INSTANCE = new FederalGovernment(
            "https://uscode.house.gov",
            "https://uscode.house.gov/browse/prelim@title%index%&edition=prelim",
            "https://uscode.house.gov/browse/prelim@title%index%/chapter%chapter%&edition=prelim",
            "https://uscode.house.gov/view.xhtml?req=granuleid:USC-prelim-title%index%-section%section%&num=0&edition=prelim"
    );

    FederalGovernment(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    private void addIndex(List<Element> list, Element element) {
        final String text = element.text();
        final String[] values = text.split(" ");
        final String title = values[0] + " " + values[1], value = text.split(title + " - ")[1].split("Authenticated PDF")[0];
        list.add(new Element(title).appendChild(new TextNode(title)));
        list.add(new Element(value).appendChild(new TextNode(value)));
    }
    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("table.table tbody tr td");
            table.remove(0);
            int index = 1;
            final List<Element> list = new ArrayList<>(), after = new ArrayList<>();
            for(Element element : table) {
                addIndex(index % 2 == 0 ? list : after, element);
                index++;
            }
            list.addAll(after);
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final List<Element> list = new ArrayList<>();
            for(Element element : doc.select("section ul")) {
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
            for(Element element : doc.select("article p.section-label")) {
                final String text = element.text().substring(2);
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
        final String path = title + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements article = doc.select("article"), labels = article.select("p.section-label");
                final HashSet<String> set = new HashSet<>();
                for(Element label : labels) {
                    set.add(label.text());
                }
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true, foundTitle = false;
                for(Element element : article.select("p")) {
                    final String text = element.text();
                    if(set.contains(text)) {
                        if(foundTitle) {
                            break;
                        }
                        foundTitle = text.substring(2).startsWith(section);
                    } else if(foundTitle) {
                        description.append(isFirst ? "" : "\n").append(text);
                        isFirst = false;
                    }
                }
                final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), null, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
