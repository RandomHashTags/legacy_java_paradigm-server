package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

public interface CountryInfoService extends CountryService {
    default Elements getInfoDocumentElements(String url, String targetElements) {
        return getInfoDocumentElements(url, targetElements, -1);
    }
    default Elements getInfoDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.INFO, url, targetElements, index);
    }
}
