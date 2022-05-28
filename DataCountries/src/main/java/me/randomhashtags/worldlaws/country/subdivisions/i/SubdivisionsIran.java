package me.randomhashtags.worldlaws.country.subdivisions.i;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsIran implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Iran
    ALBORZ,
    ARDABIL,
    EAST_AZERBAIJAN,
    WEST_AZERBAIJAN,
    BUSHEHR,
    CHAHARMAHAL_AND_BAKHTIARI,
    FARS,
    GILAN,
    GOLESTAN,
    HAMADAN,
    HORMOZGAN,
    ILAM,
    ISFAHAN,
    KERMAN,
    KERMANSHAH,
    NORTH_KHORASAN,
    RAZAVI_KHORASAN,
    SOUTH_KHORASAN,
    KHUZESTAN,
    KOHGILUYEH_AND_BOYER_AHMAD,
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case HORMOZGAN: return "Hormozgān";
            case KOHGILUYEH_AND_BOYER_AHMAD: return "Kohgiluyeh and Boyer-Ahmad";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        return suffix.toLowerCase();
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }
}
