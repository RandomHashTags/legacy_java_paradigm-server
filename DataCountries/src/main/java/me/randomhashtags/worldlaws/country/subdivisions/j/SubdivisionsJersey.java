package me.randomhashtags.worldlaws.country.subdivisions.j;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsJersey implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Parishes_of_Jersey
    GROUVILLE,
    SAINT_LAWRENCE,
    SAINT_BRELADE,
    SAINT_CLEMENT,
    SAINT_HELIER,
    SAINT_JOHN,
    SAINT_MARTIN,
    SAINT_OUEN,
    SAINT_PETER,
    SAINT_SAVIOUR,
    SAINT_MARY,
    TRINITY,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.JERSEY;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PARISHES;
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case SAINT_LAWRENCE:
            case SAINT_CLEMENT:
            case SAINT_JOHN:
            case SAINT_MARTIN:
            case SAINT_OUEN:
            case SAINT_PETER:
            case SAINT_SAVIOUR:
            case SAINT_MARY:
            case TRINITY:
                return suffix + ",_Jersey";
            default:
                return null;
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
        switch (this) {
            case SAINT_BRELADE: return "https://comite.je/wp-content/uploads/sites/13/2021/11/icon-st-brelade-1.svg";
            case SAINT_CLEMENT: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-clement.svg";
            case GROUVILLE: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-grouville.svg";
            case SAINT_HELIER: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-helier.svg";
            case SAINT_JOHN: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-john.svg";
            case SAINT_LAWRENCE: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-lawrence.svg";
            case SAINT_MARTIN: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-martin.svg";
            case SAINT_MARY: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-mary.svg";
            case SAINT_OUEN: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-ouen.svg";
            case SAINT_PETER: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-peter.svg";
            case SAINT_SAVIOUR: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-st-saviour.svg";
            case TRINITY: return "https://comite.je/wp-content/uploads/sites/13/2021/09/icon-trinity.svg";
            default: return null;
        }
    }
}
