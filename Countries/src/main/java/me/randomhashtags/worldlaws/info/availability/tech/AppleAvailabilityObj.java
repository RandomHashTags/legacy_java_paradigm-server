package me.randomhashtags.worldlaws.info.availability.tech;

import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.info.availability.AvailabilityCategory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailability;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Set;

public final class AppleAvailabilityObj implements AppleFeatureAvailability {

    private final AppleFeatureType type;
    private final SovereignStateInfo info;
    private HashMap<String, CountryAvailability> countries;

    public AppleAvailabilityObj(AppleFeatureType type, SovereignStateInfo info) {
        this.type = type;
        this.info = info;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return info;
    }

    @Override
    public JSONObjectTranslatable loadData() {
        return null;
    }

    @Override
    public JSONArray loadDataArray() {
        countries = new HashMap<>();
        final String infoName = info.name(), title = info.getTitle();
        final CountryAvailability availability = new CountryAvailability(title, getPrimaryCategory(), getImageURL(), true);
        final String sectionID = getSectionID(infoName);
        final Elements elements = getSectionElements(type, sectionID);
        if(elements != null) {
            for(Element element : elements) {
                String country = element.textNodes().get(0).text().toLowerCase().
                        replace(" ", "").replace("&", "and").replace("-", "")
                        .replace(",", "").replace("’", "").split("\\(")[0].split("1")[0].split("2")[0]
                        .replace("congodemocraticrepublicofthe", "democraticrepublicofthecongo")
                        .replace("congorepublicofthe", "republicofthecongo")
                        .replace("laopeoplesdemocraticrepublic", "laos")
                        .replace("thegambia", "gambia")
                        .replace("czechia", "czechrepublic")
                        .replace("republicofkorea", "southkorea")
                        .replace("mainland", "")
                        .replace("st.", "saint")
                        .replace("é", "e")
                        .replace("ô", "o")
                        ;
                switch (country) {
                    case "usa":
                        country = "unitedstates";
                        break;
                    case "uk":
                        country = "unitedkingdom";
                        break;
                    default:
                        break;
                }
                countries.put(country, availability);
            }
        } else {
            WLLogger.logInfo("AppleAvailabilityObj;elements==null;type=" + type.name() + ";sectionID=" + sectionID);
        }
        final Set<String> bruh = countries.keySet();
        return loadOnlyTrue(bruh.toArray(new String[bruh.size()]));
    }

    @Override
    public JSONObjectTranslatable parseData(JSONObject json) {
        return null;
    }

    private String getSectionID(String targetInfoName) {
        final int length = "availability_".length();
        final String infoName = targetInfoName.toLowerCase().substring(length).replace("_", "-");
        switch (info) {
            case AVAILABILITY_APPLE_ICLOUD_PLUS:
                return "icloud-icloud-plus";

            case AVAILABILITY_APPLE_IOS_PAY:
            case AVAILABILITY_APPLE_WATCH_OS_APPLE_PAY:
                return "apple-wallet-apple-pay";
            case AVAILABILITY_APPLE_IOS_CARD:
                return "apple-wallet-apple-card";

            case AVAILABILITY_APPLE_IOS_APP_STORE_APPS:
            case AVAILABILITY_APPLE_IOS_APP_STORE_GAMES:
            case AVAILABILITY_APPLE_IOS_MAPS_CONGESTION_ZONES:
            case AVAILABILITY_APPLE_IOS_MAPS_DIRECTIONS:
            case AVAILABILITY_APPLE_IOS_MAPS_SPEED_CAMERAS:
            case AVAILABILITY_APPLE_IOS_MAPS_SPEED_LIMITS:
            case AVAILABILITY_APPLE_IOS_MAPS_NEARBY:
            case AVAILABILITY_APPLE_IOS_SIRI:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_MUSIC:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_MOVIES:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_TV_SHOWS:
                return infoName.substring("apple-ios-".length());

            case AVAILABILITY_APPLE_IOS_HEALTH_BLOOD_GLUCOSE_HIGHLIGHTS:
                return "health-blood-glucose";
            case AVAILABILITY_APPLE_IOS_HEALTH_DOWNLOAD_HEALTH_RECORDS_TO_IPHONE:
                return "health-health-records";
            case AVAILABILITY_APPLE_IOS_HEALTH_SHARE_HEALTH_APP_DATA_WITH_HEALTHCARE_PROVIDERS:
                return "health-share-health-app-data";

            case AVAILABILITY_APPLE_WATCH_OS_APPLE_MUSIC:
                return infoName.substring("apple-watch-os-".length());
            case AVAILABILITY_APPLE_WATCH_OS_BLOOD_OXYGEN_APP:
                return "branded-blood-oxygen";
            case AVAILABILITY_APPLE_WATCH_OS_SIRI:
                return "siri-siri";
            case AVAILABILITY_APPLE_WATCH_OS_STUDENT_ID_CARDS:
                return "branded-student-id";
            case AVAILABILITY_APPLE_WATCH_OS_ECG:
            case AVAILABILITY_APPLE_WATCH_OS_RADIO:
            case AVAILABILITY_APPLE_WATCH_OS_WALKIE_TALKIE:
                return "branded-" + infoName.substring("apple-watch-os-".length());
            case AVAILABILITY_APPLE_WATCH_OS_IRREGULAR_RHYTHM_NOTIFICATION:
                return "branded-atrial-fib";
            default:
                return infoName.replace("-ios-", "-");
        }
    }

