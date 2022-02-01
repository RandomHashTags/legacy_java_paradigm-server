package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum AppleFeatureType {
    IOS,
    MAC_OS,
    WATCH_OS,
    ;

    private static final HashMap<String, Elements> SECTIONS = new HashMap<>();

    public HashMap<String, Element> getSections() {
        switch (this) {
            case IOS: return getSections("ios");
            case MAC_OS: return getSections("macos");
            case WATCH_OS: return getSections("watchos");
            default: return null;
        }
    }

    private static synchronized Elements getSectionElements(String type) {
        if(!SECTIONS.containsKey(type)) {
            SECTIONS.put(type, Jsoupable.getStaticDocumentElements(Folder.COUNTRIES_SERVICES_AVAILABILITIES, "https://www.apple.com/" + type + "/feature-availability", false, "body.page-overview main.main section.section"));
        }
        return SECTIONS.get(type);
    }

    private static HashMap<String, Element> getSections(String type) {
        final HashMap<String, Element> map = new HashMap<>();
        final Elements sections = getSectionElements(type);
        for(Element section : sections) {
            map.put(section.attr("id"), section);
        }
        return map;
    }
}
