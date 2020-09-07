package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.location.Country;

public interface USAEventController extends EventController {
    @Override
    default Country getCountryOrigin() {
        return Country.UNITED_STATES_OF_AMERICA;
    }
}
