package me.randomhashtags.worldlaws;

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
import me.randomhashtags.worldlaws.info.rankings.CountryRankingServices;
import me.randomhashtags.worldlaws.info.rankings.CountryRankings;
import me.randomhashtags.worldlaws.info.service.WikipediaFeaturedPictures;
import me.randomhashtags.worldlaws.info.service.nonstatic.CIAServices;
import me.randomhashtags.worldlaws.info.service.nonstatic.TravelAdvisories;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.locale.JSONTranslatable;
import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeCountries;
import me.randomhashtags.worldlaws.service.CountryServices;
import me.randomhashtags.worldlaws.service.CurrencyExchange;
import me.randomhashtags.worldlaws.service.NewCountryService;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// https://en.wikipedia.org/wiki/List_of_sovereign_states
// https://en.wikipedia.org/wiki/Lists_by_country
public final class Countries implements WLServer {

    private final HashMap<String, CustomCountry> countriesMap;
    private HashMap<APIVersion, JSONObjectTranslatable> countriesCacheJSON;

    public static void main(String[] args) {
         new Countries();
    }

    private Countries() {
        countriesMap = new HashMap<>();
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.COUNTRIES;
    }

    private void test() {
        loadServices();
        loadCountries(APIVersion.v1);
        final ServerRequest request = new ServerRequest(ServerRequestTypeCountries.INFORMATION, "unitedstates");
        final JSONTranslatable json = getServerResponse(APIVersion.v1, "***REMOVED***", request);
        WLLogger.logInfo("Countries;test1;string=" + (json != null ? json.toString() : "null"));
        final JSONObject translatedJSON = WLUtilities.translateJSON(json, LanguageTranslator.ARGOS, Language.SPANISH);
        final String string = translatedJSON != null ? translatedJSON.toString() : null;
        WLLogger.logInfo("Countries;test2;string=" + string);
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
        new CompletableFutures<CustomCountry>().stream(countriesMap.values(), CustomCountry::updateNonStaticInformation);
        WLLogger.logInfo("Countries - refreshed " + countriesMap.size() + " non-static country information (took " + WLUtilities.getElapsedTime(started) + ")");
    }

    private JSONObjectTranslatable getCountries(APIVersion version) {
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
                final JSONObjectTranslatable json = new JSONObjectTranslatable();
                json.put("response_version", ResponseVersions.COUNTRIES.getValue());
                final WLCountry[] countries = WLCountry.values();
                for(WLCountry country : countries) {
                    final CustomCountry customCountry = new CustomCountry(country);
                    countriesMap.put(customCountry.getBackendID(), customCountry);
                    final String id = customCountry.getBackendID();
                    json.put(id, customCountry.getJSONObject());
                    json.addTranslatedKey(id);
                }
                return json;
            default:
                return null;
        }
    }

    @Override
    public JSONTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeCountries type = (ServerRequestTypeCountries) request.getType();
        final String target = request.getTarget();
        final String[] values = target != null ? target.split("/") : null;
        switch (type) {
            case COUNTRIES:
                return getCountries(version);
            case CURRENCY_EXCHANGE:
                return CurrencyExchange.getResponse(request);
            case FILTERS:
                return getFilters();
            case RANKED:
                return CountryRankingServices.getRanked(values[0]);
            case INFORMATION:
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
                                WLLogger.logError(this, "getServerResponse - failed to get information for subdivision \"" + subdivisionBackendID + "\" from country \"" + country.getBackendID() + "\"!");
                            }
                            return null;
                        default:
                            return null;
                    }
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                new ServerRequest(ServerRequestTypeCountries.COUNTRIES),
                new ServerRequest(ServerRequestTypeCountries.FILTERS),
        };
    }

    private JSONObjectTranslatable getFilters() {
        final Set<String> services = CountryRankingServices.getNewRankingsServices().stream().map(test -> test.getInfo().getTitle()).collect(Collectors.toSet());
        final Folder folder = Folder.COUNTRIES;
        final String fileName = "filters";
        folder.setCustomFolderName(fileName, folder.getFolderName());
        JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.setFolder(folder);
        json.setFileName(fileName);
        final JSONObject local = Jsonable.getStaticLocalFileJSONObject(folder, fileName);
        if(local != null) {
            json = JSONObjectTranslatable.parse(local, folder, fileName, services, title -> {
                final JSONObjectTranslatable filterJSON = new JSONObjectTranslatable("title");
                filterJSON.put("title", title);
                return filterJSON;
            });
        } else {
            for(String title : services) {
                final JSONObjectTranslatable filterJSON = new JSONObjectTranslatable("title");
                filterJSON.put("title", title);
                json.put(title, filterJSON);
                json.addTranslatedKey(title);
            }
            Jsonable.setFileJSONObject(folder, fileName, json);
        }
        return json;
    }
}
