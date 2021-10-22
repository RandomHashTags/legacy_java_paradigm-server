package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsIran implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Iran
    ALBORZ,
    ARDABIL,
    EAST_AZERBAIJAN,
    WEST_AZERBAIJAN,
    BUSHEHR,
    CHAHAR_MAHAAL_AND_BAKHTIARI,
    FARS,
    GILAN,
    GOLESTAN,
    HAMADAN,
    HORMOZGAN, // Hormozgān
    ILAM,
    ISFAHAN,
    KERMAN,
    KERMANSHAH,
    NORTH_KHORASAN,
    RAZAVI_KHORASAN,
    SOUTH_KHORASAN,
    KHUZESTAN,
    KOHGILUYEH_AND_BOYER_AHMAD, // Kohgiluyeh and Boyer-Ahmad
    KURDISTAN,
    LORESTAN,
    MARKAZI,
    MAZANDARAN,
    QAZVIN,
    QOM,
    SEMNAN,
    SISTAN_AND_BALUCHESTAN,
    TEHRAN,
    YAZD,
    ZANJAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.IRAN;
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
}
