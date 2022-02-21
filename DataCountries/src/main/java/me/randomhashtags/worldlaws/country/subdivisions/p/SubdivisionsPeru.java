package me.randomhashtags.worldlaws.country.subdivisions.p;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsPeru implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Peru
    AMAZONAS,
    ANCASH,
    APURIMAC,
    AREQUIPA,
    AYACUCHO,
    CAJAMARCA,
    CALLAO,
    CUZCO,
    HUANCAVELICA,
    HUANUCO,
    ICA,
    JUNIN,
    LA_LIBERTAD,
    LAMBAYEQUE,
    LIMA,
    LORETO,
    MADRE_DE_DIOS,
    MOQUEGUA,
    PASCO,
    PIURA,
    PUNO,
    SAN_MARTIN,
    TACNA,
    TUMBES,
    UCAYALI,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.PERU;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.DEPARTMENTS;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case APURIMAC: return "Apurímac";
            case HUANUCO: return "Huánuco";
            case JUNIN: return "Junín";
            case MADRE_DE_DIOS: return "Madre de Dios";
            case SAN_MARTIN: return "San Martín";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLPrefix() {
        switch (this) {
            case CALLAO:
                return "";
            default:
                return "Department_of_";
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case AMAZONAS:
                return ",_Peru";
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
