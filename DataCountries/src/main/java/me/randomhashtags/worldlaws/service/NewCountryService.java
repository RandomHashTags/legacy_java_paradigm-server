package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public interface NewCountryService extends WLService {
    HashMap<SovereignStateInfo, HashMap<String, JSONObjectTranslatable>> DATA_CACHE = new HashMap<>();
    ConcurrentHashMap<SovereignStateInfo, HashMap<String, JSONObjectTranslatable>> COUNTRY_CACHE = new ConcurrentHashMap<>(), SUBDIVISION_CACHE = new ConcurrentHashMap<>();

    Folder getFolder();
    default String getServiceFileName(WLCountry country) {
        return getInfo().getTitle();
    }
    default String getServiceFileName(SovereignStateSubdivision subdivision) {
        return getInfo().getTitle();
    }
    SovereignStateInformationType getInformationType();
    SovereignStateInfo getInfo();

    default boolean doesSaveLoadedData() {
        return true;
    }
    default EventSources getResources(WLCountry country) {
        return null;
    }
    default JSONObjectTranslatable getJSONObject(WLCountry country) {
        return getJSONObject(country, false);
    }
    default JSONObjectTranslatable getJSONObject(WLCountry country, boolean isDataJSON) {
        return getJSONObject(true, isDataJSON, country, null);
    }
    default void insertCountryData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable countryJSON) {
    }
    default void insertCountryData(JSONObjectTranslatable countryJSON, WLCountry country) {
    }
    default JSONObjectTranslatable getData(SovereignStateInfo info, WLCountry country) {
        return getData(info, true, country, null);
    }
    private JSONObjectTranslatable tryLoadingData(Folder folder, String fileName, WLCountry country, SovereignStateSubdivision subdivision) {
        final JSONObject local = getLocalFileJSONObject(folder, fileName);
        JSONObjectTranslatable json;
        if(local == null) {
            json = loadData();
            if(json == null) {
                if(country != null) {
                    json = loadData(country);
                } else if(subdivision != null) {
                    json = loadData(subdivision);
                }
            }
            if(json != null && doesSaveLoadedData()) {
                setFileJSON(folder, fileName, json.toString());
            }
        } else {
            json = parseData(local);
        }
        return json;
    }

    default EventSources getResources(SovereignStateSubdivision subdivision) {
        return null;
    }
    default JSONObjectTranslatable getJSONObject(SovereignStateSubdivision subdivision) {
        return getJSONObject(subdivision, false);
    }
    default JSONObjectTranslatable getJSONObject(SovereignStateSubdivision subdivision, boolean isDataJSON) {
        return getJSONObject(false, isDataJSON, null, subdivision);
    }
    default void insertSubdivisionData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable subdivisionJSON) {
    }
    default void insertSubdivisionData(JSONObjectTranslatable subdivisionJSON, SovereignStateSubdivision subdivision) {
    }
    default JSONObjectTranslatable getData(SovereignStateInfo info, SovereignStateSubdivision subdivision) {
        return getData(info, false, null, subdivision);
    }

    private JSONObjectTranslatable getJSONObject(boolean isCountry, boolean isDataJSON, WLCountry country, SovereignStateSubdivision subdivision) {
        final String backendID = country != null ? country.getBackendID() : subdivision.getBackendID();
        final SovereignStateInfo info = getInfo();
        final ConcurrentHashMap<SovereignStateInfo, HashMap<String, JSONObjectTranslatable>> cache = isCountry ? COUNTRY_CACHE : SUBDIVISION_CACHE;
        if(cache.containsKey(info) && cache.get(info).containsKey(backendID)) {
            return cache.get(info).get(backendID);
        } else {
            final JSONObjectTranslatable data = isCountry ? getData(info, country) : getData(info, subdivision);
            if(data != null) {
                final JSONObjectTranslatable sovereignStateJSON = isDataJSON ? data : data.has(backendID) ? data.getJSONObjectTranslatable(backendID) : null;
                if(sovereignStateJSON != null) {
                    cache.putIfAbsent(info, new HashMap<>());
                    if(isCountry) {
                        insertCountryData(data, sovereignStateJSON);
                        insertCountryData(sovereignStateJSON, country);
                    } else {
                        insertSubdivisionData(data, sovereignStateJSON);
                        insertSubdivisionData(sovereignStateJSON, subdivision);
                    }
                    cache.get(info).put(backendID, sovereignStateJSON);
                }
                return sovereignStateJSON;
            }
            return null;
        }
    }
    private JSONObjectTranslatable getData(SovereignStateInfo info, boolean isCountry, WLCountry country, SovereignStateSubdivision subdivision) {
        DATA_CACHE.putIfAbsent(info, new HashMap<>());
        if(dataContainsAllCountryData()) {
            country = null;
            subdivision = null;
        }
        final String identifier = country != null ? country.getBackendID() : subdivision != null ? subdivision.getBackendID() : "null";
        if(!DATA_CACHE.get(info).containsKey(identifier)) {
            final Folder folder = getFolder();
            final String fileName = isCountry ? getServiceFileName(country) : getServiceFileName(subdivision);
            String folderName = folder.getFolderName();
            if(subdivision != null) {
                folderName = folderName.replace("%country%", subdivision.getCountry().getBackendID());
            }
            folder.setCustomFolderName(fileName, folderName);
            final JSONObjectTranslatable json = tryLoadingData(folder, fileName, country, subdivision);
            if(json != null) {
                folder.setCustomFolderName(fileName, folderName);
                json.setFolder(folder);
                json.setFileName(fileName);
            }
            DATA_CACHE.get(info).put(identifier, json);
        }
        return DATA_CACHE.get(info).get(identifier);
    }

    JSONObjectTranslatable loadData();
    default JSONObjectTranslatable loadData(WLCountry country) {
        return null;
    }
    default JSONObjectTranslatable loadData(SovereignStateSubdivision subdivision) {
        return null;
    }
    JSONObjectTranslatable parseData(JSONObject json);
    default boolean dataContainsAllCountryData() {
        return false;
    }

    default String getNotesFromElement(Element noteElement) {
        String notes = null;
        final Elements children = noteElement.children();
        final String text = noteElement.text(), firstText = children.size() > 0 ? children.get(0).text() : "";
        if(firstText.startsWith("Main article") || firstText.startsWith("See also")) {
            notes = text.equals(firstText) ? "" : text.substring(firstText.length()+1);
        } else {
            notes = text;
        }
        notes = LocalServer.removeWikipediaReferences(notes);
        return notes;
    }
}
