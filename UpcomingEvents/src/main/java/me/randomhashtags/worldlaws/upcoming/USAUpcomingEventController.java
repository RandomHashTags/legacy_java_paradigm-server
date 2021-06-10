package me.randomhashtags.worldlaws.upcoming;

import me.randomhashtags.worldlaws.location.WLCountry;

public interface USAUpcomingEventController extends UpcomingEventController {
    @Override
    default WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }
}