package me.randomhashtags.worldlaws.country.usa.state;

import me.randomhashtags.worldlaws.country.State;
import me.randomhashtags.worldlaws.country.StateIndex;

import java.util.HashMap;
import java.util.List;

public class USState implements State {

    protected String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    protected StringBuilder indexesJSON;
    protected HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    public USState() {
    }
    public USState(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
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
        return null;
    }

    @Override
    public String getTableOfChapters(String title) {
        return null;
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
