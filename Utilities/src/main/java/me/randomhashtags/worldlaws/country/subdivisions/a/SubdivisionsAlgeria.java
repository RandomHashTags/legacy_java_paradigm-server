package me.randomhashtags.worldlaws.country.subdivisions.a;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;

public enum SubdivisionsAlgeria implements SovereignStateSubdivision { // https://en.wikipedia.org/wiki/Provinces_of_Algeria
    ADRAR,
    AIN_DEFLA,
    AIN_TEMOUCHENT,
    ALGER,
    ANNABA,
    BARNA,
    BECHAR,
    BEJAIA,
    BENI_ABBES,
    BISKRA,
    BLIDA,
    BORDJ_BAJI_MOKHTAR,
    BORDJ_BOU_ARRERIDJ,
    BOUIRA,
    BOUMERDES,
    CHLEF,
    CONSTANTINE,
    DJANET,
    DJELFA,
    EL_BAYADH,
    EL_MGHAIR,
    EL_MENIA,
    EL_OUED,
    EL_TARF,
    GHARDAIA,
    GUELMA,
    ILLIZI,
    IN_GUEZZAM,
    IN_SALAH,
    JIJEL,
    KHENCHELA,
    LAGHOUAT,
    MSILA,
    MASCARA,
    MEDEA,
    MILA,
    MOSTAGANEM,
    NAAMA,
    ORAN,
    OUARGLA,
    OULED_DJELLAL,
    OUM_EL_BOUAGHI,
    RELIZANE,
    SAIDA,
    SETIF,
    SIDI_BEL_ABBES,
    SKIKDA,
    SOUK_AHRAS,
    TAMANRASSET,
    TEBESSA,
    TIARET,
    TIMIMOUN,
    TINDOUF,
    TIPAZA,
    TISSEMSILT,
    TIZI_OUZOU,
    TLEMCEN,
    TOUGGOURT,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.ALGERIA;
    }

    @Override
    public SubdivisionType getDefaultType() {
        return SubdivisionType.PROVINCES;
    }

    @Override
    public String getRealName() {
        switch (this) {
            case AIN_DEFLA: return "Aïn Defla";
            case AIN_TEMOUCHENT: return "Aïn Témouchent";
            case BECHAR: return "Béchar";
            case BEJAIA: return "Béjaïa";
            case BENI_ABBES: return "Béni Abbès";
            case BORDJ_BOU_ARRERIDJ: return "Bordj Bou Arréridj";
            case BOUIRA: return "Bouïra";
            case BOUMERDES: return "Boumerdès";
            case EL_MGHAIR: return "El M'Ghair";
            case GHARDAIA: return "Ghardaïa";
            case MSILA: return "M'Sila";
            case MEDEA: return "Médéa";
            case NAAMA: return "Naâma";
            case SAIDA: return "Saïda";
            case SETIF: return "Sétif";
            case SIDI_BEL_ABBES: return "Sidi Bel Abbès";
            case TEBESSA: return "Tébessa";
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
