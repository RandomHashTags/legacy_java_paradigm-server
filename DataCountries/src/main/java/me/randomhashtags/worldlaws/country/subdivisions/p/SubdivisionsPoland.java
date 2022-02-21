package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.VOIVODESHIPS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case KUYAVIA_POMERANIAN: return "Kuyavian-Pomeranian";
            case LODZ: return "Łódź";
            case WARMIAN_MASURIAN: return "Warmian-Masurian";
            default: return null;
        }
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
