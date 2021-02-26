package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.select.Elements;

public interface CountryValueService extends CountryService {
    default Elements getValueDocumentElements(String url, String targetElements) {
        return getValueDocumentElements(url, targetElements, -1);
    }
    default Elements getValueDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(FileType.VALUES, url, targetElements, index);
    }
}
