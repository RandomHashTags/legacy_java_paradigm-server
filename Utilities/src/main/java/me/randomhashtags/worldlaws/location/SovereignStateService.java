package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;

public interface SovereignStateService extends RestAPI, Jsoupable, Jsonable {
    FileType getFileType();
    SovereignStateInfo getInfo();
    void loadData(CompletionHandler handler);

    default void getJSONObject(SovereignStateService service, CompletionHandler handler) {
        getJSONObject(service.getFileType(), service.getInfo().getTitle(), handler);
    }
    default void getJSONArray(CountryService service, CompletionHandler handler) {
        getJSONArray(service.getFileType(), service.getInfo().getTitle(), handler);
    }
}
