package me.randomhashtags.worldlaws.country.subdivisions.h;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsHungary implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Hungary
    BACS_KISKUN,
    BARANYA,
    BEKES,
    BORSOD_ABAUJ_ZEMPLEN,
    BUDAPEST,
    CSONGRAD_CSANAD,
    FEJER,
    GYOR_MOSON_SOPRON,
    HAJDU_BIHAR,
    HEVES,
    JASZ_NAGYKUN_SZOLNOK,
    KOMAROM_ESZTERGOM,
    NOGRAD,
    PEST,
    SOMOGY,
    SZABOLCS_SZATMAR_BEREG,
    TOLNA,
    VAS,
    VESZPREM,
    ZALA
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.HUNGARY;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.COUNTIES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BACS_KISKUN: return "Bács-Kiskun";
            case BEKES: return "Békés";
            case BORSOD_ABAUJ_ZEMPLEN: return "Borsod-Abaúj-Zemplén";
            case CSONGRAD_CSANAD: return "Csongrád-Csanád";
            case FEJER: return "Fejér";
            case GYOR_MOSON_SOPRON: return "Győr-Moson-Sopron";
            case HAJDU_BIHAR: return "Hajdú-Bihar";
            case JASZ_NAGYKUN_SZOLNOK: return "Jász-Nagykun-Szolnok";
            case KOMAROM_ESZTERGOM: return "Komárom-Esztergom";
            case NOGRAD: return "Nógrád";
            case SZABOLCS_SZATMAR_BEREG: return "Szabolcs-Szatmár-Bereg";
            case VESZPREM: return "Veszprém";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BUDAPEST:
                return "";
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

    @Override
    public String getGovernmentWebsite() {
        return null;
    }
}
