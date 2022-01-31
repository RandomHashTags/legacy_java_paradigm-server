package me.randomhashtags.worldlaws.service.usa.state;

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

public final class NorthDakota extends LawSubdivisionController {
    public static final NorthDakota INSTANCE = new NorthDakota(
            "https://www.legis.nd.gov/general-information/north-dakota-century-code",
            "https://www.legis.nd.gov/cencode/t%index%.html",
            "https://www.legis.nd.gov/cencode/t%index%c%chapter%.html",
            "https://www.legis.nd.gov/cencode/t%index%c%chapter%.pdf#nameddest=%index%-%chapter%-%section%"
    );

    NorthDakota(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        super(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements column = doc.select("div.three-column"), titles = column.select("a[href]");
            final List<Element> list = new ArrayList<>();
            for(Element element : column) {
                final String text = element.text();
                final String[] values = text.split(" ");
                String title = "", previousTitle = "";
                boolean isFirst = true, isFirstTitleIndex = true;
                for(String value : values) {
                    final String key = value.split("\\.")[0];
                    if(key.matches("[0-9]+")) {
                        if(isFirst) {
                            isFirst = false;
                        } else {
                            list.add(new Element(title).appendChild(new TextNode(title)));
                            list.add(new Element(previousTitle).appendChild(new TextNode(previousTitle)));
                        }
                        title = value;
                        previousTitle = "";
                        isFirstTitleIndex = true;
                    } else {
                        previousTitle = previousTitle.concat((isFirstTitleIndex ? "" : " ") + value);
                        isFirstTitleIndex = false;
                    }
                }
            }
            iterateThroughIndexTable(new Elements(list));
        }
        return chapters;
    }
    @Override
    public void loadTableOfChapters(String title) {
        final int substring = title.length()+1;
        title = prefixZeros(title.replace(".", "-"), 2+(title.contains(".") ? 2 : 0));
        final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
        if(doc != null) {
            final Elements elements = doc.select("table.simple-table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : elements) {
                final String text = element.text().substring(substring);
                final String[] values = text.split(" ");
                final String chapter = values[0], value = text.split(chapter + " " + values[1] + " " + (text.contains("Sections") ? values[2] + " " : ""))[1], correctedChapter = chapter.substring(chapter.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedChapter).appendChild(new TextNode(correctedChapter)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(title, new Elements(list));
        }
    }
    @Override
    public void loadStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        int substring = 2+title.length();
        title = prefixZeros(title.replace(".", "-"), 2+(title.contains(".") ? 2 : 0));
        chapter = prefixZeros(chapter.replace(".", "-"), 2+(chapter.contains(".") ? 2 : 0));
        substring += chapter.length();
        final Document doc = getDocument(statutesListURL.replace("%index%", title).replaceFirst("%chapter%", chapter));
        if(doc != null) {
            final Elements elements = doc.select("table.simple-table tbody tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : elements) {
                final String text = element.text().substring(substring);
                final String[] values = text.split(" ");
                final String statute = values[0], value = text.split(statute + " ")[1], correctedStatute = statute.substring(statute.startsWith("0") ? 1 : 0);
                list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughStatuteTable(path, new Elements(list));
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
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
                final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
                final String string = statute.toString();
                statutes.put(path, string);
                return string;
            }
            return null;
        }
    }
}
