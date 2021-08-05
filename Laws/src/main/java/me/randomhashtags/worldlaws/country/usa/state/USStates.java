package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public final class USStates extends USState {
    public static USStates INSTANCE = new USStates();

    private USState get(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        return new USState(indexesURL, tableOfChaptersURL, statutesListURL, statuteURL);
    }

    public USState IDAHO = get(
            "https://legislature.idaho.gov/statutesrules/idstat/",
            "https://legislature.idaho.gov/statutesrules/idstat/Title%index%",
            "https://legislature.idaho.gov/statutesrules/idstat/Title%index%/T%index%CH%chapter%/",
            "https://legislature.idaho.gov/statutesrules/idstat/Title%index%/T%index%CH%chapter%/SECT%index%-%chapter%%section%/"
    );
    public USState SOUTH_DAKOTA = get(
            "https://sdlegislature.gov/statutes/Codified_Laws/",
            "https://sdlegislature.gov/Statutes/Codified_Laws/DisplayStatute.aspx?Type=Statute&Statute=%index%",
            "https://sdlegislature.gov/Statutes/Codified_Laws/DisplayStatute.aspx?Type=Statute&Statute=%index%-%chapter%",
            "https://sdlegislature.gov/Statutes/Codified_Laws/DisplayStatute.aspx?Type=Statute&Statute=%index%-%chapter%-%section%"
    );

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        if(this == IDAHO) {
            return getIdahoIndexes();
        }
        return super.getIndexes();
    }
    @Override
    public String getTableOfChapters(String title) {
        if(this == IDAHO) {
            return getIdahoTableOfChapters(title);
        }
        return super.getTableOfChapters(title);
    }
    @Override
    public String getStatuteList(String title, String chapter) {
        if(this == IDAHO) {
            return getIdahoStatuteList(title, chapter);
        }
        return super.getStatuteList(title, chapter);
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        if(this == IDAHO) {
            return getIdahoStatute(title, chapter, section);
        }
        return super.getStatute(title, chapter, section);
    }

    private List<SubdivisionStatuteIndex> getIdahoIndexes() {
        final List<SubdivisionStatuteIndex> chapters = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("div.vc-column-innner-wrapper tr");
            final List<Element> list = new ArrayList<>();
            for(Element element : table) {
                final String text = element.text();
                final String[] values = text.split(" ");
                final String title = values[0] + " " + values[1], value = text.split(title + " ")[1], correctedTitle = title.replace("TITLE", "Title");
                list.add(new Element(correctedTitle).appendChild(new TextNode(correctedTitle)));
                list.add(new Element(value).appendChild(new TextNode(value)));
            }
            iterateThroughChapterTable(new Elements(list), indexesJSON, true);
        }
        return chapters;
    }

    private String getIdahoTableOfChapters(String title) {
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
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
                iterateThroughChapterTable(new Elements(list), builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            return string;
        }
    }

    private String getIdahoStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(statutesListURL.replace("%index%", title).replace("%chapter%", chapter));
            if(doc != null) {
                final int substring = chapter.length();
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("div.vc-column-innner-wrapper tr")) {
                    final String text = element.text();
                    final String[] values = text.split(" ");
                    final String statute = values[0], value = text.split(statute + " ")[1], statuteValue = statute.split("-")[1].substring(substring);
                    final String correctedStatute = statuteValue.substring(statuteValue.startsWith("0") ? 1 : 0);
                    list.add(new Element(correctedStatute).appendChild(new TextNode(correctedStatute)));
                    list.add(new Element(value).appendChild(new TextNode(value)));
                }
                iterateThroughChapterTable(new Elements(list), builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            statutesJSON.put(path, string);
            return string;
        }
    }

    private String getIdahoStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        section = prefixZeros(section, 2);
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements elements = doc.select("div.pgbrk div + div");
                final String topic = elements.get(2).text();
                for(int i = 1; i <= 3; i++) {
                    elements.remove(0);
                }
                final Element history = elements.last();
                elements.remove(history);
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
