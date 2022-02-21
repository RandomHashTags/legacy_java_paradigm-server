package me.randomhashtags.worldlaws.country.subdivisions.r;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsRomania implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Counties_of_Romania
    ALBA,
    ARAD,
    ARGES,
    BACAU,
    BIHOR,
    BISTRITA_NASAUD,
    BOTOSANI,
    BRAILA,
    BRASOV,
    BUCHAREST,
    BUZAU,
    CALARASI,
    CARAS_SEVERIN,
    CLUJ,
    CONSTANTA,
    COVASNA,
    DAMBOVITA,
    DOLJ,
    GALATI,
    GIURGIU,
    GORJ,
    HARGHITA,
    HUNEDOARA,
    IALOMITA,
    IASI,
    ILFOV,
    MARAMURES,
    MEHEDINTI,
    MURES,
    NEAMT,
    OLT,
    PRAHOVA,
    SALAJ,
    SATU_MARE,
    SIBIU,
    SUCEAVA,
    TELEORMAN,
    TIMIS,
    TULCEA,
    VALCEA,
    VASLUI,
    VRANCEA,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ROMANIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.COUNTIES;
    }

    @Override
    public SubdivisionType getType() {
        switch (this) {
            case BUCHAREST: return SubdivisionType.MUNICIPALITIES;
            default: return null;
        }
    }

    @Override
    public String getRealName() {
        switch (this) {
            case ARGES: return "Argeș";
            case BACAU: return "Bacău";
            case BISTRITA_NASAUD: return "Bistrița-Năsăud";
            case BOTOSANI: return "Botoșani";
            case BRAILA: return "Brăila";
            case BRASOV: return "Brașov";
            case BUZAU: return "Buzău";
            case CALARASI: return "Călărași";
            case CARAS_SEVERIN: return "Caraș-Severin";
            case CONSTANTA: return "Constanța";
            case DAMBOVITA: return "Dâmbovița";
            case GALATI: return "Galați";
            case IALOMITA: return "Ialomița";
            case IASI: return "Iași";
            case MARAMURES: return "Maramureș";
            case MEHEDINTI: return "Mehedinți";
            case MURES: return "Mureș";
            case NEAMT: return "Neamț";
            case SALAJ: return "Sălaj";
            case TIMIS: return "Timiș";
            case VALCEA: return "Vâlcea";
            default: return null;
        }
    }

    @Override
    public String getWikipediaURLSuffix(String suffix) {
        switch (this) {
            case BUCHAREST:
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
