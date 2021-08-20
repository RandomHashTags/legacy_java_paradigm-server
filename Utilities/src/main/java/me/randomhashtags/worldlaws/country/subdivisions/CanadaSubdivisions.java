package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum CanadaSubdivisions implements SovereignStateSubdivision {
    ALBERTA,
    BRITISH_COLUMBIA,
    MANITOBA,
    NEW_BRUNSWICK,
    NEWFOUNDLAND_AND_LABRADOR,
    NOVA_SCOTIA,
    ONTARIO,
    PRINCE_EDWARD_ISLAND,
    QUEBEC,
    SASKATCHEWAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CANADA;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            case ALBERTA: return "AB";
            case BRITISH_COLUMBIA: return "BC";
            case MANITOBA: return "MB";
            case NEW_BRUNSWICK: return "NB";
            case NEWFOUNDLAND_AND_LABRADOR: return "NL";
            case NOVA_SCOTIA: return "NS";
            case ONTARIO: return "ON";
            case PRINCE_EDWARD_ISLAND: return "PE";
            case QUEBEC: return "QC";
            case SASKATCHEWAN: return "SK";
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        switch (this) {
            case NEW_BRUNSWICK: return "https://www2.gnb.ca";
            case NEWFOUNDLAND_AND_LABRADOR: return "https://www.gov.nl.ca";
            default: return "https://" + getName().toLowerCase().replace("_", "") + ".ca";
        }
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}
