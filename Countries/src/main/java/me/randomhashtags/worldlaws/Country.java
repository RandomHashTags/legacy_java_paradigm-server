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
            final JSONObjectTranslatable json = new JSONObjectTranslatable("name", "shortName", "officialNames", "aliases", "unStatus", "sovereigntyDispute");

            final WLCountry wlcountry = getWLCountry();

            final String shortName = getShortName(), name = getName();
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

            if(officialNames != null) {
                json.put("officialNames", new JSONArray(Arrays.asList(officialNames)));
            }
            if(aliases != null) {
                aliases.removeIf(alias -> alias.equalsIgnoreCase(shortName) || alias.equalsIgnoreCase(name));
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
            if(!name.equals(shortName)) {
                json.put("shortName", shortName);
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
            json = new JSONObjectTranslatable();
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
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            final JSONObjectTranslatable staticInfo = getStaticInformation(), nonStaticInfo = getNonStaticInformation();
            for(String key : staticInfo.keySet()) {
                json.put(key, staticInfo.get(key));
                json.addTranslatedKey(key);
            }
            for(String key : nonStaticInfo.keySet()) {
                json.put(key, nonStaticInfo.get(key));
                json.addTranslatedKey(key);
            }
            INFORMATION_CACHE.put(backendID, json);
        }
        return INFORMATION_CACHE.get(backendID);
    }
    private JSONObjectTranslatable getStaticInformation() {
        final String backendID = getBackendID();
        if(!STATIC_INFORMATION_CACHE.containsKey(backendID)) {
            final int version = ResponseVersions.COUNTRIES.getValue();
            final Folder folder = Folder.COUNTRIES_INFORMATION;
            folder.setCustomFolderName(backendID, folder.getFolderName());
            final JSONObject local = getLocalFileJSONObject(folder, backendID);
            final JSONObjectTranslatable json;
            if(local == null || local.getInt("response_version") != version) {
                final WLCountry country = getWLCountry();
                json = loadStaticInformation(folder, country);
            } else {
                json = new JSONObjectTranslatable();
                for(String key : local.keySet()) {
                    final JSONObjectTranslatable innerJSONTranslatable = new JSONObjectTranslatable();
                    final JSONObject innerJSON = local.getJSONObject(key);
                    for(String innerKey : innerJSON.keySet()) {
                        innerJSONTranslatable.put(innerKey, innerJSON.get(innerKey));
                        innerJSONTranslatable.addTranslatedKey(innerKey);
                    }
                    json.put(key, innerJSON);
                    json.addTranslatedKey(key);
                }
            }
            STATIC_INFORMATION_CACHE.put(backendID, json);
        }
        return STATIC_INFORMATION_CACHE.get(backendID);
    }
    private JSONObjectTranslatable loadStaticInformation(Folder folder, WLCountry country) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.setFolder(folder);
        json.setFileName(country.getBackendID());

        final HashSet<NewCountryService> services = CountryServices.STATIC_SERVICES;
        final HashSet<EventSources> resources = new HashSet<>();
        final ConcurrentHashMap<SovereignStateInformationType, HashMap<SovereignStateInfo, JSONObjectTranslatable>> values = new ConcurrentHashMap<>();
        final WLCountry[] neighbors = country.getNeighbors();

        new CompletableFutures<NewCountryService>().stream(services, service -> {
            final JSONObjectTranslatable result = service.getJSONObject(country);
            if(result != null && !result.isEmpty()) {
                final SovereignStateInformationType type = service.getInformationType();
                final SovereignStateInfo info = service.getInfo();
                values.putIfAbsent(type, new HashMap<>());
                values.get(type).put(info, result);
            }
            final EventSources serviceResources = service.getResources(country);
            if(serviceResources != null) {
                resources.add(serviceResources);
            }
        });

        final SovereignStateInformationType resourcesInformationType = SovereignStateInformationType.RESOURCES_STATIC;
        final SovereignStateInfo resourcesInfo = SovereignStateInfo.RESOURCES;
        if(!resources.isEmpty()) {
            values.putIfAbsent(resourcesInformationType, new HashMap<>());
            values.get(resourcesInformationType).putIfAbsent(resourcesInfo, new JSONObjectTranslatable());
            final JSONObjectTranslatable resourcesJSON = values.get(resourcesInformationType).get(resourcesInfo);
            for(EventSources sources : resources) {
                final JSONObjectTranslatable sourcesJSON = sources.toJSONObject();
                for(String key : sourcesJSON.keySet()) {
                    resourcesJSON.put(key, sourcesJSON.get(key));
                }
            }
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
        json.put("response_version", ResponseVersions.COUNTRIES.getValue());
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
        final HashSet<NewCountryServiceNonStatic> nonStaticServices = CountryServices.NONSTATIC_SERVICES;
        new CompletableFutures<NewCountryServiceNonStatic>().stream(nonStaticServices, service -> {
            final JSONObjectTranslatable serviceJSON = service.getJSONObject(wlcountry);
            if(serviceJSON != null) {
                final SovereignStateInfo info = service.getInfo();
                final String title = info.getTitle();
                json.put(title, serviceJSON);
                json.addTranslatedKey(title);
            }
        });
        NONSTATIC_INFORMATION_CACHE.put(backendID, json);
        INFORMATION_CACHE.remove(backendID);
        return json;
    }
}
