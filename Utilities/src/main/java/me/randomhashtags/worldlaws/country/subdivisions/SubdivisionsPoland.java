package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsPoland implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Voivodeships_of_Poland
    GREATER_POLAND,
    HOLY_CROSS,
    KUYAVIA_POMERANIAN,
    LESSER_POLAND,
    LODZ,
    LOWER_SILESIAN,
    LUBLIN,
    LUBUSZ,
    MASOVIAN,
    OPOLE,
    PODLASKIE,
    POMERANIAN,
    SILESIAN,
    SUBCARPATHIAN,
    WARMIAN_MASURIAN,
    WEST_POMERANIAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.POLAND;
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

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
