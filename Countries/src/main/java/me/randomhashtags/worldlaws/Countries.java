package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.CountryInfoKeys;
import me.randomhashtags.worldlaws.info.CountryValues;
import me.randomhashtags.worldlaws.info.NationalCapitals;
import me.randomhashtags.worldlaws.info.agriculture.ProductionFoods;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilities;
import me.randomhashtags.worldlaws.info.availability.tech.AppleAvailabilityObj;
import me.randomhashtags.worldlaws.info.legal.CountryLegalities;
import me.randomhashtags.worldlaws.info.legal.LegalityDrugs;
import me.randomhashtags.worldlaws.info.list.Flyover;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingServices;
import me.randomhashtags.worldlaws.info.rankings.CountryRankings;
import me.randomhashtags.worldlaws.info.service.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CustomCountry;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

// https://en.wikipedia.org/wiki/List_of_sovereign_states
// https://en.wikipedia.org/wiki/Lists_by_country
public final class Countries implements WLServer {

    private HashMap<String, CustomCountry> countriesMap;

    public static void main(String[] args) {
        new Countries();
    }

    private Countries() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.COUNTRIES;
    }

    private void test() {
        NationalCapitals.INSTANCE.getCountryValue("unitedstates", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Countries;test;object=" + object);
            }
        });
    }

    @Override
    public void load() {
        final CompletionHandler handler = new CompletionHandler() {
            @Override
            public void handle(Object object) {
            }
        };
        new Thread(() -> loadCountries(handler)).start();
        loadServices();
        startServer();
    }

    private void loadServices() {
        final HashSet<CountryService> services = new HashSet<>() {{
            addAll(Arrays.asList(CountryAvailabilities.values()));
            addAll(Arrays.asList(
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_CARD),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_CARPLAY),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_PAY),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_APP_STORE_APPS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_APP_STORE_GAMES),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ARCADE),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MAPS_CONGESTION_ZONES),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MAPS_DIRECTIONS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MAPS_SPEED_CAMERAS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MAPS_SPEED_LIMITS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MAPS_NEARBY),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MUSIC),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_SIRI),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_MOVIES),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_MUSIC),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_TV_APP),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_TV_PLUS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS_AUDIO),
                    new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS_PLUS)
            ));
            addAll(Arrays.asList(ProductionFoods.values()));
            addAll(Arrays.asList(
                    NationalCapitals.INSTANCE
            ));
            addAll(Arrays.asList(CountryInfoKeys.values()));
            addAll(Arrays.asList(CountryValues.values()));
            addAll(Arrays.asList(CountryLegalities.values()));
            addAll(Arrays.asList(LegalityDrugs.values()));
            addAll(Arrays.asList(
                    Flyover.INSTANCE,
                    TravelBriefing.INSTANCE,
                    CIAServices.INSTANCE,
                    Wikipedia.INSTANCE
            ));
            addAll(Arrays.asList(CountryRankings.values()));
        }};

        CountryServices.SERVICES.addAll(services);
    }

    private void loadCountries(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countriesMap = new HashMap<>();
        getJSONArray(FileType.COUNTRIES, "_List of sovereign states", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements table = getDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/List_of_sovereign_states", true, "table.sortable tbody tr");
                for(int i = 1; i <= 2; i++) {
                    table.remove(0);
                }
                table.removeIf(row -> row.select("td").get(0).select("b a[href]").size() == 0);
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element row : table) {
                    final Elements tds = row.select("td");
                    final Element nameElement = tds.get(0).select("b a[href]").get(0);
                    final String tag = nameElement.text();
                    final List<Node> sovereigntyElement = new ArrayList<>(tds.get(2).childNodes());
                    sovereigntyElement.removeIf(element -> {
                        return element.hasAttr("style") || element.hasParent() && element.parent().nodeName().equals("span");
                    });
                    final StringBuilder sovereigntyBuilder = new StringBuilder();
                    for(Node node : sovereigntyElement) {
                        String string = node instanceof TextNode ? ((TextNode) node).text() : ((Element) node).text();
                        sovereigntyBuilder.append(string);
                    }
                    final String unStatus = getUNStatus(tds.get(1).text()), sovereigntyDispute = getSovereigntyDispute(sovereigntyBuilder.toString());
                    final String targetURL;
                    switch (tag) {
                        case "Micronesia":
                            targetURL = "https://en.wikipedia.org/wiki/Federated_States_of_Micronesia";
                            break;
                        case "Netherlands":
                            targetURL = "https://en.wikipedia.org/wiki/Netherlands";
                            break;
                        default:
                            targetURL = "https://en.wikipedia.org" + nameElement.attr("href");
                            break;
                    }
                    final Country country = new Country(tag, targetURL, unStatus, sovereigntyDispute);
                    builder.append(isFirst ? "" : ",").append(country);
                    isFirst = false;
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                final FileType type = FileType.COUNTRIES;
                final String fileName = "_Countries";
                getJSONObject(type, fileName, new CompletionHandler() {
                    @Override
                    public void load(CompletionHandler handler) {
                        handler.handle("{}");
                    }

                    @Override
                    public void handleJSONObject(JSONObject countriesJSON) {
                        for(Object obj : array) {
                            final JSONObject json = (JSONObject) obj;
                            final String tag = json.getString("tag"), url = json.getString("url");
                            final String unStatus = json.has("unStatus") ? json.getString("unStatus") : null, sovereigntyDispute = json.has("sovereigntyDispute") ? json.getString("sovereigntyDispute") : null;
                            createCountry(countriesJSON, tag, unStatus, sovereigntyDispute, url);
                        }
                        WLLogger.log(Level.INFO, "Countries - loaded " + array.length() + " countries (took " + (System.currentTimeMillis()-started) + "ms)");
                        checkForMissingValues();
                        if(handler != null) {
                            handler.handle(null);
                        }
                    }
                });
            }
        });
    }
    private String getUNStatus(String input) {
        final String uppercaseInput = input.toUpperCase();
        if(uppercaseInput.contains("UN MEMBER STATE")) {
            return null;
        } else if(uppercaseInput.contains("FORMER UN MEMBER")) {
            return "FORMER UN MEMBER STATE";
        } else if(uppercaseInput.contains("UN OBSERVER STATE")) {
            return "UN OBSERVER STATE";
        } else if(uppercaseInput.contains("NO MEMBERSHIP")) {
            return "NO MEMBERSHIP";
        } else {
            return input.substring(1);
        }
    }
    private String getSovereigntyDispute(String input) {
        return input.toLowerCase().contains("none") ? null : removeReferences(input);
    }

    private void checkForMissingValues() {
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<String, CustomCountry> entry : countriesMap.entrySet()) {
            final CustomCountry country = entry.getValue();
            if(country.getFlagEmoji() == null) {
                builder.append(isFirst ? "" : ", ").append("(").append(country.getShortName()).append(", ").append(country.getName()).append(")");
                isFirst = false;
            }
        }
        final String string = builder.toString();
        if(!string.isEmpty()) {
            WLLogger.log(Level.WARN, "Countries - missing emoji flag for countries: " + string);
        }
    }

    @Override
    public void getServerResponse(ServerVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "ranked":
                CountryRankingServices.getRanked(values[1], handler);
                break;
            case "filters":
                handler.handle(getFilters());
                break;
            case "countries":
                handler.handle(getJSON());
                break;
            default:
                final CustomCountry country = valueOfBackendID(key);
                if(country != null) {
                    if(values.length == 1) {
                        handler.handle(country.toString());
                    } else {
                        switch (values[1]) {
                            case "information":
                                country.getInformation(handler);
                                break;
                            case "service":
                                final String serviceID = values[2];
                                final CountryService service = CountryServices.valueOfCountryInfo(serviceID);
                                if(service != null) {
                                    service.getCountryValue(country.getBackendID(), handler);
                                }
                                break;
                            default:
                                handler.handle(country.toString());
                                break;
                        }
                    }
                } else {
                    WLLogger.log(Level.WARN, "Countries - failed to send response using key \"" + key + "\"!");
                }
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "filters",
                "countries"
        };
    }

    private void createCountry(JSONObject countriesJSON, String tag, String unStatus, String sovereigntyDispute, String url) {
        if(countriesJSON.has(tag)) {
            final JSONObject countryJSON = countriesJSON.getJSONObject(tag);
            final CustomCountry country = new CustomCountry(countryJSON);
            countriesMap.put(country.getBackendID(), country);
        } else {
            loadCountry(tag, unStatus, sovereigntyDispute, url, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final JSONObject countryJSON = new JSONObject(object.toString());
                    final CustomCountry country = new CustomCountry(countryJSON);
                    countriesMap.put(country.getBackendID(), country);
                    countriesJSON.put(tag, countryJSON);
                    setFileJSONObject(FileType.COUNTRIES, "Countries", countriesJSON);
                }
            });
        }
    }
    private void loadCountry(String tag, String unStatus, String sovereigntyDispute, String url, CompletionHandler handler) {
        final Document doc = getDocument(FileType.COUNTRIES_COUNTRIES, url, true);
        String string = null;
        if(doc != null) {
            final CustomCountry country = new CustomCountry(tag, unStatus, sovereigntyDispute, doc);
            string = country.toServerJSON();
        }
        handler.handle(string);
    }

    private String getFilters() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        final Collection<CountryService> services = CountryRankingServices.getRankingsServices().collect(Collectors.toList());
        for(CountryService service : services) {
            final CountryInfo info = service.getInfo();
            builder.append(isFirst ? "" : ",").append("\"").append(info.getTitle()).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String getJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CustomCountry country : countriesMap.values()) {
            builder.append(isFirst ? "" : ",").append(country.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }
    public CustomCountry valueOfBackendID(String backendID) {
        return countriesMap.getOrDefault(backendID.toLowerCase().replace(" ", ""), null);
    }

    private static final class Country {
        private final String tag, url, unStatus, sovereigntyDispute;

        private Country(String tag, String url, String unStatus, String sovereigntyDispute) {
            this.tag = tag;
            this.url = url;
            this.unStatus = unStatus;
            this.sovereigntyDispute = sovereigntyDispute;
        }

        @Override
        public String toString() {
            return "{" +
                    (unStatus != null ? "\"unStatus\":\"" + unStatus + "\"," : "") +
                    (sovereigntyDispute != null ? "\"sovereigntyDispute\":\"" + sovereigntyDispute + "\"," : "") +
                    "\"tag\":\"" + tag + "\"," +
                    "\"url\":\"" + url + "\"" +
                    "}";
        }
    }
}
