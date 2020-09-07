package me.randomhashtags.worldlaws;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public interface Jsoupable {
    default Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            return null;
        }
    }
}
