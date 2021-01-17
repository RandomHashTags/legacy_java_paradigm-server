package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.WLLogger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

// https://en.wikipedia.org/wiki/List_of_administrative_divisions_by_country
public enum Territories implements Jsoupable {
    AFGHANISTAN("https://en.wikipedia.org/wiki/Provinces_of_Afghanistan"),
    ALBANIA("https://en.wikipedia.org/wiki/Counties_of_Albania"),
    ALGERIA("https://en.wikipedia.org/wiki/Provinces_of_Algeria"),
    ANDORRA("https://en.wikipedia.org/wiki/Parishes_of_Andorra"),
    ANGOLA("https://en.wikipedia.org/wiki/Provinces_of_Angola"),
    ARGENTINA("https://en.wikipedia.org/wiki/Provinces_of_Argentina"),
    ARMENIA("https://en.wikipedia.org/wiki/Administrative_divisions_of_Armenia"),
    ARTSAKH("https://en.wikipedia.org/wiki/Administrative_divisions_of_the_Republic_of_Artsakh"),
    AUSTRALIA("https://en.wikipedia.org/wiki/States_and_territories_of_Australia"),
    AUSTRIA("https://en.wikipedia.org/wiki/States_of_Austria"),
    AZERBAIJAN("https://en.wikipedia.org/wiki/Administrative_divisions_of_Azerbaijan"),

    BAHAMAS("https://en.wikipedia.org/wiki/Local_government_in_The_Bahamas"),
    BAHRAIN("https://en.wikipedia.org/wiki/Governorates_of_Bahrain"),
    BANGLADESH("https://en.wikipedia.org/wiki/Districts_of_Bangladesh"),

    CANADA("https://en.wikipedia.org/wiki/Provinces_and_territories_of_Canada"),

    CHINA("https://en.wikipedia.org/wiki/Provinces_of_China"),

    FRANCE("https://en.wikipedia.org/wiki/Regions_of_France"),
    FINLAND("https://en.wikipedia.org/wiki/Regions_of_Finland"),

    INDIA("https://en.wikipedia.org/wiki/States_and_union_territories_of_India"),
    INDONESIA("https://en.wikipedia.org/wiki/Provinces_of_Indonesia"),
    IRAN("https://en.wikipedia.org/wiki/Provinces_of_Iran"),
    IRAQ("https://en.wikipedia.org/wiki/Governorates_of_Iraq"),
    IRELAND("https://en.wikipedia.org/wiki/Provinces_of_Ireland"),

    ITALY("https://en.wikipedia.org/wiki/Regions_of_Italy"),

    MEXICO("https://en.wikipedia.org/wiki/List_of_states_of_Mexico"),

    OMAN("https://en.wikipedia.org/wiki/Governorates_of_Oman"),

    QATAR("https://en.wikipedia.org/wiki/Municipalities_of_Qatar"),

    ROMANIA("https://en.wikipedia.org/wiki/Counties_of_Romania"),
    RUSSIA("https://en.wikipedia.org/wiki/Federal_subjects_of_Russia"),
    RWANDA("https://en.wikipedia.org/wiki/Provinces_of_Rwanda"),

    TAJIKISTAN("https://en.wikipedia.org/wiki/Regions_of_Tajikistan"),
    TANZANIA("https://en.wikipedia.org/wiki/Regions_of_Tanzania"),
    THAILAND("https://en.wikipedia.org/wiki/Provinces_of_Thailand"),
    TOGO("https://en.wikipedia.org/wiki/Regions_of_Togo"),
    TONGA("https://en.wikipedia.org/wiki/Administrative_divisions_of_Tonga"),
    TRINIDAD_AND_TOBAGO("https://en.wikipedia.org/wiki/Regional_corporations_and_municipalities_of_Trinidad_and_Tobago"),
    TUNISIA("https://en.wikipedia.org/wiki/Governorates_of_Tunisia"),
    TURKEY("https://en.wikipedia.org/wiki/Provinces_of_Turkey"),
    TURKMENISTAN("https://en.wikipedia.org/wiki/Regions_of_Turkmenistan"),
    TUVALU("https://en.wikipedia.org/wiki/List_of_islands_of_Tuvalu"),

