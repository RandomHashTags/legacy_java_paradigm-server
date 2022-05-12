package me.randomhashtags.worldlaws.notifications.subcategory;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import org.json.JSONArray;
import org.json.JSONObject;

public enum RemoteNotificationSubcategoryWeather implements RemoteNotificationSubcategory {
    LOCAL_ALERT,
    ;

    @Override
    public RemoteNotificationCategory getCategory() {
        return RemoteNotificationCategory.WEATHER;
    }

    @Override
    public String getName() {
        switch (this) {
            case LOCAL_ALERT: return "Local Weather Alert";
            default: return null;
        }
    }

    @Override
    public boolean isConditional() {
        switch (this) {
            case LOCAL_ALERT: return true;
            default: return false;
        }
    }

    @Override
    public JSONObjectTranslatable getAllValues() {
        switch (this) {
            case LOCAL_ALERT:
                return getAllLocalAlertValues();
            default:
                return null;
        }
    }

    private JSONObjectTranslatable getAllLocalAlertValues() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("unitedstates");
        final JSONArrayTranslatable usa = getLocalAlertValuesUSA();
        json.put("unitedstates", usa);
        return json;
    }
    private JSONArrayTranslatable getLocalAlertValuesUSA() {
        final String countryBackendID = "unitedstates";
        final Folder folder = Folder.WEATHER_COUNTRY;
        final String fileName = "eventTypes";
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID));
        final JSONArrayTranslatable array = new JSONArrayTranslatable();
        final JSONArray local = getLocalFileJSONArray(folder, fileName);
        if(local != null) {
            array.putAll(local);
        } else {
            final String url = "https://api.weather.gov/alerts/types";
            final JSONObject json = requestJSONObject(url);
            if(json != null && json.has("eventTypes")) {
                final JSONArray eventTypesArray = json.getJSONArray("eventTypes");
                eventTypesArray.put("All");
                array.putAll(eventTypesArray);
            }
        }
        return array;
    }
}
