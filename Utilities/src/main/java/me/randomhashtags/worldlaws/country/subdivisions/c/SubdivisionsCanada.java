package me.randomhashtags.worldlaws.country.subdivisions.c;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;

public enum SubdivisionsCanada implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_and_territories_of_Canada
    ALBERTA,
    BRITISH_COLUMBIA,
    MANITOBA,
    NEW_BRUNSWICK,
    NEWFOUNDLAND_AND_LABRADOR,
    NORTHWEST_TERRITORIES,
    NOVA_SCOTIA,
    NUNAVUT,
    ONTARIO,
    PRINCE_EDWARD_ISLAND,
    QUEBEC,
    SASKATCHEWAN,
    YUKON,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CANADA;
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case ALBERTA: return "AB";
            case BRITISH_COLUMBIA: return "BC";
            case MANITOBA: return "MB";
            case NEW_BRUNSWICK: return "NB";
            case NEWFOUNDLAND_AND_LABRADOR: return "NL";
            case NORTHWEST_TERRITORIES: return "NT";
            case NOVA_SCOTIA: return "NS";
            case NUNAVUT: return "NU";
            case ONTARIO: return "ON";
            case PRINCE_EDWARD_ISLAND: return "PE";
            case QUEBEC: return "QC";
            case SASKATCHEWAN: return "SK";
            case YUKON: return "YK";
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
            case NORTHWEST_TERRITORIES: return "https://www.gov.nt.ca";
            default: return "https://" + getName().toLowerCase().replace("_", "") + ".ca";
        }
    }

    @Override
    public SovereignStateSubdivision[] getNeighbors() {
        switch (this) {
            case ALBERTA:
                return collectNeighbors(
                    NORTHWEST_TERRITORIES, SASKATCHEWAN, BRITISH_COLUMBIA,
                    SubdivisionsUnitedStates.MONTANA
                );
            case BRITISH_COLUMBIA:
                return collectNeighbors(
                        YUKON, NORTHWEST_TERRITORIES, ALBERTA,
                        SubdivisionsUnitedStates.MONTANA, SubdivisionsUnitedStates.IDAHO, SubdivisionsUnitedStates.WASHINGTON, SubdivisionsUnitedStates.ALASKA
                );
            case MANITOBA:
                return collectNeighbors(
                        NUNAVUT, ONTARIO, SASKATCHEWAN, NORTHWEST_TERRITORIES,
                        SubdivisionsUnitedStates.MINNESOTA, SubdivisionsUnitedStates.NORTH_DAKOTA
                );
            case NEW_BRUNSWICK:
                return collectNeighbors(
                        QUEBEC, PRINCE_EDWARD_ISLAND, NOVA_SCOTIA,
                        SubdivisionsUnitedStates.MAINE
                );
            case NEWFOUNDLAND_AND_LABRADOR: return collectNeighbors(QUEBEC);
            case NORTHWEST_TERRITORIES: return collectNeighbors(NUNAVUT, MANITOBA, SASKATCHEWAN, ALBERTA, BRITISH_COLUMBIA, YUKON);
            case NOVA_SCOTIA: return collectNeighbors(NEW_BRUNSWICK, PRINCE_EDWARD_ISLAND);
            case NUNAVUT: return collectNeighbors(MANITOBA, SASKATCHEWAN, NORTHWEST_TERRITORIES);
            case ONTARIO: return collectNeighbors(
                    QUEBEC, MANITOBA,
                    SubdivisionsUnitedStates.NEW_YORK, SubdivisionsUnitedStates.PENNSYLVANIA, SubdivisionsUnitedStates.OHIO, SubdivisionsUnitedStates.MICHIGAN, SubdivisionsUnitedStates.MINNESOTA
            );
            case PRINCE_EDWARD_ISLAND: return collectNeighbors(NOVA_SCOTIA, NEW_BRUNSWICK);
            case QUEBEC:
                return collectNeighbors(
                        NEWFOUNDLAND_AND_LABRADOR, NEW_BRUNSWICK, ONTARIO,
                        SubdivisionsUnitedStates.MAINE, SubdivisionsUnitedStates.NEW_HAMPSHIRE, SubdivisionsUnitedStates.VERMONT, SubdivisionsUnitedStates.NEW_YORK
                );
            case SASKATCHEWAN:
                return collectNeighbors(
                        NORTHWEST_TERRITORIES, NUNAVUT, MANITOBA, ALBERTA,
                        SubdivisionsUnitedStates.NORTH_DAKOTA, SubdivisionsUnitedStates.MONTANA
                );
            case YUKON:
                return collectNeighbors(
                        NORTHWEST_TERRITORIES, BRITISH_COLUMBIA,
                        SubdivisionsUnitedStates.ALASKA
                );
            default: return null;
        }
    }
}
