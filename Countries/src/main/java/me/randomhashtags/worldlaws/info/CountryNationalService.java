package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.jsoup.select.Elements;

public interface CountryNationalService extends CountryService {

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_NATIONAL;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.NATIONAL;
    }

    default Elements getNationalDocumentElements(String url, String targetElements) {
        return getNationalDocumentElements(url, targetElements, -1);
    }
    default Elements getNationalDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(getFolder(), url, targetElements, index);
    }
}
