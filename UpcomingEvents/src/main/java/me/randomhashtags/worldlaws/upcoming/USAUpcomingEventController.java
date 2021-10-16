package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.country.WLCountry;

public abstract class USAUpcomingEventController extends UpcomingEventController {
    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }
}
