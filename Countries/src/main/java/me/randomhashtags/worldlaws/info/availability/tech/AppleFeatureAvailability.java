package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface AppleFeatureAvailability extends CountryAvailabilityService {

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.AVAILABILITIES;
    }

    default Element getSectionElement(AppleFeatureType type, String sectionID) {
        return type.getSections().getOrDefault(sectionID, null);
    }
    default Elements getSectionElements(AppleFeatureType type, String sectionID) {
        final Element element = getSectionElement(type, sectionID);
        return element != null ? element.select("div.section-content ul li") : null;
    }
}
