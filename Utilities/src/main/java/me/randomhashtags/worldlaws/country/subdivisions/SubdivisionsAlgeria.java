package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

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
    public String getPostalCodeAbbreviation() {
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
