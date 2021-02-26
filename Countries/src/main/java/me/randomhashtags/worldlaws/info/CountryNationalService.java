package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

public interface CountryNationalService extends CountryService {
    default Elements getNationalDocumentElements(String url, String targetElements) {
        return getNationalDocumentElements(url, targetElements, -1);
    }
    default Elements getNationalDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.NATIONAL, url, targetElements, index);
    }
}
