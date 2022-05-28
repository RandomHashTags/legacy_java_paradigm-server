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
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            case ANDHRA_PRADESH: return "3/37/Emblem_of_Andhra_Pradesh.svg";
            case ARUNACHAL_PRADESH: return "en/d/d4/Arunachal_Pradesh_Seal.svg";
            case ASSAM: return "0/0e/Seal_of_Assam.svg";
            case BIHAR: return "e/e9/Seal_of_Bihar.svg";
            case CHHATTISGARH: return "5/5b/..Chhattisgarh_Flag%28INDIA%29.png";
            case GOA: return "en/8/8a/Seal_of_Goa.png";
            case GUJARAT: return "a/a6/Government_Of_Gujarat_Seal_In_All_Languages.svg";
            case HARYANA: return "3/36/Emblem_of_Haryana.svg";
            case HIMACHAL_PRADESH: return "en/b/be/Himachal_Pradesh_seal.svg";
            case JHARKHAND: return "a/a9/Jharkhand_Rajakiya_Chihna.svg";
            case KARNATAKA: return "a/aa/Seal_of_Karnataka.svg";
            case KERALA: return "5/5b/Government_of_Kerala_Logo.svg";
            case MADHYA_PRADESH: return "a/ae/Emblem_of_Madhya_Pradesh.svg";
            case MAHARASHTRA: return "f/ff/Flag_of_Maharashtra.svg";
            case MANIPUR: return "en/3/3e/Manipur_emblem.svg";
            case MEGHALAYA: return "5/59/Seal_of_Meghalaya.svg";
            case MIZORAM: return "5/56/Seal_of_Mizoram.svg";
            case NAGALAND: return "en/1/1e/Seal_of_Nagaland.svg";
            case ODISHA: return "f/fe/Seal_of_Odisha.png";
            case PUNJAB: return "5/5d/Seal_of_Punjab.svg";
            case RAJASTHAN: return "1/1e/Emblem_Rajasthan.png";
            case SIKKIM: return "c/c0/Seal_of_Sikkim.svg";
            case TAMIL_NADU: return "8/81/TamilNadu_Logo.svg";
            case TELANGANA: return "f/f7/Government_of_Telangana_Logo.png";
            case TRIPURA: return "7/73/Seal_of_Tripura.svg";
            case UTTAR_PRADESH: return "f/fa/Seal_of_Uttar_Pradesh.svg";
            case UTTARAKHAND: return "en/9/99/Seal_of_Uttarakhand.svg";
            case WEST_BENGAL: return "d/d5/Emblem_of_West_Bengal.svg";

            case ANDAMAN_AND_NICOBAR_ISLANDS: return "en/4/40/Seal_of_Andaman_Government.png";
            case CHANDIGARH: return "en/9/96/Chandigarh_emblem.png";
            case DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU: return "7/73/Seal_of_Dadra_and_Nagar_Haveli_and_Daman_and_Diu.svg";
            case DELHI: return null;
            case JAMMU_AND_KASHMIR: return null;
            case LADAKH: return "e/e1/Seal_of_Ladakh.png";
            case LAKSHADWEEP: return "en/6/61/Lakshadweep_Banner.png";
            case PUDUCHERRY: return "1/12/Seal_of_Puducherry.svg";

            default: return null;
        }
    }
}
