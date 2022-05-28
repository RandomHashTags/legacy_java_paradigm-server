package me.randomhashtags.worldlaws.country.subdivisions.d;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

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
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case DISTRITO_NACIONAL: return "Distro Nacional";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case DAJABON: return "Dajabón";
            case ELIAS_PINA: return "Elías Piña";
            case MARIA_TRINIDAD_SANCHEZ: return "María Trinidad Sánchez";
            case MONSENOR_NOUEL: return "Monseñor Nouel";
            case SAMANA: return "Samaná";
            case SAN_CRISTOBAL: return "San Cristóbal";
            case SAN_JOSE_DE_OCOA: return "San José de Ocoa";
            case SAN_PEDRO_DE_MACORIS: return "San Pedro de Macorís";
            case SANCHEZ_RAMIREZ: return "Sánchez Ramírez";
            case SANTIAGO_RODRIGUEZ: return "Santiago Rodríguez";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case LA_ROMANA:
                return ",_Dominican_Republic";
            case SAN_JUAN:
            case SANTIAGO:
                return "_(Dominican_Republic)";
            default:
                return null;
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
}
