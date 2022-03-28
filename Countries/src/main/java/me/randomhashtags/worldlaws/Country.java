package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.*;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.CountryServices;
import me.randomhashtags.worldlaws.service.NewCountryService;
import me.randomhashtags.worldlaws.service.NewCountryServiceNonStatic;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public interface Country extends SovereignState {
    ConcurrentHashMap<String, JSONObjectTranslatable> CACHE = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, JSONObjectTranslatable> INFORMATION_CACHE = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, JSONObjectTranslatable> STATIC_INFORMATION_CACHE = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, JSONObjectTranslatable> NONSTATIC_INFORMATION_CACHE = new ConcurrentHashMap<>();

    WLCountry getWLCountry();
    @Override
    default String getName() {
        return getShortName();
    }
    @Override
    default String getShortName() {
        final WLCountry country = getWLCountry();
        return country != null ? country.getShortName() : null;
    }
    @Override
    default String getISOAlpha2() {
        final WLCountry country = getWLCountry();
        return country != null ? country.getISOAlpha2Official() : null;
    }

    default JSONObjectTranslatable getJSONObject() {
        final String backendID = getBackendID();
        if(!CACHE.containsKey(backendID)) {
            final JSONObjectTranslatable json = new JSONObjectTranslatable("shortName", "officialNames", "aliases", "unStatus", "sovereigntyDispute");

            final WLCountry wlcountry = getWLCountry();

            final String shortName = getShortName();
            final HashSet<Integer> governmentAdministrations = LawUtilities.getAdministrationVersions(wlcountry);
            final boolean hasGovernmentAdministrations = governmentAdministrations != null;
            HashSet<String> aliases = null;
            WLTimeZone[] timezones = null;
            String[] officialNames = null;
            String unStatus = null, sovereigntyDispute = null, flagEmoji = null, isoAlpha2 = null, isoAlpha3 = null;
            int currentGovernmentAdministration = -1;
            SovereignStateSubdivision[] subdivisions = null;
            if(wlcountry != null) {
                officialNames = wlcountry.getOfficialNames();
                aliases = wlcountry.getAliases();
                timezones = wlcountry.getTimeZones();
                flagEmoji = wlcountry.getFlagEmoji();
                unStatus = wlcountry.getUNStatus();
                sovereigntyDispute = wlcountry.getSovereigntyDispute();
                isoAlpha2 = wlcountry.getISOAlpha2Official();
                isoAlpha3 = wlcountry.getISOAlpha3();
                currentGovernmentAdministration = LawUtilities.getCurrentAdministrationVersion(wlcountry);
                subdivisions = wlcountry.getSubdivisions();
            }

            json.put("shortName", shortName);
            if(officialNames != null) {
                json.put("officialNames", new JSONArray(Arrays.asList(officialNames)));
            }
            if(aliases != null) {
                aliases.removeIf(alias -> alias.equalsIgnoreCase(shortName));
                if(!aliases.isEmpty()) {
                    final JSONArray array = new JSONArray(aliases);
                    json.put("aliases", array);
                }
            }
            if(isoAlpha2 != null) {
                json.put("isoAlpha2", isoAlpha2);
            }
            if(isoAlpha3 != null) {
                json.put("isoAlpha3", isoAlpha3);
            }
            if(unStatus != null) {
                json.put("unStatus", unStatus);
            }
            if(sovereigntyDispute != null) {
                json.put("sovereigntyDispute", sovereigntyDispute);
            }
            if(hasGovernmentAdministrations) {
                json.put("currentGovernmentAdministration", currentGovernmentAdministration);
                json.put("governmentAdministrations", new JSONArray(governmentAdministrations));
            }
            if(timezones != null) {
                json.put("timezones", getTimeZonesJSONArray(timezones));
            }
            if(subdivisions != null) {
                final JSONObjectTranslatable subdivisionsJSON = getSubdivisionsJSON(wlcountry, subdivisions);
                json.put("subdivisions", subdivisionsJSON);
            }
            if(flagEmoji != null) {
                json.put("flagEmoji", flagEmoji);
            }
            CACHE.put(backendID, json);
        }
        return CACHE.get(backendID);
    }
    private JSONObjectTranslatable getSubdivisionsJSON(WLCountry wlcountry, SovereignStateSubdivision[] subdivisions) {
        JSONObjectTranslatable json = null;
        if(subdivisions != null) {
            json = new JSONObjectTranslatable("default_type_name_plural", "default_type_name_singular");
            final SubdivisionType defaultType = subdivisions[0].getDefaultType();
            json.put("default_type_name_plural", defaultType.getPluralName());
            json.put("default_type_name_singular", defaultType.getSingularName());
            if(LawUtilities.hasSubdivisionGovernmentsSupported(wlcountry)) {
                json.put("default_supports_government", true);
            }
            final JSONObjectTranslatable subdivisionsJSON = new JSONObjectTranslatable();
            for(SovereignStateSubdivision subdivision : subdivisions) {
                String name = subdivision.getRealName();
                if(name == null) {
                    name = subdivision.getName();
                }
                subdivisionsJSON.put(name, subdivision.toJSONObject());
            }
            json.put("subdivisions", subdivisionsJSON);
        }
        return json;
    }

    @Override
    default JSONObjectTranslatable getInformation(APIVersion version) {
        final String backendID = getBackendID();
        if(!INFORMATION_CACHE.containsKey(backendID)) {
            final JSONObjectTranslatable json = getStaticInformation(), nonStaticInfo = getNonStaticInformation();
            if(json != null) {
                if(nonStaticInfo != null && !nonStaticInfo.isEmpty()) {
                    final String key = SovereignStateInformationType.SERVICES_NONSTATIC.getName();
                    json.put(key, nonStaticInfo);
                    json.addTranslatedKey(key);
                }
                INFORMATION_CACHE.put(backendID, json);
            }
        }
        return INFORMATION_CACHE.get(backendID);
    }
    private JSONObjectTranslatable getStaticInformation() {
        final long started = System.currentTimeMillis();
        final String backendID = getBackendID();
        if(!STATIC_INFORMATION_CACHE.containsKey(backendID)) {
            final Folder folder = Folder.COUNTRIES_INFORMATION;
            folder.setCustomFolderName(backendID, folder.getFolderName());
            final JSONObject local = getLocalFileJSONObject(folder, backendID);
            final JSONObjectTranslatable json;
            if(local == null || local.getInt("response_version") != ResponseVersions.COUNTRY_INFORMATION.getValue()) {
                final WLCountry country = getWLCountry();
                json = loadStaticInformation(folder, country);
            } else {
                json = new JSONObjectTranslatable();
                for(String key : local.keySet()) {
                    final Object obj = local.get(key);
                    if(obj instanceof JSONObject) {
                        final JSONObject innerJSON = local.getJSONObject(key);
                        final JSONObjectTranslatable innerJSONTranslatable = JSONObjectTranslatable.copy(innerJSON, true);
                        json.put(key, innerJSONTranslatable);
                        json.addTranslatedKey(key);
                    } else {
                        json.put(key, obj);
                    }
                }
            }
            STATIC_INFORMATION_CACHE.put(backendID, json);
            WLLogger.logInfo("Country - loaded static information for country \"" + backendID + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return STATIC_INFORMATION_CACHE.get(backendID);
    }
    private JSONObjectTranslatable loadStaticInformation(Folder folder, WLCountry country) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.setFolder(folder);
        json.setFileName(country.getBackendID());

        final HashSet<NewCountryService> services = CountryServices.STATIC_SERVICES;
        final EventSources resources = new EventSources();
        final String governmentWebsite = country.getGovernmentWebsite();
        if(governmentWebsite != null) {
            resources.add(new EventSource("Government Website", governmentWebsite));
        }

        final ConcurrentHashMap<SovereignStateInformationType, HashMap<SovereignStateInfo, JSONObjectTranslatable>> values = new ConcurrentHashMap<>();
        final WLCountry[] neighbors = country.getNeighbors();
        if(neighbors != null) {
            final JSONArray neighborsArray = new JSONArray();
            for(WLCountry neighborCountry : neighbors) {
                neighborsArray.put(neighborCountry.getBackendID());
            }
            json.put(SovereignStateInformationType.NEIGHBORS.getName(), neighborsArray);
        }

        final AtomicReference<JSONObjectTranslatable> availabilitiesResult = new AtomicReference<>();
        new CompletableFutures<NewCountryService>().stream(services, service -> {
            final JSONObjectTranslatable result = service.getJSONObject(country);
            if(result != null && !result.isEmpty()) {
                final SovereignStateInformationType type = service.getInformationType();
                final SovereignStateInfo info = service.getInfo();
                if(type == SovereignStateInformationType.AVAILABILITIES) {
                    availabilitiesResult.set(result);
                } else {
                    values.putIfAbsent(type, new HashMap<>());
                    values.get(type).put(info, result);
                }
            }
            final EventSources serviceResources = service.getResources(country);
            if(serviceResources != null) {
                resources.addAll(serviceResources);
            }
        });

        final JSONObjectTranslatable availabilities = availabilitiesResult.get();
        if(availabilities != null) {
            json.put(SovereignStateInformationType.AVAILABILITIES.getName(), availabilities);
        }
        if(!resources.isEmpty()) {
            json.put(SovereignStateInformationType.RESOURCES_STATIC.getName(), resources.toJSONObject());
        }

        for(Map.Entry<SovereignStateInformationType, HashMap<SovereignStateInfo, JSONObjectTranslatable>> map : values.entrySet()) {
            final SovereignStateInformationType type = map.getKey();
            final String typeName = type.name();
            final JSONObjectTranslatable infoValues = new JSONObjectTranslatable();
            final HashMap<SovereignStateInfo, JSONObjectTranslatable> hashmap = map.getValue();
            for(Map.Entry<SovereignStateInfo, JSONObjectTranslatable> bruh : hashmap.entrySet()) {
                final SovereignStateInfo info = bruh.getKey();
                final String infoName = info.getTitle();
                final JSONObjectTranslatable value = bruh.getValue();
                infoValues.put(infoName, value);
                infoValues.addTranslatedKey(infoName);
            }
            json.put(typeName, infoValues);
            json.addTranslatedKey(typeName);
        }
        json.put("response_version", ResponseVersions.COUNTRY_INFORMATION.getValue());
        json.save();
        return json;
    }
    default JSONObjectTranslatable getNonStaticInformation() {
        final String backendID = getBackendID();
        if(!NONSTATIC_INFORMATION_CACHE.containsKey(backendID)) {
            final JSONObjectTranslatable json = updateNonStaticInformation();
            NONSTATIC_INFORMATION_CACHE.put(backendID, json);
        }
        return NONSTATIC_INFORMATION_CACHE.get(backendID);
    }
    default JSONObjectTranslatable updateNonStaticInformation() {
        final WLCountry wlcountry = getWLCountry();
        final String backendID = getBackendID();
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        final EventSources resources = new EventSources();
        final HashSet<NewCountryServiceNonStatic> nonStaticServices = CountryServices.NONSTATIC_SERVICES;
        new CompletableFutures<NewCountryServiceNonStatic>().stream(nonStaticServices, service -> {
            final EventSources serviceResources = service.getResources(wlcountry);
            if(serviceResources != null) {
                resources.addAll(serviceResources);
            }
            final JSONObjectTranslatable serviceJSON = service.getJSONObject(wlcountry);
            if(serviceJSON != null) {
                final SovereignStateInfo info = service.getInfo();
                final String title = info.getTitle();
                json.put(title, serviceJSON);
                json.addTranslatedKey(title);
            }
        });
        if(!resources.isEmpty()) {
            json.put(SovereignStateInformationType.RESOURCES_NONSTATIC.getName(), resources.toJSONObject());
        }
        NONSTATIC_INFORMATION_CACHE.put(backendID, json);
        INFORMATION_CACHE.remove(backendID);
        return json;
    }
}
