package me.randomhashtags.worldlaws.info.list;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.availability.tech.AppleFeatureAvailability;
import me.randomhashtags.worldlaws.info.service.CountryServiceValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.location.CountryInformationType;
import me.randomhashtags.worldlaws.location.Location;
import me.randomhashtags.worldlaws.location.TerritoryAbbreviations;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Flyover implements AppleFeatureAvailability {
    INSTANCE;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_APPLE_MAPS_FLYOVER;
    }

    @Override
    public CountryInformationType getInformationType() {
        return CountryInformationType.SERVICES;
    }

    @Override
    public void loadData(CompletionHandler handler) {
        final String fileName = getInfo().getTitle();
        getJSONObject(getFileType(), fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements elements = getSectionElements("maps-flyover");
                final HashMap<String, String> americanTerritories = TerritoryAbbreviations.getAmericanTerritories();
                final HashMap<String, HashMap<String, List<FlyoverObj>>> flyoversMap = new HashMap<>();
                for(Element element : elements) {
                    final String[] values = element.text().split(", ");
                    final int max = values.length;
                    if(max == 2) {
                        String countryBackendID = values[1].replace("England", "United Kingdom");
                        final String territory;
                        if(americanTerritories.containsKey(countryBackendID)) {
                            territory = americanTerritories.get(countryBackendID);
                            countryBackendID = "United States";
                        } else {
                            territory = null;
                        }
                        countryBackendID = countryBackendID.toLowerCase().replace(" ", "");
                        String city = values[0].split("/")[0];
                        final FlyoverObj flyoverObj = valueOf(countryBackendID, territory, city);
                        if(flyoverObj != null) {
                            flyoversMap.putIfAbsent(countryBackendID, new HashMap<>());
                            flyoversMap.get(countryBackendID).putIfAbsent(territory, new ArrayList<>());
                            flyoversMap.get(countryBackendID).get(territory).add(flyoverObj);
                        }
                    }
                }
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirstCountry = true;
                for(Map.Entry<String, HashMap<String, List<FlyoverObj>>> map : flyoversMap.entrySet()) {
                    final String country = map.getKey();
                    final HashMap<String, List<FlyoverObj>> territoriesMap = map.getValue();
                    final StringBuilder countryBuilder = new StringBuilder("{");
                    boolean isFirstTerritory = true;
                    for(Map.Entry<String, List<FlyoverObj>> territoryMap : territoriesMap.entrySet()) {
                        final String territory = territoryMap.getKey();
                        final List<FlyoverObj> flyovers = territoryMap.getValue();
                        countryBuilder.append(isFirstTerritory ? "" : ",").append("\"").append(territory).append("\":{");
                        boolean isFirstFlyover = true;
                        for(FlyoverObj flyoverObj : flyovers) {
                            final String string = flyoverObj.toString();
                            countryBuilder.append(isFirstFlyover ? "" : ",").append(string);
                            isFirstFlyover = false;
                        }
                        countryBuilder.append("}");
                        isFirstTerritory = false;
                    }
                    countryBuilder.append("}");
                    final String string = new CountryServiceValue(Flyover.INSTANCE, countryBuilder.toString()).toString();
                    builder.append(isFirstCountry ? "" : ",").append("\"").append(country).append("\":{").append(string).append("}");
                    isFirstCountry = false;
                }
                builder.append("}");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(json.toString());
            }
        });
    }

    private FlyoverObj valueOf(String countryBackendID, String territory, String city) {
        final String targetCountry = countryBackendID.toLowerCase().replace(" ", "");
        final Location location = getCoordinates(targetCountry, territory != null ? territory.toLowerCase() : null, city.toLowerCase());
        return location != null ? new FlyoverObj(countryBackendID, territory, city, location) : null;
    }
    private Location getCoordinates(String countryBackendID, String territory, String city) {
        // https://www.findlatitudeandlongitude.com
        switch (countryBackendID) {
            case "australia": return getAustraliaLocation(city);
            case "austria": return getAustriaLocation(city);
            case "belgium": return getBelgiumLocation(city);
            case "canada": return getCanadaLocation(city);
            case "czechrepublic": return null;
            case "denmark": return getDenmarkLocation(city);
            case "finland": return getFinlandLocation(city);
            case "france": return getFranceLocation(city);
            case "germany": return getGermanyLocation(city);
            case "hungary": return getHungaryLocation(city);
            case "ireland": return getIrelandLocation(city);
            case "italy": return getItalyLocation(city);
            case "japan": return getJapanLocation(city);
            case "mexico": return getMexicoLocation(city);
            case "netherlands": return getNetherlandsLocation(city);
            case "newzealand": return getNewZealandLocation(city);
            case "portugal": return null;
            case "southafrica": return null;
            case "spain": return getSpainLocation(city);
            case "sweden": return getSwedenLocation(city);
            case "switzerland": return getSwitzerlandLocation(city);
            case "taiwan": return null;
            case "unitedstates": return getUnitedStatesLocation(territory, city);
            default: return null;
        }
    }

    private Location getAustraliaLocation(String city) {
        switch (city.toLowerCase()) {
            case "adelaide": return new Location(-34.928889, 138.601111);
            case "canberra": return new Location(-35.293056, 149.126944);
            case "gold coast": return new Location(-28.016667, 153.4);
            case "melbourne": return new Location(-37.813611, 144.963056);
            case "newcastle": return new Location(-32.916667, 151.75);
            case "perth": return new Location(-31.952222, 115.858889);
            case "sydney": return new Location(-33.865, 151.209444);
            case "twelve apostles": return new Location(-38.665833, 143.104444);
            default: return null;
        }
    }
    private Location getAustriaLocation(String city) {
        switch (city) {
            case "graz": return new Location(47.070714, 15.439504);
            case "linz": return new Location(48.30694, 14.28583);
            case "salzburg": return new Location(47.80949, 13.05501);
            default: return null;
        }
    }
    private Location getBelgiumLocation(String city) {
        switch (city) {
            case "antwerp": return new Location(54.3833, 30.4061);
            case "bruges": return new Location(51.2, 3.2);
            case "brussels": return new Location(50.849492, 4.350589);
            case "ghent": return new Location(51.05, 3.716667);
            default: return null;
        }
    }
    private Location getCanadaLocation(String city) {
        switch (city.toLowerCase()) {
            case "calgary": return new Location(51.05, -114.066667);
            case "montréal": return new Location(45.508889, -73.561667);
            case "toronto": return new Location(43.741667, -79.373333);
            case "vancouver": return new Location(49.260833, -123.113889);
            default: return null;
        }
    }
    private Location getDenmarkLocation(String city) {
        switch (city.toLowerCase()) {
            case "aarhus": return new Location(56.15, 10.216667);
            case "copenhagen": return new Location(55.676111, 12.568333);
            case "odense": return new Location(55.395833, 10.388611);
            default: return null;
        }
    }
    private Location getFinlandLocation(String city) {
        switch (city.toLowerCase()) {
            case "helsinki": return new Location(60.170833, 24.9375);
            default: return null;
        }
    }
    private Location getFranceLocation(String city) {
        switch (city) {
            case "aix-en-provence": return new Location(43.529742, 5.447427);
            case "ajaccio": return new Location(41.918648, 8.736879);
            case "amiens": return new Location(42.90416, 16.855668);
            case "angers": return new Location(47.478419, -0.563166);
            case "annecy": return new Location(45.899247, 6.129384);
            case "arcachon": return new Location(44.652297, -1.178502);
            case "avignon": return new Location(43.949317, 4.805528);
            case "bastia": return new Location(42.697283, 9.450881);
            case "besancon": return new Location(41.89353, -8.76253);
            case "beziers": return new Location(43.344233, 3.215795);
            case "biarritz": return new Location(43.483152, -1.558626);
            case "bonifacio": return new Location(41.368936, 9.20472);
            case "bordeaux": return new Location(44.837789, -0.57918);
            case "calvi": return new Location(42.547243, 8.727721);
            case "cannes": return new Location(43.552847, 7.017369);
            case "carcassonne": return new Location(43.212161, 2.353663);
            case "chambord": return new Location(47.614158, 1.516878);
            case "chateauneuf-du-pape": return new Location(43.68, 6.972);
            case "chenonceaux": return new Location(47.330742, 1.065239);
            case "clermont-ferrand": return new Location(45.777222, 3.087025);
            case "collioure": return new Location(42.526752, 3.084631);
            case "corte": return new Location(42.264494, 9.091093);
            case "dijon": return new Location(47.322047, 5.04148);
            case "gorges de i'ardeche": return new Location(44.369652, 4.068301);
            case "gorges du verdon": return new Location(43.874544, 6.50925);
            case "la rochelle": return new Location(46.158022, -1.153595);
            case "le mans": return new Location(48.007716, 0.198579);
            case "lens": return new Location(50.42893, 2.83183);
            case "lille": return new Location(50.62925, 3.057256);
            case "limoges": return new Location(45.833619, 1.261105);
            case "lyon": return new Location(45.764043, 4.835659);
            case "marseille": return new Location(43.297612, 5.381042);
            case "millau": return new Location(44.100575, 3.077801);
            case "mont-saint-michel": return new Location(48.635826, -1.510725);
            case "montpellier": return new Location(43.608176, 3.879446);
            case "nantes": return new Location(47.216842, -1.556745);
            case "nice": return new Location(43.696036, 7.265592);
            case "nimes": return new Location(43.83459, 4.360864);
            case "paris": return new Location(48.856614, 2.352222);
            case "perpignan": return new Location(42.701574, 2.89415);
            case "porto-vecchio": return new Location(41.560233, 9.335506);
            case "propriano": return new Location(41.676074, 8.90458);
            case "reims": return new Location(49.258329, 4.031696);
            case "remoulins": return new Location(43.939213, 4.573472);
            case "rennes": return new Location(48.113475, -1.675708);
            case "saint-etienne": return new Location(45.439695, 4.387178);
            case "saint-tropez": return new Location(43.272993, 6.639163);
            case "strasbourg": return new Location(48.583148, 7.747882);
            case "toulouse": return new Location(43.604652, 1.44420);
            default: return null;
        }
    }
    private Location getGermanyLocation(String city) {
        switch (city) {
            case "augsburg": return new Location(48.371441, 10.898255);
            case "berlin": return new Location(52.524268, 13.40629);
            case "bielefeld": return new Location(52.023062, 8.533072);
            case "braunschweig": return new Location(52.264148, 10.52638);
            case "bremen": return new Location(53.074981, 8.807081);
            case "cologne": return new Location(50.937531, 6.960279);
            case "dresden": return new Location(51.050991, 13.733634);
            case "hamburg": return new Location(53.553815, 9.991575);
            case "hanover": return new Location(52.372068, 9.735686);
            case "karlsruhe": return new Location(49.009148, 8.379944);
            case "kiel": return new Location(54.323293, 10.122765);
            case "leipzig": return new Location(51.339673, 12.371364);
            case "mannheim": return new Location(49.484677, 8.476724);
            case "munich": return new Location(48.136607, 11.577085);
            case "munster": return new Location(51.960665, 7.626135);
            case "neuschwanstein castle": return new Location(47.557574, 10.7498);
            case "nuremberg": return new Location(49.45052, 11.08048);
            case "stuttgart": return new Location(48.775846, 9.182932);
            default: return null;
        }
    }
    private Location getHungaryLocation(String city) {
        switch (city) {
            case "budapest": return new Location(47.498406, 19.04075);
            default: return null;
        }
    }
    private Location getIrelandLocation(String city) {
        switch (city) {
            case "belfast": return null; // northern ireland
            case "cliffs of moher": return new Location(52.97188, -9.42651);
            case "cork": return new Location(51.897866, -8.471094);
            case "dublin": return new Location(53.344104, -6.267494);
            default: return null;
        }
    }
    private Location getItalyLocation(String city) {
        switch (city) {
            case "ancona": return new Location(43.615844, 13.518739);
            case "bari": return new Location(41.117143, 16.871872);
            case "babbio": return null;
            case "cittadella": return new Location(45.648826, 11.783643);
            case "florence": return new Location(43.771033, 11.248001);
            case "genoa": return new Location(44.407062, 8.933989);
            case "messina": return new Location(38.192332, 15.555523);
            case "milan": return new Location(45.465422, 9.185924);
            case "naples": return new Location(40.851775, 14.268124);
            case "padua": return new Location(45.406435, 11.876761);
            case "paestum": return new Location(40.406567, 14.997827);
            case "palermo": return new Location(38.115688, 13.361267);
            case "parma": return new Location(44.807866, 10.329548);
            case "pavia": return new Location(45.185888, 9.156563);
            case "perugia": return new Location(43.110701, 12.389172);
            case "reggio di calabria": return new Location(38.111301, 15.647291);
            case "rome": return new Location(41.902784, 12.496366);
            case "sanremo": return new Location(43.818406, 7.778422);
            case "syracuse": return new Location(37.075474, 15.286586);
            case "taormina": return new Location(37.853067, 15.287916);
            case "treviso": return new Location(45.666286, 12.242072);
            case "turin": return new Location(45.070562, 7.686619);
            case "venice": return new Location(45.440847, 12.315515);
            default: return null;
        }
    }
    private Location getJapanLocation(String city) {
        switch (city) {
            case "aizuwakamatsu": return new Location(37.494761, 139.92981);
            case "akita": return new Location(39.720008, 140.102564);
            case "aomori": return new Location(40.822072, 140.747365);
            case "fukuyama": return new Location(34.485899, 133.362313);
            case "gifu": return new Location(35.423298, 136.760654);
            case "hagi": return new Location(34.405449, 131.400816);
            case "hakodate": return new Location(41.768793, 140.72881);
            case "hamamatsu": return new Location(34.710834, 137.726126);
            case "hikone": return new Location(35.274461, 136.259623);
            case "himeiji": return null;
            case "hiroshima": return new Location(34.385203, 132.455293);
            case "izumo": return new Location(35.367035, 132.754682);
            case "izushi": return new Location(33.596361, 132.499015);
            case "kanazawa": return new Location(36.561325, 136.656205);
            case "kitakyushu": return new Location(33.883409, 130.875216);
            case "koya": return new Location(36.146291, 139.778017);
            case "kumamoto": return new Location(32.8031, 130.707891);
            case "kyoto": return new Location(35.011636, 135.768029);
            case "matsumoto": return new Location(36.238038, 137.972034);
            case "nagasaki": return new Location(32.750286, 129.877667);
            case "nagoya": return new Location(35.181446, 136.906398);
            case "naha": return new Location(26.22853, 127.68911);
            case "nara": return new Location(34.685087, 135.805);
            case "niigata": return new Location(37.916192, 139.036413);
            case "odawara": return new Location(35.264564, 139.152154);
            case "okayama": return new Location(34.655146, 133.919502);
            case "osaka": return new Location(34.693738, 135.502165);
            case "sakai": return new Location(34.573262, 135.482998);
            case "sapporo": return new Location(43.062096, 141.354376);
            case "sendai": return new Location(38.268215, 140.869356);
            case "shizuoka": return new Location(34.975562, 138.38276);
            case "takahashi": return new Location(34.791298, 133.616488);
            case "takamatsu": return new Location(34.342788, 134.046574);
            case "tojinbo": return new Location(35.710794, 139.775156);
            case "tokyo": return new Location(35.689488, 139.691706);
            case "toyama": return new Location(36.695952, 137.213677);
            case "tsu": return new Location(34.718596, 136.505698);
            case "tsunoshima": return new Location(37.688, 139.445777);
            case "yokkaichi": return new Location(34.965157, 136.624485);
            default: return null;
        }
    }
    private Location getMexicoLocation(String city) {
        switch (city) {
            case "acapulco": return new Location(16.853109, -99.823653);
            case "cabo san lucas": return new Location(22.890533, -109.916737);
            case "chichen itza": return new Location(20.678333, -88.568889);
            case "cuernavaca": return new Location(18.93402, -99.231483);
            case "culiacan": return new Location(24.804113, -107.387892);
            case "ensenada": return new Location(31.866743, -116.596371);
            case "guadalajara": return new Location(20.67359, -103.343803);
            case "guaymas": return new Location(27.913961, -110.902075);
            case "hermosillo": return new Location(29.089186, -110.96133);
            case "la paz": return new Location(24.142641, -110.312753);
            case "loreto": return new Location(26.011756, -111.347753);
            case "mazatlan": return new Location(23.249415, -106.411142);
            case "mexicali": return new Location(32.64557, -115.445342);
            case "oaxaca": return new Location(17.066944, -96.720278);
            case "puebla": return new Location(19.041297, -98.2062);
            case "puerto vallarta": return new Location(20.622018, -105.228457);
            case "teotihuacan": return new Location(19.353857, -99.241301);
            case "tijuana": return new Location(32.533489, -117.018204);
            case "tulum": return new Location(20.211419, -87.46535);
            default: return null;
        }
    }
    private Location getNetherlandsLocation(String city) {
        switch (city) {
            case "eindhoven": return new Location(51.441642, 5.469723);
            case "rotterdam": return new Location(51.924216, 4.481776);
            case "utrecht": return new Location(52.090142, 5.109665);
            default: return null;
        }
    }
    private Location getNewZealandLocation(String city) {
        switch (city) {
            case "auckland": return new Location(-36.84846, 174.763332);
            case "christchurch": return new Location(-43.531637, 172.636645);
            case "dunedin": return new Location(-45.878761, 170.502798);
            case "nelson": return new Location(-41.26904, 173.284041);
            case "queenstown": return new Location(-45.031162, 168.662644);
            case "wellington": return new Location(-41.28646, 174.776236);
            default: return null;
        }
    }
    private Location getSpainLocation(String city) {
        switch (city) {
            case "a coruna": return null;
            case "alicante": return null;
            case "almeria": return null;
            case "badajoz": return null;
            case "barcelona": return null;
            case "caceres": return null;
            case "cadiz": return null;
            case "cordoba": return null;
            case "gijon": return null;
            case "granada": return null;
            case "huelva": return null;
            case "infantes": return null;
            case "jerez de la frontera": return null;
            case "leon": return null;
            case "lugo": return null;
            case "madrid": return null;
            case "murcia": return null;
            case "pamplona": return null;
            case "salamanca": return null;
            case "san sebastian": return null;
            case "seville": return null;
            case "valencia": return null;
            case "valladolid": return null;
            case "vigo": return null;
            case "zaragoza": return null;
            default: return null;
        }
    }
    private Location getSwedenLocation(String city) {
        switch (city.toLowerCase()) {
            case "gothenburg": return new Location(57.7, 11.966667);
            case "helsingborg": return new Location(56.05, 12.716667);
            case "stockholm": return new Location(59.329444, 18.068611);
            case "visby": return new Location(57.634722, 18.299167);
            default: return null;
        }
    }
    private Location getSwitzerlandLocation(String city) {
        switch (city.toLowerCase()) {
            case "basel": return new Location(47.554722, 7.590556);
            case "berne": return new Location(46.948056, 7.4475);
            default: return null;
        }
    }
    private Location getUnitedStatesLocation(String territory, String city) {
        switch (territory) {
            case "alabama":
                switch (city) {
                    case "mobile": return new Location(30.694357, -88.043054);
                    default: return null;
                }
            case "arkansas":
                switch (city) {
                    case "royal gorge": return new Location(-16.85097, 145.646738);
                    default: return null;
                }
            case "arizona":
                switch (city) {
                    case "grand canyon": return new Location(36.018629, -112.123547);
                    case "meteor crater": return new Location(35.028072, -111.023197);
                    case "monument valley": return new Location(36.998029, -110.098457);
                    case "phoenix": return new Location(33.448377, -112.074037);
                    case "tucson": return new Location(32.221743, -110.926479);
                    default: return null;
                }
            case "california":
                switch (city) {
                    case "bakersfield": return new Location(35.373292, -119.018713);
                    case "catalina island": return new Location(33.387886, -118.41631);
                    case "fresno": return new Location(36.747727, -119.772366);
                    case "lake tahoe": return new Location(39.127256, -120.020296);
                    case "lassen volcanic national park": return new Location(40.49766, -121.420655);
                    case "los angeles": return new Location(34.052234, -118.243685);
                    case "modesto": return new Location(37.639097, -120.996878);
                    case "oakland": return new Location(37.804364, -122.271114);
                    case "pinnacles national park": return new Location(36.493686, -121.146509);
                    case "porterville": return new Location(36.06523, -119.016768);
                    case "sacramento": return new Location(38.581572, -121.4944);
                    case "san diego": return new Location(32.715329, -117.15725);
                    case "san francisco": return new Location(37.77493, -122.419416);
                    case "san jose": return new Location(37.339386, -121.894956);
                    case "stockton": return new Location(37.957702, -121.29078);
                    case "visalia": return new Location(36.330228, -119.292059);
                    case "yosemite national park": return new Location(37.743333, -119.575833);
                    default: return null;
                }
            case "colorado":
                switch (city) {
                    case "denver": return new Location(39.739154, -104.984703);
                    default: return null;
                }
            case "florida":
                switch (city) {
                    case "key west": return new Location(24.555703, -81.782591);
                    case "miami": return new Location(25.788969, -80.226439);
                    case "pensacola": return new Location(30.421309, -87.216915);
                    case "tallahassee": return new Location(30.438256, -84.280733);
                    default: return null;
                }
            case "georgia":
                switch (city) {
                    case "atlanta": return new Location(33.748995, -84.387982);
                    default: return null;
                }
            case "hawaii":
                switch (city) {
                    case "kahului": return new Location(20.889335, -156.472947);
                    case "kapaa": return new Location(22.088139, -159.337982);
                    case "lahaina": return new Location(20.878333, -156.6825);
                    case "lihue": return new Location(21.981111, -159.371111);
                    case "oahu": return new Location(21.438912, -158.000057);
                    case "wailua": return new Location(22.047566, -159.335126);
                    default: return null;
                }
            case "idaho":
                switch (city) {
                    case "boise": return new Location(43.615019, -116.202314);
                    default: return null;
                }
            case "illinois":
                switch (city) {
                    case "chicago": return new Location(41.878114, -87.629798);
                    default: return null;
                }
            case "indiana":
                switch (city) {
                    case "indianapolis": return new Location(39.768403, -86.158068);
                    case "south bend": return new Location(41.683381, -86.250007);
                    default: return null;
                }
            case "kansas":
                switch (city) {
                    case "wichita": return new Location(37.687176, -97.330053);
                    default: return null;
                }
            case "kentucky":
                switch (city) {
                    case "louisville": return new Location(38.252665, -85.758456);
                    default: return null;
                }
            case "louisiana":
                switch (city) {
                    case "baton rouge": return new Location(30.458283, -91.14032);
                    case "new orleans": return new Location(29.951066, -90.071532);
                    default: return null;
                }
            case "maine":
                switch (city) {
                    case "portland": return new Location(43.661471, -70.255326);
                    default: return null;
                }
            case "maryland":
                switch (city) {
                    case "baltimore": return new Location(39.290385, -76.612189);
                    default: return null;
                }
            case "massachusetts":
                switch (city) {
                    case "boston": return new Location(42.360083, -71.05888);
                    case "foxborough": return new Location(42.065381, -71.247825);
                    case "martha’s vineyard": return new Location(41.454279, -70.60364);
                    case "salem": return new Location(42.51954, -70.896716);
                    case "springfield": return new Location(42.101483, -72.589811);
                    default: return null;
                }
            case "michigan":
                switch (city) {
                    case "detroit": return new Location(42.331427, -83.045754);
                    default: return null;
                }
            case "minnesota":
                switch (city) {
                    case "minneapolis": return new Location(44.977753, -93.265011);
                    default: return null;
                }
            case "missouri":
                switch (city) {
                    case "springfield": return new Location(37.208957, -93.292299);
                    case "st. louis": return new Location(38.646991, -90.224967);
                    default: return null;
                }
            case "nebraska":
                switch (city) {
                    case "omaha": return new Location(41.252363, -95.997988);
                    default: return null;
                }
            case "nevada":
                switch (city) {
                    case "las vegas": return new Location(36.169941, -115.13983);
                    default: return null;
                }
            case "new mexico":
                switch (city) {
                    case "albuquerque": return new Location(35.085334, -106.605553);
                    default: return null;
                }
            case "new york":
                switch (city) {
                    case "albany": return new Location(42.652579, -73.756232);
                    case "buffalo": return new Location(42.886447, -78.878369);
                    case "new york": return new Location(40.705631, -73.978004);
                    case "poughkeepsie": return new Location(41.700371, -73.92097);
                    case "rochester": return new Location(43.16103, -77.610922);
                    default: return null;
                }
            case "north carolina":
                switch (city) {
                    case "greensboro": return new Location(36.072635, -79.791975);
                    case "raleigh": return new Location(35.77959, -78.638179);
                    default: return null;
                }
            case "ohio":
                switch (city) {
                    case "akron": return new Location(41.081445, -81.519005);
                    case "cincinnati": return new Location(39.103118, -84.51202);
                    case "cleveland": return new Location(41.499495, -81.695409);
                    case "columbus": return new Location(39.961176, -82.998794);
                    case "toledo": return new Location(41.663938, -83.555212);
                    default: return null;
                }
            case "oklahoma":
                switch (city) {
                    case "oklahoma city": return new Location(35.46756, -97.516428);
                    case "tulsa": return new Location(36.153982, -95.992775);
                    default: return null;
                }
            case "oregon":
                switch (city) {
                    case "portland": return new Location(45.512231, -122.658719);
                    default: return null;
                }
            case "pennsylvania":
                switch (city) {
                    case "allentown": return new Location(40.608431, -75.490183);
                    case "philadelphia": return new Location(39.952584, -75.165222);
                    case "pittsburgh": return new Location(40.440625, -79.995886);
                    default: return null;
                }
            case "rhode island":
                switch (city) {
                    case "providence": return new Location(41.823989, -71.412834);
                    default: return null;
                }
            case "south carolina":
                switch (city) {
                    case "charleston": return new Location(32.776566, -79.930922);
                    case "columbia": return new Location(34.00071, -81.034814);
                    default: return null;
                }
            case "south dakota":
                switch (city) {
                    case "mt. rushmore": return new Location(43.880217, -103.449176);
                    case "rapid city": return new Location(44.080543, -103.231015);
                    default: return null;
                }
            case "tennessee":
                switch (city) {
                    case "memphis": return new Location(35.149534, -90.04898);
                    case "nashville": return new Location(36.166667, -86.783333);
                    default: return null;
                }
            case "texas":
                switch (city) {
                    case "amarillo": return new Location(35.221997, -101.831297);
                    case "austin": return new Location(30.267153, -97.743061);
                    case "dallas": return new Location(32.776664, -96.796988);
                    case "houston": return new Location(29.760193, -95.36939);
                    case "san antonio": return new Location(29.424122, -98.493628);
                    default: return null;
                }
            case "utah":
                switch (city) {
                    case "arches national park": return new Location(38.733081, -109.592514);
                    case "lake powell": return new Location(37.620937, -109.486468);
                    case "zion national park": return new Location(37.208573, -112.982125);
                    default: return null;
                }
            case "washington":
                switch (city) {
                    case "seattle": return new Location(47.60621, -122.332071);
                    default: return null;
                }
            case "wisconsin":
                switch (city) {
                    case "green bay": return new Location(44.513319, -88.013296);
                    case "milwaukee": return new Location(43.038903, -87.906474);
                    default: return null;
                }
            case "wyoming":
                switch (city) {
                    case "cheyenne": return new Location(41.139981, -104.820246);
                    case "devil’s tower": return new Location(44.588333, -104.698333);
                    default: return null;
                }
            default: return null;
        }
    }
}
