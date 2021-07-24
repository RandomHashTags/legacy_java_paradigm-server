package me.randomhashtags.worldlaws.info.availability;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;

public enum CountryAvailabilities implements CountryAvailabilityService {
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
            case ALEXA: return "https://upload.wikimedia.org/wikipedia/commons/4/4a/Amazon_Alexa_logo.svg";
            case AT_AND_T_TV: return "https://upload.wikimedia.org/wikipedia/commons/d/d5/AT%26T_TV.svg";
            case DAZN: return "https://upload.wikimedia.org/wikipedia/commons/5/56/DAZN_LogoInBox_black.svg";
            case DISCOVERY_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/d/df/Discovery%2B.png";
            case DISNEY_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/3/3e/Disney%2B_logo.svg";
            case ESPN_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/8/80/ESPN_Plus.svg";
            case GOOGLE_ASSISTANT: return "https://upload.wikimedia.org/wikipedia/commons/c/cb/Google_Assistant_logo.svg";
            case GOOGLE_PAY: return "https://upload.wikimedia.org/wikipedia/commons/c/c7/Google_Pay_Logo_%282020%29.svg";
            case HBO_MAX: return "https://upload.wikimedia.org/wikipedia/commons/1/17/HBO_Max_Logo.svg";
            case HULU: return "https://upload.wikimedia.org/wikipedia/commons/e/e4/Hulu_Logo.svg";
            case NVIDIA_GEFORCE_NOW: return "https://upload.wikimedia.org/wikipedia/en/5/50/GeForce_Now_logo.png";
            case PARAMOUNT_PLUS: return "https://upload.wikimedia.org/wikipedia/commons/a/a5/Paramount_Plus.svg";
            case PEACOCK: return "https://upload.wikimedia.org/wikipedia/commons/d/d3/NBCUniversal_Peacock_Logo.svg";
            case PLAYSTATION_NOW: return "https://upload.wikimedia.org/wikipedia/commons/4/4c/Playstation-now.png";
            case SAMSUNG_PAY: return "https://upload.wikimedia.org/wikipedia/commons/7/7e/Samsung_Pay_icon.svg";
            case SHOWTIME: return "https://upload.wikimedia.org/wikipedia/commons/2/22/Showtime.svg";
            case SPOTIFY: return "https://upload.wikimedia.org/wikipedia/commons/2/26/Spotify_logo_with_text.svg";
            case STADIA: return "https://upload.wikimedia.org/wikipedia/commons/9/98/Stadia_logo.svg";
            case TIDAL: return "https://upload.wikimedia.org/wikipedia/commons/1/18/Tidal_%28service%29_logo.svg";
            case VENMO: return "https://upload.wikimedia.org/wikipedia/commons/4/45/Venmo_Logo.svg";
            case YOUTUBE_PREMIUM: return "https://upload.wikimedia.org/wikipedia/commons/d/dd/YouTube_Premium_logo.svg";
            case YOUTUBE_TV: return "https://upload.wikimedia.org/wikipedia/commons/f/f7/YouTube_TV_logo.svg";
            default: return null;
        }
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.valueOf("AVAILABILITY_" + name());
    }

    @Override
    public void loadData(CompletionHandler handler) {
        reload(handler);
    }

    private void reload(CompletionHandler handler) {
        switch (this) {
            case ALEXA:
                loadAlexa(handler);
                break;
            case AMC_PLUS:
                loadAMCPlus(handler);
                break;
            case AT_AND_T_TV:
                loadAT_AND_T(handler);
                break;
            case DAZN:
                loadDAZN(handler);
                break;
            case DISCOVERY_PLUS:
                loadDiscoveryPlus(handler);
                break;
            case DISNEY_PLUS:
                loadDisneyPlus(handler);
                break;
            case ESPN_PLUS:
                loadESPNPlus(handler);
                break;
            case GOOGLE_ASSISTANT:
                loadGoogleAssistant(handler);
                break;
            case GOOGLE_PAY:
                loadGooglePay(handler);
                break;
            case GOOGLE_PLAY_PASS:
                loadGooglePlayPass(handler);
                break;
            case HBO_MAX:
                loadHBOMax(handler);
                break;
            case HULU:
                loadHulu(handler);
                break;
            case NVIDIA_GEFORCE_NOW:
                loadNvidiaGeforceNOW(handler);
                break;
            case PARAMOUNT_PLUS:
                loadParamountPlus(handler);
                break;
            case PEACOCK:
                loadPeacock(handler);
                break;
            case PLAYSTATION_NOW:
                loadPlayStationNow(handler);
                break;
            case SAMSUNG_PAY:
                loadSamsungPay(handler);
                break;
            case SHOWTIME:
                loadShowtime(handler);
                break;
            case SPOTIFY:
                loadSpotify(handler);
                break;
            case STADIA:
                loadStadia(handler);
                break;
            case STARZ:
                loadStarz(handler);
                break;
            case STARZPLAY:
                loadStarzplay(handler);
                break;
            case TIDAL:
                loadTidal(handler);
                break;
            case VENMO:
                loadVenmo(handler);
                break;
            case XBOX_CLOUD_GAMING:
                loadXboxCloudGaming(handler);
                break;
            case XBOX_GAME_PASS:
                loadXboxGamePass(handler);
                break;
            case XBOX_LIVE:
                loadXboxLive(handler);
                break;
            case YOUTUBE_PREMIUM:
                loadYouTubePremium(handler);
                break;
            case YOUTUBE_TV:
                loadYouTubeTV(handler);
                break;
            default:
                WLLogger.log(Level.WARN, "CountryAvailabilities - reload - missing for " + name() + "!");
                handler.handleString(null);
                break;
        }
    }
    private void loadOnlyTrue(CompletionHandler handler, String...countries) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String country : countries) {
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
            isFirst = false;
        }
        builder.append("]");
        handler.handleJSONArray(new JSONArray(builder.toString()));
    }

    private void loadAlexa(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadAMCPlus(CompletionHandler handler) {
        // https://www.amcplus.com/faqs
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadAT_AND_T(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/AT%26T_TV
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadDAZN(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadDiscoveryPlus(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadDisneyPlus(CompletionHandler handler) {
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://en.wikipedia.org/wiki/Disney%2B";
                final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable").get(0).select("tbody tr");
                trs.remove(0);

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final Elements tds = element.select("td");
                    final String targetCountry = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
                    final HashSet<String> countries = getCountriesFromText(targetCountry);
                    for(String country : countries) {
                        builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                        isFirst = false;
                    }
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadESPNPlus(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/ESPN%2B
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadGoogleAssistant(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadGooglePay(CompletionHandler handler) {
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://en.wikipedia.org/wiki/Google_Pay";
                final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
                trs.remove(0);

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final Elements tds = element.select("td");
                    final int size = tds.size();
                    final String country = tds.get(1-(size == 3 || size == 4 ? 1 : 0)).select("a").get(0).text().toLowerCase().replace(" ", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadGooglePlayPass(CompletionHandler handler) {
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://play.google.com/about/pass-availability/";
                final Elements elements = getAvailabilityDocumentElements(url, "body main.h-c-page ul li");

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : elements) {
                    final String country = element.text().toLowerCase().replace(" ", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadHBOMax(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/HBO_Max
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadHulu(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Hulu#International_availability
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadNvidiaGeforceNOW(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadParamountPlus(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Paramount%2B
        // https://help.cbs.com/s/article/I-m-traveling-out-of-the-country-Can-I-watch-CBSN
        final String[] array = {
                "australia",
                "canada",
                "unitedstates",
        };
        loadOnlyTrue(handler, array);
    }
    private void loadPeacock(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Peacock_(streaming_service)
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadPlayStationNow(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadSamsungPay(CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/Samsung_Pay";
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements trs = getAvailabilityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
                trs.remove(0);
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final Elements tds = element.select("td");
                    final int size = tds.size();
                    final String country = tds.get(1-(size == 1 ? 1 : 0)).select("a").get(1).text().toLowerCase().replace(" ", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadShowtime(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Showtime_(TV_network)
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadSpotify(CompletionHandler handler) {
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://support.spotify.com/us/article/full-list-of-territories-where-spotify-is-available/";
                final Elements trs = getAvailabilityDocumentElements(url, "body.type-normal div div.mainContainer div.container div.Layout_main__3m1yK div.raw-content div.RawContent_tableWrapper__3mA43 table tbody tr");
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;

                for(Element element : trs) {
                    final Elements tds = element.select("td");
                    final String[] countries = tds.get(1).text().split("\\.")[0].split(", ");
                    for(String country : countries) {
                        country = country.toLowerCase().replace(" ", "");
                        builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                        isFirst = false;
                    }
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadStadia(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadStarz(CompletionHandler handler) {
        // https://en.wikipedia.org/wiki/Starz
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadStarzplay(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadTidal(CompletionHandler handler) {
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://support.tidal.com/hc/en-us/articles/202453191-Which-countries-is-TIDAL-available-";
                final Elements trs = getAvailabilityDocumentElements(url, "section.categories-wrapper div ul li.txt-content", 0).select("p");
                for(int i = 0; i < 2; i++) {
                    trs.remove(0);
                }
                trs.remove(trs.size()-1);
                trs.remove(trs.size()-1);

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element element : trs) {
                    final String country = element.text().toLowerCase().replace(" ", "").replace("ofamerica", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadVenmo(CompletionHandler handler) {
        // https://help.venmo.com/hc/en-us/articles/209690188-Requirements
        loadOnlyTrue(handler, "unitedstates");
    }
    private void loadXboxCloudGaming(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadXboxGamePass(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadXboxLive(CompletionHandler handler) {
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
        loadOnlyTrue(handler, array);
    }
    private void loadYouTubePremium(CompletionHandler handler) {
        getJSONArray(new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String url = "https://en.wikipedia.org/wiki/YouTube_Premium";
                final Elements lis = getAvailabilityDocumentElements(url, "div.mw-parser-output table.infobox", 0).select("tbody tr td div.NavFrame ul.NavContent li");

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element li : lis) {
                    final String country = li.select("a").get(0).text().toLowerCase().replace(" ", "");
                    builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
                    isFirst = false;
                }
                builder.append("]");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                handler.handleJSONArray(array);
            }
        });
    }
    private void loadYouTubeTV(CompletionHandler handler) {
        // https://support.google.com/youtubetv/answer/7370552?hl=en
        loadOnlyTrue(handler, "unitedstates");
    }
}
