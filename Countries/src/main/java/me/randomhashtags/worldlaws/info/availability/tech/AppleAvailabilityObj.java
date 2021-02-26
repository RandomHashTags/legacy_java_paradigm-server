package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public final class AppleAvailabilityObj implements AppleFeatureAvailability {

    private final CountryInfo info;
    private HashMap<String, String> countries;

    public AppleAvailabilityObj(CountryInfo info) {
        this.info = info;
    }

    @Override
    public CountryInfo getInfo() {
        return info;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries == null) {
            refresh(null);
        }
        if(!countries.containsKey(countryBackendID)) {
            countries.put(countryBackendID, new CountryAvailability(info.getTitle(), false, CountryAvailabilityCategory.APPLE).toString());
        }
        handler.handle(countries.get(countryBackendID));
    }

    @Override
    public void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String name = info.name(), title = info.getTitle();
        final CountryAvailabilityCategory category = getCategory();
        final String availability = new CountryAvailability(title, true, category).toString();
        final String sectionID = getSectionID();
        final Elements elements = getSectionElements(sectionID);
        for(Element element : elements) {
            final String country = element.textNodes().get(0).text().toLowerCase().replace(" ", "");
            countries.put(country, availability);
        }
        WLLogger.log(Level.INFO, "AppleAvailabilityObj - " + name + " - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
    }

    private String getSectionID() {
        final int length = "availability_".length();
        final String infoName = info.name().toLowerCase().substring(length).replace("_", "-");
        switch (info) {
            case AVAILABILITY_APPLE_APP_STORE_APPS:
            case AVAILABILITY_APPLE_APP_STORE_GAMES:
            case AVAILABILITY_APPLE_ITUNES_STORE_MUSIC:
            case AVAILABILITY_APPLE_ITUNES_STORE_MOVIES:
            case AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS:
                return infoName.substring("apple-".length());
            default:
                return infoName;
        }
    }
    private CountryAvailabilityCategory getCategory() {
        switch (info) {
            case AVAILABILITY_APPLE_APP_STORE_APPS:
            case AVAILABILITY_APPLE_APP_STORE_GAMES:
                return CountryAvailabilityCategory.APP_STORE;
            case AVAILABILITY_APPLE_CARD:
            case AVAILABILITY_APPLE_PAY:
                return CountryAvailabilityCategory.PAYMENT_METHOD;
            case AVAILABILITY_APPLE_MUSIC:
                return CountryAvailabilityCategory.ENTERTAINMENT_MUSIC;
            case AVAILABILITY_APPLE_TV_APP:
            case AVAILABILITY_APPLE_TV_PLUS:
                return CountryAvailabilityCategory.ENTERTAINMENT_STREAMING;
            case AVAILABILITY_APPLE_ARCADE:
                return CountryAvailabilityCategory.ENTERTAINMENT_GAMING;
            case AVAILABILITY_APPLE_NEWS:
            case AVAILABILITY_APPLE_NEWS_AUDIO:
            case AVAILABILITY_APPLE_NEWS_PLUS:
                return CountryAvailabilityCategory.NEWS;
            default:
                return CountryAvailabilityCategory.APPLE;
        }
    }
}
