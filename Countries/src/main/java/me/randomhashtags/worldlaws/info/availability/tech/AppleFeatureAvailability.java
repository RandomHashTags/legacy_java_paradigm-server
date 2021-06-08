package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInformationType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface AppleFeatureAvailability extends CountryService {

    HashMap<String, Element> SECTIONS = test();

    private static HashMap<String, Element> test() {
        final HashMap<String, Element> map = new HashMap<>();
        final Elements sections = Jsoupable.getStaticDocumentElements(FileType.COUNTRIES_AVAILABILITIES, "https://www.apple.com/ios/feature-availability", true, "body.page-overview main.main section.section");
        for(Element section : sections) {
            map.put(section.attr("id"), section);
        }
        return map;
    }

    @Override
    default CountryInformationType getInformationType() {
        return CountryInformationType.AVAILABILITIES;
    }

    default Element getSectionElement(String sectionID) {
        return SECTIONS.getOrDefault(sectionID, null);
    }
    default Elements getSectionElements(String sectionID) {
        return getSectionElement(sectionID).select("div.section-content ul li");
    }
}
