package me.randomhashtags.worldlaws.country.subdivisions.b;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsBrazil implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Federative_units_of_Brazil
    ACRE,
    ALAGOAS,
    AMAPA,
    AMAZONAS,
    BAHIA,
    CEARA,
    ESPIRITO_SANTO,
    GOIAS,
    MARANHAO,
    MATO_GROSSO,
    MATO_GROSSO_DO_SUL,
    MINAS_GERAIS,
    PARA,
    PARAIBA,
    PARANA,
    PERNAMBUCO,
    PIAUI,
    RIO_DE_JANEIRO,
    RIO_GRANDE_DO_NORTE,
    RIO_GRANDE_DO_SUL,
    RONDONIA,
    RORAIMA,
    SANTA_CATARINA,
    SAO_PAULO,
    SERGIPE,
    TOCANTINS,

    DISTRITO_FEDERAL,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.BRAZIL;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.STATES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case DISTRITO_FEDERAL: return SubdivisionType.FEDERAL_DISTRICTS;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AMAPA: return "Amapá";
            case CEARA: return "Ceará";
            case ESPIRITO_SANTO: return "Espírito Santo";
            case GOIAS: return "Goiás";
            case MARANHAO: return "Maranhão";
            case PARA: return "Pará";
            case PARAIBA: return "Paraíba";
            case PARANA: return "Paraná";
            case PIAUI: return "Piauí";
            case RONDONIA: return "Rondônia";
            default: return null;
        }
    }

    @Override
    public String getISOAlpha2() {
        switch (this) {
            case ACRE: return "AC";
            case ALAGOAS: return "AL";
            case AMAPA: return "AP";
            case AMAZONAS: return "AM";
            case BAHIA: return "BA";
            case CEARA: return "CE";
            case DISTRITO_FEDERAL: return "DF";
            case ESPIRITO_SANTO: return "ES";
            case GOIAS: return "GO";
            case MARANHAO: return "MA";
            case MATO_GROSSO: return "MT";
            case MATO_GROSSO_DO_SUL: return "MS";
            case MINAS_GERAIS: return "MG";
            case PARA: return "PA";
            case PARAIBA: return "PB";
            case PARANA: return "PR";
            case PERNAMBUCO: return "PE";
            case PIAUI: return "PI";
            case RIO_DE_JANEIRO: return "RJ";
            case RIO_GRANDE_DO_NORTE: return "RN";
            case RIO_GRANDE_DO_SUL: return "RS";
            case RONDONIA: return "RO";
            case RORAIMA: return "RR";
            case SANTA_CATARINA: return "SC";
            case SAO_PAULO: return "SP";
            case SERGIPE: return "SE";
            case TOCANTINS: return "TO";
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
