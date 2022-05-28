package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsPalestine implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Governorates_of_Tunisia
    BETHLEHEM,
    DEIR_AL_BALAH,
    GAZA,
    HEBRON,
    JENIN,
    JERICHO,
    JERUSALEM,
    KHAN_YUNIS,
    NABLUS,
    NORTH_GAZA,
    QALQILIYA,
    RAFAH,
    RAMALLAH_AND_AL_BIREH,
    SALFIT,
    TUBAS,
    TULKARM,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PALESTINE;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.GOVERNORATES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case DEIR_AL_BALAH: return "Deir al-Balah";
            case RAMALLAH_AND_AL_BIREH: return "Ramallah and al-Bireh";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        return "_Governorate";
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
