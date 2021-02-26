package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

public interface CountryLegalityService extends CountryService {
    default Elements getLegalityDocumentElements(String url, String targetElements) {
        return getLegalityDocumentElements(url, targetElements, -1);
    }
    default Elements getLegalityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.LEGALITIES, url, targetElements, index);
    }
}
