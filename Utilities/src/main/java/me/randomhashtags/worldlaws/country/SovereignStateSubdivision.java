package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
import me.randomhashtags.worldlaws.service.WikipediaService;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface SovereignStateSubdivision extends SovereignState, WikipediaService {
    String name();
    WLCountry getCountry();
    SubdivisionType getDefaultType();
    default SubdivisionType getType() {
        return null;
    }
    default String getBackendID() {
        return getName().toLowerCase().replace(" ", "");
    }
    default String getShortName() {
        return null;
    }
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
        return "https://en.wikipedia.org/wiki/" + getName().replace(" ", "_");
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
    default HashSet<SovereignStateResource> getCustomResources() {
        return null;
    }

    default void getInformation(APIVersion version, CompletionHandler handler) {
        final Folder folder = Folder.COUNTRIES_SUBDIVISIONS_INFORMATION;
        final String fileName = name();
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", getCountry().getBackendID()));
        getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values = new ConcurrentHashMap<>();

                final HashSet<String> neighbors = getNeighborsJSON();
                if(neighbors != null) {
                    values.put(SovereignStateInformationType.NEIGHBORS, neighbors);
                }

                final HashSet<SovereignStateResource> resources = new HashSet<>(), customResources = getCustomResources();
                final String governmentWebsite = getGovernmentWebsite();
                if(governmentWebsite != null) {
                    resources.add(new SovereignStateResource("Government Website", governmentWebsite));
                }
                if(customResources != null) {
                    resources.addAll(customResources);
                }

                final HashSet<String> set = new HashSet<>();
                final SovereignStateInformationType resourcesInformationType = SovereignStateInformationType.RESOURCES_STATIC;
                if(!resources.isEmpty()) {
                    for(SovereignStateResource resource : resources) {
                        set.add(resource.toString());
                    }
                    values.put(resourcesInformationType, set);
                }

                final HashSet<CountryService> services = new HashSet<>() {{
                    add(new WikipediaCountryService(false));
                }};

                final CompletionHandler serviceHandler = new CompletionHandler() {
                    @Override
                    public void handleServiceResponse(CountryService service, String string) {
                        if(string != null && !string.equals("null")) {
                            final SovereignStateInformationType type = service.getInformationType();
                            values.putIfAbsent(type, new HashSet<>());
                            values.get(type).add(string);
                        }
                    }
                };
                final String name = getName();
                final String backendID = name.toLowerCase().replace(" ", "");
                ParallelStream.stream(services, serviceObj -> {
                    final CountryService service = (CountryService) serviceObj;
                    final SovereignStateInfo info = service.getInfo();
                    final String territory;
                    switch (info) {
                        case SERVICE_WIKIPEDIA:
                            territory = name;
                            break;
                        default:
                            territory = backendID;
                            break;
                    }
                    if(territory != null) {
                        final HashSet<SovereignStateResource> nonStaticResources = service.getResources(territory);
                        if(nonStaticResources != null && !nonStaticResources.isEmpty()) {
                            values.putIfAbsent(resourcesInformationType, new HashSet<>());
                            for(SovereignStateResource resource : nonStaticResources) {
                                values.get(resourcesInformationType).add(resource.toString());
                            }
                        }

                        service.getCountryValue(territory, serviceHandler);
                    }
                });

                final SovereignStateInformation info = new SovereignStateInformation(values);
                handler.handleString(info.toString());
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                final String string = json != null ? json.toString() : null;
                handler.handleString(string);
            }
        });
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

    default JSONObject toJSONObject() {
        final String flagURL = getFlagURL();
        final WLTimeZone[] timezones = getTimeZones();
        final SubdivisionType type = getType();

        final JSONObject json = new JSONObject();
        if(type != null) {
            json.put("type_name_singular", type.getSingularName());
            json.put("type_name_plural", type.getPluralName());
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
