package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.*;
import me.randomhashtags.worldlaws.info.agriculture.*;
import me.randomhashtags.worldlaws.info.availability.tech.*;
import me.randomhashtags.worldlaws.info.legal.*;
import me.randomhashtags.worldlaws.info.list.Flyover;
import me.randomhashtags.worldlaws.info.rankings.*;
import me.randomhashtags.worldlaws.info.service.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CustomCountry;
import me.randomhashtags.worldlaws.location.Territories;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

// https://simple.wikipedia.org/wiki/List_of_countries
// https://en.wikipedia.org/wiki/Lists_by_country
public final class Countries implements DataValues, Jsoupable, RestAPI {

    private String filters, json;
    private HashMap<String, CustomCountry> countriesMap;

    public static void main(String[] args) {
        new Countries().init();
    }

    private void init() {
        //test();
        load();
    }

    private void test() {
        MinimumAnnualLeave.INSTANCE.getValue("madagascar", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Countries;test;MinimumAnnualLeave;madagascar=" + object.toString());
            }
        });
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
                AppleCarPlay.INSTANCE,
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_PAY),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ONE),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_APP_STORE),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ARCADE),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_MUSIC),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_MOVIES),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_MUSIC),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_RINGTONES_AND_TONES),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_TV_APP),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_TV_PLUS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_PODCASTS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_NEWS_PLUS),
                new AppleAvailabilityObj(CountryInfo.AVAILABILITY_APPLE_FITNESS_PLUS),
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

        services.addAll(Arrays.asList(
                ProductionFoodApple.INSTANCE,
                ProductionFoodApricot.INSTANCE,
                ProductionFoodAvocado.INSTANCE,
                ProductionFoodCherry.INSTANCE,
                ProductionFoodCoconut.INSTANCE,
                ProductionFoodCucumber.INSTANCE,
                ProductionFoodGarlic.INSTANCE,
                ProductionFoodGrape.INSTANCE,
                ProductionFoodPapaya.INSTANCE,
                ProductionFoodPear.INSTANCE,
                ProductionFoodPineapple.INSTANCE,
                ProductionFoodPlum.INSTANCE,
                ProductionFoodPotato.INSTANCE,
                ProductionFoodSoybean.INSTANCE,
                ProductionFoodTomato.INSTANCE
        ));

        services.addAll(Arrays.asList(
                NationalAnimals.INSTANCE,
                NationalCapitals.INSTANCE
        ));

        services.addAll(Arrays.asList(
                AgeStructure.INSTANCE,
                BloodTypeDistribution.INSTANCE,
                HealthCareSystem.INSTANCE,
                MilitaryEnlistmentAge.INSTANCE,
                MinimumAnnualLeave.INSTANCE,
                MinimumDrivingAge.INSTANCE,
                SystemOfGovernment.INSTANCE,
                TrafficSide.INSTANCE,
                VotingAge.INSTANCE
        ));

        services.addAll(Arrays.asList(
                LegalityAbortion.INSTANCE,
                LegalityBitcoin.INSTANCE,
                LegalityCannabis.INSTANCE,
                LegalityDrinkingAge.INSTANCE,
                LegalityDrugAyahuasca.INSTANCE,
                LegalityDrugCocaine.INSTANCE,
                LegalityDrugIbogaine.INSTANCE,
                LegalityDrugMeth.INSTANCE,
                LegalityDrugPsilocybinMushrooms.INSTANCE,
                LegalityDrugPsychoactiveCactus.INSTANCE,
                LegalityDrugSalviaDivinorum.INSTANCE,
                LegalityIncest.INSTANCE,
                LegalityMaritalRape.INSTANCE,
                LegalityPornography.INSTANCE,
                LegalityProstitution.INSTANCE,
                LegalitySmokingAge.INSTANCE
        ));

        services.addAll(Arrays.asList(
                TravelBriefing.INSTANCE,
                CIAServices.INSTANCE
        ));

        services.addAll(Arrays.asList(
                AdultHIVRate.INSTANCE,
                CannabisUse.INSTANCE,
                CivilianFirearms.INSTANCE,
                ClimateChangePerformanceIndex.INSTANCE,
                CO2Emissions.INSTANCE,
                CorruptionPerceptionIndex.INSTANCE,
                DemocracyIndex.INSTANCE,
                EconomicFreedomIndex.INSTANCE,
                EducationIndex.INSTANCE,
                ElectricityConsumption.INSTANCE,
                FragileStateIndex.INSTANCE,
                FreedomRankings.INSTANCE,
                GlobalPeaceIndex.INSTANCE,
                GlobalTerrorismIndex.INSTANCE,
                HomicideRate.INSTANCE,
                HumanDevelopmentIndex.INSTANCE,
                IncarcerationRate.INSTANCE,
                InfantMortalityRate.INSTANCE,
                InflationRate.INSTANCE,
                LegatumProsperityIndex.INSTANCE,
                LifeExpectancy.INSTANCE,
                MaternalMortalityRate.INSTANCE,
                //MinimumWage.INSTANCE,
                NaturalDisasterRisk.INSTANCE,
                ObesityRate.INSTANCE,
                Population.INSTANCE,
                PressFreedomIndex.INSTANCE,
                QualityOfLifeIndex.INSTANCE,
                QualityOfNationalityIndex.INSTANCE,
                SocialProgressIndex.INSTANCE,
                SuicideRate.INSTANCE,
                UnemploymentRate.INSTANCE,
                //DebtCurrent.INSTANCE
                WorldGivingIndex.INSTANCE,
                WorldHappinessReport.INSTANCE
        ));

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
        countriesMap = new HashMap<>();
        final Elements table = getDocumentElements("https://simple.wikipedia.org/wiki/List_of_countries", "table.sortable tbody tr");
        for(int i = 1; i <= 2; i++) {
            table.remove(0);
        }
        table.removeIf(row -> row.select("td").get(0).select("b a[href]").size() == 0);
        for(Element row : table) {
            final Elements tds = row.select("td");
            final Element nameElement = tds.get(0).select("b a[href]").get(0);
            final String tag = nameElement.text(), targetURL = "https://simple.wikipedia.org" + nameElement.attr("href");
            new Thread(() -> createCountry(tag, targetURL)).start();
        }
        checkForMissingValues();
    }

    private void checkForMissingValues() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
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
                    WLLogger.log(Level.WARNING, "Countries - missing emoji flag for countries: " + string);
                }
            }
        }, 5*1000);
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
                    case "debt":
                        DebtCurrent.INSTANCE.getValue(key, handler);
                        break;
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
                case "filters":
                    handler.handle(getFilters());
                    break;
                case "ranked":
                    CountryRankingServices.getRanked(values[1], handler);
                    break;
                default:
                    handler.handle(getJSON());
                    break;
            }
        }
    }

    private void createCountry(String tag, String url) {
        final Document doc = getDocument(url);
        if(doc != null) {
            final CustomCountry country = new CustomCountry(tag, doc);
            final String backendID = country.getBackendID();
            final Territories territories = Territories.valueOfBackendID(backendID);
            country.setTerritories(territories);
            countriesMap.put(backendID, country);
        }
    }

    private String getFilters() {
        if(filters == null) {
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            final Collection<CountryService> services = CountryRankingServices.getRankingsServices().collect(Collectors.toList());
            for(CountryService service : services) {
                final CountryInfo info = service.getInfo();
                final String name = info.name(), backendID = name.toLowerCase().replace("_", "");
                final CountryFilter filter = new CountryFilter(backendID, name.replace("_", " "));
                builder.append(isFirst ? "" : ",").append(filter.toString());
                isFirst = false;
            }
            builder.append("]");
            filters = builder.toString();
        }
        return filters;
    }

    public String getJSON() {
        if(json == null) {
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(CustomCountry country : countriesMap.values()) {
                builder.append(isFirst ? "" : ",").append(country.toString());
                isFirst = false;
            }
            builder.append("]");
            json = builder.toString();
        }
        return json;
    }
    public CustomCountry valueOfBackendID(String backendID) {
        return countriesMap.getOrDefault(backendID.toLowerCase().replace(" ", ""), null);
    }
}
