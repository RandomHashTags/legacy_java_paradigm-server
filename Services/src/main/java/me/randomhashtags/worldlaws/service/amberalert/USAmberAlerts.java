package me.randomhashtags.worldlaws.service.amberalert;

import me.randomhashtags.worldlaws.country.WLCountry;
import org.jsoup.nodes.Document;

// https://amberalert.ojp.gov/news/active-AMBER-alerts
// https://www.missingkids.org/gethelpnow/amber
public enum USAmberAlerts implements AmberAlertService {
    INSTANCE;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    public void getCurrent() {
        final Document doc = getDocument("https://www.missingkids.org/gethelpnow/amber");
        if(doc != null) {
        }
    }
}
