package me.randomhashtags.worldlaws.location.subdivisions;

import me.randomhashtags.worldlaws.location.SovereignStateSubdivision;

public enum MexicoSubdivisions implements SovereignStateSubdivision {
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
    public String getGovernmentURL() {
        return null;
    }
}
