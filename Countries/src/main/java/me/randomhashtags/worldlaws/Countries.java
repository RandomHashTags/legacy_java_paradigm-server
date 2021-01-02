package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.elections.Election;
import me.randomhashtags.worldlaws.info.*;
import me.randomhashtags.worldlaws.info.availability.tech.*;
import me.randomhashtags.worldlaws.info.legal.*;
import me.randomhashtags.worldlaws.info.list.Flyover;
import me.randomhashtags.worldlaws.info.rankings.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CustomCountry;
import me.randomhashtags.worldlaws.location.Territories;
import me.randomhashtags.worldlaws.service.CountryService;
import me.randomhashtags.worldlaws.service.CountryServices;
import me.randomhashtags.worldlaws.service.TravelBriefing;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;

// https://simple.wikipedia.org/wiki/List_of_countries
// https://en.wikipedia.org/wiki/Lists_by_country
public final class Countries implements DataValues, Jsoupable {

    private String filters, json;
    private HashMap<String, CustomCountry> countriesMap;

    public static void main(String[] args) {
        new Countries().init();
    }

    private void init() {
        test();
        //load();
    }

    private void test() {
        Elections.INSTANCE.getResponse("unitedstates", new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Countries;test;Elections;unitedstates=" + object.toString());
            }
        });
    }

    private void load() {
        new Thread(this::loadCountries).start();
        loadServices();
        startServer();
    }

    private void loadServices() {
        final List<CountryService> listServices = Arrays.asList(
                Flyover.INSTANCE
        );
        CountryServices.SERVICES.addAll(listServices);

        final List<CountryService> availabilityServices = Arrays.asList(
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
        );
        CountryServices.SERVICES.addAll(availabilityServices);

        final List<CountryService> governmentServices = Arrays.asList(
                SystemOfGovernment.INSTANCE
        );
        CountryServices.SERVICES.addAll(governmentServices);

        final List<CountryService> nationalServices = Arrays.asList(
                NationalCapitals.INSTANCE
        );
        CountryServices.SERVICES.addAll(nationalServices);

        final List<CountryService> infoServices = Arrays.asList(
                BloodTypeDistribution.INSTANCE,
                HealthCareSystems.INSTANCE
        );
        CountryServices.SERVICES.addAll(infoServices);

        final List<CountryService> legalityServices = Arrays.asList(
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
                LegalityMaritalRape.INSTANCE,
                LegalityPornography.INSTANCE
        );
        CountryServices.SERVICES.addAll(legalityServices);

        final List<CountryService> services = Arrays.asList(
                TravelBriefing.INSTANCE
        );
        CountryServices.SERVICES.addAll(services);

        final List<CountryRankingService> rankingServices = Arrays.asList(
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
                SocialProgressIndex.INSTANCE,
                SuicideRate.INSTANCE,
                UnemploymentRate.INSTANCE
                //DebtCurrent.INSTANCE
        );
        CountryRankingServices.SERVICES.addAll(rankingServices);
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
        final Document doc = getDocument("https://simple.wikipedia.org/wiki/List_of_countries");
        if(doc != null) {
            final Elements table = doc.select("table.sortable tbody tr");
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
        }
        checkForMissingValues();
    }

    private void checkForMissingValues() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder("");
                boolean isFirst = true;
                for(String key : countriesMap.keySet()) {
                    final CustomCountry country = countriesMap.get(key);
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
                final Territories territories;
                switch (values[1]) {
                    case "debt":
                        DebtCurrent.INSTANCE.getValue(key, handler);
                        break;
                    case "territories":
                        territories = country.getTerritories();
                        final String json = territories != null ? territories.getTerritoriesJSONArray() : "[]";
                        handler.handle(json);
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
                case "compare":
                    final StringBuilder builder = new StringBuilder("{");
                    final String[] countries = values[1].split("\\+");
                    for(String targetCountry : countries) {
                        final CustomCountry target = valueOfBackendID(targetCountry);
                    }
                    builder.append("}");
                    handler.handle(builder.toString());
                    break;
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
            for(CountryRankingService service : CountryRankingServices.SERVICES) {
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
