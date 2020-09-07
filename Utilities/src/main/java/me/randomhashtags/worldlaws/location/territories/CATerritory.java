package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

public enum CATerritory implements Territory {
    ALBERTA(
            "AB",
            "https://www.alberta.ca"
    ),
    BRITISH_COLUMBIA(
            "BC",
            "https://www2.gov.bc.ca"
    ),
    MANITOBA(
            "MB",
            "https://www.gov.mb.ca"
    ),
    NEW_BRUNSWICK(
            "NB",
            "https://www2.gnb.ca"
    ),
    NEWFOUNDLAND_AND_LABRADOR(
            "NL",
            "https://www.gov.nl.ca"
    ),
    NOVA_SCOTIA(
            "NS",
            "https://novascotia.ca"
    ),
    ONTARIO(
            "ON",
            "https://www.ontario.ca"
    ),
    PRINCE_EDWARD_ISLAND(
            "PE",
            "https://www.princeedwardisland.ca"
    ),
    QUEBEC(
            "QC",
            "https://www.quebec.ca"
    ),
    SASKATCHEWAN(
            "SK",
            "https://www.saskatchewan.ca"
    ),

    NORTHWEST_TERRITORIES(
            "NT",
            "https://www.gov.nt.ca"
    ),
    YUKON(
            "YT",
            "https://yukon.ca"
    ),
    NUNAVUT(
            "NU",
            "https://www.gov.nu.ca"
    ),
    ;

    public static final Territory[] TERRITORIES = CATerritory.values();
    private String backendID, name, abbreviation, flagURL, governmentURL;

    CATerritory(String abbreviation, String governmentURL) {
        final String realName = name();
        backendID = realName.toLowerCase().replace("_", "");
        name = LocalServer.toCorrectCapitalization(realName, "and");
        this.abbreviation = abbreviation;
        this.governmentURL = governmentURL;
    }

    @Override
    public Country getCountry() {
        return Country.CANADA;
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
        return "https://upload.wikimedia.org/wikipedia/en/thumb/c/cf/Flag_of_Canada.svg/1024px-Flag_of_Canada.svg.png";
    }
    @Override
    public PopulationEstimate getPopulationEstimate() {
        return null;
    }
    @Override
    public String getGovernmentURL() {
        return governmentURL;
    }
}
