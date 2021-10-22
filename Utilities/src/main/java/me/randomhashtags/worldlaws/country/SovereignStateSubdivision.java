package me.randomhashtags.worldlaws.country;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.service.WikipediaCountryService;
import me.randomhashtags.worldlaws.service.WikipediaService;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface SovereignStateSubdivision extends SovereignState, WikipediaService {
    String name();
    WLCountry getCountry();
    default String getBackendID() {
        return getName().toLowerCase().replace(" ", "");
    }
    default String getShortName() {
        return null;
    }
    default String getName() {
        return LocalServer.toCorrectCapitalization(name());
    }
    String getPostalCodeAbbreviation();
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

                final HashSet<SovereignStateResource> resources = new HashSet<>();
                final String governmentWebsite = getGovernmentWebsite();
                if(governmentWebsite != null) {
                    resources.add(new SovereignStateResource("Government Website", governmentWebsite));
                }
                final HashSet<SovereignStateResource> customResources = getCustomResources();
                if(customResources != null) {
                    resources.addAll(customResources);
                }

                final HashSet<String> set = new HashSet<>();
                if(!resources.isEmpty()) {
                    for(SovereignStateResource resource : resources) {
                        set.add(resource.toString());
                    }
                    values.put(SovereignStateInformationType.RESOURCES, set);
                }

                final HashSet<CountryService> services = new HashSet<>() {{
                    add(new WikipediaCountryService(false));
                }};

                final AtomicInteger completed = new AtomicInteger(0);
                final int max = services.size();
                final CompletionHandler serviceHandler = new CompletionHandler() {
                    @Override
                    public void handleServiceResponse(CountryService service, String string) {
                        if(string != null && !string.equals("null")) {
                            final SovereignStateInformationType type = service.getInformationType();
                            values.putIfAbsent(type, new HashSet<>());
                            values.get(type).add(string);
                        }
                        tryCompletingInformation(max, completed, values, handler);
                    }
                };
                final String name = getName();
                final String backendID = name.toLowerCase().replace(" ", "");
                services.parallelStream().forEach(service -> {
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
                        service.getCountryValue(territory, serviceHandler);
                    }
                });
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                final String string = json != null ? json.toString() : null;
                handler.handleString(string);
            }
        });
    }
    private void tryCompletingInformation(int max, AtomicInteger completed, ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values, CompletionHandler handler) {
        if(completed.addAndGet(1) == max) {
            final SovereignStateInformation info = new SovereignStateInformation(values);
            handler.handleString(info.toString());
        }
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

    default String toJSON() {
        final String flagURL = getFlagURL();
        final WLTimeZone[] timezones = getTimeZones();
        return "\"" + getName() + "\":{" +
                (timezones != null ? "\"timezones\":" + getTimeZonesJSON(timezones) + (flagURL != null ? "," : "") : "") +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"" : "") +
                "}";
    }
}
