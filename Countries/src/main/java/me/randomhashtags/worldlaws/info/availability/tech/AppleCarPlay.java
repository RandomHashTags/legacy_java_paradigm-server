package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.AppleFeatureAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum AppleCarPlay implements AppleAvailability, AppleFeatureAvailability {
    INSTANCE;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.AVAILABILITY_APPLE_CARPLAY;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        getAppleValue(countryBackendID, handler);
    }

    @Override
    public void load(CountryInfo info) {
        final long started = System.currentTimeMillis();
        COUNTRIES.put(info, new HashMap<>());
        final String title = info.getTitle();
        final CountryAvailabilityCategory category = CountryAvailabilityCategory.APPLE;
        final Elements elements = getSectionValues("apple-carplay").select("div.section-content ul li");
        for(Element element : elements) {
            final String country = element.textNodes().get(0).text().toLowerCase().replace(" ", "");
            final CountryAvailability availability = new CountryAvailability(title, true, category);
            COUNTRIES.get(info).put(country, availability.toString());
        }
        WLLogger.log(Level.INFO, "AppleCarPlay - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
    }
}
