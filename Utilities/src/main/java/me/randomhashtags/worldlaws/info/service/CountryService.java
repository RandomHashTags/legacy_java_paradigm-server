package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.SovereignStateService;
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
    default void getCountryValue(String countryBackendID, CompletionHandler handler) {
        getCountryValueFromCountryJSONObject(countryBackendID, handler);
    }
    default void getCountryValueFromCountryJSONObject(String countryBackendID, CompletionHandler handler) {
        final SovereignStateInfo info = getInfo();
        final CountryService self = this;
        if(COUNTRY_SERVICE_JSON_VALUES.containsKey(info)) {
            final JSONObject cachedJSON = COUNTRY_SERVICE_JSON_VALUES.get(info);
            final JSONObject json = cachedJSON.has(countryBackendID) ? cachedJSON.getJSONObject(countryBackendID) : null;
            String string = null;
            if(json != null) {
                insertValuesIntoCountryValueJSONObject(json);
                string = json.toString();
            }
            handler.handleServiceResponse(self, string);
        } else {
            final long started = System.currentTimeMillis();
            final String fileName = info.getTitle();
            getJSONData(getFolder(), fileName, countryBackendID, new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    COUNTRY_SERVICE_JSON_VALUES.put(info, json);
                    final JSONObject targetJSON = json.has(countryBackendID) ? json.getJSONObject(countryBackendID) : null;
                    String string = null;
                    if(targetJSON != null) {
                        insertValuesIntoCountryValueJSONObject(targetJSON);
                        string = targetJSON.toString();
                    }
                    WLLogger.logInfo(fileName + " - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleServiceResponse(self, string);
                }
            });
        }
    }
    default void insertValuesIntoCountryValueJSONObject(JSONObject json) {
    }
    default void getJSONData(Folder folder, String fileName, String countryBackendID, CompletionHandler handler) {
        getJSONObjectData(folder, fileName, countryBackendID, handler);
    }
    default void getJSONObjectData(Folder folder, String fileName, String countryBackendID, CompletionHandler handler) {
        getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String data = loadData();
                handler.handleString(data);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
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
