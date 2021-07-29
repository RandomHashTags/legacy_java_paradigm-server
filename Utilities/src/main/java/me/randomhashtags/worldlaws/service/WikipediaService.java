package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.RestAPI;
import org.jsoup.nodes.Document;

public interface WikipediaService extends RestAPI, Jsonable, Jsoupable {

    default WikipediaDocument getWikipediaDocument(String url) {
        final Document doc = getDocument(url);
        return doc != null ? new WikipediaDocument(doc) : null;
    }
}
