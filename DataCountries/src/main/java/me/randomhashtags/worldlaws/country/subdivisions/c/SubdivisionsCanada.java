package me.randomhashtags.worldlaws.country.subdivisions.c;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case NORTHWEST_TERRITORIES:
            case NUNAVUT:
            case YUKON:
                return SubdivisionType.TERRITORIES;
            default: return null;
        }
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
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            case ALBERTA: return "f/f5/Flag_of_Alberta.svg";
            case BRITISH_COLUMBIA: return "b/b8/Flag_of_British_Columbia.svg";
            case MANITOBA: return "c/c4/Flag_of_Manitoba.svg";
            case NEW_BRUNSWICK: return "f/fb/Flag_of_New_Brunswick.svg";
            case NEWFOUNDLAND_AND_LABRADOR: return "d/dd/Flag_of_Newfoundland_and_Labrador.svg";
            case NOVA_SCOTIA: return "c/c0/Flag_of_Nova_Scotia.svg";
            case ONTARIO: return "8/88/Flag_of_Ontario.svg";
            case PRINCE_EDWARD_ISLAND: return "d/d7/Flag_of_Prince_Edward_Island.svg";
            case QUEBEC: return "5/5f/Flag_of_Quebec.svg";
            case SASKATCHEWAN: return "b/bb/Flag_of_Saskatchewan.svg";

            case NORTHWEST_TERRITORIES: return "c/c1/Flag_of_the_Northwest_Territories.svg";
            case NUNAVUT: return "9/90/Flag_of_Nunavut.svg";
            case YUKON: return "6/69/Flag_of_Yukon.svg";
            default: return null;
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
