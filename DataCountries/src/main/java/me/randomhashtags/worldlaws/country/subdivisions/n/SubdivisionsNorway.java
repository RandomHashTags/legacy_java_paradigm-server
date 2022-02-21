package me.randomhashtags.worldlaws.country.subdivisions.n;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsNorway implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Norway
    AGDER,
    INNLANDET,
    MORE_OG_ROMSDAL,
    NORDLAND,
    OSLO,
    ROGALAND,
    TROMS_OG_FINNMARK,
    TRONDELAG,
    VESTFOLD_OF_TELEMARK,
    VESTLAND,
    VIKEN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.NORWAY;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.COUNTIES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case MORE_OG_ROMSDAL: return "Møre og Romsdal";
            case TROMS_OG_FINNMARK: return "Troms og Finnmark";
            case TRONDELAG: return "Trøndelag";
            case VESTFOLD_OF_TELEMARK: return "Vestfold og Telemark";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case VIKEN:
                return "(" + suffix.toLowerCase() + ")";
            default:
                return "";
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
