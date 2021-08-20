package me.randomhashtags.worldlaws.webcrawler;

import me.randomhashtags.worldlaws.CompletionHandler;

public final class SiteCrawler implements Crawler {

    private final String siteDomain;
    private final String[] keywords;
    private final CompletionHandler handler;

    public SiteCrawler(String siteDomain, CompletionHandler handler, String...keywords) {
        this.siteDomain = siteDomain;
        this.handler = handler;
        this.keywords = keywords;
    }

    @Override
    public String getSiteDomain() {
        return siteDomain;
    }

    @Override
    public String[] getKeywords() {
        return keywords;
    }

    @Override
    public CompletionHandler getHandler() {
        return handler;
    }
}
