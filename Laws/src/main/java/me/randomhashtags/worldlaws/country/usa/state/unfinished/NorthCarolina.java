package me.randomhashtags.worldlaws.country.usa.state.unfinished;

import me.randomhashtags.worldlaws.law.StateIndex;
import me.randomhashtags.worldlaws.law.State;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum NorthCarolina implements State {
    INSTANCE(
            "https://www.ncleg.gov/Laws/GeneralStatutesTOC",
            "https://www.ncleg.gov/Laws/GeneralStatuteSections/Chapter%index%",
            "",
            "https://www.ncleg.gov/enactedlegislation/statutes/pdf/bysection/chapter_%chapter%/gs_%chapter%-%section%.pdf"
    );

    private String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private StringBuilder indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    NorthCarolina(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;

        tableOfChaptersJSON = new HashMap<>();
        statutesJSON = new HashMap<>();
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
    public List<StateIndex> getIndexes() {
        final List<StateIndex> chapters = new ArrayList<>();
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
            iterateIndexTable(new Elements(list), indexesJSON, true);
        }
        return chapters;
    }

    @Override
    public String getTableOfChapters(String title) {
        if(tableOfChaptersJSON.containsKey(title)) {
            return tableOfChaptersJSON.get(title);
        } else {
            final StringBuilder builder = new StringBuilder("[");
            final Document doc = getDocument(tableOfChaptersURL.replace("%index%", title));
            if(doc != null) {
                // pdf decoder
            }
            builder.append("]");
            final String string = builder.toString();
            tableOfChaptersJSON.put(title, string);
            return string;
        }
    }

    @Override
    public String getStatuteList(String title, String chapter) {
        return null;
    }

    @Override
    public String getStatute(String title, String chapter, String section) {
        return null;
    }
}
