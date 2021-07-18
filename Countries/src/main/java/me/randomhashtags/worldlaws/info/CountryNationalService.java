package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.jsoup.select.Elements;

public interface CountryNationalService extends CountryService {

    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_NATIONAL;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.NATIONAL;
    }

    default Elements getNationalDocumentElements(String url, String targetElements) {
        return getNationalDocumentElements(url, targetElements, -1);
    }
    default Elements getNationalDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(getFileType(), url, targetElements, index);
    }
}
