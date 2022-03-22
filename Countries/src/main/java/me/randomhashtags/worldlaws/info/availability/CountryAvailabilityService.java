package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.service.NewCountryService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public interface CountryAvailabilityService extends NewCountryService {
    HashMap<SovereignStateInfo, JSONArray> AVAILABILITY_VALUES = new HashMap<>();

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_AVAILABILITIES;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.AVAILABILITIES;
    }

    default Elements getAvailabilityDocumentElements(String url, String targetElements) {
        return getAvailabilityDocumentElements(url, targetElements, -1);
    }
    default Elements getAvailabilityDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(getFolder(), url, targetElements, index);
    }

    default CountryAvailability getAvailability(boolean value) {
        return new CountryAvailability(getInfo().getTitle(), getPrimaryCategory(), getImageURL(), value);
    }
    default JSONArray loadOnlyTrue(String...countries) {
        final JSONArray array = new JSONArray();
        for(String country : countries) {
            array.put(country);
        }
        return array;
    }

    JSONArray loadDataArray();

    AvailabilityCategory getPrimaryCategory();
    String getImageURL();

    default CountryAvailability getAvailability(JSONObject json, String countryBackendID) {
        final SovereignStateInfo info = getInfo();
        final boolean isTrue;
        if(AVAILABILITY_VALUES.containsKey(info)) {
            isTrue = isTrue(countryBackendID, AVAILABILITY_VALUES.get(info));
        } else if(json.has(info.getTitle())) {
            final JSONArray array = json.getJSONArray(info.getTitle());
            AVAILABILITY_VALUES.put(info, array);
            isTrue = isTrue(countryBackendID, array);
        } else {
            return null;
        }
        return getAvailability(isTrue);
    }

    default boolean isTrue(String countryBackendID, JSONArray array) {
        for(Object obj : array) {
            final String string = (String) obj;
            if(countryBackendID.equals(string)) {
                return true;
            }
        }
        return false;
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
}
