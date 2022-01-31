package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.select.Elements;

public interface CountryLegalityService extends CountryService {
    String getURL();
    int getYearOfData();
    default EventSources getSources() {
        final String url = getURL();
        final String siteName = url.split("/wiki/")[1].replace("_", " ");
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        return new EventSources(source);
    }

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_LEGALITIES;
    }
    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.LEGALITIES;
    }

    default Elements getLegalityDocumentElements(String url, String targetElements) {
        return getLegalityDocumentElements(url, targetElements, -1);
    }
    default Elements getLegalityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(Folder.COUNTRIES_LEGALITIES, url, targetElements, index);
    }
}
