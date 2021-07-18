package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface AppleFeatureAvailability extends CountryService {

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.AVAILABILITIES;
    }

    default Element getSectionElement(AppleFeatureType type, String sectionID) {
        return type.getSections().getOrDefault(sectionID, null);
    }
    default Elements getSectionElements(AppleFeatureType type, String sectionID) {
        return getSectionElement(type, sectionID).select("div.section-content ul li");
    }
}
