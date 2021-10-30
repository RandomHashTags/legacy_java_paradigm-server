package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsPapuaNewGuinea implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Administrative_divisions_of_Papua_New_Guinea
    BOUGAINVILLE,
    CENTRAL,
    CHIMBU, // Simbu
    EAST_NEW_BRITAIN,
    EAST_SEPIK,
    EASTERN_HIGHLANDS,
    ENGA,
    ORO, // Northern
    GULF,
    HELA,
    JIWAKA,
    MADANG,
    MANUS,
    MILNE_BAY,
    MOROBE,
    NATIONAL_CAPITAL_DISTRICT,
    NEW_IRELAND,
    SANDAUN, // West Sepik
    SOUTHERN_HIGHLANDS,
    WEST_NEW_BRITAIN,
    WESTERN_HIGHLANDS,
    WESTERN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PAPUA_NEW_GUINEA;
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case BOUGAINVILLE: return "NSB";
            case CENTRAL: return "CPM";
            case CHIMBU: return "CPK";
            case EAST_NEW_BRITAIN: return "EBR";
            case EAST_SEPIK: return "ESW";
            case EASTERN_HIGHLANDS: return "EHG";
            case ENGA: return "EPW";
            case GULF: return "GPK";
            case HELA: return "HLA";
            case JIWAKA: return "JWK";
            case MADANG: return "MPM";
            case MANUS: return "MRL";
            case MILNE_BAY: return "MBA";
            case MOROBE: return "MPL";
            case NATIONAL_CAPITAL_DISTRICT: return "NCD";
            case NEW_IRELAND: return "NIK";
            case ORO: return "NPP";
            case SANDAUN: return "SAN";
            case SOUTHERN_HIGHLANDS: return "SHM";
            case WEST_NEW_BRITAIN: return "WBK";
            case WESTERN: return "WPD";
            case WESTERN_HIGHLANDS: return "WHM";
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
