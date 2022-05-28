package me.randomhashtags.worldlaws.country.subdivisions.a;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsArgentina implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Argentina
    AUTONOMOUS_CITY_OF_BUENOS_AIRES,
    BUENOS_AIRES,
    CATAMARCA,
    CHACO,
    CHUBUT,
    CORDOBA,
    CORRIENTES,
    ENTRE_RIOS,
    FORMOSA,
    JUJUY,
    LA_PAMPA,
    LA_RIOJA,
    MENDOZA,
    MISIONES,
    NEUQUEN,
    RIO_NEGRO,
    SALTA,
    SAN_JUAN,
    SAN_LUIS,
    SANTA_CRUZ,
    SANTA_FE,
    SANTIAGO_DEL_ESTERO,
    TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR,
    TUCUMAN,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ARGENTINA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BUENOS_AIRES: return SubdivisionType.AUTONOMOUS_CITIES;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR:
                return "Tierra del Fuego";
            default:
                return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BUENOS_AIRES: return "Buenos Aires";
            case CORDOBA: return "Córdoba";
            case NEUQUEN: return "Neuquén";
            case RIO_NEGRO: return "Río Negro";
            case TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR: return "Tierra del Fuego, Antártida e Islas del Atlántico Sur";
            case TUCUMAN: return "Tucumán";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case CORDOBA:
            case LA_RIOJA:
            case SAN_JUAN:
            case SANTA_CRUZ:
            case TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR:
                return suffix + ",_Argentina";
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
    public String getFlagURLWikipediaSVGID() {
        switch (this) {
            case AUTONOMOUS_CITY_OF_BUENOS_AIRES: return "f/f5/Bandera_de_la_Ciudad_de_Buenos_Aires.svg";
            case BUENOS_AIRES: return "1/15/Bandera_de_la_Provincia_de_Buenos_Aires.svg";
            case CATAMARCA: return "7/7b/Bandera_de_la_Provincia_de_Catamarca.svg";
            case CHACO: return "3/33/Bandera_de_la_Provincia_del_Chaco.svg";
            case CHUBUT: return "8/88/Bandera_de_la_Provincia_del_Chubut.svg";
            case CORDOBA: return "f/f6/Bandera_de_la_Provincia_de_Córdoba_2014.svg";
            case CORRIENTES: return "4/46/Bandera_de_la_Provincia_de_Corrientes.svg";
            case ENTRE_RIOS: return "5/5b/Bandera_de_la_Provincia_de_Entre_R%C3%ADos.svg";
            case FORMOSA: return "4/42/Bandera_de_la_Provincia_de_Formosa.svg";
            case JUJUY: return "c/c9/Bandera_de_la_Provincia_de_Jujuy.svg";
            case LA_PAMPA: return "8/81/Bandera_de_la_Provincia_de_La_Pampa.svg";
            case LA_RIOJA: return "6/60/Bandera_de_la_Provincia_de_La_Rioja.svg";
            case MENDOZA: return "7/7c/Bandera_de_la_Provincia_de_Mendoza.svg";
            case MISIONES: return "c/ce/Bandera_de_la_Provincia_de_Misiones.svg";
            case NEUQUEN: return "b/bc/Bandera_de_la_Provincia_de_Neuquén.svg";
            case RIO_NEGRO: return "5/5d/Bandera_de_la_Provincia_del_R%C3%ADo_Negro.svg";
            case SALTA: return "6/6a/Bandera_de_la_Provincia_de_Salta.svg";
            case SAN_JUAN: return "b/b0/Flag_of_the_San_Juan_Province.svg";
            case SAN_LUIS: return "0/0e/Bandera_de_la_Provincia_de_San_Luis.svg";
            case SANTA_CRUZ: return "4/45/Bandera_de_la_Provincia_de_Santa_Cruz.svg";
            case SANTA_FE: return "8/84/Bandera_de_la_Provincia_de_Santa_Fe.svg";
            case SANTIAGO_DEL_ESTERO: return "0/07/Bandera_de_la_Provincia_de_Santiago_del_Estero.svg";
            case TIERRA_DEL_FUEGO_ANTARTIDA_E_ISLAS_DEL_ATLANTICO_SUR: return "9/94/Bandera_de_la_Provincia_de_Tierra_del_Fuego.svg";
            case TUCUMAN: return "c/ce/Bandera_de_la_Provincia_de_Tucumán.svg";
            default: return null;
        }
    }
}
