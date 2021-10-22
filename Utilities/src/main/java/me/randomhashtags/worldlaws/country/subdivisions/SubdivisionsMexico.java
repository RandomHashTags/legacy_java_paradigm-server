package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
    public String getPostalCodeAbbreviation() {
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
