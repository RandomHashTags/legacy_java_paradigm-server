package me.randomhashtags.worldlaws.location.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.location.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public enum UnitedStatesSubdivisions implements SovereignStateSubdivision {
    ALABAMA,
    ALASKA,
    ARIZONA,
    ARKANSAS,
    CALIFORNIA,
    COLORADO,
    CONNECTICUT,
    DELAWARE,
    FLORIDA,
    GEORGIA,
    HAWAII,
    IDAHO,
    ILLINOIS,
    INDIANA,
    IOWA,
    KANSAS,
    KENTUCKY,
    LOUISIANA,
    MAINE,
    MARYLAND,
    MASSACHUSETTS,
    MICHIGAN,
    MINNESOTA,
    MISSISSIPPI,
    MISSOURI,
    MONTANA,
    NEBRASKA,
    NEVADA,
    NEW_HAMPSHIRE,
    NEW_JERSEY,
    NEW_MEXICO,
    NEW_YORK,
    NORTH_CAROLINA,
    NORTH_DAKOTA,
    OHIO,
    OKLAHOMA,
    OREGON,
    PENNSYLVANIA,
    RHODE_ISLAND,
    SOUTH_CAROLINA,
    SOUTH_DAKOTA,
    TENNESSEE,
    TEXAS,
    UTAH,
    VERMONT,
    VIRGINIA,
    WASHINGTON,
    WEST_VIRGINA,
    WISCONSIN,
    WYOMING,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            case ALABAMA: return "AL";
            case ALASKA: return "AK";
            case ARIZONA: return "AZ";
            case ARKANSAS: return "AR";
            case CALIFORNIA: return "CA";
            case COLORADO: return "CO";
            case CONNECTICUT: return "CT";
            case DELAWARE: return "DE";
            case FLORIDA: return "FL";
            case GEORGIA: return "GA";
            case HAWAII: return "HI";
            case IDAHO: return "ID";
            case ILLINOIS: return "IL";
            case INDIANA: return "IN";
            case IOWA: return "IO";
            case KANSAS: return "KS";
            case KENTUCKY: return "KY";
            case LOUISIANA: return "LA";
            case MAINE: return "ME";
            case MARYLAND: return "MD";
            case MASSACHUSETTS: return "MA";
            case MICHIGAN: return "MI";
            case MINNESOTA: return "MN";
            case MISSISSIPPI: return "MS";
            case MISSOURI: return "MO";
            case MONTANA: return "MT";
            case NEBRASKA: return "NE";
            case NEVADA: return "NV";
            case NEW_HAMPSHIRE: return "NH";
            case NEW_JERSEY: return "NJ";
            case NEW_MEXICO: return "NM";
            case NEW_YORK: return "NY";
            case NORTH_CAROLINA: return "NC";
            case NORTH_DAKOTA: return "ND";
            case OHIO: return "OH";
            case OKLAHOMA: return "OK";
            case OREGON: return "OR";
            case PENNSYLVANIA: return "PA";
            case RHODE_ISLAND: return "RI";
            case SOUTH_CAROLINA: return "SC";
            case SOUTH_DAKOTA: return "SD";
            case TENNESSEE: return "TN";
            case TEXAS: return "TX";
            case UTAH: return "UT";
            case VERMONT: return "VT";
            case VIRGINIA: return "VA";
            case WASHINGTON: return "WA";
            case WEST_VIRGINA: return "WV";
            case WISCONSIN: return "WI";
            case WYOMING: return "WY";
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        final String name = name().toLowerCase().replace("_", "-");;
        return "https://flaglane.com/download/" + name + "-flag/" + name + "-flag-large.png";
    }

    @Override
    public String getGovernmentURL() {
        switch (this) {
            case HAWAII: return "https://hawaii.gov";
            case TEXAS: return "https://texas.gov";
            default: return "https://" + getPostalCodeAbbreviation().toLowerCase() + ".gov";
        }
    }

    @Override
    public String getWikipediaURL() {
        switch (this) {
            case GEORGIA: return "https://en.wikipedia.org/wiki/Georgia_(U.S._state)";
            case NEW_YORK: return "https://en.wikipedia.org/wiki/New_York_(state)";
            case WASHINGTON: return "https://en.wikipedia.org/wiki/Washington_(state)";
            default: return SovereignStateSubdivision.super.getWikipediaURL();
        }
    }

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Category:Lists_of_cities_in_the_United_States_by_state
        final String suffix = this == GEORGIA ? "_(U.S._state)" : "";
        final String url = "https://en.wikipedia.org/wiki/List_of_cities_in_" + getName().replace(" ", "_") + suffix;
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            final Element table = tables.get(getCityTableIndex());
            for(Element element : table.select("tbody tr")) {
                final Elements tds = element.select("td");
                final String cityName = tds.get(getCityNameIndex()).text();
            }
        }
    }

    private int getCityTableIndex() {
        switch (this) {
            case KENTUCKY:
                return 1;
            default:
                return 0;
        }
    }
    private int getCityNameIndex() {
        switch (this) {
            case ALABAMA:
            case ALASKA:
            case CALIFORNIA:
            case COLORADO:
            case KENTUCKY:
            case NEW_HAMPSHIRE:
            case NEW_MEXICO:
            case NEW_YORK:
            case OHIO:
            case WASHINGTON:
            case WISCONSIN:
                return 0;
            case IDAHO:
            case MINNESOTA:
            case NEW_JERSEY:
            case OKLAHOMA:
            case TEXAS:
                return 1;
            default: return -1;
        }
    }
}
