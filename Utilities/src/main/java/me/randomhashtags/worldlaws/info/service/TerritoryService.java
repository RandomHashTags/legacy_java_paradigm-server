package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.location.SovereignStateService;
import me.randomhashtags.worldlaws.location.TerritoryInfo;
import me.randomhashtags.worldlaws.location.TerritoryInformationType;

public interface TerritoryService extends SovereignStateService {
    TerritoryInformationType getInformationType();
    TerritoryInfo getInfo();
}
