package me.randomhashtags.worldlaws.country.subdivisions.m;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsMexico implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/List_of_states_of_Mexico
    AGUASCALIENTES,
    BAJA_CALIFORNIA,
    BAJA_CALIFORNIA_SUR,
    CAMPECHE,
    CHIAPAS,
    CHIHUAHUA,
    COAHUILA,
    COLIMA,
    MEXICO_CITY,
    DURANGO,
    GUANAJUATO,
    GUERRERO,
    HIDALGO,
    JALISCO,
    MEXICO,
    MICHOACAN,
    MORELOS,
    NAYARIT,
    NUEVO_LEON,
    OAXACA,
    PUEBLA,
    QUERETARO,
    QUINTANA_ROO,
    SAN_LUIS_POTOSI,
    SINALOA,
    SONORA,
    TABASCO,
    TAMAULIPAS,
    TLAXCALA,
    VERACRUZ,
    YUCATAN,
    ZACATECAS,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.MEXICO;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case MEXICO:
                return "State of Mexico";
            default:
                return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case MEXICO_CITY: return SubdivisionType.FEDERAL_ENTITIES;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case MICHOACAN: return "Michoacán";
            case NUEVO_LEON: return "Nuevo León";
            case QUERETARO: return "Querétaro";
            case SAN_LUIS_POTOSI: return "San Luis Potosí";
            case YUCATAN: return "Yucatán";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case CHIHUAHUA:
            case HIDALGO:
                return "(" + suffix.toLowerCase() + ")";
            default:
                return "";
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            default: return null;
        }
    }

    @Override
    public String getFlagURL() {
        return null;
    }

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
