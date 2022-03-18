package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.CountryService;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
import me.randomhashtags.worldlaws.service.WikipediaService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
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
        final Folder folder = Folder.COUNTRIES_SUBDIVISIONS_INFORMATION;
        final WLCountry country = getCountry();
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", country.getBackendID()));
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        final JSONObject local = getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                final ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values = new ConcurrentHashMap<>();

                final HashSet<String> neighbors = getNeighborsJSON();
                if(neighbors != null) {
                    values.put(SovereignStateInformationType.NEIGHBORS, neighbors);
                }

                final EventSources resources = new EventSources(), customResources = getCustomResources();
                final String governmentWebsite = getGovernmentWebsite();
                if(governmentWebsite != null) {
                    resources.add(new EventSource("Government Website", governmentWebsite));
                }
                if(customResources != null) {
                    resources.addAll(customResources);
                }

                final HashSet<String> set = new HashSet<>();
                final SovereignStateInformationType resourcesInformationType = SovereignStateInformationType.RESOURCES_STATIC;
                if(!resources.isEmpty()) {
                    for(EventSource resource : resources) {
                        set.add(resource.toString());
                    }
                    values.put(resourcesInformationType, set);
                }

                final HashSet<CountryService> services = new HashSet<>() {{
                    add(new WikipediaCountryService(false));
                }};

                final String name = getName();
                final String backendID = name.toLowerCase().replace(" ", "");
                new CompletableFutures<CountryService>().stream(services, service -> {
                    final SovereignStateInfo info = service.getInfo();
                    final String territory;
                    String string = null;
                    switch (info) {
                        case SERVICE_WIKIPEDIA:
                            territory = name;
                            break;
                        default:
                            territory = backendID;
                            break;
                    }
                    if(territory != null) {
                        final EventSources nonStaticResources = service.getResources(territory);
                        if(nonStaticResources != null && !nonStaticResources.isEmpty()) {
                            values.putIfAbsent(resourcesInformationType, new HashSet<>());
                            for(EventSource resource : nonStaticResources) {
                                values.get(resourcesInformationType).add(resource.toString());
                            }
                        }
                        string = service.getCountryValue(territory);
                    }
                    if(string != null && !string.equals("null")) {
                        final SovereignStateInformationType type = service.getInformationType();
                        values.putIfAbsent(type, new HashSet<>());
                        values.get(type).add(string);
                    }
                });

                final SovereignStateInformation info = new SovereignStateInformation(values);
                return info.toString(false);
            }
        });
        for(String key : local.keySet()) {
            json.put(key, local.get(key));
            json.addTranslatedKey(key);
        }

        INFORMATION_CACHE.put(fileName, json);
        return json;
    }

    private HashSet<String> getNeighborsJSON() {
        final SovereignStateSubdivision[] neighbors = getNeighbors();
        if(neighbors != null) {
            final HashMap<String, HashSet<String>> values = new HashMap<>();
            for(SovereignStateSubdivision subdivision : neighbors) {
                final String countryBackendID = subdivision.getCountry().getBackendID();
                if(!values.containsKey(countryBackendID)) {
                    values.put(countryBackendID, new HashSet<>());
                }
                values.get(countryBackendID).add("\"" + subdivision.getName() + "\"");
            }
            final HashSet<String> set = new HashSet<>();
            for(Map.Entry<String, HashSet<String>> map : values.entrySet()) {
                final String countryBackendID = map.getKey();
                final StringBuilder builder = new StringBuilder("\"" + countryBackendID + "\":[");
                boolean isFirst = true;
                for(String subdivision : map.getValue()) {
                    builder.append(isFirst ? "" : ",").append(subdivision);
                    isFirst = false;
                }
                builder.append("]");
                set.add(builder.toString());
            }
            return set;
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