    UGANDA("https://en.wikipedia.org/wiki/Districts_of_Uganda"),
    UKRAINE("https://en.wikipedia.org/wiki/Administrative_divisions_of_Ukraine"),
    UNITED_ARAB_EMIRATES("https://en.wikipedia.org/wiki/Emirates_of_the_United_Arab_Emirates"),
    UNITED_STATES("https://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States"),
    UZBEKISTAN("https://en.wikipedia.org/wiki/Regions_of_Uzbekistan"),

    VANUATU("https://en.wikipedia.org/wiki/Provinces_of_Vanuatu"),
    VENEZUELA("https://en.wikipedia.org/wiki/States_of_Venezuela"),
    VIETNAM("https://en.wikipedia.org/wiki/Provinces_of_Vietnam"),

    YEMEN("https://en.wikipedia.org/wiki/Governorates_of_Yemen"),

    ZAMBIA("https://en.wikipedia.org/wiki/Provinces_of_Zambia"),
    ZIMBABWE("https://en.wikipedia.org/wiki/Provinces_of_Zimbabwe")
    ;

    private static final HashMap<String, Territory> ABBREVIATIONS = new HashMap<>();
    private final String backendID, wikipageURL;
    private String territoriesJSONArray;
    private HashSet<Territory> territories;

    Territories(String wikipageURL) {
        backendID = name().toLowerCase().replace("_", "").replace(" and ", "&");
        this.wikipageURL = wikipageURL;
    }

    public String getBackendID() {
        return backendID;
    }
    public String getWikipageURL() {
        return wikipageURL;
    }
    public String getTerritoriesJSONArray() {
        if(territoriesJSONArray == null) {
            loadTerritories();
        }
        return territoriesJSONArray;
    }
    public HashSet<Territory> getTerritories() {
        if(territories == null) {
            loadTerritories();
        }
        return territories;
    }

    private Territory createTerritory(String name, String abbreviation, String flagURL) {
        final Territory territory = new Territory() {
            @Override
            public String getBackendID() {
                return name.toLowerCase().replace(" ", "");
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
                return null;
            }

            @Override
            public String getGovernmentURL() {
                return null;
            }
        };
        if(abbreviation != null) {
            ABBREVIATIONS.put(abbreviation, territory);
        }
        if(territories == null) {
            territories = new HashSet<>();
        }
        territories.add(territory);
        return territory;
    }

