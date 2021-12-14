package me.randomhashtags.worldlaws.country.subdivisions.d;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsDominicanRepublic implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_the_Dominican_Republic
    AZUA,
    BAORUCO,
    BARAHONA,
    DAJABON,
    DISTRITO_NACIONAL,
    DUARTE,
    EL_SEIBO,
    ELIAS_PINA,
    ESPAILLAT,
    HATO_MAYOR,
    HERMANAS_MIRABAL,
    INDEPENDENCIA,
    LA_ALTAGRACIA,
    LA_ROMANA,
    LA_VEGA,
    MARIA_TRINIDAD_SANCHEZ,
    MONSENOR_NOUEL,
    MONTE_CRISTI,
    MONTE_PLATA,
    PEDERNALES,
    PERAVIA,
    PUERTO_PLATA,
    SAMANA,
    SAN_CRISTOBAL,
    SAN_JOSE_DE_OCOA,
    SAN_JUAN,
    SAN_PEDRO_DE_MACORIS,
    SANCHEZ_RAMIREZ,
    SANTIAGO,
    SANTIAGO_RODRIGUEZ,
    SANTO_DOMINGO,
    VALVERDE,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.DOMINICAN_REPUBLIC;
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
