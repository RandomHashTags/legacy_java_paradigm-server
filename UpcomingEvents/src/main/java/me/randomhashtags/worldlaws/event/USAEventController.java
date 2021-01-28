package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.EventController;
import me.randomhashtags.worldlaws.location.WLCountry;

public interface USAEventController extends EventController {
    @Override
    default WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }
}
