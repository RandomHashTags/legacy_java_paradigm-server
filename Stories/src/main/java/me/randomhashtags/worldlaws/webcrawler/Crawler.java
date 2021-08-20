package me.randomhashtags.worldlaws.webcrawler;

import me.randomhashtags.worldlaws.CompletionHandler;

public interface Crawler {
    String getSiteDomain();
    String[] getKeywords();
    CompletionHandler getHandler();
}
