package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsPeru implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Peru
    AMAZONAS,
    ANCASH,
    APURIMAC,
    AREQUIPA,
    AYACUCHO,
    CAJAMARCA,
    CALLAO,
    CUSCO,
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
