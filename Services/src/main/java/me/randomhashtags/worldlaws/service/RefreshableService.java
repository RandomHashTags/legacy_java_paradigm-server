package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public interface RefreshableService extends WLService {
    JSONObjectTranslatable refresh();
}
