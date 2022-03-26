package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.List;

public enum MovieProductionCompanies {

    _1492_PICTURES("1492 Pictures"),
    _21_LAPS_ENTERTAINMENT("21 Laps Entertainment"),
    _87NORTH_PRODUCTIONS("87North Productions"),

    // Amazon
    AMAZON_STUDIOS,
    LIGHTWORKERS_MEDIA,
    METRO_GOLDWYN_MAYER("Metro-Goldwyn-Mayer", "Metro-Goldwyn-Mayer Studios", "MGM"),
    ORION_CLASSICS,
    ORION_PICTURES("Orion Pictures", "Orion Releasing", "Orion Pictures Corporation"),

    A24("A24 (company)", "A24 Films"),
    AGBO("AGBO", "Gozie AGBO"),
    AGC_STUDIOS("AGC Studios"),
    ALCON_ENTERTAINMENT,
    ALLSPARK_PICTURES("Allspark Pictures", "Hasbro Films", "Hasbro Film Group"),
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
    BLACK_LABEL_MEDIA,
    BLEECKER_STREET("Bleecker Street (company)"),
    BLISS_MEDIA,
    BLUEPRINT_PICTURES,
    BLUMHOUSE_PRODUCTIONS,
    BRON_STUDIOS("Bron Studios", "Bron Creative", "BRON"),

    CBS_FILMS("CBS Films"),
    CENTROPOLIS_ENTERTAINMENT,
    CHERNIN_ENTERTAINMENT,
    CHOCKSTONE_PICTURES,
    CINEDIGM,
    CINEREACH,
    CONSTANTIN_FILM,
    CROSS_CREEK_PICTURES,

    DARK_HORSE_ENTERTAINMENT,
    DI_BONAVENTURA_PICTURES("Di Bonaventura Pictures", "dB Pictures"),
    DNA_FILMS("DNA Films"),
    DREAMCREW("DreamCrew"),

    // Disney
    _20TH_CENTURY_ANIMATION("20th Century Animation"),
    _20TH_CENTURY_STUDIOS("20th Century Studios", "20th Century Fox"),
    DISCOVERY_PLUS("Discovery+"),
    DISNEY_CHANNEL("Disney Channel", "The Disney Channel", "Disney"),
    DISNEY_PLUS("Disney+"),
    DISNEY_TELEVISION_ANIMATION,
    DISNEYNATURE,
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

    // Fox Entertainment
    BENTO_BOX_ENTERTAINMENT,

    GHOST_HOUSE_PICTURES,
    GK_FILMS("GK Films"),
    GOLD_CIRCLE_FILMS("Gold Circle Films", "Gold Circle Entertainment"),
    GOOD_UNIVERSE,
    GRACIE_FILMS,
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
    ROADSIDE_ATTRACTIONS,
    SUMMIT_ENTERTAINMENT,

    MANDEVILLE_FILMS,
    MAXIMUM_EFFORT("Maximum Effort", "Ryan Reynolds"),
    MILLENNIUM_MEDIA("Millennium Media", "Millennium Films"),
    MIRAMAX("Miramax", "Miramax Films"),
    MONKEYPAW_PRODUCTIONS,
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

    public static JSONObjectTranslatable getTypesJSON() {
        JSONObjectTranslatable json = new JSONObjectTranslatable();
        final int responseVersion = ResponseVersions.MOVIE_PRODUCTION_COMPANIES.getValue();
        final JSONObject local = Jsonable.getStaticLocalFileJSONObject(Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies");
        if(local != null) {
            final int previousVersion = local.has("version") ? local.getInt("version") : 0;
            if(previousVersion < responseVersion) {
                json = loadJSON();
            } else {
                for(String key : local.keySet()) {
                    json.put(key, local.get(key));
                }
                final JSONObject localCompaniesJSON = local.getJSONObject("companies");
                final JSONObjectTranslatable companiesJSON = JSONObjectTranslatable.parse(localCompaniesJSON, Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies", localCompaniesJSON.keySet(), originalWikipediaName -> {
                    final MovieProductionCompanyDetails details = MovieProductionCompanyDetails.parse(localCompaniesJSON.getJSONObject(originalWikipediaName));
                    return details.toJSONObject();
                });
                json.put("companies", companiesJSON);
                json.addTranslatedKey("companies");
            }
        } else {
            json = loadJSON();
        }
        return json;
    }
    private static JSONObjectTranslatable loadJSON() {
        final MovieProductionCompanies[] companies = values();
        final JSONObjectTranslatable companiesJSON = new JSONObjectTranslatable();
        new CompletableFutures<MovieProductionCompanies>().stream(Arrays.asList(companies), company -> {
            final JSONObjectTranslatable details = company.getDetails().toJSONObject();
            final String originalWikipediaName = company.getOriginalWikipediaName();
            companiesJSON.put(originalWikipediaName, details);
            companiesJSON.addTranslatedKey(originalWikipediaName);
        });

        final JSONObjectTranslatable json = new JSONObjectTranslatable("companies");
        json.put("version", ResponseVersions.MOVIE_PRODUCTION_COMPANIES.getValue());
        json.put("imageURLPrefix", getImageURLPrefix());
        json.put("companies", companiesJSON);
        Jsonable.setFileJSONObject(Folder.UPCOMING_EVENTS_MOVIES, "productionCompanies", json);
        return json;
    }
    private static String getImageURLPrefix() {
        return "https://upload.wikimedia.org/wikipedia/";
    }
    private String getOriginalWikipediaName() {
        return wikipediaName.replace("_", " ");
    }
    public MovieProductionCompanyDetails getDetails() {
        final String originalWikipediaName = getOriginalWikipediaName();
        final String url = "https://en.wikipedia.org/wiki/" + wikipediaName.replace(" ", "_");
        final WikipediaDocument doc = new WikipediaDocument(url);
        final StringBuilder description = new StringBuilder();
        final List<Element> paragraphs = doc.getConsecutiveParagraphs();
        if(paragraphs != null) {
            boolean isFirst = true;
            for(Element paragraph : paragraphs) {
                final String text = paragraph.text();
                description.append(isFirst ? "" : "\n\n").append(text);
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

        EventSources sources = doc.getExternalLinks();
        if(sources == null) {
            sources = new EventSources();
        }
        sources.add(new EventSource("Wikipedia: " + originalWikipediaName, url));
        return new MovieProductionCompanyDetails(aliases, description.toString(), imageURL, sources);
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
