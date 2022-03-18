package me.randomhashtags.worldlaws.service.entertainment;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.RefreshableService;

public enum DailyDoseOfInternet implements RefreshableService {
    INSTANCE;

    @Override
    public JSONObjectTranslatable refresh() {
        final String url = "https://www.youtube.com/c/DailyDoseOfInternet/videos";
        return null;
    }
}
