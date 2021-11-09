package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.LocalServer;

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
    BALBOA_PRODUCTIONS,
    BLUMHOUSE_PRODUCTIONS,
    BRON_STUDIOS("Bron Studios", "Bron Creative", "BRON"),

    CBS_FILMS,
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
    DISNEY_PLUS("Disney+"),
    DISNEY_TELEVISION_ANIMATION,
    LUCASFILM,
    MARVEL_STUDIOS,
    MARVEL_ENTERTAINMENT,
    PIXAR("Pixar", "Pixar Animation Studios"),
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

    FORTIS_FILMS,

    GHOST_HOUSE_PICTURES,
    GK_FILMS("GK Films"),
    GOOD_UNIVERSE,

    HAPPY_MADISON_PRODUCTIONS,

    // HBO
    HBO_FILMS("HBO Films", "HBO Pictures"),
    HBO_MAX("HBO Max"),

    HEYDEY_FILMS("Heyday Films"),
    HULU,

    ILLUMINATION("Illumination (company)", "Illumination Entertainment"),
    IMAGEMOVERS("ImageMovers", "ImageMovers Digital"),
    INTREPID_PICTURES,

    LEGENDARY("Legendary Entertainment", "Legendary Pictures Productions", "Legendary Entertainment", "Legendary Pictures"),
    LIGHTSTORM_ENTERTAINMENT,

    // Lionsgate
    LIONSGATE,
    LIONSGATE_FILMS,
    LIONSGATE_PREMIERE,
    SUMMIT_ENTERTAINMENT,

    // Metro-Goldwyn-Mayer
    METRO_GOLDWYN_MAYER("Metro-Goldwyn-Mayer", "Metro-Goldwyn-Mayer Studios", "MGM"),
    ORION_CLASSICS,
    ORION_PICTURES("Orion Pictures", "Orion Releasing"),

    MILLENNIUM_MEDIA("Millennium Media", "Millennium Films"),
    MRC("MRC (company)", "Media Rights Capital", "MRC Film"),

    NEON("Neon (company)", "NEON"),

    // Netflix
    NETFLIX,
    NETFLIX_ANIMATION,

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

    PARTICIPANT("Participant (company)", "Participant Media"),
    PEACOCK,
    PERFECT_WORLD_PICTURES,
    PLATINUM_DUNES,
    POINT_GREY_PICTURES,

    QED_INTERNATIONAL("QED International"),

    RADIO_SILENCE("Radio Silence Productions", "Radio Silence"),
    RATPAC_DUNE_ENTERTAINMENT("RatPac-Dune Entertainment"),

    SABAN_FILMS,
    SCOTT_FREE_PRODUCTIONS,
    SCREEN_MEDIA_FILMS,
    SEVEN_BUCKS_PRODUCTIONS,
    SHOWTIME,
    SKYDANCE_MEDIA,
    SMOKEHOUSE_PICTURES,
    STX_ENTERTAINMENT("STX Entertainment", "STX Financing", "STX Films"),

    // Sony
    GHOST_CORPS,
    SCREEN_GEMS,
    SONY_PICTURES("Sony Pictures", "Sony Pictures Entertainment", "SPE", "Columbia Pictures", "Columbia Pictures Entertainment"),
    SONY_PICTURES_ANIMATION,
    SONG_PICTURES_CLASSICS,
    SONG_PICTURES_IMAGEWORKS,
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

    UNIVERSAL_PICTURES("Universal Pictures", "Universal", "Universal Studios", "Universal City Studios", "Universal Pictures Home Entertainment"),

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
    WARNER_BROS_ENTERTAINMENT("Warner Bros.", "Warmer Bros. Entertainment", "WB"),
    WARNER_BROS_PICTURES,

    WINGNUT_FILMS("WingNut Films"),
    WWE_STUDIOS("WWE Studios", "WWE Films"),

    XYZ_FILMS("XYZ Films"),

    ;

    private static String CACHE;
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

    public static void getResponse(String input, CompletionHandler handler) {
        if(input.isEmpty()) {
            handler.handleString(getJSON());
        } else {
            final String[] values = input.split("/");
            final String key = values[0];
            switch (key) {
                default:
                    break;
            }
        }
    }
    public static String getJSON() {
        if(CACHE != null) {
            return CACHE;
        } else {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(MovieProductionCompanies company : MovieProductionCompanies.values()) {
                builder.append(isFirst ? "" : ",").append(company.toString());
                isFirst = false;
            }
            builder.append("}");
            final String string = builder.toString();
            CACHE = string;
            return string;
        }
    }

    public String getName() {
        return LocalServer.toCorrectCapitalization(name().replace("_PLUS", "+"));
    }
    public String getWikipediaName() {
        return wikipediaName;
    }
    public String[] getAliases() {
        return aliases;
    }

    private String getAliasesArray() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String alias : aliases) {
            builder.append(isFirst ? "" : ",").append("\"").append(alias).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + getName() + "\":{" +
                (aliases != null ? "\"aliases\":" + getAliasesArray() : "") +
                "}";
    }
}
