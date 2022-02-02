package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;

public interface SovereignStateService extends WLService {
    Folder getFolder();
    SovereignStateInfo getInfo();
    String loadData();
}
