package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.List;

public enum MovieProductionCompanies {

    _1492_PICTURES("1492 Pictures"),
    _21_LAPS_ENTERTAINMENT("21 Laps Entertainment"),
    _87NORTH_PRODUCTIONS("87North Productions"),

    A24("A24 (company)", "A24 Films"),
    AGBO("AGBO", "Gozie AGBO"),
    AGC_STUDIOS("AGC Studios"),
    ALCON_ENTERTAINMENT,
    ALLSPARK_PICTURES("Allspark Pictures", "Hasbro Films", "Hasbro Film Group"),
    AMAZON_STUDIOS,
    AMERICAN_ZOETROPE,
    ANIMAL_LOGIC,
    ANNAPURNA_PICTURES,
    ATOMIC_MONSTER_PRODUCTIONS,

    // Amblin
    AMBLIN_ENTERTAINMENT,
    AMBLIN_PARTNERS,
    DREAMWORKS_ANIMATION("DreamWorks Animation"),
    DREAMWORKS_PICTURES("DreamWorks Pictures"),

    APATOW_PRODUCTIONS("Apatow Productions", "The Apatow Company"),
    ATLAS_ENTERTAINMENT,

    // Apple
    APPLE_STUDIOS("Apple Studios (production company)"),
    APPLE_TV_PLUS("Apple TV+"),

    BAD_ROBOT_PRODUCTIONS("Bad Robot Productions", "Bad Robot"),
    BALBOA_PRODUCTIONS("Balboa Productions", "Sylvester Stallone"),
    BARDEL_ENTERTAINMENT,
    BLACK_BEAR_PICTURES,
    BLISS_MEDIA,
    BLUMHOUSE_PRODUCTIONS,
    BRON_STUDIOS("Bron Studios", "Bron Creative", "BRON"),

    CBS_FILMS("CBS Films"),
    CENTROPOLIS_ENTERTAINMENT,
    CHERNIN_ENTERTAINMENT,
    CONSTANTIN_FILM,
    CROSS_CREEK_PICTURES,

    DARK_HORSE_ENTERTAINMENT,
    DI_BONAVENTURA_PICTURES("Di Bonaventura Pictures", "dB Pictures"),
    DREAMCREW("DreamCrew"),

    // Disney
    _20TH_CENTURY_ANIMATION("20th Century Animation"),
    _20TH_CENTURY_STUDIOS("20th Century Studios", "20th Century Fox"),
    DISCOVERY_PLUS("Discovery+"),
    DISNEY_CHANNEL("Disney Channel", "The Disney Channel", "Disney"),
    DISNEY_PLUS("Disney+"),
    DISNEY_TELEVISION_ANIMATION,
    LUCASFILM,
    MARVEL_STUDIOS,
    MARVEL_ENTERTAINMENT,
    PIXAR("Pixar", "Pixar Animation Studios"),
    REGENCY_ENTERPRISES("Regency Enterprises", "Regency", "Regency Entertainment", "Monarchy Enterprises"),
    SEARCHLIGHT_PICTURES("Searchlight Pictures", "Searchlight"),
    TOUCHSTONE_PICTURES,
    WALT_DISNEY_ANIMATION_STUDIOS,
    WALT_DISNEY_PICTURES,
    WALT_DISNEY_STUDIOS_MOTION_PICTURES,

    DAVIS_ENTERTAINMENT,
    DIMENSION_FILMS,

    ELECTRIC_ENTERTAINMENT,
    ENTERTAINMENT_ONE("Entertainment One", "eOne"),
    EON_PRODUCTIONS,
    ESCAPE_ARTISTS("Escape Artists", "Escape Artists Productions"),
    EUROPACORP("EuropaCorp"),

    FILMNATION_ENTERTAINMENT("FilmNation Entertainment"),
    FORTIS_FILMS,

    GHOST_HOUSE_PICTURES,
    GK_FILMS("GK Films"),
    GOLD_CIRCLE_FILMS("Gold Circle Films", "Gold Circle Entertainment"),
    GOOD_UNIVERSE,
    GRAVITAS_VENTURES,

    HAPPY_MADISON_PRODUCTIONS,

    // HBO
    HBO_FILMS("HBO Films", "HBO Pictures"),
    HBO_MAX("HBO Max"),

    HELLO_SUNSHINE("Hello Sunshine (company)", "Reese Witherspoon"),
    HEYDAY_FILMS("Heyday Films"),
    HULU,

    IAC_FILMS("IAC (company)", "IAC"),
    IMAGEMOVERS("ImageMovers", "ImageMovers Digital"),
    IMAGINE_ENTERTAINMENT("Imagine Entertainment", "Imagine", "Imagine Films Entertainment"),
    INTREPID_PICTURES,

    LEGENDARY("Legendary Entertainment", "Legendary Pictures Productions", "Legendary Entertainment", "Legendary Pictures"),
    LIGHTSTORM_ENTERTAINMENT("Lightstorm Entertainment", "James Cameron", "Lawrence Kasanoff"),

    // Lionsgate
    LIONSGATE,
    LIONSGATE_FILMS,
    LIONSGATE_PREMIERE,
    SUMMIT_ENTERTAINMENT,