    private void loadTerritories() {
        final long started = System.currentTimeMillis();
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        final String tableClassName = getTableClassName();
        final Elements territories = getDocumentElements(wikipageURL, "table." + tableClassName);
        for(int tableIndex : getTableIndexes()) {
            final Element first = territories.get(tableIndex);
            final Elements states = first.select("tbody tr");
            removeUnnecessaryElements(states);
            for(Element state : states) {
                final Elements head = state.select("th"), rows = state.select("td");
                final String name = getName(head, rows);
                final String abbreviation = getAbbreviation(head, rows);
                final String flagURL = getFlagURL(name);
                final Territory territory = createTerritory(name, abbreviation, flagURL);
                builder.append(isFirst ? "" : ",").append(territory.toJSON());
                isFirst = false;
            }
        }
        builder.append("]");
        territoriesJSONArray = builder.toString();
        WLLogger.log(Level.INFO, "Countries - loaded \"" + getBackendID() + "\"'s territories (took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private String getTableClassName() {
        switch (this) {
            case UKRAINE:
            case ZIMBABWE:
                return "sortable";
            default:
                return "wikitable";
        }
    }
    private int[] getTableIndexes() {
        switch (this) {
            case AZERBAIJAN:
            case VENEZUELA:
            case UKRAINE:
                return new int[] { 1 };
            case UNITED_STATES:
                return new int[] { 0, 1, 2 };
            case RUSSIA:
                return new int[] { 2 };
            case FRANCE:
                return new int[] { 3 };
            default:
                return new int[] { 0 };
        }
    }
    private void removeUnnecessaryElements(Elements elements) {
        switch (this) {
            case AFGHANISTAN:
            case ALBANIA:
            case ANGOLA:
            case ARGENTINA:
            case ARMENIA:
            case ARTSAKH:
            case AUSTRALIA:
            case AUSTRIA:
            case AZERBAIJAN:

            case BAHAMAS:
            case BANGLADESH:

            case FRANCE:
            case FINLAND:

            case INDIA:
            case INDONESIA:
            case IRAN:
            case IRAQ:
            case IRELAND:

            case OMAN:

            case QATAR:

            case ROMANIA:
            case RWANDA:

            case TAJIKISTAN:
            case TANZANIA:
            case THAILAND:
            case TOGO:
            case TONGA:
            case TUNISIA:
            case TURKEY:
            case TURKMENISTAN:

            case UGANDA:
            case UKRAINE:
            case UNITED_ARAB_EMIRATES:
            case UZBEKISTAN:

            case VANUATU:
            case VENEZUELA:

            case VIETNAM:
            case ZAMBIA:
                elements.remove(0);
                if(this == AZERBAIJAN || this == IRAN || this == QATAR || this == TONGA || this == VANUATU || this == ZAMBIA) {
                    elements.remove(elements.last());
                }
                break;
            default:
                final boolean isTuvalu = this == TUVALU, isYemen = this == YEMEN;
                final int max = this == TRINIDAD_AND_TOBAGO ? 3 : 2;
                for(int i = 1; i <= max; i++) {
                    elements.remove(0);
                }
                if(isTuvalu) {
                    elements.removeIf(row -> !row.select("td b a[href]").isEmpty());
                    elements.remove(elements.last());
                } else if(this == ALGERIA || this == ANDORRA || this == CANADA || this == ITALY || isYemen) {
                    elements.remove(elements.last());
                    if(isYemen) {
                        elements.removeIf(row -> row.select("td").isEmpty());
                    }
                }
                break;
        }
    }
    private String getFlagURL(String name) {
        switch (this) {
            case UNITED_STATES:
                name = name.toLowerCase().replace(" ", "-");
                return "https://flaglane.com/download/" + name + "-flag/" + name + "-flag-large.png";
            default:
                return null;
        }
    }
    private String getName(Elements head, Elements rows) {
        int nameIndex = getNameIndex();
        final Elements links, span;
        switch (this) {
            case AFGHANISTAN:
            case AUSTRALIA:
                return rows.get(nameIndex).text();
            case ALGERIA:
            case ANDORRA:
            case ANGOLA:
            case ARGENTINA:
            case ARMENIA:
            case ARTSAKH:
            case AUSTRIA:
            case AZERBAIJAN:

            case BAHAMAS:
            case BAHRAIN:
            case BANGLADESH:

            case CHINA:

            case FRANCE:
            case FINLAND:

            case INDONESIA:
            case IRAN:
            case IRAQ:
            case IRELAND:
            case ITALY:

            case MEXICO:

            case OMAN:

            case QATAR:

            case RUSSIA:
            case RWANDA:

            case TAJIKISTAN:
            case TANZANIA:
            case THAILAND:
            case TOGO:
            case TONGA:
            case TRINIDAD_AND_TOBAGO:
            case TUNISIA:
            case TURKEY:
            case TUVALU:

            case UGANDA:
            case UKRAINE:
            case UNITED_ARAB_EMIRATES:

            case VANUATU:
            case VENEZUELA:
            case VIETNAM:

            case YEMEN:

            case ZAMBIA:
                links = rows.select("a[href]");
                span = rows.select("span.nowrap a[href]");
                if(this == ARMENIA || this == FINLAND) {
                    links.removeIf(link -> link.hasClass("image"));
                }
                break;
            default:
                final boolean isRomania = this == ROMANIA;
                final Elements heads = head.select("a[href]");
                links = isRomania && heads.isEmpty() ? rows.select("a[href]") : heads;
                span = head.select("span.nowrap a[href]");
                break;
        }
        return links.size() > nameIndex ? links.get(nameIndex).text() : span.size() > nameIndex ? span.get(nameIndex).text() : "Unknown";
    }
    private int getNameIndex() {
        switch (this) {
            case ANDORRA:
            case ARGENTINA:
            case AUSTRALIA:
            case FRANCE:
            case INDONESIA:
            case ITALY:
            case THAILAND:
            case UNITED_ARAB_EMIRATES:
            case VENEZUELA:
                return 1;
            case CANADA:
                return 2;
            default:
                return 0;
        }
    }
    private String getAbbreviation(Elements head, Elements rows) {
        switch (this) {
            case UNITED_STATES:
                return rows.get(0).text();
            default:
                return null;
        }
    }

    public static Territories valueOfBackendID(String backendID) {
        for(Territories territory : values()) {
            if(territory.getBackendID().equalsIgnoreCase(backendID)) {
                return territory;
            }
        }
        return null;
    }
    public static Territory valueOfTerritoryAbbreviation(String abbreviation) {
        return ABBREVIATIONS.getOrDefault(abbreviation, null);
    }
}
