package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class EventSource {
    private final String siteName, fixedSiteName, homepageURL;

    public EventSource(String siteName, String homepageURL) {
        this.siteName = siteName;
        this.fixedSiteName = parseSiteName(siteName);
        this.homepageURL = homepageURL;
    }
    public EventSource(String siteName, JSONObject json) {
        this.siteName = siteName;
        this.fixedSiteName = parseSiteName(siteName);
        this.homepageURL = json.getString("homepageURL");
    }

    private String parseSiteName(String input) {
        return LocalServer.fixEscapeValues(input)
                .replace("%26", "&")
                .replace("%2B", "+")
                .replace("%27", "'")
                .replace("%C3%AD", "í")
                .replace("%CA%BB", "ʻ")
                .replace("%E2%80%93", "–")
                .replace("%E2%80%98", "ʻ")
                ;
    }

    public String getSiteName(boolean fixed) {
        return fixed ? fixedSiteName : siteName;
    }
    public JSONObjectTranslatable toJSONObject(boolean withSiteName) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(withSiteName) {
            final JSONObjectTranslatable test = new JSONObjectTranslatable();
            test.put("homepageURL", homepageURL);
            json.put(siteName, test);
        } else {
            json.put("homepageURL", homepageURL);
        }
        return json;
    }
}
