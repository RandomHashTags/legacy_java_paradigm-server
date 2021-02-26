package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

public enum TerritoryDetails implements CountryService {
    INSTANCE;

    private final HashMap<String, String> territories;
    private final Elements elements;

    TerritoryDetails() {
        territories = new HashMap<>();
        elements = getDocumentElements(FileType.COUNTRIES_SUBDIVISIONS, "https://en.wikipedia.org/wiki/List_of_administrative_divisions_by_country", "div.mw-parser-output table.wikitable tbody tr");
    }

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.TERRITORIES;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return null;
    }

    @Override
    public void refresh(CompletionHandler handler) {
    }

    public void getValues(WLCountry country, CompletionHandler handler) {
        final String countryBackendID = country.getBackendID();
        if(territories.containsKey(countryBackendID)) {
            handler.handle(territories.get(countryBackendID));
        } else {
            loadTerritories(country, handler);
        }
    }

    private void loadTerritories(WLCountry country, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        final String wikipageURL = getURL(country);
        final String tableClassName = getTableClassName(country);
        final Elements territoryElements = getDocumentElements(FileType.COUNTRIES_SUBDIVISIONS, wikipageURL, "table." + tableClassName);
        final int[] tableIndexes = getTableIndexes(country);
        for(int tableIndex : tableIndexes) {
            final Element first = territoryElements.get(tableIndex);
            final Elements states = first.select("tbody tr");
            removeUnnecessaryElements(country, states);
            for(Element state : states) {
                final Elements head = state.select("th"), rows = state.select("td");
                final String name = getName(country, head, rows);
                final String flagURL = getFlagURL(country, name);
                final Territory territory = createTerritory(name, flagURL);
                builder.append(isFirst ? "" : ",").append(territory.toJSON());
                isFirst = false;
            }
        }
        builder.append("]");
        final String string = builder.toString(), countryBackendID = country.getBackendID();
        territories.put(countryBackendID, string);
        WLLogger.log(Level.INFO,  getInfo().name() + " - loaded \"" + countryBackendID + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(string);
    }
    public String getURL(WLCountry country) {
        switch (country) {
            case ARTSAKH:
                return "https://en.wikipedia.org/wiki/Administrative_divisions_of_the_Republic_of_Artsakh";
            case IRELAND:
                return "https://en.wikipedia.org/wiki/Provinces_of_Ireland";
            case UNITED_STATES:
                return "https://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States";
            default:
                final String countryBackendID = country.getBackendID();
                final Stream<Element> stream = elements.stream().filter(element -> {
                    final Elements tds = element.select("td");
                    if(!tds.isEmpty()) {
                        final Elements hrefs = tds.get(0).select("a[href]");
                        if(!hrefs.isEmpty()) {
                            final String targetCountry = hrefs.get(0).text();
                            return countryBackendID.equalsIgnoreCase(targetCountry);
                        } else {
                            return false;
                        }
                    }
                    return false;
                });
                final Optional<Element> element = stream.findFirst();
                if(element.isPresent()) {
                    final Elements tds = element.get().select("td");
                    return "https://en.wikipedia.org" + tds.get(1).select("a").get(0).attr("href");
                }
                return null;
        }
    }

    private Territory createTerritory(String name, String flagURL) {
        return new Territory() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getFlagURL() {
                return flagURL;
            }

            @Override
            public String getGovernmentURL() {
                return null;
            }
        };
    }

    private String getTableClassName(WLCountry country) {
        switch (country) {
            case UKRAINE:
            case ZIMBABWE:
                return "sortable";
            default:
                return "wikitable";
        }
    }
    private int[] getTableIndexes(WLCountry country) {
        switch (country) {
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
    private String getFlagURL(WLCountry country, String name) {
        switch (country) {
            case UNITED_STATES:
                name = name.toLowerCase().replace(" ", "-");
                return "https://flaglane.com/download/" + name + "-flag/" + name + "-flag-large.png";
            default:
                return null;
        }
    }
    private void removeUnnecessaryElements(WLCountry country, Elements elements) {
        switch (country) {
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
                switch (country) {
                    case AZERBAIJAN:
                    case IRAN:
                    case QATAR:
                    case TONGA:
                    case VANUATU:
                    case ZAMBIA:
                        elements.remove(elements.last());
                        break;
                    default:
                        break;
                }
                break;
            default:
                final int max = country == WLCountry.TRINIDAD_AND_TOBAGO ? 3 : 2;
                for(int i = 1; i <= max; i++) {
                    elements.remove(0);
                }
                switch (country) {
                    case ALGERIA:
                    case ANDORRA:
                    case CANADA:
                    case ITALY:
                    case YEMEN:
                        final boolean isYemen = country == WLCountry.YEMEN;
                        elements.remove(elements.last());
                        if(isYemen) {
                            elements.removeIf(row -> row.select("td").isEmpty());
                        }
                        break;
                    case TUVALU:
                        elements.removeIf(row -> !row.select("td b a[href]").isEmpty());
                        elements.remove(elements.last());
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    private String getName(WLCountry country, Elements head, Elements rows) {
        final int nameIndex = getNameIndex(country);
        final Elements links, span;
        switch (country) {
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
                switch (country) {
                    case ARMENIA:
                    case FINLAND:
                        links.removeIf(link -> link.hasClass("image"));
                        break;
                    default:
                        break;
                }
                break;
            default:
                final boolean isRomania = country == WLCountry.ROMANIA;
                final Elements heads = head.select("a[href]");
                links = isRomania && heads.isEmpty() ? rows.select("a[href]") : heads;
                span = head.select("span.nowrap a[href]");
                break;
        }
        return links.size() > nameIndex ? links.get(nameIndex).text() : span.size() > nameIndex ? span.get(nameIndex).text() : "Unknown";
    }
    private int getNameIndex(WLCountry country) {
        switch (country) {
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
}
