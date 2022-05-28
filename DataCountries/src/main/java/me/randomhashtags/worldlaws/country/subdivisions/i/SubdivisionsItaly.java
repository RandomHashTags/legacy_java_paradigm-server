package me.randomhashtags.worldlaws.country.subdivisions.i;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsItaly implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Regions_of_Italy
    ABRUZZO,
    AOSTA_VALLEY,
    APULIA,
    BASILICATA,
    CALABRIA,
    CAMPANIA,
    EMILIA_ROMAGNA,
    FRIULI_VENEZLA_GIULIA,
    LAZIO,
    LIGURIA,
    LOMBARDY,
    MARCHE,
    MOLISE,
    PIEDMONT,
    SARDINIA,
    SICILY,
    TRENTINO_SOUTH_TYROL,
    TUSCANY,
    UMBRIA,
    VENETO,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ITALY;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.REGIONS;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case AOSTA_VALLEY:
            case FRIULI_VENEZLA_GIULIA:
            case SARDINIA:
            case SICILY:
            case TRENTINO_SOUTH_TYROL:
                return SubdivisionType.AUTONOMOUS_REGIONS;
            default: return null;
        }
    }

    @Override
    public String getConditionalName() {
        switch (this) {
            case TRENTINO_SOUTH_TYROL: return "Trentino-Alto_Adige/Südtirol";
            default: return SovereignStateSubdivision.super.getConditionalName();
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case EMILIA_ROMAGNA: return "Emilia-Romagna";
            case FRIULI_VENEZLA_GIULIA: return "Friuli-Venezia Giulia";
            case TRENTINO_SOUTH_TYROL: return "Trentino-South Tyrol";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            default:
                return "";
        }
    }

    @Override
    public String getISOAlpha2() {
        return null;
    }

    @Override
    public String getFlagURL() {
        switch (this) {
            case ABRUZZO: return "https://upload.wikimedia.org/wikipedia/commons/4/45/Flag_of_Abruzzo.svg";
            case AOSTA_VALLEY: return "https://upload.wikimedia.org/wikipedia/commons/9/90/Flag_of_Valle_d%27Aosta.svg";
            case APULIA: return "https://upload.wikimedia.org/wikipedia/commons/b/b8/Flag_of_Apulia.svg";
            case BASILICATA: return "https://upload.wikimedia.org/wikipedia/commons/8/8e/Flag_of_Basilicata.svg";
            case CALABRIA: return "https://upload.wikimedia.org/wikipedia/commons/8/8b/Flag_of_Calabria.svg";
            case CAMPANIA: return "https://upload.wikimedia.org/wikipedia/commons/c/c5/Flag_of_Campania.svg";
            case EMILIA_ROMAGNA: return "https://upload.wikimedia.org/wikipedia/commons/7/77/Flag_of_Emilia-Romagna_%28de_facto%29.svg";
            case FRIULI_VENEZLA_GIULIA: return "https://upload.wikimedia.org/wikipedia/commons/5/55/Flag_of_Friuli-Venezia_Giulia.svg";
            case LAZIO: return "https://upload.wikimedia.org/wikipedia/commons/e/e1/Flag_of_Lazio.svg";
            case LIGURIA: return "https://upload.wikimedia.org/wikipedia/commons/8/88/Flag_of_Liguria.svg";
            case LOMBARDY: return "https://upload.wikimedia.org/wikipedia/commons/e/ea/Flag_of_Lombardy.svg";
            case MARCHE: return "https://upload.wikimedia.org/wikipedia/commons/0/07/Flag_of_Marche.svg";
            case MOLISE: return "https://upload.wikimedia.org/wikipedia/commons/8/84/Flag_of_Molise.svg";
            case PIEDMONT: return "https://upload.wikimedia.org/wikipedia/commons/b/b9/Flag_of_Piedmont.svg";
            case SARDINIA: return "https://upload.wikimedia.org/wikipedia/commons/4/4e/Flag_of_Sardinia%2C_Italy.svg";
            case SICILY: return "https://upload.wikimedia.org/wikipedia/commons/8/84/Sicilian_Flag.svg";
            case TRENTINO_SOUTH_TYROL: return "https://upload.wikimedia.org/wikipedia/commons/7/7f/Flag_of_Trentino-South_Tyrol.svg";
            case TUSCANY: return "https://upload.wikimedia.org/wikipedia/commons/f/f2/Flag_of_Tuscany.svg";
            case UMBRIA: return "https://upload.wikimedia.org/wikipedia/commons/c/cc/Flag_of_Umbria.svg";
            case VENETO: return "https://upload.wikimedia.org/wikipedia/commons/d/d5/Flag_of_Veneto.svg";
            default: return null;
        }
    }
}
