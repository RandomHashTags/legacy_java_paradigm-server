package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilityCategory;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public interface AppleAvailability extends CountryService {

    Elements ELEMENTS = Jsoupable.getStaticDocumentElements("https://en.wikipedia.org/wiki/Apple_Media_Services", "div.mw-parser-output table.wikitable", 0).select("tbody tr");
    HashMap<CountryInfo, HashMap<String, String>> COUNTRIES = new HashMap<>();

    @Override
    default HashMap<String, String> getCountries() {
        return null;
    }

    @Override
    default void refresh(CompletionHandler handler) {
    }

    default void getAppleValue(String countryBackendID, CompletionHandler handler) {
        final CountryInfo info = getInfo();
        if(!COUNTRIES.containsKey(info)) {
            load(info);
        }
        if(!COUNTRIES.get(info).containsKey(countryBackendID)) {
            COUNTRIES.get(info).put(countryBackendID, new CountryAvailability(info.getTitle(), false, CountryAvailabilityCategory.APPLE).toString());
        }
        handler.handle(COUNTRIES.get(info).get(countryBackendID));
    }
    default void load(CountryInfo info) {
        final long started = System.currentTimeMillis();
        COUNTRIES.put(info, new HashMap<>());
        final String title = info.getTitle();
        final int index = getTableIndex(info);
        final CountryAvailabilityCategory category = getCategory(info);
        final Elements elements = new Elements(ELEMENTS);
        elements.remove(0);
        elements.remove(0);
        final String available = "/wiki/File:Yes_check.svg";
        for(Element element : elements) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final boolean value = tds.get(index).select("a").attr("href").equals(available);
            final CountryAvailability availability = new CountryAvailability(title, value, category);
            COUNTRIES.get(info).put(country, availability.toString());
        }
        WLLogger.log(Level.INFO, "AppleAvailability - loaded " + title + " (took " + (System.currentTimeMillis()-started) + "ms)");
    }
    private int getTableIndex(CountryInfo info) {
        switch (info) {
            case AVAILABILITY_APPLE_PAY:
                return 1;
            case AVAILABILITY_APPLE_ONE:
                return 2;
            case AVAILABILITY_APPLE_APP_STORE:
                return 3;
            case AVAILABILITY_APPLE_ARCADE:
                return 4;
            case AVAILABILITY_APPLE_MUSIC:
                return 5;
            case AVAILABILITY_APPLE_ITUNES_STORE_MOVIES:
                return 6;
            case AVAILABILITY_APPLE_ITUNES_STORE_MUSIC:
                return 7;
            case AVAILABILITY_APPLE_ITUNES_STORE_RINGTONES_AND_TONES:
                return 8;
            case AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS:
                return 9;
            case AVAILABILITY_APPLE_TV_APP:
                return 10;
            case AVAILABILITY_APPLE_TV_PLUS:
                return 11;
            case AVAILABILITY_APPLE_PODCASTS:
                return 12;
            case AVAILABILITY_APPLE_NEWS:
                return 13;
            case AVAILABILITY_APPLE_NEWS_PLUS:
                return 14;
            case AVAILABILITY_APPLE_FITNESS_PLUS:
                return 15;
            default:
                return -1;
        }
    }
    private CountryAvailabilityCategory getCategory(CountryInfo info) {
        switch (info) {
            case AVAILABILITY_APPLE_PAY:
                return CountryAvailabilityCategory.PAYMENT_METHOD;
            case AVAILABILITY_APPLE_MUSIC:
                return CountryAvailabilityCategory.ENTERTAINMENT_MUSIC;
            case AVAILABILITY_APPLE_TV_APP:
            case AVAILABILITY_APPLE_TV_PLUS:
                return CountryAvailabilityCategory.ENTERTAINMENT_STREAMING;
            default:
                return CountryAvailabilityCategory.APPLE;
        }
    }
}
