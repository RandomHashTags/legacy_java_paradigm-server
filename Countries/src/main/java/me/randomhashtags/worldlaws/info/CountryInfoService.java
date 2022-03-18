package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.NewCountryService;
import org.jsoup.select.Elements;

public interface CountryInfoService extends NewCountryService {
    String getURL();
    int getYearOfData();
    default EventSources getSources() {
        final String url = getURL();
        final String siteName;
        switch (url) {
            default:
                final boolean isWikipedia = url.contains("/wiki/");
                siteName = isWikipedia ? "Wikipedia: " + url.split("/wiki/")[1].replace("_", " ") : "Unknown";
                break;
        }
        final EventSource source = new EventSource(siteName, url);
        return new EventSources(source);
    }

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_INFO;
    }
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.INFORMATION;
    }

    default Elements getInfoDocumentElements(String url, String targetElements) {
        return getInfoDocumentElements(url, targetElements, -1);
    }
    default Elements getInfoDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(Folder.COUNTRIES_INFO, url, targetElements, index);
    }
}
