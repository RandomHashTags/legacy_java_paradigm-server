package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateResource;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

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
    public String getGovernmentWebsite() {
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
    public SovereignStateSubdivision[] getNeighbors() {
        switch (this) {
            case ARKANSAS: return collectNeighbors(MISSOURI, TENNESSEE, MISSISSIPPI, LOUISIANA, TEXAS, OKLAHOMA);
            case ALABAMA: return collectNeighbors(TENNESSEE, GEORGIA, FLORIDA, MISSISSIPPI);
            case COLORADO: return collectNeighbors(WYOMING, NEBRASKA, KANSAS, OKLAHOMA, NEW_MEXICO, ARIZONA, UTAH);
            case CONNECTICUT: return collectNeighbors(MASSACHUSETTS, RHODE_ISLAND, NEW_YORK);
            case DELAWARE: return collectNeighbors(PENNSYLVANIA, NEW_JERSEY, MARYLAND);
            case KANSAS: return collectNeighbors(NEBRASKA, MISSOURI, OKLAHOMA, COLORADO);
            case GEORGIA: return collectNeighbors(TENNESSEE, NORTH_CAROLINA, SOUTH_CAROLINA, FLORIDA, ALABAMA);
            case IDAHO: return collectNeighbors(CanadaSubdivisions.BRITISH_COLUMBIA, MONTANA, WYOMING, UTAH, NEVADA, OREGON, WASHINGTON);
            case ILLINOIS: return collectNeighbors(WISCONSIN, MICHIGAN, INDIANA, KENTUCKY, MISSOURI, IOWA);
            case INDIANA: return collectNeighbors(ILLINOIS, MICHIGAN, OHIO, KENTUCKY);
            case IOWA: return collectNeighbors(MINNESOTA, WISCONSIN, ILLINOIS, MISSOURI, NEBRASKA, SOUTH_DAKOTA);
            case KENTUCKY: return collectNeighbors(ILLINOIS, INDIANA, OHIO, WEST_VIRGINA, VIRGINIA, TENNESSEE, MISSOURI);
            case LOUISIANA: return collectNeighbors(ARKANSAS, MISSISSIPPI, TEXAS);
            case MAINE: return collectNeighbors(CanadaSubdivisions.QUEBEC, CanadaSubdivisions.NEW_BRUNSWICK, NEW_HAMPSHIRE);
            case MARYLAND: return collectNeighbors(PENNSYLVANIA, DELAWARE, VIRGINIA, WEST_VIRGINA);
            case MASSACHUSETTS: return collectNeighbors(VERMONT, NEW_HAMPSHIRE, RHODE_ISLAND, CONNECTICUT, NEW_YORK);
            case MICHIGAN: return collectNeighbors(CanadaSubdivisions.ONTARIO, OHIO, INDIANA, ILLINOIS, WISCONSIN, MINNESOTA);
            case MINNESOTA: return collectNeighbors(CanadaSubdivisions.MANITOBA, CanadaSubdivisions.ONTARIO, MICHIGAN, WISCONSIN, IOWA, SOUTH_DAKOTA, NORTH_DAKOTA);
            case MISSISSIPPI: return collectNeighbors(ARKANSAS, TENNESSEE, ALABAMA, LOUISIANA);
            case MISSOURI: return collectNeighbors(IOWA, ILLINOIS, KENTUCKY, TENNESSEE, ARKANSAS, OKLAHOMA, KANSAS, NEBRASKA);
            case MONTANA: return collectNeighbors(CanadaSubdivisions.ALBERTA, CanadaSubdivisions.SASKATCHEWAN, NORTH_DAKOTA, SOUTH_DAKOTA, WYOMING, IDAHO);
            case NEBRASKA: return collectNeighbors(SOUTH_DAKOTA, IOWA, MISSOURI, KANSAS, COLORADO, WYOMING);
            case NEVADA: return collectNeighbors(OREGON, IDAHO, UTAH, ARIZONA, CALIFORNIA);
            case NEW_JERSEY: return collectNeighbors(NEW_YORK, DELAWARE, PENNSYLVANIA);
            case NORTH_CAROLINA: return collectNeighbors(VIRGINIA, SOUTH_CAROLINA, GEORGIA, TENNESSEE);
            case NORTH_DAKOTA: return collectNeighbors(CanadaSubdivisions.SASKATCHEWAN, CanadaSubdivisions.MANITOBA, MINNESOTA, SOUTH_DAKOTA, MONTANA);
            case NEW_HAMPSHIRE: return collectNeighbors(CanadaSubdivisions.QUEBEC, MAINE, MASSACHUSETTS, VERMONT);
            case NEW_YORK: return collectNeighbors(CanadaSubdivisions.ONTARIO, VERMONT, MASSACHUSETTS, CONNECTICUT, NEW_JERSEY, PENNSYLVANIA);
            case OHIO: return collectNeighbors(MICHIGAN, CanadaSubdivisions.ONTARIO, PENNSYLVANIA, WEST_VIRGINA, KENTUCKY, INDIANA);
            case OKLAHOMA: return collectNeighbors(COLORADO, KANSAS, MISSOURI, ARKANSAS, TEXAS, NEW_MEXICO);
            case OREGON: return collectNeighbors(WASHINGTON, IDAHO, NEVADA, CALIFORNIA);
            case PENNSYLVANIA: return collectNeighbors(CanadaSubdivisions.ONTARIO, NEW_YORK, NEW_JERSEY, DELAWARE, MARYLAND, WEST_VIRGINA, OHIO);
            case RHODE_ISLAND: return collectNeighbors(MASSACHUSETTS, CONNECTICUT);
            case SOUTH_CAROLINA: return collectNeighbors(NORTH_CAROLINA, GEORGIA);
            case SOUTH_DAKOTA: return collectNeighbors(NORTH_DAKOTA, MINNESOTA, IOWA, NEBRASKA, WYOMING, MONTANA);
            case TENNESSEE: return collectNeighbors(KENTUCKY, VIRGINIA, NORTH_CAROLINA, GEORGIA, ALABAMA, MISSISSIPPI, ARKANSAS, MISSOURI);
            case UTAH: return collectNeighbors(IDAHO, WYOMING, COLORADO, NEW_MEXICO, ARIZONA, NEVADA);
            case VERMONT: return collectNeighbors(CanadaSubdivisions.QUEBEC, NEW_HAMPSHIRE, MASSACHUSETTS, NEW_YORK);
            case VIRGINIA: return collectNeighbors(KENTUCKY, WEST_VIRGINA, MARYLAND, NORTH_CAROLINA, TENNESSEE);
            case WASHINGTON: return collectNeighbors(CanadaSubdivisions.BRITISH_COLUMBIA, IDAHO, OREGON);
            case WEST_VIRGINA: return collectNeighbors(OHIO, PENNSYLVANIA, MARYLAND, VIRGINIA, KENTUCKY);
            case WISCONSIN: return collectNeighbors(MINNESOTA, MICHIGAN, ILLINOIS, IOWA);
            case WYOMING: return collectNeighbors(MONTANA, SOUTH_DAKOTA, NEBRASKA, COLORADO, UTAH, IDAHO);
            default: return null;
        }
    }

    @Override
    public HashSet<SovereignStateResource> getCustomResources() {
        final HashSet<SovereignStateResource> resources = new HashSet<>();
        final String governmentConstitution = getGovernmentConstitutionURL();
        if(governmentConstitution != null) {
            final boolean isPDF = governmentConstitution.endsWith(".pdf");
            resources.add(new SovereignStateResource("Government Constitution" + (isPDF ? " PDF" : ""), governmentConstitution));
        }
        return resources.isEmpty() ? null : resources;
    }
    private String getGovernmentConstitutionURL() {
        switch (this) {
            case ALASKA: return "https://ltgov.alaska.gov/information/alaskas-constitution/";
            case ARIZONA: return "https://www.azleg.gov/constitution/";
            case CALIFORNIA: return "https://leginfo.legislature.ca.gov/faces/codesTOCSelected.xhtml?tocCode=CONS&tocTitle=+California+Constitution+-+CONS";
            case COLORADO: return "http://leg.colorado.gov/colorado-constitution";
            case DELAWARE: return "http://delcode.delaware.gov/constitution/";
            case FLORIDA: return "https://www.flsenate.gov/Laws/Constitution";
            case HAWAII: return "https://lrb.hawaii.gov/constitution";
            case IDAHO: return "https://legislature.idaho.gov/statutesrules/idconst/";
            case KENTUCKY: return "https://apps.legislature.ky.gov/law/constitution";
            case LOUISIANA: return "https://senate.la.gov/Documents/LAConstitution.pdf";
            case MAINE: return "https://www.maine.gov/legis/const/";
            case MARYLAND: return "https://msa.maryland.gov/msa/mdmanual/43const/html/const.html";
            case MICHIGAN: return "http://www.legislature.mi.gov/(S(nilfdmsunscqoewob1q3g53a))/mileg.aspx?page=GetObject&objectname=mcl-Constitution";
            case MINNESOTA: return "https://www.revisor.mn.gov/constitution/";
            case MISSISSIPPI: return "https://www.sos.ms.gov/content/documents/ed_pubs/pubs/Mississippi_Constitution.pdf";
            case MONTANA: return "https://leg.mt.gov/bills/mca/title_0000/chapters_index.html";
            case NEBRASKA: return "https://www.nebraskalegislature.gov/FloorDocs/Current/PDF/Constitution/constitution.pdf";
            case NEVADA: return "https://www.leg.state.nv.us/Const/NvConst.html";
            case NEW_HAMPSHIRE: return "https://www.nh.gov/glance/constitution.htm";
            case NEW_JERSEY: return "https://www.nj.gov/state/archives/docconst47.html";
            case NEW_MEXICO: return "https://nmonesource.com/nmos/c/en/item/5916/index.do#!fragment//BQCwhgziBcwMYgK4DsDWszIQewE4BUBTADwBdoByCgSgBpltTCIBFRQ3AT0otokLC4EbDtyp8BQkAGU8pAELcASgFEAMioBqAQQByAYRW1SYAEbRS2ONWpA";
            case NEW_YORK: return "https://www.nysenate.gov/new-york-state-constitution";
            case NORTH_CAROLINA: return "https://www.ncleg.gov/Laws/Constitution";
            case NORTH_DAKOTA: return "https://www.legis.nd.gov/constitution";
            case OHIO: return "https://www.sos.state.oh.us/globalassets/publications/election/constitution.pdf";
            case OREGON: return "https://sos.oregon.gov/blue-book/Documents/oregon-constitution.pdf";
            case SOUTH_CAROLINA: return "https://www.scstatehouse.gov/scconstitution/scconst.php";
            case SOUTH_DAKOTA: return "https://sdlegislature.gov/Statutes/Constitution";
            case TENNESSEE: return "https://www.capitol.tn.gov/about/docs/TN-Constitution.pdf";
            case UTAH: return "https://le.utah.gov/xcode/constitution.html";
            case VERMONT: return "https://legislature.vermont.gov/statutes/constitution-of-the-state-of-vermont/";
            case VIRGINIA: return "https://law.lis.virginia.gov/constitution/";
            case WASHINGTON: return "https://leg.wa.gov/CodeReviser/Pages/WAConstitution.aspx";
            case WEST_VIRGINA: return "http://www.wvlegislature.gov/WVCODE/WV_CON.cfm";
            case WISCONSIN: return "https://docs.legis.wisconsin.gov/constitution/wi_unannotated";
            case WYOMING: return "https://sos.wyo.gov/Forms/Publications/WYConstitution.pdf";
            default: return null;
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
