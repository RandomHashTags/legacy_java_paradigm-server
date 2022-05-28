package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsPapuaNewGuinea implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Papua_New_Guinea
    BOUGAINVILLE,
    CENTRAL,
    CHIMBU,
    EAST_NEW_BRITAIN,
    EAST_SEPIK,
    EASTERN_HIGHLANDS,
    ENGA,
    ORO,
    GULF,
    HELA,
    JIWAKA,
    MADANG,
    MANUS,
    MILNE_BAY,
    MOROBE,
    NATIONAL_CAPITAL_DISTRICT,
    NEW_IRELAND,
    SANDAUN,
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BOUGAINVILLE: return SubdivisionType.AUTONOMOUS_REGIONS;
            case NATIONAL_CAPITAL_DISTRICT: return SubdivisionType.INCORPORATED_AREAS;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case BOUGAINVILLE: return "Autonomous Region of Bougainville";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case CENTRAL:
            case WESTERN:
                return suffix + "_(Papua_New_Guinea)";
            case NATIONAL_CAPITAL_DISTRICT:
                return "(Papua_New_Guinea)";
            default:
                return null;
        }
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
}
