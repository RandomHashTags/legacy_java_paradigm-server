package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.info.service.CountryService;

public final class CountryServiceValue {

    private final CountryService service;
    private final String value;

    public CountryServiceValue(CountryService service, String value) {
        this.service = service;
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + service.getInfo().getTitle() + "\":" + value;
    }
}
