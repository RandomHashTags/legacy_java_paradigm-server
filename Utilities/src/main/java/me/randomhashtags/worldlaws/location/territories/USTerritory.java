package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.TimeZones;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.TimeZone;

public enum USTerritory implements Territory {
    ALABAMA(
            "AL",
            "https://www.alabama.gov"
    ),
    ALASKA(
            "AK",
            "http://www.alaska.gov"
    ),
    ARIZONA(
            "AZ",
            "https://az.gov"
    ),
    ARKANSAS(
            "AR",
            "https://arkansas.gov"
    ),
    CALIFORNIA(
            "CA",
            "https://www.ca.gov"
    ),
    COLORADO(
            "CO",
            "https://www.colorado.gov"
    ),
    CONNECTICUT(
            "CT",
            "https://ct.gov"
    ),
    DELAWARE(
            "DE",
            "https://delaware.gov"
    ),
    FLORIDA(
            "FL",
            "http://www.myflorida.com"
    ),
    GEORGIA(
            "GA",
            "https://georgia.gov"
    ),
    HAWAII(
            "HI",
            "https://hawaii.gov"
    ),
    IDAHO(
            "ID",
            "https://www.idaho.gov"
    ),
    ILLINOIS(
            "IL",
            "https://www.illinois.gov"
    ),
    INDIANA(
            "IN",
            "https://www.in.gov"
    ),
    IOWA(
            "IA",
            "https://www.iowa.gov"
    ),
    KANSAS(
            "KS",
            "https://kansas.gov"
    ),
    KENTUCKY(
            "KY",
            "https://kentucky.gov"
    ),
    LOUISIANA(
            "LA",
            "https://www.louisiana.gov"
    ),
    MAINE(
            "ME",
            "https://www.maine.gov"
    ),
    MARYLAND(
            "MD",
            "https://www.maryland.gov"
    ),
    MASSACHUSETTS(
            "MA",
            "https://www.mass.gov"
    ),
    MICHIGAN(
            "MI",
            "https://www.michigan.gov"
    ),
    MINNESOTA(
            "MN",
            "https://mn.gov"
    ),
    MISSISSIPPI(
            "MS",
            "https://www.ms.gov"
    ),
    MISSOURI(
            "MO",
            "https://www.mo.gov"
    ),
    MONTANA(
            "MT",
            "https://montana.gov"
    ),
    NEBRASKA(
            "NE",
            "https://www.nebraska.gov"
    ),
    NEVADA(
            "NV",
            "http://nv.gov"
    ),
    NEW_HAMPSHIRE(
            "NH",
            "https://www.nh.gov"
    ),
    NEW_JERSEY(
            "NJ",
            "https://nj.gov"
    ),
    NEW_MEXICO(
            "NM",
            "https://www.nm.gov"
    ),
    NEW_YORK(
            "NY",
            "https://www.ny.gov"
    ),
    NORTH_CAROLINA(
            "NC",
            "https://www.nc.gov"
    ),
    NORTH_DAKOTA(
            "ND",
            "https://www.nd.gov"
    ),
    OHIO(
            "OH",
            "https://ohio.gov/"
    ),
    OKLAHOMA(
            "OK",
            "https://oklahoma.gov"
    ),
    OREGON(
            "OR",
            "https://www.oregon.gov"
    ),
    PENNSYLVANIA(
            "PA",
            "https://www.pa.gov"
    ),
    RHODE_ISLAND(
            "RI",
            "https://www.ri.gov"
    ),
    SOUTH_CAROLINA(
            "SC",
            "https://sc.gov"
    ),
    SOUTH_DAKOTA(
            "SD",
            "https://sd.gov"
    ),
    TENNESSEE(
            "TN",
            "https://www.tn.gov"
    ),
    TEXAS(
            "TX",
            "https://www.texas.gov"
    ),
    UTAH(
            "UT",
            "https://www.utah.gov"
    ),
    VERMONT(
            "VT",
            "https://www.vermont.gov"
    ),
    VIRGINIA(
            "VA",
            "https://www.virginia.gov"
    ),
    WASHINGTON(
            "WA",
            "https://wa.gov"
    ),
    WEST_VIRGINIA(
            "WV",
            "https://www.wv.gov"
    ),
    WISCONSIN(
            "WI",
            "https://www.wisconsin.gov/"
    ),
    WYOMING(
            "WY",
            "http://www.wyo.gov"
    ),

