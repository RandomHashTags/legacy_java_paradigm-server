package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryService;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
import me.randomhashtags.worldlaws.service.WikipediaService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface SovereignStateSubdivision extends SovereignState, WikipediaService {
    HashMap<String, JSONObjectTranslatable> INFORMATION_CACHE = new HashMap<>();

    String name();
    WLCountry getCountry();
    SubdivisionType getDefaultType();
    default SubdivisionType getType() {
        return null;
    }
    @Override
    default String getBackendID() {
        return getName().toLowerCase().replace(" ", "");
    }
    @Override
    default String getShortName() {
        return null;
    }
    default String getConditionalName() {
        final String realName = getRealName();
        return realName != null ? realName : getName();
    }
    @Override
    default String getName() {
        return LocalServer.toCorrectCapitalization(name(), "del", "de", "la", "of", "al", "and");
    }
    default String getRealName() {
        return null;
    }
    default String getGovernmentWebsite() {
        return null;
    }
    default String getWikipediaURL() {
        final SubdivisionType type = getType();
        final String prefix = getWikipediaURLPrefix(), suffix = (type != null ? type : getDefaultType()).getSingularName();
        final String customSuffix = getWikipediaURLSuffix(suffix);
        final boolean hasSpace = !(customSuffix != null && customSuffix.isEmpty());
        return "https://en.wikipedia.org/wiki/" + (prefix != null ? prefix : "") + getConditionalName().replace(" ", "_") + (hasSpace ? "_" + (customSuffix != null ? customSuffix : suffix) : "");
    }
    default String getWikipediaURLPrefix() {
        return null;
    }
    default String getWikipediaURLSuffix(String suffix) {
        return null;
    }
    //String[] getMottos(); // TODO: implement (https://en.wikipedia.org/wiki/List_of_U.S._state_and_territory_mottos)
    default String[] collectMottos(String...mottos) {
        return mottos;
    }

    default SovereignStateSubdivision[] getNeighbors() {
        return null;
    }
    default SovereignStateSubdivision[] collectNeighbors(SovereignStateSubdivision...subdivisions) {
        return subdivisions;
    }
    default EventSources getCustomResources() {
        return null;
    }

    @Override
    default JSONObjectTranslatable getInformation(APIVersion version) {
        final String fileName = name();
        if(INFORMATION_CACHE.containsKey(fileName)) {
            return INFORMATION_CACHE.get(fileName);
        }
        final Folder folder = Folder.COUNTRIES_INFORMATION_SUBDIVISIONS;
        final WLCountry country = getCountry();
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", country.getBackendID()));
        final JSONObjectTranslatable json;
        final JSONObject local = Jsonable.getStaticLocalFileJSONObject(folder, fileName);
        if(local != null) {
            json = JSONObjectTranslatable.parse(local, folder, fileName, local.keySet(), key -> {
                final JSONObject keyJSON = local.getJSONObject(key);
                return JSONObjectTranslatable.copy(keyJSON);
            });
        } else {
            json = loadStaticInformation();
        }
        INFORMATION_CACHE.put(fileName, json);
        return json;
    }
    private JSONObjectTranslatable loadStaticInformation() {
        final ConcurrentHashMap<SovereignStateInformationType, HashMap<SovereignStateInfo, JSONObjectTranslatable>> values = new ConcurrentHashMap<>();

        final JSONObjectTranslatable neighbors = getNeighborsJSON();
        if(neighbors != null) {
            values.put(SovereignStateInformationType.NEIGHBORS, new HashMap<>());
            values.get(SovereignStateInformationType.NEIGHBORS).put(SovereignStateInfo.NEIGHBORS, neighbors);
        }

        final EventSources resources = new EventSources(), customResources = getCustomResources();
        final String governmentWebsite = getGovernmentWebsite();
        if(governmentWebsite != null) {
            resources.add(new EventSource("Government Website", governmentWebsite));
        }
        if(customResources != null) {
            resources.addAll(customResources);
        }

        final HashSet<NewCountryService> services = new HashSet<>() {{
            add(new WikipediaCountryService(false));
        }};

        final SovereignStateSubdivision subdivision = this;
        new CompletableFutures<NewCountryService>().stream(services, service -> {
            final SovereignStateInformationType type = service.getInformationType();
            final SovereignStateInfo info = service.getInfo();
            final EventSources sources = service.getResources(subdivision);
            if(sources != null) {
                resources.addAll(sources);
            }
            final JSONObjectTranslatable result = service.getJSONObject(subdivision);
            if(result != null && !result.isEmpty()) {
                values.putIfAbsent(type, new HashMap<>());
                values.get(type).put(info, result);
            }
        });

        if(!resources.isEmpty()) {
            values.put(SovereignStateInformationType.RESOURCES_STATIC, new HashMap<>());
            values.get(SovereignStateInformationType.RESOURCES_STATIC).put(SovereignStateInfo.RESOURCES, resources.toJSONObject());
        }

        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(Map.Entry<SovereignStateInformationType, HashMap<SovereignStateInfo, JSONObjectTranslatable>> map : values.entrySet()) {
            final SovereignStateInformationType informationType = map.getKey();
            final JSONObjectTranslatable informationTypeJSON = new JSONObjectTranslatable();
            for(Map.Entry<SovereignStateInfo, JSONObjectTranslatable> entry : map.getValue().entrySet()) {
                final SovereignStateInfo info = entry.getKey();
                final String id = info.getID();
                final JSONObjectTranslatable result = entry.getValue();
                informationTypeJSON.put(id, result);
                informationTypeJSON.addTranslatedKey(id);
            }
            informationTypeJSON.put(informationType.getName(), informationType);
            informationTypeJSON.addTranslatedKey(informationType.getName());
        }
        return json;
    }

    private JSONObjectTranslatable getNeighborsJSON() {
        final SovereignStateSubdivision[] neighbors = getNeighbors();
        if(neighbors != null) {
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            final HashMap<WLCountry, HashSet<SovereignStateSubdivision>> values = new HashMap<>();
            for(SovereignStateSubdivision subdivision : neighbors) {
                final WLCountry country = subdivision.getCountry();
                if(!values.containsKey(country)) {
                    values.put(country, new HashSet<>());
                }
                values.get(country).add(subdivision);
            }
            for(Map.Entry<WLCountry, HashSet<SovereignStateSubdivision>> map : values.entrySet()) {
                final String countryBackendID = map.getKey().getBackendID();
                final JSONArray neighboringSubdivisions = new JSONArray();
                for(SovereignStateSubdivision subdivision : map.getValue()) {
                    neighboringSubdivisions.put(subdivision.getBackendID());
                }
                json.put(countryBackendID, neighboringSubdivisions);
            }
            return json;
        }
        return null;
    }

    default JSONObjectTranslatable toJSONObject() {
        final String flagURL = getFlagURL(), isoAlpha2 = getISOAlpha2();
        final WLTimeZone[] timezones = getTimeZones();
        final SubdivisionType type = getType();

        final JSONObjectTranslatable json = new JSONObjectTranslatable("type_name_singular", "type_name_plural");
        if(type != null) {
            json.put("type_name_singular", type.getSingularName());
            json.put("type_name_plural", type.getPluralName());
        }
        if(isoAlpha2 != null) {
            json.put("isoAlpha2", isoAlpha2);
        }
        if(timezones != null) {
            json.put("timezones", getTimeZonesJSONArray(timezones));
        }
        if(flagURL != null) {
            json.put("flagURL", flagURL);
        }
        return json;
    }
}
