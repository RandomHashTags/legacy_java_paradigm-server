package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.nodes.Document;

import java.util.HashMap;

// https://simple.wikipedia.org/wiki/List_of_countries
public enum Countries implements Jsoupable {
    INSTANCE;

    private HashMap<String, CustomCountry> countries;

    public void init() {
        final String url = "https://simple.wikipedia.org/wiki/List_of_countries";
        final Document doc = getDocument(url);
        if(doc != null) {

        }
    }

    private CustomCountry createCountry(String name, String url) {

        return new CustomCountry();
    }
}
