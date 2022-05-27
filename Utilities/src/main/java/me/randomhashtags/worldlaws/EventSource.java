package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
        return URLDecoder.decode(LocalServer.fixEscapeValues(input), StandardCharsets.UTF_8);
    }

    public String getSiteName(boolean fixed) {
        return fixed ? fixedSiteName : siteName;
    }
    public String getHomepageURL() {
        return homepageURL;
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
