package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCurrency;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingServices;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.service.CurrencyExchange;
import me.randomhashtags.worldlaws.service.NewCountryService;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum ServerRequestTypeCountries implements ServerRequestType {
    COUNTRIES,
    FILTERS,
    INFORMATION,
    RANKED,
    CURRENCIES,
    CURRENCY_EXCHANGE,
    ;

    private static JSONObjectTranslatable CACHE_CURRENCIES, CACHE_FILTERS;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (version) {
            case v1: return getV1Handler(version);
            default: return null;
        }
    }

    @Override
    public boolean isConditional() {
        return this == INFORMATION;
    }

    private WLHttpHandler getV1Handler(APIVersion version) {
        switch (this) {
            case COUNTRIES:
                return httpExchange -> Countries.INSTANCE.getCountries(version);
            case CURRENCIES:
                return httpExchange -> getCurrencies();
            case CURRENCY_EXCHANGE:
                return httpExchange -> {
                    final HashMap<String, String> query = httpExchange.getQuery();
                    return CurrencyExchange.getResponse(query);
                };
            case FILTERS:
                return httpExchange -> getFilters();
            case RANKED:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return CountryRankingServices.getRanked(values[0]);
                };
            case INFORMATION:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return Countries.INSTANCE.getInformationResponse(version, values);
                };
            default:
                return null;
        }
    }

    private JSONObjectTranslatable getCurrencies() {
        if(CACHE_CURRENCIES != null) {
            return CACHE_CURRENCIES;
        }
        final JSONObjectTranslatable json = new JSONObjectTranslatable("currencies");
        json.put(WLUtilities.RESPONSE_VERSION_KEY, ResponseVersions.COUNTRY_CURRENCIES.getValue());
        final JSONArrayTranslatable array = new JSONArrayTranslatable();
        for(WLCurrency currency : WLCurrency.values()) {
            array.put(currency.toJSONObject());
        }
        json.put("currencies", array);
        CACHE_CURRENCIES = json;
        return json;
    }
    private JSONObjectTranslatable getFilters() {
        if(CACHE_FILTERS != null) {
            return CACHE_FILTERS;
        }
        final Set<SovereignStateInfo> services = CountryRankingServices.getNewRankingsServices().stream().map(NewCountryService::getInfo).collect(Collectors.toSet());
        final HashMap<String, String> servicesMap = new HashMap<>();
        for(SovereignStateInfo info : services) {
            servicesMap.put(info.getID(), info.getTitle());
        }
        final Folder folder = Folder.COUNTRIES;
        final String fileName = "filters";
        folder.setCustomFolderName(fileName, folder.getFolderName());
        JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.setFolder(folder);
        json.setFileName(fileName);
        final JSONObject local = Jsonable.getStaticLocalFileJSONObject(folder, fileName);
        final String responseVersionKey = WLUtilities.RESPONSE_VERSION_KEY;
        final int responseVersion = ResponseVersions.COUNTRY_FILTERS.getValue();
        if(local == null || local.optInt(responseVersionKey, 0) < responseVersion) {
            final JSONObjectTranslatable filters = new JSONObjectTranslatable();
            for(Map.Entry<String, String> entry : servicesMap.entrySet()) {
                final String id = entry.getKey(), title = entry.getValue();
                final JSONObjectTranslatable filterJSON = new JSONObjectTranslatable("title");
                filterJSON.put("title", title);
                filters.put(id, filterJSON, true);
            }
            json.put("filters", filters, true);
            json.put(responseVersionKey, responseVersion);
            Jsonable.setFileJSONObject(folder, fileName, json);
        } else {
            final Set<String> keys = servicesMap.keySet();
            json = JSONObjectTranslatable.copy(local, true);
            final JSONObject filtersJSON = json.getJSONObject("filters");
            final JSONObjectTranslatable filters = JSONObjectTranslatable.parse(filtersJSON, folder, fileName, keys, id -> {
                final JSONObjectTranslatable filterJSON = new JSONObjectTranslatable("title");
                filterJSON.put("title", filtersJSON.getJSONObject(id).getString("title"));
                return filterJSON;
            });
            json.put("filters", filters, true);
            json.put(responseVersionKey, responseVersion);
        }
        CACHE_FILTERS = json;
        return json;
    }
}
