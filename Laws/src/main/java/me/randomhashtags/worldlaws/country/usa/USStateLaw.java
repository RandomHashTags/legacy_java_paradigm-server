package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.country.State;

import java.util.HashMap;

public abstract class USStateLaw implements State {

    private final String indexesURL, tableOfChaptersURL, statutesListURL, statuteURL;
    private String indexesJSON;
    private HashMap<String, String> tableOfChaptersJSON, statutesJSON, statutes;

    public USStateLaw(String indexesURL, String tableOfChaptersURL, String statutesListURL, String statuteURL) {
        this.indexesURL = indexesURL;
        this.tableOfChaptersURL = tableOfChaptersURL;
        this.statutesListURL = statutesListURL;
        this.statuteURL = statuteURL;
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
        }
        return indexesJSON;
    }

    @Override
    public String getTableOfChaptersJSON() {
        return tableOfChaptersJSON.toString();
    }
}
