package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.LocalServer;

public enum SubdivisionType {
    AUTONOMOUS_CITIES,
    AUTONOMOUS_COMMUNITIES,
    AUTONOMOUS_REGIONS,
    AUTONOMOUS_REPUBLICS,
    CANTONS,
    CHARTERED_CITIES,
    CITIES,
    COMMUNES,
    COUNTIES,
    DEPARTMENTS,
    DISTRICTS,
    FEDERAL_DISTRICTS,
    FEDERAL_ENTITIES,
    FEDERAL_TERRITORIES,
    FREELY_ASSOCIATED_STATES,
    GOVERNORATES,
    INCORPORATED_AREAS,
    INDEPENDENT_CITIES,
    INDIGENOUS_PROVINCE,
    METROPOLITAN_CITIES,
    MUNICIPALITIES,
    OBLASTS,
    PARISHES,
    PREFECTURES,
    PROVINCES,
    REGIONS,
    SPECIAL_CITIES,
    SPECIAL_MUNICIPALITIES,
    SPECIAL_SELF_GOVERNING_CITIES,
    STATES,
    SUBDIVISIONS,
    TERRITORIES,
    UNION_TERRITORIES,
    VOIVODESHIPS,
    ;

    public String getSingularName() {
        switch (this) {
            case AUTONOMOUS_CITIES: return "Autonomous City";
            case AUTONOMOUS_COMMUNITIES: return "Autonomous Community";
            case AUTONOMOUS_REGIONS: return "Autonomous Region";
            case AUTONOMOUS_REPUBLICS: return "Autonomous Republic";
            case CANTONS: return "Canton";
            case CHARTERED_CITIES: return "Charter City";
            case CITIES: return "City";
            case COMMUNES: return "Commune";
            case COUNTIES: return "County";
            case DEPARTMENTS: return "Department";
            case DISTRICTS: return "District";
            case FEDERAL_DISTRICTS: return "Federal District";
            case FEDERAL_ENTITIES: return "Federal Entity";
            case FEDERAL_TERRITORIES: return "Federal Territory";
            case FREELY_ASSOCIATED_STATES: return "Freely Associated State";
            case GOVERNORATES: return "Governorate";
            case INCORPORATED_AREAS: return "Incorporated Area";
            case INDEPENDENT_CITIES: return "Independent City";
            case INDIGENOUS_PROVINCE: return "Indigenous Province";
            case METROPOLITAN_CITIES: return "Metropolitan City";
            case MUNICIPALITIES: return "Municipality";
            case OBLASTS: return "Oblast";
            case PARISHES: return "Parish";
            case PREFECTURES: return "Prefecture";
            case PROVINCES: return "Province";
            case REGIONS: return "Region";
            case SPECIAL_CITIES: return "Special City";
            case SPECIAL_MUNICIPALITIES: return "Special Municipality";
            case SPECIAL_SELF_GOVERNING_CITIES: return "Special self-governing City";
            case STATES: return "State";
            case SUBDIVISIONS: return "Subdivision";
            case TERRITORIES: return "Territory";
            case UNION_TERRITORIES: return "Union Territory";
            case VOIVODESHIPS: return "Voivodeship";
            default: return null;
        }
    }
    public String getPluralName() {
        return LocalServer.toCorrectCapitalization(name());
    }
}
