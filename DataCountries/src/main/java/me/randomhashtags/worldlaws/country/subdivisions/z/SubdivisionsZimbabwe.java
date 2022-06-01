package me.randomhashtags.worldlaws.country.subdivisions.z;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsZimbabwe implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Zimbabwe
    BULAWAYO,
    HARARE,
    MANICALAND,
    MASHONALAND_CENTRAL,
    MASHONALAND_EAST,
    MASHONALAND_WEST,
    MASVINGO,
    MATABELELAND_NORTH,
    MATABELELAND_SOUTH,
    MIDLANDS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ZIMBABWE;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BULAWAYO:
            case HARARE:
                return SubdivisionType.CITIES;
            default:
                return null;
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            case BULAWAYO: return "3/35/Flag_of_Bulawayo.svg";
            case HARARE: return "e/e7/Flag_of_Harare.svg";
            case MANICALAND: return null;
            case MASHONALAND_CENTRAL: return null;
            case MASHONALAND_EAST: return null;
            case MASHONALAND_WEST: return null;
            case MASVINGO: return null;
            case MATABELELAND_NORTH: return null;
            case MATABELELAND_SOUTH: return null;
            case MIDLANDS: return null;
            default: return null;
        }
    }
}
