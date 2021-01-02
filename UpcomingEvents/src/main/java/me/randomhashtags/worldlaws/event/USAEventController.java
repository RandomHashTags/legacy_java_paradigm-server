package me.randomhashtags.worldlaws.event;

import me.randomhashtags.worldlaws.EventController;
import me.randomhashtags.worldlaws.location.CountryBackendID;

public interface USAEventController extends EventController {
    @Override
    default CountryBackendID getCountryBackendID() {
        return CountryBackendID.UNITED_STATES;
    }
}
