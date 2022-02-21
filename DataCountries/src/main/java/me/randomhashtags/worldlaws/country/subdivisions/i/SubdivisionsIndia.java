package me.randomhashtags.worldlaws.country.subdivisions.i;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsIndia implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/States_and_union_territories_of_India
    ANDHRA_PRADESH,
    ARUNACHAL_PRADESH,
    ASSAM,
    BIHAR,
    CHHATTISGARH,
    GOA,
    GUJARAT,
    HARYANA,
    HIMACHAL_PRADESH,
    JHARKHAND,
    KARNATAKA,
    KERALA,
    MADHYA_PRADESH,
    MAHARASHTRA,
    MANIPUR,
    MEGHALAYA,
    MIZORAM,
    NAGALAND,
    ODISHA,
    PUNJAB,
    RAJASTHAN,
    SIKKIM,
    TAMIL_NADU,
    TELANGANA,
    TRIPURA,
    UTTAR_PRADESH,
    UTTARAKHAND,
    WEST_BENGAL,

    ANDAMAN_AND_NICOBAR_ISLANDS,
    CHANDIGARH,
    DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU,
    DELHI,
    JAMMU_AND_KASHMIR,
    LADAKH,
    LAKSHADWEEP,
    PUDUCHERRY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.INDIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case ANDAMAN_AND_NICOBAR_ISLANDS:
            case CHANDIGARH:
            case DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU:
            case DELHI:
            case JAMMU_AND_KASHMIR:
            case LADAKH:
            case LAKSHADWEEP:
            case PUDUCHERRY:
                return SubdivisionType.UNION_TERRITORIES;
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case PUNJAB:
                return ",_India";
            case JAMMU_AND_KASHMIR:
            case PUDUCHERRY:
                return "(union territory)";
            default:
                return "";
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case ANDAMAN_AND_NICOBAR_ISLANDS: return "AN";
            case ANDHRA_PRADESH: return "AP";
            case ARUNACHAL_PRADESH: return "AR";
            case ASSAM: return "AS";
            case BIHAR: return "BR";
            case CHANDIGARH: return "CH";
            case CHHATTISGARH: return "CT";
            case DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU: return "DH";
            case DELHI: return "DL";
            case GOA: return "GA";
            case GUJARAT: return "GJ";
            case HARYANA: return "HR";
            case HIMACHAL_PRADESH: return "HP";
            case JAMMU_AND_KASHMIR: return "JK";
            case JHARKHAND: return "JH";
            case KARNATAKA: return "KA";
            case KERALA: return "KL";
            case LADAKH: return "LA";
            case LAKSHADWEEP: return "LD";
            case MADHYA_PRADESH: return "MP";
            case MAHARASHTRA: return "MH";
            case MANIPUR: return "MN";
            case MEGHALAYA: return "ML";
            case MIZORAM: return "MZ";
            case NAGALAND: return "NL";
            case ODISHA: return "OR";
            case PUDUCHERRY: return "PY";
            case PUNJAB: return "PB";
            case RAJASTHAN: return "RJ";
            case SIKKIM: return "SK";
            case TAMIL_NADU: return "TN";
            case TELANGANA: return "TG";
            case TRIPURA: return "TR";
            case UTTAR_PRADESH: return "UP";
            case UTTARAKHAND: return "UT";
            case WEST_BENGAL: return "WB";
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
