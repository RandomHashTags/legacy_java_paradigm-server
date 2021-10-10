package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsIndia implements SovereignStateSubdivision {
    ANDAMAN_AND_NICOBAR_ISLANDS,
    ANDHRA_PRADESH,
    ARUNACHAL_PRADESH,
    ASSAM,
    BIHAR,
    CHANDIGARH,
    CHHATTISGARH,
    DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU,
    DELHI,
    GOA,
    GUJARAT,
    HARYANA,
    HIMACHAL_PRADESH,
    JAMMU_AND_KASHMIR,
    JHARKHAND,
    KARNATAKA,
    KERALA,
    LADAKH,
    LAKSHADWEEP,
    MADHYA_PRADESH,
    MAHARASHTRA,
    MANIPUR,
    MEGHALAYA,
    MIZORAM,
    NAGALAND,
    ODISHA,
    PUDUCHERRY,
    PUNJAB,
    RAJASTHAN,
    SIKKIM,
    TAMIL_NADU,
    TELANGANA,
    TRIPURA,
    UTTAR_PRADESH,
    UTTARAKHAND,
    WEST_BENGAL,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.INDIA;
    }

    @Override
    public String getPostalCodeAbbreviation() {
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

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
