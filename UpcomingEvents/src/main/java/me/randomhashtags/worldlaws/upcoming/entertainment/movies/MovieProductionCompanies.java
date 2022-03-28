package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public enum MovieProductionCompanies {
    INSTANCE;

    public static JSONObjectTranslatable getTypesJSON() {
        JSONObjectTranslatable json = new JSONObjectTranslatable();
        final int responseVersion = ResponseVersions.MOVIE_PRODUCTION_COMPANIES.getValue();
        final JSONObject local = Jsonable.getStaticLocalFileJSONObject(Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies");
        final int version = local != null && local.has("version") ? local.getInt("version") : -1;
        if(local == null || version < responseVersion) {
            json = loadJSON();
        } else {
            for(String key : local.keySet()) {
                json.put(key, local.get(key));
            }
            final JSONObject localCompaniesJSON = local.getJSONObject("companies");
            final JSONObjectTranslatable companiesJSON = JSONObjectTranslatable.parse(localCompaniesJSON, Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies", localCompaniesJSON.keySet(), name -> {
                final MovieProductionCompany company = new MovieProductionCompany(name, localCompaniesJSON.getJSONObject(name));
                return company.toJSONObject();
            });
            json.put("companies", companiesJSON);
            json.addTranslatedKey("companies");
        }
        return json;
    }
    private static JSONObjectTranslatable loadJSON() {
        final JSONObject productionCompaniesJSON = Settings.ServerValues.UpcomingEvents.getMovieProductionCompanies();
        final HashMap<String, JSONObjectTranslatable> companies = new HashMap<>();
        new CompletableFutures<String>().stream(productionCompaniesJSON.keySet(), key -> {
            final JSONObject productionCompanyJSON = productionCompaniesJSON.getJSONObject(key);
            final MovieProductionCompany company = new MovieProductionCompany(key, productionCompanyJSON);
            companies.put(key, company.toJSONObject());
        });

        final JSONObjectTranslatable companiesJSON = new JSONObjectTranslatable();
        for(Map.Entry<String, JSONObjectTranslatable> entry : companies.entrySet()) {
            final String key = entry.getKey();
            companiesJSON.put(key, entry.getValue());
            companiesJSON.addTranslatedKey(key);
        }

        final JSONObjectTranslatable json = new JSONObjectTranslatable("companies");
        json.put("version", ResponseVersions.MOVIE_PRODUCTION_COMPANIES.getValue());
        json.put("imageURLPrefix", getImageURLPrefix());
        json.put("companies", companiesJSON);
        Jsonable.setFileJSONObject(Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies", json);
        return json;
    }
    private static String getImageURLPrefix() {
        return "https://upload.wikimedia.org/wikipedia/";
    }
}
