package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsIndonesia implements SovereignStateSubdivision {
    ACEH,
    BALI,
    BANGKA_BELITUNG_ISLANDS,
    BANTEN,
    BENGKULU,
    CENTRAL_JAVA,
    CENTRAL_KALIMANTAN,
    CENTRAL_SULAWESI,
    EAST_JAVA,
    EAST_KALIMANTAN,
    EAST_NUSA_TENGGARA,
    GORONTALI,
    JAKARTA,
    JAMBI,
    LAMPUNG,
    MALUKU,
    NORTH_KALIMANTAN,
    NORTH_MALUKU,
    NORTH_SULAWESI,
    NORTH_SUMATRA,
    PAPUA,
    RIAU,
    RIAU_ISLANDS,
    SOUTHEAST_SULAWESI,
    SOUTH_KALIMANTAN,
    SOUTH_SULAWESI,
    SOUTH_SUMATRA,
    WEST_JAVA,
    WEST_KALIMANTAN,
    WEST_NUSA_TENGGARA,
    WEST_PAPUA,
    WEST_SULAWESI,
    WEST_SUMATRA,
    YOGYAKARTA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.INDONESIA;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return null;
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
