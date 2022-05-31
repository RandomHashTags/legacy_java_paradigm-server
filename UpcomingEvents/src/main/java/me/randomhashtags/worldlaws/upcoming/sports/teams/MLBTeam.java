package me.randomhashtags.worldlaws.upcoming.sports.teams;

import me.randomhashtags.worldlaws.LocalServer;

public enum MLBTeam {
    ARIZONA_DIAMONDBACKS,
    ATLANTA_BRAVES,
    BALTIMORE_ORIOLES,
    BOSTON_RED_SOX,
    CHICAGO_CUBS,
    CHICAGO_WHITE_SOX,
    CINCINNATI_REDS,
    CLEVELAND_GUARDIANS,
    COLORADO_ROCKIES,
    DETROIT_TIGERS,
    HOUSTON_ASTROS,
    KANSAS_CITY_ROYALS,
    LOS_ANGELES_ANGELS,
    LOS_ANGELES_DODGERS,
    MIAMI_MARLINS,
    MILWAUKEE_BREWERS,
    MINNESOTA_TWINS,
    NEW_YORK_METS,
    NEW_YORK_YANKEES,
    OAKLAND_ATHLETICS,
    PHILADELPHIA_PHILLIES,
    PITTSBURGH_PIRATES,
    SAN_DIEGO_PADRES,
    SAN_FRANCISCO_GIANTS,
    SEATTLE_MARINERS,
    ST_LOUIS_CARDINALS,
    TAMPA_BAY_RAYS,
    TEXAS_RANGERS,
    TORONTO_BLUE_JAYS,
    WASHINGTON_NATIONALS,
    ;

    public static MLBTeam valueOfInput(String string) {
        for(MLBTeam team : MLBTeam.values()) {
            if(team.getShortName().equalsIgnoreCase(string)) {
                return team;
            }
        }
        return null;
    }

    public String getShortName() {
        switch (this) {
            case ARIZONA_DIAMONDBACKS: return "Diamondbacks";
            case ATLANTA_BRAVES: return "Braves";
            case BALTIMORE_ORIOLES: return "Orioles";
            case BOSTON_RED_SOX: return "Red Sox";
            case CHICAGO_CUBS: return "Cubs";
            case CHICAGO_WHITE_SOX: return "White Sox";
            case CINCINNATI_REDS: return "Reds";
            case CLEVELAND_GUARDIANS: return "Guardians";
            case COLORADO_ROCKIES: return "Rockies";
            case DETROIT_TIGERS: return "Tigers";
            case HOUSTON_ASTROS: return "Astros";
            case KANSAS_CITY_ROYALS: return "Royals";
            case LOS_ANGELES_ANGELS: return "Angels";
            case LOS_ANGELES_DODGERS: return "Dodgers";
            case MIAMI_MARLINS: return "Marlins";
            case MILWAUKEE_BREWERS: return "Brewers";
            case MINNESOTA_TWINS: return "Twins";
            case NEW_YORK_METS: return "Mets";
            case NEW_YORK_YANKEES: return "Yankees";
            case OAKLAND_ATHLETICS: return "Athletics";
            case PHILADELPHIA_PHILLIES: return "Phillies";
            case PITTSBURGH_PIRATES: return "Pirates";
            case SAN_DIEGO_PADRES: return "Padres";
            case SAN_FRANCISCO_GIANTS: return "Giants";
            case SEATTLE_MARINERS: return "Mariners";
            case ST_LOUIS_CARDINALS: return "Cardinals";
            case TAMPA_BAY_RAYS: return "Rays";
            case TEXAS_RANGERS: return "Rangers";
            case TORONTO_BLUE_JAYS: return "Blue Jays";
            case WASHINGTON_NATIONALS: return "Nationals";
            default: return "null";
        }
    }

    public String getWikipediaURL() {
        final String correctCapitalization = LocalServer.toCorrectCapitalization(name()).replace(" ", "_"), suffix = this == TEXAS_RANGERS ? "_(baseball)" : "";
        return "https://en.wikipedia.org/wiki/" + correctCapitalization + suffix;
    }

