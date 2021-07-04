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
            loadData(new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    AppleAvailabilityObj.this.handle(countryBackendID, handler);
                }
            });
        } else {
            handle(countryBackendID, handler);
        }
    }

    private void handle(String countryBackendID, CompletionHandler handler) {
        countries.putIfAbsent(countryBackendID, new CountryAvailability(info.getTitle(), getImageURL(), false).toString());
        handler.handleString(countries.get(countryBackendID));
    }

    @Override
    public void loadData(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String infoName = info.name(), title = info.getTitle();
        final String availability = new CountryAvailability(title, getImageURL(), true).toString();
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
        if(handler != null) {
            handler.handleString(null);
        }
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

    private String getImageURL() {
        switch (info) {
            case AVAILABILITY_APPLE_APP_STORE_APPS:
            case AVAILABILITY_APPLE_APP_STORE_GAMES:
                return "https://upload.wikimedia.org/wikipedia/commons/6/67/App_Store_%28iOS%29.svg";
            case AVAILABILITY_APPLE_ARCADE: return "https://upload.wikimedia.org/wikipedia/commons/b/b9/Apple-arcade-logo.svg";
            case AVAILABILITY_APPLE_CARD: return "https://upload.wikimedia.org/wikipedia/commons/2/28/Apple_Card.svg";
            case AVAILABILITY_APPLE_CARPLAY: return "https://upload.wikimedia.org/wikipedia/commons/9/92/Apple_CarPlay_Logo.png";
            case AVAILABILITY_APPLE_ITUNES_STORE_MOVIES:
            case AVAILABILITY_APPLE_ITUNES_STORE_MUSIC:
            case AVAILABILITY_APPLE_ITUNES_STORE_TV_SHOWS:
                return "https://upload.wikimedia.org/wikipedia/commons/b/b8/ITunes_Store_logo.svg";
            case AVAILABILITY_APPLE_MAPS_CONGESTION_ZONES:
            case AVAILABILITY_APPLE_MAPS_DIRECTIONS:
            case AVAILABILITY_APPLE_MAPS_NEARBY:
            case AVAILABILITY_APPLE_MAPS_SPEED_CAMERAS:
            case AVAILABILITY_APPLE_MAPS_SPEED_LIMITS:
                return "https://upload.wikimedia.org/wikipedia/commons/1/17/AppleMaps_logo.svg";
            case AVAILABILITY_APPLE_MUSIC: return "https://upload.wikimedia.org/wikipedia/commons/9/9d/AppleMusic_2019.svg";
            case AVAILABILITY_APPLE_NEWS: return "https://upload.wikimedia.org/wikipedia/commons/f/f3/Apple_News_2019_icon_%28iOS%29.png";
            case AVAILABILITY_APPLE_PAY: return "https://upload.wikimedia.org/wikipedia/commons/b/b0/Apple_Pay_logo.svg";
            case AVAILABILITY_APPLE_SIRI: return "https://upload.wikimedia.org/wikipedia/en/8/8e/AppleSiriIcon2017.png";
            case AVAILABILITY_APPLE_TV_APP: return "https://upload.wikimedia.org/wikipedia/commons/3/39/Apple_TV.svg";
            case AVAILABILITY_APPLE_TV_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/2/28/Apple_TV_Plus_Logo.svg";
            default: return null;
        }
    }
}
