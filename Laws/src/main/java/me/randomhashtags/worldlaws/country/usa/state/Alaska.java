package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.RomanNumeral;
import me.randomhashtags.worldlaws.LawSubdivisionController;
import me.randomhashtags.worldlaws.country.SubdivisionStatuteIndex;
import me.randomhashtags.worldlaws.country.StateReference;
import me.randomhashtags.worldlaws.country.SubdivisionStatute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Alaska implements LawSubdivisionController {
    INSTANCE("https://www.ecfr.gov/cgi-bin/ECFR?page=browse",
            "https://www.ecfr.gov/%token%",
            "https://www.ecfr.gov/cgi-bin/text-idx?SID=%token%&mc=true&tpl=/ecfrbrowse/Title%title%/%title%chapter%chapter%.tpl",
            "https://www.ecfr.gov/cgi-bin/text-idx?SID=%token%&mc=true&node=se%index%.1.%chapter%_1%section%"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL, pageToken;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes, tokenIDs;

    Alaska(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

        tableOfChaptersJSON = new HashMap<>();
        statutesJSON = new HashMap<>();
        statutes = new HashMap<>();
        tokenIDs = new HashMap<>();
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
    public String getIndexesJSON() {
        if(indexesJSON == null) {
            indexesJSON = new StringBuilder("[");
            getIndexes();
            indexesJSON.append("]");
        }
        return indexesJSON.toString();
    }
    @Override
    public String getTableOfChaptersJSON() {
        return tableOfChaptersJSON.toString();
    }

    @Override
    public List<SubdivisionStatuteIndex> getIndexes() {
        final List<SubdivisionStatuteIndex> indexes = new ArrayList<>();
        final Document doc = getDocument(indexesURL);
        if(doc != null) {
            final Elements table = doc.select("option");
            iterateIndexTable(table, indexesJSON, true, true);
            for(Element element : table) {
                final String value = element.val();
                if(value != null && !value.isEmpty()) {
                    if(pageToken == null) {
                        pageToken = value.split("SID=")[1].split("&mc")[0];
                    }
                    tokenIDs.put(element.text().split(" - ")[1].replace(" ", "_"), value);
                }
            }
        }
        return indexes;
    }
    @Override
    public String getTableOfChapters(String title) {
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%token%", tokenIDs.get(title)));

            if(doc != null) {
                final List<Element> table = new ArrayList<>();
                for(int i = 1; i <= doc.select("tr a[href]").size(); i++) {
                    final String string = Integer.toString(i);
                    table.add(new Element(string).appendChild(new TextNode(string)));
                }

                int index = 1;
                final List<Element> list = new ArrayList<>();
                for(Element element : doc.select("td.titlepage")) {
                    if(!element.text().equals("[RESERVED]")) {
                        list.add(element);
                    } else {
                        table.remove(index-1);
                        index++;
                        final String string = Integer.toString(index);
                        table.add(new Element(string).appendChild(new TextNode(string)));
                    }
                    index++;
                }
                iterateIndexTable(new Elements(table), builder, false, false, new Elements(list));
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            return string;
        }
    }

    @Override
    public String getStatuteList(String title, String chapter) {
        final String path = title + "." + chapter;
        if(statutesJSON.containsKey(path)) {
            return statutesJSON.get(path);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final String zeroedTitle = prefixZeros(title, 2), romanChapter = RomanNumeral.toRoman(Integer.parseInt(chapter));
            final String url = statutesListURL.replace("%token%", pageToken).replaceFirst("%title%", zeroedTitle).replace("%title%", title).replace("%chapter%", romanChapter);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements table = doc.select("div div div table tbody tr p ~ table");
                table.remove(0);
                table.remove(0);

                final List<Element> list = new ArrayList<>();
                for(Element element : table) {
                    final String text = element.text();
                    final boolean isReserved = text.contains("[RESERVED]");
                    if(!isReserved) {
                        final String indexString = text.split(" ")[0];
                        final int index = Integer.parseInt(indexString);
                        final String string = text.substring(indexString.length()+1);
                        final String[] values = string.split(" ");
                        final boolean isRange = values.length > 1 && values[1].equals("to");
                        final String statutes = isRange ? values[0] + " " + values[1] + " " + values[2] : values[0], topic = text.split(statutes + " ")[1];
                        final String statuteList = isRange ? statutes.replace(index + ".", "").replace(" to ", "-") : statutes;
                        final Element topicElement = new Element(topic).appendChild(new TextNode(topic));
                        if(isRange) {
                            final String[] range = statuteList.split("-");
                            final int starting = Integer.parseInt(range[0]), ending = Integer.parseInt(range[1]);
                            for(int i = starting; i <= ending; i++) {
                                final String value = index + "." + i;
                                list.add(new Element(value).appendChild(new TextNode(value)));
                                list.add(topicElement);
                            }
                        } else {
                            list.add(new Element(statuteList).appendChild(new TextNode(statuteList)));
                            list.add(topicElement);
                        }
                    }
                }
                iterateThroughChapterTable(new Elements(list), builder, false);
            }
            builder.append("]");
            final String string = builder.toString();
            statutesJSON.put(path, string);
            return string;
        }
    }
    @Override
    public String getStatute(String title, String chapter, String section) {
        final String path = title + "." + chapter + "." + section;
        chapter = section.split("\\.")[0];
        if(statutes.containsKey(path)) {
            return statutes.get(path);
        } else {
            final String url = statuteURL.replace("%token%", pageToken).replace("%index%", title).replace("%chapter%", chapter).replace("%section%", section.split("\\.")[1]);
            final Document doc = getDocument(url);
            String topic = "";
            final StringBuilder description = new StringBuilder();
            if(doc != null) {
                final Elements layout = doc.select("div.two-col-layout-right");
                topic = layout.select("h2").text();
                final String[] values = topic.split(" ");
                topic = topic.split(values[0] + " ")[1];

                final Elements table = layout.select("p ~ p");
                table.remove(table.size()-1);

                final String last = table.get(table.size()-1).text();
                final boolean lastIsHistory = last.startsWith("[") && last.endsWith("]");
                if(lastIsHistory) {
                    table.remove(table.size()-1);
                }

                boolean isFirst = true;
                for(Element element : table) {
                    description.append(isFirst ? "" : "\n").append(element.text());
                    isFirst = false;
                }
            }

            final SubdivisionStatute statute = new SubdivisionStatute(StateReference.build(title, chapter, section, url), topic, description.toString());
            final String string = statute.toString();
            statutes.put(path, string);
            return string;
        }
    }
}
