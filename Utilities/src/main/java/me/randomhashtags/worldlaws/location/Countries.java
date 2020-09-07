package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

// https://simple.wikipedia.org/wiki/List_of_countries
public enum Countries implements Jsoupable {
    INSTANCE;

    private static HashMap<String, CustomCountry> COUNTRIES;

    public void init() {
        final String url = "https://simple.wikipedia.org/wiki/List_of_countries";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("table.sortable tbody tr");
            table.removeIf(row -> row.attr("style") != null);
            table.removeIf(row -> row.select("td").get(0).select("b a[href]").size() == 0);
            for(Element row : table) {
                final Elements tds = row.select("td");
                final Element nameElement = tds.get(0).select("b a[href]").get(0);
                final String targetURL = "https://simple.wikipedia.org" + nameElement.attr("href");
                final CustomCountry country = createCountry(targetURL);
            }
        }
    }

    private CustomCountry createCountry(String url) {
        final Document doc = getDocument(url);
        if(doc != null) {
            final CustomCountry country = new CustomCountry(doc);
            COUNTRIES.put(country.getBackendID(), country);
        }
        return null;
    }

    public static CustomCountry get(String name) {
        return COUNTRIES.getOrDefault(name.toLowerCase().replace(" ", ""), null);
    }
}
