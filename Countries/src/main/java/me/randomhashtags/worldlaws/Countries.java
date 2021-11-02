package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.CountryHistory;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.info.CountryInfoKeys;
import me.randomhashtags.worldlaws.info.CountryValues;
import me.randomhashtags.worldlaws.info.agriculture.ProductionFoods;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilities;
import me.randomhashtags.worldlaws.info.legal.CountryLegalities;
import me.randomhashtags.worldlaws.info.legal.LegalityDrugs;
import me.randomhashtags.worldlaws.info.list.Flyover;
import me.randomhashtags.worldlaws.info.national.NationalAnthems;
import me.randomhashtags.worldlaws.info.national.NationalCapitals;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingServices;
import me.randomhashtags.worldlaws.info.rankings.CountryRankings;
import me.randomhashtags.worldlaws.info.service.CIAServices;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;
import me.randomhashtags.worldlaws.info.service.TravelBriefing;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
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
    private String countriesCacheJSON;

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
        loadServices();
        loadCountries(new CompletionHandler() {
            @Override
            public void handleString(String string) {
                getServerResponse(APIVersion.v1, "information/unitedstates", new CompletionHandler() {
                    @Override
                    public void handleString(String string) {
                        WLLogger.logInfo("Countries;test;string=" + string);
                    }
                });
            }
        });
    }

    @Override
    public void load() {
        loadServices();
        startServer();
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
    }

    private void loadServices() {
        final HashSet<CountryService> services = new HashSet<>() {{
            addAll(Arrays.asList(CountryAvailabilities.INSTANCE));
            addAll(Arrays.asList(ProductionFoods.values()));
            addAll(Arrays.asList(
                    NationalAnthems.INSTANCE,
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
                    new WikipediaCountryService(true)
            ));
            addAll(Arrays.asList(CountryRankings.values()));
            add(CountryHistory.INSTANCE);
        }};

        CountryServices.SERVICES.addAll(services);
    }

    private void getCountries(CompletionHandler handler) {
        if(countriesCacheJSON != null) {
            handler.handleString(countriesCacheJSON);
        } else {
            loadCountries(handler);
        }
    }
    private void loadCountries(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        getJSONArray(Folder.COUNTRIES, "_List of sovereign states", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements table = getDocumentElements(Folder.COUNTRIES, "https://en.wikipedia.org/wiki/List_of_sovereign_states", true, "table.sortable tbody tr");
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
                    sovereigntyElement.removeIf(element -> element.hasAttr("style") || element.hasParent() && element.parent().nodeName().equals("span"));
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
                        case "Saint Martin":
                            targetURL = "https://en.wikipedia.org/wiki/Saint_Martin_(island)";
                            break;
                        default:
                            targetURL = "https://en.wikipedia.org" + nameElement.attr("href");
                            break;
                    }
                    final Country country = new Country(tag, targetURL, unStatus, sovereigntyDispute);
                    builder.append(isFirst ? "" : ",").append(country.toString());
                    isFirst = false;
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                loadCountries(started, array, handler);
            }
        });
    }
    private void loadCountries(long started, JSONArray array, CompletionHandler handler) {
        final Folder folder = Folder.COUNTRIES;
        final String fileName = "_Countries";
        countriesMap = new HashMap<>();

        getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String tag = json.getString("tag"), url = json.getString("url");
                    final String unStatus = json.has("unStatus") ? json.getString("unStatus") : null, sovereigntyDispute = json.has("sovereigntyDispute") ? json.getString("sovereigntyDispute") : null;
                    final Document doc = getDocument(Folder.COUNTRIES_WIKIPEDIA_PAGES, url, true);
                    final CustomCountry country = new CustomCountry(tag, unStatus, sovereigntyDispute, doc);
                    countriesMap.put(country.getBackendID(), country);
                    builder.append(isFirst ? "" : ",").append(country.toString());
                    isFirst = false;
                }
                builder.append("}");
                final String string = builder.toString();
                countriesCacheJSON = string;
                handler.handleString(string);
            }

            @Override
            public void handleJSONObject(JSONObject countriesJSON) {
                if(countriesCacheJSON == null) {
                    countriesCacheJSON = getLocalFileString(folder, fileName, "json");
                    for(String name : countriesJSON.keySet()) {
                        final JSONObject json = countriesJSON.getJSONObject(name);
                        final CustomCountry country = new CustomCountry(name, json);
                        countriesMap.put(country.getBackendID(), country);
                    }
                }
                checkForMissingValues();
                WLLogger.logInfo("Countries - loaded " + countriesMap.size() + " countries (took " + (System.currentTimeMillis()-started) + "ms)");
                if(handler != null) {
                    handler.handleString(countriesCacheJSON);
                }
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
            WLLogger.logWarning("Countries - missing emoji flag for countries: " + string);
        }
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "ranked":
                CountryRankingServices.getRanked(values[1], handler);
                break;
            case "filters":
                handler.handleString(getFilters());
                break;
            case "countries":
                getCountries(handler);
                break;
            case "information":
                final String value = values[1];
                final CustomCountry country = countriesMap.get(value);
                if(country != null) {
                    final int length = values.length;
                    switch (length) {
                        case 2:
                            country.getInformation(version, handler);
                            break;
                        case 3:
                            final String subdivisionBackendID = values[2];
                            final SovereignStateSubdivision subdivision = country.getWLCountry().valueOfSovereignStateSubdivision(subdivisionBackendID);
                            if(subdivision != null) {
                                subdivision.getInformation(version, handler);
                            } else {
                                WLLogger.logError(this, "getServerResponse - failed to get information for subdivision \"" + subdivisionBackendID + "\" from country \"" + country.getBackendID() + "\"!");
                            }
                            break;
                        default:
                            handler.handleString(null);
                            break;
                    }
                } else {
                    handler.handleString(null);
                }
                break;
            default:
                WLLogger.logError(this, "getServerResponse - failed to send response using target \"" + target + "\"!");
                handler.handleString(null);
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

    private String getFilters() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        final Collection<CountryService> services = CountryRankingServices.getRankingsServices().collect(Collectors.toList());
        for(CountryService service : services) {
            final SovereignStateInfo info = service.getInfo();
            builder.append(isFirst ? "" : ",").append("\"").append(info.getTitle()).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
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
