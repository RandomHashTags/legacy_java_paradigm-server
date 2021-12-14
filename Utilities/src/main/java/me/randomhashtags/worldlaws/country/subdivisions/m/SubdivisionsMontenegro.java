package me.randomhashtags.worldlaws.country.subdivisions.m;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsMontenegro implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Municipalities_of_Montenegro
    ANDRIJEVICA,
    BAR,
    BERANE,
    BIJELO_POLJE,
    BUDVA,
    CETINJE,
    DANILOVGRAD,
    GUSINJE,
    HERCEG_NOVI,
    KOLASIN,
    KOTOR,
    MOJKOVAC,
    NIKSIC,
    PETNJICA,
    PLAV,
    PLIJEVLJA,
    PLUZINE,
    PODGORICA,
    ROZAJE,
    SAVNIK,
    TIVAT,
    TUZI,
    ULCINJ,
    ZABLJAK,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.MONTENEGRO;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case KOLASIN: return "Kolašin";
            case NIKSIC: return "Nikšić";
            case PLUZINE: return "Plužine";
            case ROZAJE: return "Rožaje";
            case SAVNIK: return "Šavnik";
            case ZABLJAK: return "Žabljak";
            default: return null;
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
