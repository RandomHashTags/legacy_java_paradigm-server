package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateService;

public interface TerritoryService extends SovereignStateService {
    SovereignStateInformationType getInformationType();
    SovereignStateInfo getInfo();
}
