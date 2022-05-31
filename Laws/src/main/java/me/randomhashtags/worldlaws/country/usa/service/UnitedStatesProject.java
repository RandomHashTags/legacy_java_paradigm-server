package me.randomhashtags.worldlaws.country.usa.service;

import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.usa.CongressService;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public enum UnitedStatesProject implements RestAPI, CongressService {
    INSTANCE;

    private final ConcurrentHashMap<String, JSONObjectTranslatable> cache;
    private final ConcurrentHashMap<String, JSONObjectTranslatable> currentPoliticians, historicalPoliticians;
    private final ConcurrentHashMap<String, JSONObject> socialMedias;

    UnitedStatesProject() {
        cache = new ConcurrentHashMap<>();
        currentPoliticians = new ConcurrentHashMap<>();
        historicalPoliticians = new ConcurrentHashMap<>();
        socialMedias = new ConcurrentHashMap<>();
    }

    public JSONObjectTranslatable getPolitician(String id) {
        if(currentPoliticians.isEmpty()) {
            loadSocialMedias();
            new CompletableFutures<CongressType>().stream(Arrays.asList(CongressType.values()), type -> {
                loadPoliticians(type, type == CongressType.CURRENT ? currentPoliticians : historicalPoliticians);
            });
        }
        if(!cache.containsKey(id)) {
            final JSONObjectTranslatable json = currentPoliticians.getOrDefault(id, historicalPoliticians.getOrDefault(id, null));
            if(json == null) {
                WLLogger.logError(this, "politician doesn't exist with id \"" + id + "\"!");
            } else {
                cache.put(id, json);
            }
        }
        return cache.getOrDefault(id, null);
    }

    private void loadSocialMedias() {
        final long started = System.currentTimeMillis();
        final String url = "https://theunitedstates.io/congress-legislators/legislators-social-media.json";
        final JSONArray array = requestJSONArray(url);
        if(array != null) {
            final int amount = array.length();
            new CompletableFutures<JSONObject>().stream(array, json -> {
                final String id = json.getJSONObject("id").getString("bioguide");
                socialMedias.put(id, json.getJSONObject("social"));
            });
            WLLogger.logInfo("UnitedStatesProject - loaded " + amount + " politician social medias (took " + WLUtilities.getElapsedTime(started) + ")");
        }
    }
    private void loadPoliticians(CongressType type, ConcurrentHashMap<String, JSONObjectTranslatable> map) {
        final long started = System.currentTimeMillis();
        final String typeString = type.name();
        final String url = "https://theunitedstates.io/congress-legislators/legislators-" + typeString.toLowerCase() + ".json";
        final JSONArray array = requestJSONArray(url);
        int amount = 0;
        if(array != null) {
            amount = array.length();
            new CompletableFutures<JSONObject>().stream(array, json -> {
                final UnitedStatesProjectPolitician politician = new UnitedStatesProjectPolitician(json);
                final HumanName name = politician.getName();
                final String id = name.getFirstName() + name.getMiddleName() + name.getLastName();
                map.put(id, politician.toJSONObject());
            });
        } else {
            WLLogger.logError(this, "loadPoliticians - array == null!");
        }
        WLLogger.logInfo("UnitedStatesProject - loaded " + amount + " " + typeString + " politicians (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private enum CongressType {
        CURRENT,
        HISTORICAL
    }
}