    @Override
    public AvailabilityCategory getPrimaryCategory() {
        switch (info) {
            case AVAILABILITY_APPLE_ICLOUD_PLUS:
                return AvailabilityCategory.DIGITAL_STORAGE_SERVICE;
            case AVAILABILITY_APPLE_IOS_CARD:
                return AvailabilityCategory.PHYSICAL_PAYMENT_METHOD;
            case AVAILABILITY_APPLE_IOS_CARPLAY:
            case AVAILABILITY_APPLE_IOS_MAPS_CONGESTION_ZONES:
            case AVAILABILITY_APPLE_IOS_MAPS_DIRECTIONS:
            case AVAILABILITY_APPLE_IOS_MAPS_SPEED_CAMERAS:
            case AVAILABILITY_APPLE_IOS_MAPS_SPEED_LIMITS:
            case AVAILABILITY_APPLE_IOS_MAPS_NEARBY:
                return AvailabilityCategory.VEHICLE_SERVICE;
            case AVAILABILITY_APPLE_IOS_PAY:
            case AVAILABILITY_APPLE_WATCH_OS_APPLE_PAY:
                return AvailabilityCategory.DIGITAL_PAYMENT_METHOD;
            case AVAILABILITY_APPLE_IOS_APP_STORE_APPS:
            case AVAILABILITY_APPLE_IOS_APP_STORE_GAMES:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_MOVIES:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_MUSIC:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_TV_SHOWS:
                return AvailabilityCategory.DIGITAL_STORE_SERVICE;
            case AVAILABILITY_APPLE_IOS_ARCADE:
                return AvailabilityCategory.GAMING_SERVICE;
            case AVAILABILITY_APPLE_IOS_MUSIC:
            case AVAILABILITY_APPLE_WATCH_OS_APPLE_MUSIC:
                return AvailabilityCategory.MUSIC_SERVICE;
            case AVAILABILITY_APPLE_IOS_SIRI:
            case AVAILABILITY_APPLE_WATCH_OS_SIRI:
                return AvailabilityCategory.VIRTUAL_ASSISTANT;
            case AVAILABILITY_APPLE_IOS_TV_APP:
            case AVAILABILITY_APPLE_IOS_TV_PLUS:
            case AVAILABILITY_APPLE_WATCH_OS_RADIO:
                return AvailabilityCategory.ENTERTAINMENT_SERVICE;
            case AVAILABILITY_APPLE_IOS_NEWS:
            case AVAILABILITY_APPLE_IOS_NEWS_AUDIO:
            case AVAILABILITY_APPLE_IOS_NEWS_PLUS:
                return AvailabilityCategory.NEWS_SERVICE;
            case AVAILABILITY_APPLE_IOS_HEALTH_BLOOD_GLUCOSE_HIGHLIGHTS:
            case AVAILABILITY_APPLE_IOS_HEALTH_DOWNLOAD_HEALTH_RECORDS_TO_IPHONE:
            case AVAILABILITY_APPLE_IOS_HEALTH_SHARE_HEALTH_APP_DATA_WITH_HEALTHCARE_PROVIDERS:
            case AVAILABILITY_APPLE_WATCH_OS_BLOOD_OXYGEN_APP:
            case AVAILABILITY_APPLE_WATCH_OS_ECG:
            case AVAILABILITY_APPLE_WATCH_OS_IRREGULAR_RHYTHM_NOTIFICATION:
                return AvailabilityCategory.HEALTH_SERVICE;
            case AVAILABILITY_APPLE_WATCH_OS_STUDENT_ID_CARDS:
                return AvailabilityCategory.FINANCIAL_SERVICE;
            case AVAILABILITY_APPLE_WATCH_OS_WALKIE_TALKIE:
                return AvailabilityCategory.COMMUNICATION_SERVICE;
            default:
                return null;
        }
    }

