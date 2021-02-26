package me.randomhashtags.worldlaws.recode;

import me.randomhashtags.worldlaws.location.WLCountry;

public interface NewUSAEventController extends NewEventController {

    @Override
    default WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }
}
