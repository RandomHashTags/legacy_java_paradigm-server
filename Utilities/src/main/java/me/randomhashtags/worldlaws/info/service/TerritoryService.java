package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import me.randomhashtags.worldlaws.location.SovereignStateService;

public interface TerritoryService extends SovereignStateService {
    SovereignStateInformationType getInformationType();
    SovereignStateInfo getInfo();
}
