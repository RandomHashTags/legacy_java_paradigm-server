package me.randomhashtags.worldlaws;

import java.util.Arrays;
import java.util.HashSet;

public enum ClientEmoji {
    SPORT_ARCHERY(
            "Archery"
    ),
    SPORT_BADMINTON(
            "Badminton"
    ),
    SPORT_BASEBALL(
            "Baseball"
    ),
    SPORT_BASKETBALL(
            "Basketball",
            "Wheelchair Basketball"
    ),
    SPORT_BOWLING(
            "Bowling",
            "Ten-pin Bowling",
            "Nine-pin Bowling"
    ),
    SPORT_BOXING(
            "Boxing",
            "Amateur Boxing",
            "Professional Boxing"
    ),
    SPORT_CANOEING(
            "Canoeing",
            "Canoe Sprint",
            "Canoe Marathon",
            "Wildwater Canoeing"
    ),
    SPORT_CAR_RACING(
            "Racing",
            "Stock Car Racing",
            "Formula Racing",
            "Motorsport",
            "Rallying",
            "Off-Road Racing",
            "Indy Car Racing",
            "IndyCar",
            "Rallycross",
            "Formula One"
    ),
    SPORT_CHESS(
            "Chess"
    ),
    SPORT_CLIMBING(
            "Climbing",
            "Ice Climbing",
            "Sport Climbing"
    ),
    SPORT_CRICKET(
            "Cricket",
            "Test Cricket"
    ),
    SPORT_CYCLING(
            "Cycling"
    ),
    SPORT_DARTS(
            "Darts"
    ),
    SPORT_FIELD_HOCKEY(
            "Field Hockey"
    ),
    SPORT_FISHING(
            "Fishing"
    ),
    SPORT_FENCING(
            "Fencing"
    ),
    SPORT_FOOTBALL(
            "American Football",
            "Canadian Football"
    ),
    SPORT_GOLF(
            "Golf"
    ),
    SPORT_GYMNASTICS(
            "Gymnastics",
            "Acrobatic Gymnastics",
            "Aerobic Gymnastics",
            "Rhythmic Gymnastics",
            "Trampoline Gymnastics",
            "Aesthetic Group Gymnastics"
    ),
    SPORT_HORSE_RACING(
            "Horse Racing"
    ),
    SPORT_ICE_HOCKEY(
            "Ice Hockey",
            "Para Ice Hockey"
    ),
    SPORT_JUDO(
            "Judo"
    ),
    SPORT_KARATE(
            "Karate"
    ),
    SPORT_LACROSSE(
            "Lacrosse"
    ),
    SPORT_MOTORCYCLE_RACING(
            "Motorcycle Racing"
    ),
    SPORT_OLYMPICS(
            "Olympics"
    ),
    SPORT_ORIENTEERING(
            "Orienteering"
    ),
    SPORT_POOL(
            "Pool",
            "Nine-ball Pool"
    ),
    SPORT_RUGBY(
            "Rugby",
            "Rugby Union",
            "Rugby Sevens",
            "Rugby League",
            "Wheelchair Rugby League"
    ),
    SPORT_RUNNING(
            "Marathon",
            "Mountain Running",
            "Ultra Running",
            "Ultramarathon",
            "Cross Country Running"
    ),
    SPORT_SKATING(
            "Skating",
            "Synchronized Skating",
            "Figure Skating",
            "Short Track Speed Skating",
            "Ice Skating"
    ),
    SPORT_SKIING(
            "Skiing",
            "Speed Skiing",
            "Nordic Skiing",
            "Alpine Skiing",
            "Telemarking",
            "Telemark Skiing"
    ),
    SPORT_SOFTBALL(
            "Softball"
    ),
    SPORT_SOCCER(
            "Soccer",
            "Association Football",
            "Beach Soccer"
    ),
    SPORT_SURFING(
            "Surfing"
    ),
    SPORT_SWIMMING(
            "Swimming",
            "Para Swimming",
            "Finswimming",
            "Artistic Swimming",
            "Open Water Swimming"
    ),
    SPORT_TABLE_TENNIS(
            "Table Tennis",
            "Para Table Tennis",
            "Ping Pong"
    ),
    SPORT_TAEKWONDO(
            "Taekwondo"
    ),
    SPORT_TENNIS(
            "Tennis",
            "Beach Tennis"
    ),
    SPORT_VOLLEYBALL(
            "Beach Volleyball",
            "Volleyball"
    ),
    SPORT_WATER_POLO(
            "Water Polo"
    ),
    SPORT_WEIGHT_LIFTING(
            "Weight Lifting",
            "Weightlifting",
            "Powerlifting",
            "Para Powerlifting"
    ),
    SPORT_WRESTLING(
            "Wrestling"
    ),
    ;

    public static ClientEmoji valueOfString(String input) {
        for(ClientEmoji emoji : ClientEmoji.values()) {
            if(emoji.matchesAlias(input)) {
                return emoji;
            }
        }
        return null;
    }

    private final String identifier;
    private final HashSet<String> aliases;

    ClientEmoji(String...aliases) {
        this.identifier = name().toLowerCase();
        this.aliases = new HashSet<>(Arrays.asList(aliases));
    }

    public String getIdentifier() {
        return identifier;
    }
    public HashSet<String> getAliases() {
        return aliases;
    }
    private boolean matchesAlias(String input) {
        for(String alias : aliases) {
            if(alias.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
}
