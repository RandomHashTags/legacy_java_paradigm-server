package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;

public interface TerritoryService extends SovereignStateService {
    SovereignStateInformationType getInformationType();
    SovereignStateInfo getInfo();
}
