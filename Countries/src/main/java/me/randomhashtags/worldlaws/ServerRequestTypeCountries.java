package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.info.rankings.CountryRankingServices;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.service.CurrencyExchange;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public enum ServerRequestTypeCountries implements ServerRequestType {
    COUNTRIES,
    FILTERS,
    INFORMATION,
    RANKED,
    CURRENCY_EXCHANGE,
    ;

    private static JSONObjectTranslatable CACHE_FILTERS;

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


    private JSONObjectTranslatable getFilters() {
        if(CACHE_FILTERS != null) {
            return CACHE_FILTERS;
        }
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
        CACHE_FILTERS = json;
        return json;
    }
}
