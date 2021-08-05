package me.randomhashtags.worldlaws.service.amberalert;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.country.WLCountry;

public interface AmberAlertService extends Jsoupable {
    WLCountry getCountry();
}