    MANDEVILLE_FILMS,

    // Metro-Goldwyn-Mayer
    METRO_GOLDWYN_MAYER("Metro-Goldwyn-Mayer", "Metro-Goldwyn-Mayer Studios", "MGM"),
    ORION_CLASSICS,
    ORION_PICTURES("Orion Pictures", "Orion Releasing", "Orion Pictures Corporation"),

    MILLENNIUM_MEDIA("Millennium Media", "Millennium Films"),
    MIRAMAX("Miramax", "Miramax Films"),
    MRC("MRC (company)", "Media Rights Capital", "MRC Film"),
    MTV_ENTERTAINMENT_STUDIOS("MTV Entertainment Studios"),

    NEON("Neon (company)", "NEON"),

    // Netflix
    NETFLIX,
    NETFLIX_ANIMATION,

    NEW_REPUBLIC_PICTURES,
    NINTENDO,

    ONE_RACE_FILMS("One Race Films", "One Race Productions"),
    OPEN_ROAD_FILMS,
    ORIGINAL_FILM,
    OVERBROOK_ENTERTAINMENT,
    OUTERBANKS_ENTERTAINMENT,

    // Paramount
    NICKELODEON_MOVIES,
    PARAMOUNT_ANIMATION,
    PARAMOUNT_PICTURES,
    PARAMOUNT_PLUS("Paramount+"),
    PARAMOUNT_PLAYERS,

    PARTICIPANT("Participant (company)", "Participant Media", "Participant Productions"),
    PEACOCK("Peacock (streaming service)"),
    PERFECT_WORLD_PICTURES,
    PINNACLE_PEAK_PICTURES("Pinnacle Peak Pictures", "Pure Flix Entertainment"),
    PLAN_B_ENTERTAINMENT("Plan B Entertainment", "Plan B", "Brad Pitt"),
    PLATINUM_DUNES,
    POINT_GREY_PICTURES,

    QED_INTERNATIONAL("QED International"),

    RADIO_SILENCE("Radio Silence Productions", "Radio Silence"),
    RATPAC_DUNE_ENTERTAINMENT("RatPac-Dune Entertainment"),
    RVK_STUDIOS("RVK Studios"),

    SABAN_FILMS,
    SATURN_FILMS("Saturn Films", "Nicolas Cage"),
    SEGA_SAMMY_GROUP("Sega Sammy Holdings", "Sega Sammy"),
    SCOTT_FREE_PRODUCTIONS,
    SCREEN_MEDIA_FILMS,
    SEVEN_BUCKS_PRODUCTIONS,
    SHOWTIME("Showtime (TV network)"),
    SKYDANCE_ANIMATION("Skydance Media#Skydance Animation"),
    SKYDANCE_MEDIA,
    SMOKEHOUSE_PICTURES,
    STX_ENTERTAINMENT("STX Entertainment", "STX Financing", "STX Films", "STXfilms"),

    // Sony
    GHOST_CORPS,
    PLAYSTATION_PRODUCTIONS("PlayStation Productions", "PlayStation Originals"),
    SCREEN_GEMS,
    SONY_PICTURES("Sony Pictures", "Sony Pictures Entertainment", "SPE", "Columbia Pictures", "Columbia Pictures Entertainment"),
    SONY_PICTURES_ANIMATION,
    SONY_PICTURES_CLASSICS,
    SONY_PICTURES_IMAGEWORKS,
    SONY_PICTURES_MOTION_PICTURE_GROUP("Sony Pictures Motion Picture Group", "3000 Pictures", "Sony Pictures Entertainment Motion Picture Group", "Columbia TriStar Motion Picture Group", "SPMPG"),
    TRISTAR_PICTURES("TriStar Pictures"),
    TRISTAR_PRODUCTIONS("TriStar Productions"),

    SPYGLASS_MEDIA_GROUP("Spyglass Media Group", "Spyglass Entertainment"),
    STAGE_6_FILMS,
    SUNDAY_NIGHT_PRODUCTIONS,
    SYNCOPY("Syncopy Inc.", "Syncopy"),

    TENCENT_PICTURES,
    THE_IMAGINARIUM("The Imaginarium", "Imaginarium Productions", "The Imaginarium Studios"),
    THE_STONE_QUARRY("The Stone Quarry", "Cruel and Unusual Films"),
    THUNDER_ROAD_FILMS,
    TOHO,
    TOONBOX_ENTERTAINMENT("ToonBox Entertainment"),
    TROUBLEMAKER_STUDIOS,
    TSG_ENTERTAINMENT("TSG Entertainment"),
    TWISTED_PICTURES,

    // NBC Universal
    CARNIVAL_FILMS,
    FOCUS_FEATURES,
    ILLUMINATION("Illumination (company)", "Illumination Entertainment"),
    UNIVERSAL_ANIMATION_STUDIOS("Universal Animation Studios", "Universal Cartoon Studios"),
    UNIVERSAL_PICTURES("Universal Pictures", "Universal", "Universal Studios", "Universal City Studios", "Universal Pictures Home Entertainment"),
    WORKING_TITLE_FILMS("Working Title Films", "Working Title"),

