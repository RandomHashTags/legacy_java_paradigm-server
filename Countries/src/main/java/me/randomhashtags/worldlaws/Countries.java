package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SovereignState;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.info.CountryInfoKeys;
import me.randomhashtags.worldlaws.info.CountryValues;
import me.randomhashtags.worldlaws.info.agriculture.ProductionFoods;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilities;
import me.randomhashtags.worldlaws.info.legal.CountryLegalities;
import me.randomhashtags.worldlaws.info.legal.LegalityDrugs;
import me.randomhashtags.worldlaws.info.list.Flyover;
import me.randomhashtags.worldlaws.info.national.NationalAnimals;
import me.randomhashtags.worldlaws.info.national.NationalAnthems;
import me.randomhashtags.worldlaws.info.national.NationalCapitals;
import me.randomhashtags.worldlaws.info.national.NationalTrees;
import me.randomhashtags.worldlaws.info.rankings.CountryRankings;
import me.randomhashtags.worldlaws.info.service.WikipediaFeaturedPictures;
import me.randomhashtags.worldlaws.info.service.nonstatic.CIAServices;
import me.randomhashtags.worldlaws.info.service.nonstatic.TravelAdvisories;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.service.CountryServices;
import me.randomhashtags.worldlaws.service.CurrencyExchange;
import me.randomhashtags.worldlaws.service.NewCountryService;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

// https://en.wikipedia.org/wiki/List_of_sovereign_states
// https://en.wikipedia.org/wiki/Lists_by_country
public final class Countries implements WLServer {
    public static Countries INSTANCE = new Countries();
    private HashMap<String, CustomCountry> countriesMap;
    private HashMap<APIVersion, JSONObjectTranslatable> countriesCacheJSON;

    public static void main(String[] args) {
         INSTANCE.init();
    }

    private void init() {
        countriesMap = new HashMap<>();
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.COUNTRIES;
    }

    private void test() {
        final long started = System.currentTimeMillis();
        loadServices();
        final JSONTranslatable json = getInformationResponse(APIVersion.v1, new String[] { "unitedstates" });
        WLLogger.logInfo("Countries;test;json=" + (json != null ? json.toString() : "null") + ";took " + WLUtilities.getElapsedTime(started));
        /*
        final NewCountryService service = WikipediaFeaturedPictures.INSTANCE;
        //for(CountryValueService service : CountryValues.values()) {
            final JSONObjectTranslatable json = service.getJSONObject(WLCountry.UNITED_STATES);
            WLLogger.logInfo("Countries;test1;string=" + (json != null ? json.toString() : "null"));
            final JSONObject translatedJSON = WLUtilities.translateJSON(json, LanguageTranslator.ARGOS, Language.SPANISH);
            final String string = translatedJSON != null ? translatedJSON.toString() : null;
            WLLogger.logInfo("Countries;test2;string=" + string);
        //}*/
    }

    @Override
    public void load() {
        loadServices();
        startServer();
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        registerFixedTimer(UpdateIntervals.Countries.CURRENCY_EXCHANGE_CLEAR_CACHE, CurrencyExchange::clear);
        return 0;
    }

    private void loadServices() {
        loadCountries(APIVersion.getLatest());

        final HashSet<NewCountryService> newServices = new HashSet<>() {{
            addAll(Arrays.asList(
                    CountryAvailabilities.INSTANCE,
                    Flyover.INSTANCE,
                    new WikipediaCountryService(true),
                    WikipediaFeaturedPictures.INSTANCE
            ));
            addAll(Arrays.asList(ProductionFoods.values()));
            addAll(Arrays.asList(CountryInfoKeys.values()));
            addAll(Arrays.asList(CountryLegalities.values()));
            addAll(Arrays.asList(LegalityDrugs.values()));
            addAll(Arrays.asList(CountryRankings.values()));
            addAll(Arrays.asList(CountryValues.values()));
            addAll(Arrays.asList(
                    NationalAnimals.INSTANCE,
                    NationalAnthems.INSTANCE,
                    NationalCapitals.INSTANCE,
                    NationalTrees.INSTANCE
            ));
        }};
        CountryServices.STATIC_SERVICES.addAll(newServices);

        CountryServices.NONSTATIC_SERVICES.addAll(Arrays.asList(
                CIAServices.INSTANCE,
                TravelAdvisories.INSTANCE
        ));

        registerFixedTimer(UpdateIntervals.Countries.NON_STATIC_VALUES, this::updateNonStaticInformation);
    }

    private void updateNonStaticInformation() {
        final long started = System.currentTimeMillis();
        final AtomicInteger countriesUpdated = new AtomicInteger(0);
        final HashSet<Country> updatableCountries = new HashSet<>(countriesMap.values());
        updatableCountries.removeIf((country -> !country.canUpdateNonStaticInformation()));
        if(!updatableCountries.isEmpty()) {
            new CompletableFutures<CustomCountry>().stream(updatableCountries, country -> {
                if(country.updateNonStaticInformationIfExists()) {
                    countriesUpdated.addAndGet(1);
                }
            });
        }
        final int updatedCountries = countriesUpdated.get();
        WLLogger.logInfo("Countries - refreshed " + updatedCountries + " non-static country information (took " + WLUtilities.getElapsedTime(started) + ")");
    }

    public JSONObjectTranslatable getCountries(APIVersion version) {
        if(countriesCacheJSON == null) {
            countriesCacheJSON = new HashMap<>();
        }
        if(!countriesCacheJSON.containsKey(version)) {
            final JSONObjectTranslatable json = loadCountries(version);
            countriesCacheJSON.put(version, json);
        }
        return countriesCacheJSON.get(version);
    }
    private JSONObjectTranslatable loadCountries(APIVersion version) {
        switch (version) {
            case v1:
                final JSONObjectTranslatable json = new JSONObjectTranslatable("countries");
                json.put("response_version", ResponseVersions.COUNTRIES.getValue());
                final JSONObjectTranslatable countriesJSON = new JSONObjectTranslatable();
                final WLCountry[] countries = WLCountry.values();
                for(WLCountry country : countries) {
                    final CustomCountry customCountry = new CustomCountry(country);
                    final String backendID = customCountry.getBackendID();
                    countriesMap.put(backendID, customCountry);
                    countriesJSON.put(backendID, customCountry.getJSONObject(), true);
                }
                json.put("countries", countriesJSON);
                json.put("flagURLPrefix", SovereignState.FLAG_URL_PREFIX);
                return json;
            default:
                return null;
        }
    }

    public JSONTranslatable getInformationResponse(APIVersion version, String[] values) {
        final String value = values[0];
        final CustomCountry country = countriesMap.get(value);
        if(country != null) {
            final int length = values.length;
            switch (length) {
                case 1:
                    return country.getInformation(version);
                case 2:
                    final String subdivisionBackendID = values[1];
                    final SovereignStateSubdivision subdivision = country.getWLCountry().valueOfSovereignStateSubdivision(subdivisionBackendID);
                    if(subdivision != null) {
                        return subdivision.getInformation(version);
                    } else {
                        WLLogger.logError(this, "getInformationResponse - failed to get information for subdivision \"" + subdivisionBackendID + "\" from country \"" + country.getBackendID() + "\"!");
                        return null;
                    }
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                new ServerRequest(ServerRequestTypeCountries.COUNTRIES),
                new ServerRequest(ServerRequestTypeCountries.CURRENCIES),
                new ServerRequest(ServerRequestTypeCountries.FILTERS),
        };
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeCountries.values();
    }
}
