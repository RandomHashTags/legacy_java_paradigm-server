package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public final class CustomCountry implements Jsoupable {
    private Document page;
    private Elements infobox;
    private String backendID, name, shortName, flagURL, flagEmoji;
    private String moto;
    private Location location;
    private CountryInformation info;

    public CustomCountry(Document page) {
        this.page = page;
        init();
    }
    public Document getPage() {
        return page;
    }
    public String getBackendID() {
        return backendID;
    }
    public String getName() {
        return name;
    }

    private void init() {
        infobox = page.select("table.infobox tbody tr");
        name = infobox.get(0).text();
        backendID = name.toLowerCase().replace(" ", "");
    }

    public CountryInformation getInformation() {
        if(info != null) {
            return info;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"backendID\":\"" + getBackendID() + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"shortName\":\"" + (shortName != null ? shortName : name) + "\"," +
                "\"flagURL\":\"" + flagURL + "\"," +
                "\"flagEmoji\":\"" + flagEmoji + "\"," +
                "\"location\":" + (location != null ? location.toString() : "null") +
                "}";
    }
}
