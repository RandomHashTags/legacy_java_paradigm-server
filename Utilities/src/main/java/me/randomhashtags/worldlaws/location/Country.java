package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CountryDebt;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.TargetServer;
import me.randomhashtags.worldlaws.location.territories.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public enum Country {
    AFGHANISTAN(
            null,
            null,
            null,
            AFTerritory.TERRITORIES,
            null,
            "",
            "\uD83C\uDDE6\uD83C\uDDEB",
            "AF"
    ),
    /*
    ALBANIA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDF1",
            "AL"
    ),
    ALGERIA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE9\uD83C\uDDFF",
            "AG"
    ),
    ANDORRA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDE9",
            "AN"
    ),
    ANGOLA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDF4",
            "AO"
    ),
    ANTIGUA_AND_BARBUDA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDEC",
            "AC"
    ),
    ARGENTINA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDF7",
            "AR"
    ),
    ARMENIA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDF2",
            "AM"
    ),*/
    AUSTRALIA(
            null,
            null,
            null,
            ASTerritory.TERRITORIES,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDFA",
            "AS"
    ),
    /*
    AUSTRIA(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDF9",
            "AU"
    ),
    AZERBAIJAN(
            null,
            null,
            null,
            null,
            "\uD83C\uDDE6\uD83C\uDDFF",
            "AJ"
    ),*/

    CANADA(
            TargetServer.CANADA,
            null,
            null,
            CATerritory.TERRITORIES,
            new Location(56.130367, -106.346771),
            "",
            "\uD83C\uDDE8\uD83C\uDDE6",
            "CA"
    ),

    CHINA(null,
            null,
            null,
            CHTerritory.TERRITORIES,
            null,
            null,
            "\uD83C\uDDE8\uD83C\uDDF3",
            "CH"
    ),

    RUSSIA(
            null,
            null,
            null,
            RUTerritory.TERRITORIES,
            null,
            "",
            "\uD83C\uDDF7\uD83C\uDDFA",
            "RS"
    ),

    UGANDA(
            null,
            null,
            null,
            UGTerritory.TERRITORIES,
            null,
            "",
            "\uD83C\uDDFA\uD83C\uDDEC",
            "UG"
    ),
    UKRAINE(
            null,
            null,
            null,
            UPTerritory.TERRITORIES,
            null,
            "",
            "\uD83C\uDDFA\uD83C\uDDE6",
            "UP"
    ),
    UNITED_ARAB_EMIRATES(
            null,
            null,
            "UAE",
            null,
            null,
            "",
            "\uD83C\uDDE6\uD83C\uDDEA",
            "AE"
    ),
    UNITED_KINGDOM(
            null,
            null,
            "UK",
            null,
            null,
            "",
            "\uD83C\uDDEC\uD83C\uDDE7",
            "UK"
    ),
    UNITED_STATES_OF_AMERICA(
            TargetServer.USA,
            Arrays.asList("the United States", "United States", "America"),
            "USA",
            USTerritory.TERRITORIES,
            new Location(37.090240, -95.712891),
            "",
            "\uD83C\uDDFA\uD83C\uDDF8",
            "US"
    ),

    ;

    private TargetServer server;
    private String name, shortName, flagURL, flagEmoji, territoriesJSON, ciaWorldFactbookISO;
    private Location location;
    private HashSet<String> aliases;
    private Territory[] territories;
    private CountryDebt debt;

    Country(TargetServer server, List<String> aliases, String shortName, Territory[] territories, Location location, String flagURL, String flagEmoji, String ciaWorldFactbookISO) {
        this.server = server;
        this.aliases = new HashSet<>();
        this.shortName = shortName;
        if(aliases != null) {
            this.aliases.addAll(aliases);
        }
        name = LocalServer.toCorrectCapitalization(name(), "and", "of", "the");
        this.territories = territories;
        this.location = location;
        this.flagURL = flagURL;
        this.flagEmoji = flagEmoji;
        this.ciaWorldFactbookISO = ciaWorldFactbookISO;
        debt = new CountryDebt("0", "0", "0");
    }

    public TargetServer getServer() {
        return server;
    }
    public String getBackendID() {
        return name.toLowerCase().replace(" ", "");
    }
    public String getName() {
        return name;
    }
    public HashSet<String> getAliases() {
        return aliases;
    }
    public String getShortName() {
        return shortName;
    }
    public Territory[] getTerritories() {
        return territories;
    }
    public String getTerritoriesJSON() {
        final boolean exists = territories != null;
        if(exists && territoriesJSON == null) {
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(Territory territory : territories) {
                builder.append(isFirst ? "" : ",").append(territory.toJSON());
                isFirst = false;
            }
            builder.append("]");
            territoriesJSON = builder.toString();
        }
        return exists ? territoriesJSON : "[]";
    }
    public String getCIAWorldFactbookSummaryURL() {
        return "https://www.cia.gov/library/publications/resources/the-world-factbook/attachments/summaries/" + ciaWorldFactbookISO + "-summary.pdf";
    }

    public String getInformationJSON() {
        return "{" +
                "\"ciaWorldFactbookSummaryURL\":\"" + getCIAWorldFactbookSummaryURL() + "\"," +
                "\"debt\":" + debt.toString() +
                "}";
    }

    @Override
    public String toString() {
        return "{" +
                "\"backendID\":\"" + getBackendID() + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"shortName\":\"" + (shortName != null ? shortName : name) + "\"," +
                "\"flagURL\":\"" + flagURL + "\"," +
                "\"flagEmoji\":\"" + flagEmoji + "\"," +
                "\"location\":" + (location != null ? location.toString() : "null") +
                "}";
    }

    public static Country valueOfBackendID(String input) {
        for(Country country : Country.values()) {
            final String name = country.getName().replace(" ", "");
            if(name.equalsIgnoreCase(input)) {
                return country;
            }
        }
        return null;
    }
}
