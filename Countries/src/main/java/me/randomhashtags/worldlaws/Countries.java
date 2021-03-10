package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.CountryInfoKeys;
import me.randomhashtags.worldlaws.info.CountryValues;
import me.randomhashtags.worldlaws.info.NationalCapitals;
import me.randomhashtags.worldlaws.info.agriculture.ProductionFoods;
import me.randomhashtags.worldlaws.info.availability.tech.*;
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
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// https://en.wikipedia.org/wiki/List_of_sovereign_states
// https://en.wikipedia.org/wiki/Lists_by_country
public final class Countries implements DataValues, Jsoupable, Jsonable, RestAPI {

    private String homeJSON;
    private HashMap<String, CustomCountry> countriesMap;

    public static void main(String[] args) {
        new Countries().init();
    }

    private void init() {
        //test();
        load();
    }

    private void test() {
        final String[] values = {
                "Norway",
        };
        for(String string : values) {
            Wikipedia.INSTANCE.getValue(string, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    WLLogger.log(Level.INFO, "Countries;test;string=" + string + ";object=" + object);
                }
            });
        }
    }

    private void testBandwidth() {
        final String uuid = "***REMOVED***";
        final HashMap<String, String> headers = new HashMap<>() {{
            put("Content-Type", "application/json");
            put("Charset", "UTF-8");
            put("***REMOVED***", "***REMOVED***");
            put("***REMOVED***", uuid);
        }};
        final HashSet<Integer> test = new HashSet<>();
        for(int i = 1; i <= 5000; i++) {
            test.add(i);
        }
        test.stream().parallel().forEach(integer -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    requestJSONObject("http://localhost:0/countries/all", RequestMethod.GET, headers, new CompletionHandler() {
                        @Override
                        public void handleJSONObject(JSONObject object) {
                            WLLogger.log(Level.INFO, "" + integer);
                        }
                    });
                }
            }).start();
        });
    }

    private void load() {
        new Thread(this::loadCountries).start();
        loadServices();
        startServer();
    }

    private void loadServices() {
        final HashSet<CountryService> services = new HashSet<>();

        services.add(Flyover.INSTANCE);

        services.addAll(Arrays.asList(
                AMCPlus.INSTANCE,
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_CARD),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_CARPLAY),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_PAY),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_APP_STORE_APPS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_APP_STORE_GAMES),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ARCADE),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MUSIC),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_MOVIES),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_MUSIC),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_TV_APP),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_TV_PLUS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS_AUDIO),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS_PLUS),
                ATandTTV.INSTANCE,
                DAZN.INSTANCE,
                DiscoveryPlus.INSTANCE,
                DisneyPlus.INSTANCE,
                ESPNPlus.INSTANCE,
                GooglePay.INSTANCE,
                GooglePlayPass.INSTANCE,
                HBOMax.INSTANCE,
                Hulu.INSTANCE,
                NvidiaGeforceNOW.INSTANCE,
                ParamountPlus.INSTANCE,
                Peacock.INSTANCE,
                PlayStationNow.INSTANCE,
                SamsungPay.INSTANCE,
                Spotify.INSTANCE,
                Stadia.INSTANCE,
                Tidal.INSTANCE,
                XboxCloudGaming.INSTANCE,
                XboxGamePass.INSTANCE,
                XboxLive.INSTANCE,
                YouTubePremium.INSTANCE,
                YouTubeTV.INSTANCE
        ));

        services.addAll(Arrays.asList(ProductionFoods.values()));

        services.addAll(Arrays.asList(
                NationalCapitals.INSTANCE
        ));

        services.addAll(Arrays.asList(CountryInfoKeys.values()));
        services.addAll(Arrays.asList(CountryValues.values()));

        services.addAll(Arrays.asList(CountryLegalities.values()));
        services.addAll(Arrays.asList(LegalityDrugs.values()));

        services.addAll(Arrays.asList(
                TravelBriefing.INSTANCE,
                CIAServices.INSTANCE,
                Wikipedia.INSTANCE
        ));

        services.addAll(Arrays.asList(CountryRankings.values()));

        CountryServices.SERVICES.addAll(services);
    }

    private void startServer() {
        LocalServer.start("Countries", WL_COUNTRIES_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object != null ? object.toString() : null;
                        client.sendResponse(string);
                    }
                });
            }
        });
    }

    private void loadCountries() {
        final long started = System.currentTimeMillis();
        countriesMap = new HashMap<>();
        getJSONArray(FileType.COUNTRIES, "_List of sovereign states", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements table = getDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/List_of_sovereign_states", "table.sortable tbody tr");
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
                    builder.append(isFirst ? "" : ",").append("{\"tag\":\"").append(tag).append("\",\"url\":\"").append(targetURL).append("\"}");
                    isFirst = false;
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String tag = json.getString("tag"), url = json.getString("url");
                    createCountry(tag, url);
                }
                WLLogger.log(Level.INFO, "Countries - loaded countries (took " + (System.currentTimeMillis()-started) + "ms)");
                checkForMissingValues();
            }
        });
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

    private void getResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
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
                            service.getValue(country.getBackendID(), handler);
                        }
                        break;
                    default:
                        handler.handle(country.toString());
                        break;
                }
            }
        } else {
            switch (key) {
                case "elections":
                    Elections.INSTANCE.getResponse("", handler);
                    break;
                case "ranked":
                    CountryRankingServices.getRanked(values[1], handler);
                    break;
                case "home":
                    getHomeJSON(handler);
                    break;
                default:
                    WLLogger.log(Level.WARN, "Countries - failed to send response using key \"" + key + "\"!");
                    break;
            }
        }
    }

    private void createCountry(String tag, String url) {
        final FileType type = FileType.COUNTRIES;
        getJSONObject(type, tag, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Document doc = getDocument(type, url, false);
                if(doc != null) {
                    final CustomCountry country = new CustomCountry(tag, doc);
                    handler.handle(country.toServerJSON());
                }
            }

            @Override
            public void handleJSONObject(JSONObject object) {
                final CustomCountry country = new CustomCountry(object);
                final String backendID = country.getBackendID();
                countriesMap.put(backendID, country);
            }
        });
    }

    private void getHomeJSON(CompletionHandler handler) {
        if(homeJSON != null) {
            handler.handle(homeJSON);
        } else {
            final HashSet<String> urls = new HashSet<>() {{
                add("filters");
                add("countries");
            }};
            final int max = urls.size();
            final StringBuilder builder = new StringBuilder("{");
            final AtomicInteger completed = new AtomicInteger(0);
            urls.parallelStream().forEach(url -> {
                final CompletionHandler completionHandler = new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final int value = completed.addAndGet(1);
                        builder.append(value == 1 ? "" : ",").append("\"").append(url).append("\":").append(object.toString());
                        if(value == max) {
                            builder.append("}");
                            homeJSON = builder.toString();
                            handler.handle(homeJSON);
                        }
                    }
                };
                switch (url) {
                    case "filters":
                        completionHandler.handle(getFilters());
                        break;
                    case "countries":
                        completionHandler.handle(getJSON());
                        break;
                }
            });
        }
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
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(CustomCountry country : countriesMap.values()) {
            builder.append(isFirst ? "" : ",").append(country.toString());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    public CustomCountry valueOfBackendID(String backendID) {
        return countriesMap.getOrDefault(backendID.toLowerCase().replace(" ", ""), null);
    }
}
