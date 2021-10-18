package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsEgypt implements SovereignStateSubdivision {
    ALEXANDRIA,
    ASWAN,
    ASYUT,
    BEHEIRA,
    BENI_SUEF,
    CAIRO,
    DAKAHLIA,
    DAMIETTA,
    FAIYUM,
    GHARBIA,
    GIZA,
    ISMAILIA,
    KAFR_EL_SHEIKH,
    LUXOR,
    MATROUH,
    MINYA,
    MONUFIA,
    NEW_VALLEY,
    NORTH_SINAI,
    PORT_SAID,
    QALYUBIA,
    QENA,
    RED_SEA,
    SHARQIA,
    SOHAG,
    SOUTH_SINAI,
    SUEZ,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.EGYPT;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            case ALEXANDRIA: return "ALX";
            case ASWAN: return "ASN";
            case ASYUT: return "AST";
            case BEHEIRA: return "BH";
            case BENI_SUEF: return "BNS";
            case CAIRO: return "C";
            case DAKAHLIA: return "DK";
            case DAMIETTA: return "DT";
            case FAIYUM: return "FYM";
            case GHARBIA: return "GH";
            case GIZA: return "GZ";
            case ISMAILIA: return "IS";
            case KAFR_EL_SHEIKH: return "KFS";
            case LUXOR: return "LX";
            case MATROUH: return "MT";
            case MINYA: return "MN";
            case MONUFIA: return "MNF";
            case NEW_VALLEY: return "WAD";
            case NORTH_SINAI: return "SIN";
            case PORT_SAID: return "PTS";
            case QALYUBIA: return "KB";
            case QENA: return "KN";
            case RED_SEA: return "BA";
            case SHARQIA: return "SHR";
            case SOHAG: return "SHG";
            case SOUTH_SINAI: return "JS";
            case SUEZ: return "SUZ";
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

    @Override
    public void getCitiesHashSet(CompletionHandler handler) {
    }
}