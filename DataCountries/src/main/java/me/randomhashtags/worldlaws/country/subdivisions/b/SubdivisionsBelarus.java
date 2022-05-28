package me.randomhashtags.worldlaws.country.subdivisions.b;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsBelarus implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Belarus
    BREST,
    GOMEL,
    GRODNO,
    MINSK,
    MINSK_CITY,
    MOGILEV,
    VITESBSK,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.BELARUS;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.OBLASTS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case MINSK_CITY: return SubdivisionType.SPECIAL_CITIES;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case MINSK_CITY:
                return "Minsk";
            default:
                return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case MINSK_CITY:
                return "";
            default:
                return "_" + suffix;
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
            case BREST: return "e/ec/Flag_of_Brest_Voblast%2C_Belarus.svg";
            case MINSK_CITY: return "6/69/Flag_of_Minsk%2C_Belarus.svg";
            case GOMEL: return "4/49/Flag_of_Homyel_Voblast.svg";
            case GRODNO: return "f/f8/Flag_of_Hrodna_Voblasts.svg";
            case MINSK: return "2/2e/Flag_of_Minsk_Voblast.svg";
            case MOGILEV: return "b/ba/Flag_of_Mahilyow_Voblast.svg";
            case VITESBSK: return "a/a8/Flag_of_Vitsebsk_Voblasts.svg";
            default: return null;
        }
    }
}
