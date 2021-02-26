package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface AppleFeatureAvailability extends CountryService {

    Elements SECTIONS = Jsoupable.getStaticDocumentElements(FileType.AVAILABILITIES, "https://www.apple.com/ios/feature-availability", "body.page-overview main.main section.section");

    default Element getSectionElement(String sectionID) {
        final Elements elements = new Elements(SECTIONS);
        elements.removeIf(element -> !sectionID.equals(element.attr("id")));
        return elements.isEmpty() ? null : elements.get(0);
    }
    default Elements getSectionElements(String sectionID) {
        return getSectionElement(sectionID).select("div.section-content ul li");
    }
}
