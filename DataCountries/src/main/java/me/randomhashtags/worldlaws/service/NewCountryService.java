package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface NewCountryService extends WLService {
    HashMap<SovereignStateInfo, JSONObjectTranslatable> DATA_CACHE = new HashMap<>();
    HashMap<SovereignStateInfo, HashMap<String, JSONObjectTranslatable>> COUNTRY_CACHE = new HashMap<>();

    Folder getFolder();
    default String getServiceFileName(WLCountry country) {
        return getInfo().getTitle();
    }
    SovereignStateInformationType getInformationType();
    SovereignStateInfo getInfo();

    default EventSources getResources(WLCountry country) {
        return null;
    }

    default JSONObjectTranslatable getJSONObject(WLCountry country) {
        return getJSONObject(country, false);
    }
    default JSONObjectTranslatable getJSONObject(WLCountry country, boolean isDataJSON) {
        final String countryBackendID = country.getBackendID();
        final SovereignStateInfo info = getInfo();
        if(COUNTRY_CACHE.containsKey(info) && COUNTRY_CACHE.get(info).containsKey(countryBackendID)) {
            return COUNTRY_CACHE.get(info).get(countryBackendID);
        } else {
            final JSONObjectTranslatable data = getData(info, country);
            if(data != null) {
                final JSONObjectTranslatable countryJSON = isDataJSON ? data : data.has(countryBackendID) ? data.getJSONObjectTranslatable(countryBackendID) : null;
                if(countryJSON != null) {
                    COUNTRY_CACHE.putIfAbsent(info, new HashMap<>());
                    insertCountryData(data, countryJSON);
                    insertCountryData(countryJSON, country);
                    COUNTRY_CACHE.get(info).put(countryBackendID, countryJSON);
                }
                return countryJSON;
            }
            return null;
        }
    }
    default void insertCountryData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable countryJSON) {
    }
    default void insertCountryData(JSONObjectTranslatable countryJSON, WLCountry country) {
    }

    private JSONObjectTranslatable getData(SovereignStateInfo info, WLCountry country) {
        if(!DATA_CACHE.containsKey(info)) {
            final Folder folder = getFolder();
            final String fileName = getServiceFileName(country), folderName = folder.getFolderName();
            folder.setCustomFolderName(fileName, folderName);
            final JSONObjectTranslatable json = tryLoadingData(folder, fileName, country);
            if(json != null) {
                folder.setCustomFolderName(fileName, folderName);
                json.setFolder(folder);
                json.setFileName(fileName);
            }
            DATA_CACHE.put(info, json);
        }
        return DATA_CACHE.get(info);
    }

    private JSONObjectTranslatable tryLoadingData(Folder folder, String fileName, WLCountry country) {
        final JSONObject local = getLocalFileJSONObject(folder, fileName);
        JSONObjectTranslatable json;
        if(local == null) {
            json = loadData();
            if(json == null) {
                json = loadData(country);
            }
            if(json != null) {
                setFileJSON(folder, fileName, json.toString());
            }
        } else {
            json = parseData(local);
        }
        return json;
    }

    JSONObjectTranslatable loadData();
    default JSONObjectTranslatable loadData(WLCountry country) {
        return null;
    }
    JSONObjectTranslatable parseData(JSONObject json);

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