    @Override
    public String getImageURL() {
        switch (info) {
            case AVAILABILITY_APPLE_IOS_APP_STORE_APPS:
            case AVAILABILITY_APPLE_IOS_APP_STORE_GAMES:
                return "https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/App_Store_%28iOS%29.svg/%quality%px-App_Store_%28iOS%29.svg.png";
            case AVAILABILITY_APPLE_IOS_ARCADE: return "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b9/Apple-arcade-logo.svg/%quality%px-Apple-arcade-logo.svg.png";
            case AVAILABILITY_APPLE_IOS_CARD: return "https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Apple_Card.svg/%quality%px-Apple_Card.svg.png";
            case AVAILABILITY_APPLE_IOS_CARPLAY: return "https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Apple_CarPlay_Logo.png/%quality%px-Apple_CarPlay_Logo.png";
            case AVAILABILITY_APPLE_ICLOUD_PLUS: return "https://upload.wikimedia.org/wikipedia/en/thumb/f/f0/ICloud_logo_%28new%29.png/%quality%px-ICloud_logo_%28new%29.png";
            case AVAILABILITY_APPLE_IOS_HEALTH_BLOOD_GLUCOSE_HIGHLIGHTS:
            case AVAILABILITY_APPLE_IOS_HEALTH_DOWNLOAD_HEALTH_RECORDS_TO_IPHONE:
            case AVAILABILITY_APPLE_IOS_HEALTH_SHARE_HEALTH_APP_DATA_WITH_HEALTHCARE_PROVIDERS:
                return "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Health_icon_iOS_12.png/%quality%px-Health_icon_iOS_12.png";
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_MOVIES:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_MUSIC:
            case AVAILABILITY_APPLE_IOS_ITUNES_STORE_TV_SHOWS:
                return "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/ITunes_Store_logo.svg/%quality%px-ITunes_Store_logo.svg.png";
            case AVAILABILITY_APPLE_IOS_MAPS_CONGESTION_ZONES:
            case AVAILABILITY_APPLE_IOS_MAPS_DIRECTIONS:
            case AVAILABILITY_APPLE_IOS_MAPS_NEARBY:
            case AVAILABILITY_APPLE_IOS_MAPS_SPEED_CAMERAS:
            case AVAILABILITY_APPLE_IOS_MAPS_SPEED_LIMITS:
                return "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Apple_Maps_logo.svg/%quality%px-Apple_Maps_logo.svg.png";
            case AVAILABILITY_APPLE_IOS_MUSIC: return "https://upload.wikimedia.org/wikipedia/commons/thumb/9/9d/AppleMusic_2019.svg/%quality%px-AppleMusic_2019.svg.png";
            case AVAILABILITY_APPLE_IOS_NEWS: return "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/Apple_News_2019_icon_%28iOS%29.png/%quality%px-Apple_News_2019_icon_%28iOS%29.png";
            case AVAILABILITY_APPLE_IOS_PAY: return "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Apple_Pay_logo.svg/%quality%px-Apple_Pay_logo.svg.png";
            case AVAILABILITY_APPLE_IOS_SIRI: return "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7a/Siri_Logo_in_2022.png/%quality%px-Siri_Logo_in_2022.png";
            case AVAILABILITY_APPLE_IOS_TV_APP: return "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e9/TV_%28iOS%29.png/%quality%px-TV_%28iOS%29.png";
            case AVAILABILITY_APPLE_IOS_TV_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/thumb/2/28/Apple_TV_Plus_Logo.svg/%quality%px-Apple_TV_Plus_Logo.svg.png";
            default: return null;
        }
    }
}
