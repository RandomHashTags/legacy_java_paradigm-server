package me.randomhashtags.worldlaws.country.subdivisions.c;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsCuba implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Cuba
    ARTEMISA,
    CAMAGUEY,
    CIEGO_DE_AVILA,
    CIENFUEGOS,
    GRANMA,
    GUANTANAMO,
    HOLGUIN,
    ISLA_DE_LA_JUVENTUD,
    LA_HABANA,
    LAS_TUNAS,
    MATANZAS,
    MAYABEQUE,
    PINAR_DEL_RIO,
    SANCTI_SPIRITUS,
    SANTIAGO_DE_CUBA,
    VILLA_CLARA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.CUBA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case ISLA_DE_LA_JUVENTUD: return SubdivisionType.SPECIAL_MUNICIPALITIES;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case CAMAGUEY: return "Camagüey";
            case CIEGO_DE_AVILA: return "Ciego de Ávila";
            case GUANTANAMO: return "Guantánamo";
            case HOLGUIN: return "Holguín";
            case PINAR_DEL_RIO: return "Pinar del Río";
            case SANCTI_SPIRITUS: return "Sancti Spíritus";
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
        switch (this) {
            default: return null;
        }
    }

    @Override
    public SovereignStateSubdivision[] getNeighbors() {
        switch (this) {
            default: return null;
        }
    }
}