    DISTRICT_OF_COLUMBIA(
            "DC",
            "https://dc.gov"
    ),
    AMERICAN_SAMOA(
            "AS",
            "https://www.americansamoa.gov"
    ),
    GUAM(
            "GU",
            "https://www.guam.gov"
    ),
    NORTHERN_MARIANA_ISLANDS(
            "MP",
            "https://governor.gov.mp"
    ),
    PUERTO_RICO(
            "PR",
            "https://www.pr.gov"
    ),
    VIRGIN_ISLANDS(
            "VI",
            "https://www.vi.gov"
    ),
    ;

    public static final USTerritory[] TERRITORIES = USTerritory.values();
    private static HashMap<String, Integer> TABLE;
    private String backendID, name, abbreviation, flagURL, governmentURL;
    private PopulationEstimate populationEstimate;

    USTerritory(String abbreviation, String governmentURL) {
        final String realName = name();
        backendID = realName.toLowerCase().replace("_", "");
        name = LocalServer.toCorrectCapitalization(realName, "of");
        this.abbreviation = abbreviation;
        final String name = realName.toLowerCase().replace("_", "-");
        flagURL = "https://flaglane.com/download/" + name + "-flag/" + name + "-flag-large.png";
        this.governmentURL = governmentURL;
        setupPopulationEstimate();
    }

    private int getEstimate() {
        if(TABLE == null) {
            TABLE = new HashMap<>();
            final String url = "https://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States";
            final Element table = getDocument(url).select("div.mw-parser-output table.wikitable").first();
            final Elements list = table.select("tbody tr");
            for(int i = 1; i <= 2; i++) {
                list.remove(0);
            }
            for(Element element : list) {
                final String state = element.select("th a[href]").first().text();
                final Elements span = element.select("td span");
                final int estimate = span.size() > 1 ? Integer.parseInt(span.get(1).text().replace(",", "")) : -1;
                TABLE.put(state, estimate);
            }
        }
        return TABLE.getOrDefault(name, -1);
    }

    private void setupPopulationEstimate() {
        final int estimate = getEstimate();
        populationEstimate = new PopulationEstimate(estimate, 2000);
    }
    private TimeZone getRealTimeZone() {
        switch (this) {
            case WASHINGTON:
            case OREGON:
            case CALIFORNIA:
            case NEVADA:
                return TimeZones.PACIFIC_STANDARD_TIME;
            case MONTANA:
            case IDAHO:
            case WYOMING:
            case UTAH:
            case COLORADO:
            case NEW_MEXICO:
            case ARIZONA:
                return TimeZones.MOUNTAIN_STANDARD_TIME;
            case NORTH_DAKOTA:
            case SOUTH_DAKOTA:
            case NEBRASKA:
            case KANSAS:
            case OKLAHOMA:
            case TEXAS:
            case MINNESOTA:
            case IOWA:
            case MISSOURI:
            case ARKANSAS:
            case LOUISIANA:
            case WISCONSIN:
            case ILLINOIS:
            case TENNESSEE:
            case MISSISSIPPI:
            case ALABAMA:
                return TimeZones.CENTRAL_STANDARD_TIME;
            case HAWAII:
                return TimeZones.HAWAII_STANDARD_TIME;
            case ALASKA:
                return TimeZones.ALASKA_STANDARD_TIME;
            case PUERTO_RICO:
                return TimeZones.PUERTO_RICO_TIME;
            default:
                return TimeZones.EASTERN_STANDARD_TIME;
        }
    }

    @Override
    public Country getCountry() {
        return Country.UNITED_STATES_OF_AMERICA;
    }
    @Override
    public String getBackendID() {
        return backendID;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getAbbreviation() {
        return abbreviation;
    }
    @Override
    public String getFlagURL() {
        return flagURL;
    }
    @Override
    public PopulationEstimate getPopulationEstimate() {
        return populationEstimate;
    }
    @Override
    public String getGovernmentURL() {
        return governmentURL;
    }
}
