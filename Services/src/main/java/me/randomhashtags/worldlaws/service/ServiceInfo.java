package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.location.SovereignStateInfo;

public enum ServiceInfo implements SovereignStateInfo {
    NASA_ASTRONOMY_PICTURE_OF_THE_DAY("Astronomy Picture of the Day"),
    YAHOO_FINANCE("Yahoo Finance"),
    ;

    private final String title;

    ServiceInfo(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
