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
    public String getFlagURL() {
        switch (this) {
            case BREST: return "https://upload.wikimedia.org/wikipedia/commons/e/ec/Flag_of_Brest_Voblast%2C_Belarus.svg";
            case MINSK_CITY: return "https://upload.wikimedia.org/wikipedia/commons/6/69/Flag_of_Minsk%2C_Belarus.svg";
            case GOMEL: return "https://upload.wikimedia.org/wikipedia/commons/4/49/Flag_of_Homyel_Voblast.svg";
            case GRODNO: return "https://upload.wikimedia.org/wikipedia/commons/f/f8/Flag_of_Hrodna_Voblasts.svg";
            case MINSK: return "https://upload.wikimedia.org/wikipedia/commons/2/2e/Flag_of_Minsk_Voblast.svg";
            case MOGILEV: return "https://upload.wikimedia.org/wikipedia/commons/b/ba/Flag_of_Mahilyow_Voblast.svg";
            case VITESBSK: return "https://upload.wikimedia.org/wikipedia/commons/a/a8/Flag_of_Vitsebsk_Voblasts.svg";
            default: return null;
        }
    }
}
