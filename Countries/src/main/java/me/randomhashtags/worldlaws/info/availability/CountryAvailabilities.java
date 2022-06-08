package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.info.availability.tech.AppleAvailabilityObj;
import me.randomhashtags.worldlaws.info.availability.tech.AppleFeatureType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum CountryAvailabilities implements CountryAvailabilityService {
    INSTANCE,

    ALEXA,
    AMC_PLUS,
    AT_AND_T_TV,
    DAZN,
    DISCOVERY_PLUS,
    DISNEY_PLUS,
    ESPN_PLUS,
    GOOGLE_ASSISTANT,
    GOOGLE_PAY,
    GOOGLE_PLAY_PASS,
    HBO_MAX,
    HULU,
    NVIDIA_GEFORCE_NOW,
    PARAMOUNT_PLUS,
    PEACOCK,
    PLAYSTATION_NOW,
    SAMSUNG_PAY,
    SHOWTIME,
    SPOTIFY,
    STADIA,
    STARZ,
    STARZPLAY,
    TIDAL,
    VENMO,
    XBOX_CLOUD_GAMING,
    XBOX_GAME_PASS,
    XBOX_LIVE,
    YOUTUBE_PREMIUM,
    YOUTUBE_TV,

    ;

    private static final HashSet<CountryAvailabilityService> SERVICES;

    static {
        SERVICES = new HashSet<>(Arrays.asList(values()));
        SERVICES.remove(INSTANCE);
        SERVICES.addAll(Arrays.asList(
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_ICLOUD_PLUS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_APP_STORE_APPS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_APP_STORE_GAMES),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_ARCADE),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_CARD),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_CARPLAY),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_HEALTH_BLOOD_GLUCOSE_HIGHLIGHTS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_HEALTH_DOWNLOAD_HEALTH_RECORDS_TO_IPHONE),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_HEALTH_SHARE_HEALTH_APP_DATA_WITH_HEALTHCARE_PROVIDERS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_ITUNES_STORE_MOVIES),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_ITUNES_STORE_MUSIC),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_ITUNES_STORE_TV_SHOWS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_MAPS_CONGESTION_ZONES),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_MAPS_DIRECTIONS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_MAPS_SPEED_CAMERAS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_MAPS_SPEED_LIMITS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_MAPS_NEARBY),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_MUSIC),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_NEWS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_NEWS_AUDIO),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_NEWS_PLUS),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_PAY),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_SIRI),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_TV_APP),
                new AppleAvailabilityObj(AppleFeatureType.IOS, SovereignStateInfo.AVAILABILITY_APPLE_IOS_TV_PLUS),

                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_APPLE_MUSIC),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_APPLE_PAY),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_BLOOD_OXYGEN_APP),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_ECG),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_IRREGULAR_RHYTHM_NOTIFICATION),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_RADIO),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_SIRI),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_STUDENT_ID_CARDS),
                new AppleAvailabilityObj(AppleFeatureType.WATCH_OS, SovereignStateInfo.AVAILABILITY_APPLE_WATCH_OS_WALKIE_TALKIE)
        ));
    }

    @Override
    public String getServiceFileName(WLCountry country) {
        return "availabilities";
    }

    @Override
    public AvailabilityCategory getPrimaryCategory() {
        switch (this) {
            case ALEXA:
            case GOOGLE_ASSISTANT:
                return AvailabilityCategory.VIRTUAL_ASSISTANT;
            case AMC_PLUS:
            case AT_AND_T_TV:
            case DAZN:
            case DISCOVERY_PLUS:
            case DISNEY_PLUS:
            case ESPN_PLUS:
            case HBO_MAX:
            case HULU:
            case PARAMOUNT_PLUS:
            case PEACOCK:
            case PLAYSTATION_NOW:
            case SHOWTIME:
            case STARZ:
            case STARZPLAY:
            case YOUTUBE_PREMIUM:
            case YOUTUBE_TV:
                return AvailabilityCategory.ENTERTAINMENT_SERVICE;
            case GOOGLE_PAY:
            case SAMSUNG_PAY:
            case VENMO:
                return AvailabilityCategory.DIGITAL_PAYMENT_METHOD;
            case GOOGLE_PLAY_PASS:
            case NVIDIA_GEFORCE_NOW:
            case STADIA:
            case XBOX_CLOUD_GAMING:
            case XBOX_GAME_PASS:
            case XBOX_LIVE:
                return AvailabilityCategory.GAMING_SERVICE;
            case SPOTIFY:
            case TIDAL:
                return AvailabilityCategory.MUSIC_SERVICE;
            default:
                return null;
        }
    }

    @Override
    public String getImageURL() {
        switch (this) {
            case ALEXA: return "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4a/Amazon_Alexa_logo.svg/%quality%px-Amazon_Alexa_logo.svg.png";
            case AT_AND_T_TV: return "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8a/DIRECTV_STREAM_2021_logo.svg/%quality%px-DIRECTV_STREAM_2021_logo.svg.png";
            case DAZN: return "https://upload.wikimedia.org/wikipedia/commons/thumb/0/06/DAZN_Logo_Master.svg/%quality%px-DAZN_Logo_Master.svg.png";
            case DISCOVERY_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/thumb/6/61/Discovery_Plus_logo.svg/%quality%px-Discovery_Plus_logo.svg.png";
            case DISNEY_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Disney%2B_logo.svg/%quality%px-Disney%2B_logo.svg.png";
            case ESPN_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/ESPN_Plus.svg/%quality%px-ESPN_Plus.svg.png";
            case GOOGLE_ASSISTANT: return "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Google_Assistant_logo.svg/%quality%px-Google_Assistant_logo.svg.png";
            case GOOGLE_PAY: return "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c7/Google_Pay_Logo_%282020%29.svg/%quality%px-Google_Pay_Logo_%282020%29.svg.png";
            case HBO_MAX: return "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/HBO_Max_Logo.svg/%quality%px-HBO_Max_Logo.svg.png";
            case HULU: return "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Hulu_Logo.svg/%quality%px-Hulu_Logo.svg.png";
            case NVIDIA_GEFORCE_NOW: return "https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/GeForce_Now_logo.svg/%quality%px-GeForce_Now_logo.svg.png";
            case PARAMOUNT_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Paramount%2B_logo.png/%quality%px-Paramount%2B_logo.png";
            case PEACOCK: return "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/NBCUniversal_Peacock_Logo.svg/%quality%px-NBCUniversal_Peacock_Logo.svg.png";
            case PLAYSTATION_NOW: return "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Playstation-now.png/%quality%px-Playstation-now.png";
            case SAMSUNG_PAY: return "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Samsung_Pay_icon.svg/%quality%px-Samsung_Pay_icon.svg.png";
            case SHOWTIME: return "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Showtime.svg/%quality%px-Showtime.svg.png";
            case SPOTIFY: return "https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Spotify_logo_with_text.svg/%quality%px-Spotify_logo_with_text.svg.png";
            case STADIA: return "https://upload.wikimedia.org/wikipedia/commons/thumb/9/98/Stadia_logo.svg/%quality%px-Stadia_logo.svg.png";
            case TIDAL: return "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Tidal_%28service%29_logo.svg/%quality%px-Tidal_%28service%29_logo.svg.png";
            case VENMO: return "https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Venmo_Logo.svg/%quality%px-Venmo_Logo.svg.png";
            case YOUTUBE_PREMIUM: return "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/YouTube_Premium_logo.svg/%quality%px-YouTube_Premium_logo.svg.png";
            case YOUTUBE_TV: return "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/YouTube_TV_logo.svg/%quality%px-YouTube_TV_logo.svg.png";
            default: return null;
        }
    }

    @Override
    public SovereignStateInfo getInfo() {
        return this == INSTANCE ? SovereignStateInfo.AVAILABILITIES : SovereignStateInfo.valueOf("AVAILABILITY_" + name());
    }

    @Override
    public JSONObjectTranslatable loadData() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        new CompletableFutures<CountryAvailabilityService>().stream(SERVICES, availability -> {
            final JSONArray data = availability.loadDataArray();
            json.put(availability.getInfo().getTitle(), data);
        });
        return json;
    }

    @Override
    public JSONObjectTranslatable parseData(JSONObject json) {
        return JSONObjectTranslatable.parse(json, Folder.COUNTRIES_AVAILABILITIES, "availabilities", null, null);
    }

    @Override
    public JSONObjectTranslatable getJSONObject(WLCountry country) {
        final String countryBackendID = country.getBackendID();
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();

        final HashMap<Boolean, HashMap<String, JSONArray>> values = new HashMap<>();
        final JSONObject json = getData(SovereignStateInfo.AVAILABILITIES, country);
        for(CountryAvailabilityService service : SERVICES) {
            final CountryAvailability availability = service.getAvailability(json, countryBackendID);
            if(availability != null) {
                final String primaryCategory = availability.getPrimaryCategory().name();
                final boolean availabilityIsAvailable = availability.isAvailable();
                values.putIfAbsent(availabilityIsAvailable, new HashMap<>());
                values.get(availabilityIsAvailable).putIfAbsent(primaryCategory, new JSONArray());
                values.get(availabilityIsAvailable).get(primaryCategory).put(availability.toJSONObject());
            }
        }
        for(Map.Entry<Boolean, HashMap<String, JSONArray>> map : values.entrySet()) {
            final boolean isAvailable = map.getKey();
            final JSONObjectTranslatable statusJSON = new JSONObjectTranslatable();
            for(Map.Entry<String, JSONArray> entry : map.getValue().entrySet()) {
                final String primaryCategory = entry.getKey();
                statusJSON.put(primaryCategory, entry.getValue(), true);
            }
            translatable.put("" + isAvailable, statusJSON, true);
        }
        return translatable;
    }

    @Override
    public JSONArray loadDataArray() {
        switch (this) {
            case ALEXA: return loadAlexa();
            case AMC_PLUS: return loadAMCPlus();
            case AT_AND_T_TV: return loadAT_AND_T();
            case DAZN: return loadDAZN();
            case DISCOVERY_PLUS: return loadDiscoveryPlus();
            case DISNEY_PLUS: return loadDisneyPlus();
            case ESPN_PLUS: return loadESPNPlus();
            case GOOGLE_ASSISTANT: return loadGoogleAssistant();
            case GOOGLE_PAY: return loadGooglePay();
            case GOOGLE_PLAY_PASS: return loadGooglePlayPass();
            case HBO_MAX: return loadHBOMax();
            case HULU: return loadHulu();
            case NVIDIA_GEFORCE_NOW: return loadNvidiaGeforceNOW();
            case PARAMOUNT_PLUS: return loadParamountPlus();
            case PEACOCK: return loadPeacock();
            case PLAYSTATION_NOW: return loadPlayStationNow();
            case SAMSUNG_PAY: return loadSamsungPay();
            case SHOWTIME: return loadShowtime();
            case SPOTIFY: return loadSpotify();
            case STADIA: return loadStadia();
            case STARZ: return loadStarz();
            case STARZPLAY: return loadStarzplay();
            case TIDAL: return loadTidal();
            case VENMO: return loadVenmo();
            case XBOX_CLOUD_GAMING: return loadXboxCloudGaming();
            case XBOX_GAME_PASS: return loadXboxGamePass();
            case XBOX_LIVE: return loadXboxLive();
            case YOUTUBE_PREMIUM: return loadYouTubePremium();
            case YOUTUBE_TV: return loadYouTubeTV();
            default:
                WLLogger.logError(this, "reload - missing completion handler for " + name() + "!");
                return null;
        }
    }

    private JSONArray loadAlexa() {
        // https://en.wikipedia.org/wiki/Amazon_Alexa
        final String[] array = {
                "unitedstates",
                "unitedkingdom",
                "germany",
                "austria",
                "india",
                "japan",
                "canada",
                "belgium",
                "chile",
                "colombia",
                "costarica",
                "cyprus",
                "czechrepublic",
                "ecuador",
                "elsalvador",
                "estonia",
                "finland",
                "greece",
                "hungary",
                "iceland",
                "latvia",
                "liechtenstein",
                "lithuania",
                "luxembourg",
                "malta",
                "netherlands",
                "panama",
                "peru",
                "poland",
                "portugal",
                "slovakia",
                "sweden",
                "uruguay",
                "ireland",
                "australia",
                "newzealand",
                "france",
                "italy",
                "spain",
                "mexico",
                "brazil"
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadAMCPlus() {
        // https://www.amcplus.com/faqs
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadAT_AND_T() {
        // https://en.wikipedia.org/wiki/AT%26T_TV
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadDAZN() {
        // https://www.dazn.com/en-DE/help/articles/where-is-dazn-available
        final String[] array = {
                "austria",
                "brazil",
                "canada",
                "germany",
                "italy",
                "japan",
                "spain",
                "switzerland",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadDiscoveryPlus() {
        // https://www.discoveryplus.com
        final String[] array = {
                "denmark",
                "finland",
                "india",
                "ireland",
                "italy",
                "japan",
                "netherlands",
                "norway",
                "poland",
                "spain",
                "sweden",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadDisneyPlus() {
        final String url = "https://en.wikipedia.org/wiki/Disney%2B";
        final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable").get(0).select("tbody tr");
        trs.remove(0);

        final JSONArray array = new JSONArray();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String targetCountry = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final HashSet<String> countries = getCountriesFromText(targetCountry);
            array.putAll(countries);
        }
        return array;
    }
    private JSONArray loadESPNPlus() {
        // https://en.wikipedia.org/wiki/ESPN%2B
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadGoogleAssistant() {
        // https://www.androidauthority.com/google-assistant-countries-864554/ | https://events.google.com/io2018/recap/
        final String[] array = {
                // africa
                "algeria",
                "botswana",
                "cameroon",
                "ivorycoast",
                "democraticrepublicofthecongo",
                "egypt",
                "ghana",
                "kenya",
                "lesotho",
                "morocco",
                "namibia",
                "nigeria",
                "senegal",
                "southafrica",
                "tanzania",
                "tunisia",
                "uganda",
                "westernsahara",
                "zambia",
                "zimbabwe",

                // asia/pacific
                "australia",
                "india",
                "indonesia",
                "japan",
                "malaysia",
                "myanmar",
                "newzealand",
                "pakistan",
                "papuanewguinea",
                "philippines",
                "southkorea",
                "taiwan",
                "thailand",
                "vietnam",

                // europe
                "austria",
                "belgium",
                "czechrepublic",
                "denmark",
                "france",
                "germany",
                "ireland",
                "italy",
                "netherlands",
                "norway",
                "poland",
                "russia",
                "spain",
                "sweden",
                "switzerland",
                "turkey",
                "unitedkingdom",

                // middle east
                "saudi arabia",
                "unitedarabemirates",

                // americas
                "argentina",
                "brazil",
                "bolivia",
                "canada",
                "chile",
                "colombia",
                "costarica",
                "dominicanrepublic",
                "ecuador",
                "elsalvador",
                "guatemala",
                "honduras",
                "mexico",
                "nicaragua",
                "panama",
                "paraguay",
                "peru",
                "uruguay",
                "unitedstates",
                "venezuela"
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadGooglePay() {
        final String url = "https://en.wikipedia.org/wiki/Google_Pay";
        final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);

        final JSONArray array = new JSONArray();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int size = tds.size();
            final String country = tds.get(1-(size == 3 || size == 4 ? 1 : 0)).select("a").get(0).text().toLowerCase().replace(" ", "");
            array.put(country);
        }
        return array;
    }
    private JSONArray loadGooglePlayPass() {
        final String url = "https://play.google.com/about/pass-availability/";
        final Elements elements = getAvailabilityDocumentElements(url, "body main.h-c-page ul li");

        final JSONArray array = new JSONArray();
        for(Element element : elements) {
            final String country = element.text().toLowerCase().replace(" ", "");
            array.put(country);
        }
        return array;
    }
    private JSONArray loadHBOMax() {
        // https://en.wikipedia.org/wiki/HBO_Max
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadHulu() {
        // https://en.wikipedia.org/wiki/Hulu#International_availability
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadNvidiaGeforceNOW() {
        // https://nvidia.custhelp.com/app/answers/detail/a_id/5023
        final String[] array = {
                "albania",
                "andorra",
                "anguilla",
                "austria",
                "bahamas",
                "barbados",
                "belgium",
                "bermuda",
                "bosniaandherzegovina",
                "britishvirginislands",
                "bulgaria",
                "canaryislands",
                "caymanislands",
                "croatia",
                "czechrepublic",
                "denmark",
                "dominica",
                "dominicanrepublic",
                "estonia",
                "faroeislands",
                "finland",
                "france",
                "germany",
                "gibraltar",
                "greece",
                "hungary",
                "iceland",
                "israel",
                "italy",
                "jamaica",
                "latvia",
                "lichtenstein",
                "luxembourg",
                "macedonia",
                "malta",
                "mexico",
                "montenegro",
                "montserrat",
                "morocco",
                "netherlands",
                "norway",
                "poland",
                "portugal",
                "puertorico",
                "romania",
                "saintbarthelemy",
                "saintpierreandmiquelon",
                "serbia",
                "spain",
                "sweden",
                "switzerland",
                "tunisia",
                "turkey",
                "turksandcaicosislands",
                "u.s.virginislands",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadParamountPlus() {
        // https://en.wikipedia.org/wiki/Paramount%2B
        // https://help.cbs.com/s/article/I-m-traveling-out-of-the-country-Can-I-watch-CBSN
        final String[] array = {
                "australia",
                "canada",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadPeacock() {
        // https://en.wikipedia.org/wiki/Peacock_(streaming_service)
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadPlayStationNow() {
        // https://en.wikipedia.org/wiki/PlayStation_Now
        // https://www.playstation.com/en-us/support/subscriptions/playstation-now-support/
        final String[] array = {
                "austria",
                "belgium",
                "canada",
                "denmark",
                "finland",
                "france",
                "germany",
                "ireland",
                "italy",
                "japan",
                "luxembourg",
                "netherlands",
                "norway",
                "portugal",
                "spain",
                "sweden",
                "switzerland",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadSamsungPay() {
        final String url = "https://en.wikipedia.org/wiki/Samsung_Pay";
        final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final JSONArray array = new JSONArray();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int size = tds.size();
            final String country = tds.get(1-(size == 1 ? 1 : 0)).select("a").get(1).text().toLowerCase().replace(" ", "");
            array.put(country);
        }
        return array;
    }
    private JSONArray loadShowtime() {
        // https://en.wikipedia.org/wiki/Showtime_(TV_network)
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadSpotify() {
        final String url = "https://support.spotify.com/us/article/full-list-of-territories-where-spotify-is-available/";
        final Elements trs = getAvailabilityDocumentElements(url, "div.raw-content ul li");
        final JSONArray array = new JSONArray();
        for(Element element : trs) {
            final String country = element.text().toLowerCase().replace(" ", "");
            array.put(country);
        }
        return array;
    }
    private JSONArray loadStadia() {
        // https://support.google.com/stadia/answer/9566513?hl=en
        // https://en.wikipedia.org/wiki/Google_Stadia
        final String[] array = {
                "austria",
                "belgium",
                "canada",
                "czechrepublic",
                "denmark",
                "finland",
                "france",
                "germany",
                "hungary",
                "ireland",
                "italy",
                "netherlands",
                "norway",
                "romania",
                "poland",
                "portugal",
                "slovakia",
                "spain",
                "sweden",
                "switzerland",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadStarz() {
        // https://en.wikipedia.org/wiki/Starz
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadStarzplay() {
        // https://en.wikipedia.org/wiki/Starz#Starzplay
        final String[] array = {
                "argentina",
                "algeria",
                "bahrain",
                "brazil",
                "chile",
                "djibouti",
                "egypt",
                "france",
                "germany",
                "iraq",
                "italy",
                "jordan",
                "kuwait",
                "lebanon",
                "libya",
                "mauritania",
                "morocco",
                "oman",
                "pakistan",
                "palestine",
                "qatar",
                "saudiarabia",
                "spain",
                "tunisia",
                "unitedarabemirates",
                "unitedkingdom"
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadTidal() {
        final String url = "https://support.tidal.com/hc/en-us/articles/202453191-Which-countries-is-TIDAL-available-";
        final Elements trs = getAvailabilityDocumentElements(url, "section.categories-wrapper div ul li.txt-content", 0).select("p");
        for(int i = 0; i < 2; i++) {
            trs.remove(0);
        }
        trs.remove(trs.size()-1);
        trs.remove(trs.size()-1);

        final JSONArray array = new JSONArray();
        for(Element element : trs) {
            final String country = element.text().toLowerCase().replace(" ", "").replace("ofamerica", "");
            array.put(country);
        }
        return array;
    }
    private JSONArray loadVenmo() {
        // https://help.venmo.com/hc/en-us/articles/209690188-Requirements
        return loadOnlyTrue("unitedstates");
    }
    private JSONArray loadXboxCloudGaming() {
        // https://en.wikipedia.org/wiki/Xbox_Cloud_Gaming
        final String[] array = {
                "austria",
                "belgium",
                "canada",
                "czechrepublic",
                "denmark",
                "finland",
                "france",
                "germany",
                "hungary",
                "ireland",
                "italy",
                "northkorea",
                "southkorea",
                "netherlands",
                "norway",
                "poland",
                "portugal",
                "slovakia",
                "spain",
                "sweden",
                "switzerland",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadXboxGamePass() {
        // https://en.wikipedia.org/wiki/Xbox_Game_Pass
        final String[] array = {
                "argentina",
                "australia",
                "austria",
                "belgium",
                "brazil",
                "canada",
                "chile",
                "colombia",
                "czechrepublic",
                "denmark",
                "finland",
                "france",
                "germany",
                "greece",
                "hongkong",
                "hungary",
                "india",
                "ireland",
                "israel",
                "italy",
                "japan",
                "mexico",
                "netherlands",
                "newzealand",
                "norway",
                "poland",
                "portugal",
                "russia",
                "saudiarabia",
                "singapore",
                "slovakia",
                "southafrica",
                "southkorea",
                "spain",
                "sweden",
                "switzerland",
                "taiwan",
                "turkey",
                "unitedarabemirates",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadXboxLive() {
        // https://www.xbox.com/en-us/Shell/ChangeLocale?rtc=1
        // https://en.wikipedia.org/wiki/Xbox_Live
        final String[] array = {
                "argentina",
                "australia",
                "austria",
                "belgium",
                "brazil",
                "canada",
                "chile",
                "china",
                "colombia",
                "czechrepublic",
                "denmark",
                "finland",
                "france",
                "germany",
                "greece",
                "hongkong",
                "hungary",
                "india",
                "ireland",
                "israel",
                "italy",
                "japan",
                "mexico",
                "netherlands",
                "newzealand",
                "norway",
                "poland",
                "portugal",
                "russia",
                "saudiarabia",
                "singapore",
                "slovakia",
                "southafrica",
                "southkorea",
                "spain",
                "sweden",
                "switzerland",
                "taiwan",
                "turkey",
                "unitedarabemirates",
                "unitedkingdom",
                "unitedstates",
        };
        return loadOnlyTrue(array);
    }
    private JSONArray loadYouTubePremium() {
        final String url = "https://support.google.com/youtube/answer/6307365?hl=en#zippy=%2Cpremium-memberships-available-locations";
        final Elements list = getAvailabilityDocumentElements(url, "div table.no-stripes", 0).select("tbody tr td");

        final JSONArray array = new JSONArray();
        for(Element li : list) {
            final String country = li.text().toLowerCase().replace(" ", "").replace("&", "and").replace(".", "");
            array.put(country);
        }
        return array;
    }
    private JSONArray loadYouTubeTV() {
        // https://support.google.com/youtubetv/answer/7370552?hl=en
        return loadOnlyTrue("unitedstates");
    }
}