    private String getLogoURLSuffix() {
        switch (this) {
            case ARIZONA_DIAMONDBACKS: return "8/89/Arizona_Diamondbacks_logo.svg/%quality%px-Arizona_Diamondbacks_logo.svg.png";
            case ATLANTA_BRAVES: return "7/7a/Atlanta_Braves_Insignia.svg/%quality%px-Atlanta_Braves_Insignia.svg.png";
            case BALTIMORE_ORIOLES: return "en/thumb/7/75/Baltimore_Orioles_cap.svg/%quality%px-Baltimore_Orioles_cap.svg.png";
            case BOSTON_RED_SOX: return "f/fe/Boston_Red_Sox_cap_logo.svg/%quality%px-Boston_Red_Sox_cap_logo.svg.png";
            case CHICAGO_CUBS: return "8/89/Chicago_Cubs_Cap_Insignia.svg/%quality%px-Chicago_Cubs_Cap_Insignia.svg.png";
            case CHICAGO_WHITE_SOX: return "c/c1/Chicago_White_Sox.svg/%quality%px-Chicago_White_Sox.svg.png";
            case CINCINNATI_REDS: return "7/71/Cincinnati_Reds_Cap_Insignia.svg/%quality%px-Cincinnati_Reds_Cap_Insignia.svg.png";
            case CLEVELAND_GUARDIANS: return "3/3f/Cleveland_Guardians_cap_logo.svg/%quality%px-Cleveland_Guardians_cap_logo.svg.png";
            case COLORADO_ROCKIES: return "3/31/Colorado_Rockies_logo.svg/%quality%px-Colorado_Rockies_logo.svg.png";
            case DETROIT_TIGERS: return "e/e3/Detroit_Tigers_logo.svg/%quality%px-Detroit_Tigers_logo.svg.png";
            case HOUSTON_ASTROS: return "f/f6/Houston_Astros_cap_logo.svg/%quality%px-Houston_Astros_cap_logo.svg.png";
            case KANSAS_CITY_ROYALS: return "8/88/Kansas_City_Royals_Insignia.svg/%quality%px-Kansas_City_Royals_Insignia.svg.png";
            case LOS_ANGELES_ANGELS: return "8/8b/Los_Angeles_Angels_of_Anaheim.svg/%quality%px-Los_Angeles_Angels_of_Anaheim.svg.png";
            case LOS_ANGELES_DODGERS: return "f/f6/LA_Dodgers.svg/%quality%px-LA_Dodgers.svg.png";
            case MIAMI_MARLINS: return "en/thumb/c/c3/Miami_Marlins_cap_insignia.svg/%quality%px-Miami_Marlins_cap_insignia.svg.png";
            case MILWAUKEE_BREWERS: return "en/thumb/b/b8/Milwaukee_Brewers_logo.svg/%quality%px-Milwaukee_Brewers_logo.svg.png";
            case MINNESOTA_TWINS: return "en/thumb/b/b4/Minnesota_Twins_logo_%28low_res%29.svg/%quality%px-Minnesota_Twins_logo_%28low_res%29.svg.png";
            case NEW_YORK_METS: return "9/98/New_York_Mets_Insignia.svg/%quality%px-New_York_Mets_Insignia.svg.png";
            case NEW_YORK_YANKEES: return "7/70/NewYorkYankees_caplogo.svg/%quality%px-NewYorkYankees_caplogo.svg.png";
            case OAKLAND_ATHLETICS: return "7/7c/Oakland_A%27s_cap_logo.svg/%quality%px-Oakland_A%27s_cap_logo.svg.png";
            case PHILADELPHIA_PHILLIES: return "a/a3/Philadelphia_Phillies_Insignia.svg/%quality%px-Philadelphia_Phillies_Insignia.svg.png";
            case PITTSBURGH_PIRATES: return "8/81/Pittsburgh_Pirates_logo_2014.svg/%quality%px-Pittsburgh_Pirates_logo_2014.svg.png";
            case SAN_DIEGO_PADRES: return "e/e2/SD_Logo_Brown.svg/%quality%px-SD_Logo_Brown.svg.png";
            case SAN_FRANCISCO_GIANTS: return "4/49/San_Francisco_Giants_Cap_Insignia.svg/%quality%px-San_Francisco_Giants_Cap_Insignia.svg.png";
            case SEATTLE_MARINERS: return "en/thumb/8/8a/Seattle_Mariners_Insignia.svg/%quality%px-Seattle_Mariners_Insignia.svg.png";
            case ST_LOUIS_CARDINALS: return "3/39/St._Louis_Cardinals_insignia_logo.svg/%quality%px-St._Louis_Cardinals_insignia_logo.svg.png";
            case TAMPA_BAY_RAYS: return "5/52/Tampa_Bay_Rays_cap_logo.svg/%quality%px-Tampa_Bay_Rays_cap_logo.svg.png";
            case TEXAS_RANGERS: return "e/e2/Texas_Rangers_Insignia.svg/%quality%px-Texas_Rangers_Insignia.svg.png";
            case TORONTO_BLUE_JAYS: return "en/thumb/b/ba/Toronto_Blue_Jays_logo.svg/%quality%px-Toronto_Blue_Jays_logo.svg.png";
            case WASHINGTON_NATIONALS: return "e/e5/Washington_Nationals_Cap_Insig.svg/%quality%px-Washington_Nationals_Cap_Insig.svg.png";
            default: return null;
        }
    }
    public String getLogoURL() {
        final String suffix = getLogoURLSuffix(), type = suffix.startsWith("en") ? "" : "commons/thumb/";
        return "https://upload.wikimedia.org/wikipedia/" + type + suffix;
    }
}