    VERTICAL_ENTERTAINMENT,
    VERTIGO_ENTERTAINMENT,
    VILLAGE_ROADSHOW_PICTURES,
    VOLTAGE_PICTURES,

    WALDEN_MEDIA,

    // Warner Bros
    CASTLE_ROCK_ENTERTAINMENT,
    DC_ENTERTAINMENT("DC Entertainment"),
    DC_FILMS("DC Films"),
    NEW_LINE_CINEMA("New Line Cinema", "New Line Productions"),
    WARNER_ANIMATION_GROUP("Warner Animation Group", "WAG"),
    WARNER_BROS_ANIMATION("Warner Bros. Animation"),
    WARNER_BROS_ENTERTAINMENT("Warner Bros.", "Warner Bros. Entertainment", "WB"),
    WARNER_BROS_PICTURES("Warner Bros. Pictures"),

    WILL_PACKER_PRODUCTIONS,
    WINGNUT_FILMS("WingNut Films"),
    WWE_STUDIOS("WWE Studios", "WWE Films"),

    XYZ_FILMS("XYZ Films"),

    ;

    private static String CACHE;

    public static void getResponse(String input, CompletionHandler handler) {
        getJSON(handler);
    }
    private static void getJSON(CompletionHandler handler) {
        if(CACHE != null) {
            handler.handleString(CACHE);
        } else {
            Jsonable.getStaticJSONObject(Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies", new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadJSON(handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final int responseVersion = ResponseVersions.MOVIE_PRODUCTION_COMPANIES.getValue();
                    if(json.getInt("response_version") < responseVersion) {
                        loadJSON(new CompletionHandler() {
                            @Override
                            public void handleJSONObject(JSONObject json) {
                                CACHE = json.toString();
                                handler.handleString(CACHE);
                            }
                        });
                    } else {
                        CACHE = json.toString();
                        handler.handleString(CACHE);
                    }
                }
            });
        }
    }
    private static void loadJSON(CompletionHandler handler) {
        final MovieProductionCompanies[] companies = values();
        final JSONObject companiesJSON = new JSONObject();
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleJSONValue(String key, JSONObject json) {
                companiesJSON.put(key, json);
            }
        };
        ParallelStream.stream(Arrays.asList(companies), company -> ((MovieProductionCompanies) company).getDetails(completionHandler));

        final JSONObject json = new JSONObject();
        final int responseVersion = ResponseVersions.MOVIE_PRODUCTION_COMPANIES.getValue();
        json.put("response_version", responseVersion);
        json.put("imageURLPrefix", getImageURLPrefix());
        json.put("companies", companiesJSON);
        handler.handleJSONObject(json);
    }
    private static String getImageURLPrefix() {
        return "https://upload.wikimedia.org/wikipedia/en/thumb/";
    }
    public void getDetails(CompletionHandler handler) {
        final String originalWikipediaName = wikipediaName.replace("_", " ");
        final String url = "https://en.wikipedia.org/wiki/" + wikipediaName.replace(" ", "_");
        final WikipediaDocument doc = new WikipediaDocument(url);
        final StringBuilder builder = new StringBuilder();
        final List<Element> paragraphs = doc.getConsecutiveParagraphs();
        if(paragraphs != null) {
            boolean isFirst = true;
            for(Element paragraph : paragraphs) {
                String text = LocalServer.removeWikipediaReferences(paragraph.text());
                text = LocalServer.fixEscapeValues(LocalServer.removeWikipediaTranslations(text));
                builder.append(isFirst ? "" : "\n\n").append(text);
                isFirst = false;
            }
        }
        final List<String> images = doc.getImages();
        String imageURL = !images.isEmpty() ? images.get(0) : null;
        if(imageURL != null) {
            final String imageURLPrefix = getImageURLPrefix();
            if(imageURL.startsWith(imageURLPrefix)) {
                imageURL = imageURL.substring(imageURLPrefix.length());
            }
        }

        final JSONObject json = new JSONObject();
        final String[] aliases = getAliases();
        if(aliases != null && aliases.length > 0) {
            json.put("aliases", new JSONArray(aliases));
        }
        json.put("description", builder.toString());
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }

        final EventSources sources = doc.getExternalLinks();
        sources.add(new EventSource("Wikipedia: " + originalWikipediaName, url));

        final JSONObject sourcesJSON = new JSONObject(sources.toString());
        json.put("sources", sourcesJSON);

        handler.handleJSONValue(originalWikipediaName.split(" \\(")[0], json);
    }

    private final String wikipediaName;
    private final String[] aliases;

    MovieProductionCompanies() {
        wikipediaName = LocalServer.toCorrectCapitalization(name()).replace(" ", "_");
        aliases = null;
    }
    MovieProductionCompanies(String wikipediaName, String...aliases) {
        this.wikipediaName = wikipediaName.replace(" ", "_");
        this.aliases = aliases;
    }

    public String getName() {
        return wikipediaName.split(" \\(")[0];
    }
    public String getWikipediaName() {
        return wikipediaName;
    }
    public String[] getAliases() {
        return aliases;
    }
}
