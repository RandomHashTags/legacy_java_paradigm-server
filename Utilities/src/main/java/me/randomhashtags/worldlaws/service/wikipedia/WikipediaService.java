package me.randomhashtags.worldlaws.service.wikipedia;

import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.LinkedHashMap;

public interface WikipediaService extends RestAPI, Jsonable, Jsoupable {

    static String getPictureThumbnailImageURL(Element img) {
        return img.attr("src").replaceAll("/[0-9]+px-", "/%quality%px-");
    }

    default WikipediaDocument getWikipediaDocument(String url) {
        return new WikipediaDocument(url);
    }


    // API
    // https://en.wikipedia.org/w/api.php?action=query&origin=*&format=json&generator=search&gsrnamespace=0&gsrlimit=5&gsrsearch='New_England_Patriots'
    static void getWikipediaPage(String wikipediaPageSlug) {
        final String url = "https://en.wikipedia.org/w/api.php";
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "application/json");
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("action", "query");
        query.put("prop", "revisions");
        query.put("titles", wikipediaPageSlug);
        query.put("rvslots", "*");
        query.put("rvprop", "content");
        query.put("formatversion", "2");
        final JSONObject json = RestAPI.requestStaticJSONObject(url, headers, query);
        WLLogger.logInfo("WikipediaService;getWikipediaPage;wikipediaPageSlug=" + wikipediaPageSlug + ";json=" + (json != null ? json.toString() : "null"));
    }
}
