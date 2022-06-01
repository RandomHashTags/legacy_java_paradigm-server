package me.randomhashtags.worldlaws.country.subdivisions.y;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsYemen implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Yemen
    AMRAN,
    ABYAN,
    DHALE,
    ADEN,
    AL_BAYDA,
    AL_HUDAYDAH,
    AL_JAWF,
    AL_MAHRAH,
    AL_MAHWIT,
    SANAA_CITY,
    DHAMAR,
    HADHRAMAUT,
    HAJJAH,
    IBB,
    LAHIJ,
    MARIB,
    RAYMAH,
    SAADA,
    SANAA,
    SHABWAH,
    SOCOTRA,
    TAIZ,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.YEMEN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.GOVERNORATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case SANAA_CITY:
                return SubdivisionType.CITIES;
            default:
                return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case SANAA_CITY: return "Sanaa";
            case AMRAN: return "'Amran";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case SANAA_CITY: return null;
            default: return "_Governorate";
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
            default: return null;
        }
    }
}
