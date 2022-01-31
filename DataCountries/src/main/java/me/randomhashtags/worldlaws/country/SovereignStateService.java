package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLService;

public interface SovereignStateService extends WLService {
    Folder getFolder();
    SovereignStateInfo getInfo();
    String loadData();
}
