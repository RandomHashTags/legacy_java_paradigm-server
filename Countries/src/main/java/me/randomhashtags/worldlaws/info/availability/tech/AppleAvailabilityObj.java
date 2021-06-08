package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.apache.logging.log4j.Level;
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
    public void getCountryValue(String countryBackendID, CompletionHandler handler) {
        if(countries == null) {
            loadData(null);
        }
        countries.putIfAbsent(countryBackendID, new CountryAvailability(info.getTitle(), false).toString());
        handler.handle(countries.get(countryBackendID));
    }

    @Override
    public void loadData(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String infoName = info.name(), title = info.getTitle();
        final String availability = new CountryAvailability(title, true).toString();
        final String sectionID = getSectionID(infoName);
        final Elements elements = getSectionElements(sectionID);
        elements.parallelStream().forEach(element -> {
            final String country = element.textNodes().get(0).text().toLowerCase().replace(" ", "").replace(",", "").split("\\(")[0]
                    .replace("congodemocraticrepublicofthe", "democraticrepublicofthecongo")
                    .replace("congorepublicofthe", "republicofthecongo")
                    .replace("laopeople’sdemocraticrepublic", "laos")
                    .replace("mainland", "")
                    ;
            countries.put(country, availability);
        });
        WLLogger.log(Level.INFO, "AppleAvailabilityObj - " + infoName + " - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
    }

    private String getSectionID(String targetInfoName) {
        final int length = "availability_".length();
        final String infoName = targetInfoName.toLowerCase().substring(length).replace("_", "-");
        switch (info) {
            case AVAILABILITY_APPLE_APP_STORE_APPS:
            case AVAILABILITY_APPLE_APP_STORE_GAMES:
            case AVAILABILITY_APPLE_MAPS_CONGESTION_ZONES:
            case AVAILABILITY_APPLE_MAPS_DIRECTIONS:
            case AVAILABILITY_APPLE_MAPS_SPEED_CAMERAS:
            case AVAILABILITY_APPLE_MAPS_SPEED_LIMITS:
            case AVAILABILITY_APPLE_MAPS_NEARBY:
            case AVAILABILITY_APPLE_SIRI:
            case AVAILABILITY_APPLE_ITUNES_STORE_MUSIC:
            case AVAILABILITY_APPLE_ITUNES_STORE_MOVIES:
            case AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS:
                return infoName.substring("apple-".length());
            default:
                return infoName;
        }
    }
}
