package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;

public interface WLService extends Jsoupable, Jsonable, RestAPI, DataValues {
    SovereignStateInfo getInfo();
}
