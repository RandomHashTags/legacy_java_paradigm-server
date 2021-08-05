package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLService;

public interface SovereignStateService extends WLService {
    Folder getFolder();
    SovereignStateInfo getInfo();
    void loadData(CompletionHandler handler);

    default void getJSONObject(CompletionHandler handler) {
        getJSONObject(getFolder(), getInfo().getTitle(), handler);
    }
    default void getJSONArray(CompletionHandler handler) {
        getJSONArray(getFolder(), getInfo().getTitle(), handler);
    }
}
