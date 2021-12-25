package me.randomhashtags.worldlaws.country.subdivisions.e;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsEcuador implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Ecuador
    AZUAY,
    BOLIVAR,
    CANAR,
    CARCHI,
    CHIMBORAZO,
    COTOPAXI,
    EL_ORO,
    ESMERALDAS,
    GALAPAGOS,
    GUAYAS,
    IMBABURA,
    LOJA,
    LOS_RIOS,
    MANABI,
    MORONA_SANTIGAO,
    NAPO,
    ORELLANA,
    PASTAZA,
    PICHINCHA,
    SANTA_ELENA,
    SANTO_DOMINGO_DE_LOS_TSACHILAS,
    SUCUMBIOS,
    TUNGURAHUA,
    ZAMORA_CHINCHIPE,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ECUADOR;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case BOLIVAR: return "Bolívar";
            case CANAR: return "Cañar";
            case GALAPAGOS: return "Galápagos";
            case LOS_RIOS: return "Los Ríos";
            case MANABI: return " Manabí";
            case SANTO_DOMINGO_DE_LOS_TSACHILAS: return "Santo Domingo de los Tsáchilas";
            case SUCUMBIOS: return "Sucumbíos";
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
