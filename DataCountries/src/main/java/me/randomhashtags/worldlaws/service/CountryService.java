package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public interface CountryService extends SovereignStateService {
    ConcurrentHashMap<SovereignStateInfo, JSONObject> COUNTRY_SERVICE_JSON_VALUES = new ConcurrentHashMap<>();
    default Folder getFolder() {
        return Folder.COUNTRIES_SERVICES;
    }
    SovereignStateInformationType getInformationType();
    SovereignStateInfo getInfo();

    default EventSources getResources(String countryBackendID) {
        return null;
    }
    default String getCountryValue(String countryBackendID) {
        return getCountryValueFromCountryJSONObject(countryBackendID);
    }
    default String getCountryValueFromCountryJSONObject(String countryBackendID) {
        final SovereignStateInfo info = getInfo();
        String string = null;
        if(COUNTRY_SERVICE_JSON_VALUES.containsKey(info)) {
            final JSONObject cachedJSON = COUNTRY_SERVICE_JSON_VALUES.get(info);
            final JSONObject json = cachedJSON.has(countryBackendID) ? cachedJSON.getJSONObject(countryBackendID) : null;
            if(json != null) {
                insertValuesIntoCountryValueJSONObject(json);
                string = json.toString();
            }
        } else {
            final String fileName = info.getTitle();
            final Object object = getJSONData(getFolder(), fileName, countryBackendID);
            final boolean exists = object != null;
            if(exists) {
                final JSONObject json = (JSONObject) object;
                COUNTRY_SERVICE_JSON_VALUES.put(info, json);
                final JSONObject targetJSON = json.has(countryBackendID) ? json.getJSONObject(countryBackendID) : null;
                if(targetJSON != null) {
                    insertValuesIntoCountryValueJSONObject(targetJSON);
                    string = targetJSON.toString();
                }
            } else {
                WLUtilities.saveLoggedError("CountryService", "failed to load JSONObject for CountryService " + info.name());
            }
        }
        return string;
    }
    default void insertValuesIntoCountryValueJSONObject(JSONObject json) {
    }
    default Object getJSONData(Folder folder, String fileName, String countryBackendID) {
        return getJSONObjectData(folder, fileName, countryBackendID);
    }
    default JSONObject getJSONObjectData(Folder folder, String fileName, String countryBackendID) {
        return getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                return loadData();
            }
        });
    }

    default HashSet<String> getCountriesFromText(String text) {
        final HashSet<String> countries = new HashSet<>();
        switch (text) {
            case "carribean":
                // https://en.wikipedia.org/wiki/Caribbean
                countries.addAll(Arrays.asList(
                        "antiguaandbarbuda",
                        "bahamas",
                        "barbados",
                        "cuba",
                        "dominica",
                        "dominicanrepublic",
                        "grenada",
                        "haiti",
                        "jamaica",
                        "saintkittsandnevis",
                        "saintlucia",
                        "saintvincentandthegrenadines",
                        "trinidadandtobago"
                ));
                break;
            case "easterneurope":
                // https://en.wikipedia.org/wiki/Eastern_Europe
                countries.addAll(Arrays.asList(
                        "bulgaria",
                        "croatia",
                        "cyprus",
                        "czechrepublic",
                        "estonia",
                        "hungary",
                        "latvia",
                        "lithuania",
                        "malta",
                        "poland",
                        "romania",
                        "slovakia",
                        "slovenia"
                ));
                break;
            default:
                countries.add(text);
                break;
        }
        return countries;
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
        notes = removeReferences(notes);
        return notes;
    }
}
