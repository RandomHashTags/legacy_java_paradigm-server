package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.RestAPI;
import org.jsoup.nodes.Element;

public interface WikipediaService extends RestAPI, Jsonable, Jsoupable {

    static String getPictureThumbnailImageURL(Element img) {
        return img.attr("src").replaceAll("/[0-9]+px-", "/%quality%px-");
    }

    default WikipediaDocument getWikipediaDocument(String url) {
        return new WikipediaDocument(url);
    }
}
