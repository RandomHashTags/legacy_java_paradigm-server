package me.randomhashtags.worldlaws.country.subdivisions.s;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsSweden implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Sweden
    BLEKINGE,
    DALARNA,
    GOTLAND,
    GAVLEBORG,
    HALLAND,
    JAMTLAND,
    JONKOPING,
    KALMAR,
    KRONOBERG,
    NORRBOTTEN,
    SKANE,
    STOCKHOLM,
    SODERMANLAND,
    UPPSALA,
    VARMLAND,
    VASTERBOTTEN,
    VASTERNORRLAND,
    VASTMANDLAND,
    VASTRA_GOTALAND,
    OREBRO,
    OSTERGOTLAND,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.SWEDEN;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.COUNTIES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case GAVLEBORG: return "Gävleborg";
            case JAMTLAND: return "Jämtland";
            case JONKOPING: return "Jönköping";
            case SKANE: return "Skåne";
            case SODERMANLAND: return "Södermanland";
            case VARMLAND: return "Värmland";
            case VASTERBOTTEN: return "Västerbotten";
            case VASTERNORRLAND: return "Västernorrland";
            case VASTMANDLAND: return "Västmanland";
            case VASTRA_GOTALAND: return "Västra Götaland";
            case OREBRO: return "Örebro";
            case OSTERGOTLAND: return "Östergötland";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            default: return "_County";
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case BLEKINGE: return "K";
            case DALARNA: return "W";
            case GOTLAND: return "I";
            case GAVLEBORG: return "X";
            case HALLAND: return "N";
            case JAMTLAND: return "Z";
            case JONKOPING: return "F";
            case KRONOBERG: return "G";
            case NORRBOTTEN: return "BD";
            case SKANE: return "M";
            case STOCKHOLM: return "AB";
            case SODERMANLAND: return "D";
            case UPPSALA: return "C";
            case VARMLAND: return "S";
            case VASTERBOTTEN: return "AC";
            case VASTERNORRLAND: return "Y";
            case VASTMANDLAND: return "U";
            case VASTRA_GOTALAND: return "O";
            case OREBRO: return "T";
            case OSTERGOTLAND: return "E";
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }
}
