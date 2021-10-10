package me.randomhashtags.worldlaws.country.subdivisions;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public enum SubdivisionsAzerbaijan implements SovereignStateSubdivision {
    ABSHERON,
    AGDAM,
    AGDASH,
    AGHJABADI,
    AGSTAFA,
    AGSU,
    ASTARA,
    BAKU,
    BALAKAN,
    BARDA,
    BEYLAGAN,
    BILASUVAR,
    DASHKASAN,
    FUZULI,
    GADABAY,
    GANJA,
    GOBUSTAN,
    GORANBOY,
    GOYCHAY,
    GOYGOL,
    HAJIGABUL,
    IMISHLI,
    ISMAYILLI,
    JABRAYIL,
    JALILABAD,
    KALBAJAR,
    KHACHMAZ,
    KHANKENDI,
    KHIZI,
    KHOJALY,
    KHOJAVEND,
    KURDAMIR,
    LACHIN,
    LANKARAN,
    LERIK,
    MASALLY,
    MINGACHEVIR,
    NAFTALAN,
    NEFTCHALA,
    OGHUZ,
    QABALA,
    QAKH,
    QAZAKH,
    QUBA,
    QUBADLI,
    QUSAR,
    SAATLY,
    SABIRABAD,
    SALYAN,
    SAMUKH,
    SHABRAN,
    SHAKI_CITY,
    SHAKI_DISTRICT,
    SHIRVAN,
    SHUSHA,
    SIYAZAN,
    SUMQAYIT,
    TARTAR,
    TOVUZ,
    UJAR,
    YARDIMLI,
    YEVLAKH_CITY,
    YEVLAKH_DISTRICT,
    ZANGILAN,
    ZAQATALA,
    ZARDAB,

    BABEK,
    JULFA,
    KANGARLI,
    NAKHCHIVAN,
    ORDUBAD,
    SADARAK,
    SHAHBUZ,
    SHARUR,
    ;

    @Override
    public WLCountry getCountry() {
        return WLCountry.AZERBAIJAN;
    }

    @Override
    public String getPostalCodeAbbreviation() {
        switch (this) {
            case ABSHERON: return "ABS";
            case AGDAM: return "AGM";
            case AGDASH: return "AGS";
            case AGHJABADI: return "AGC";
            case AGSTAFA: return "AGA";
            case AGSU: return "AGU";
            case ASTARA: return "AST";
            case BABEK: return "BAB";
            case BAKU: return "BA";
            case BALAKAN: return "BAL";
            case BARDA: return "BAR";
            case BEYLAGAN: return "BEY";
            case BILASUVAR: return "BIL";
            case DASHKASAN: return "DAS";
            case FUZULI: return "FUZ";
            case GADABAY: return "GAD";
            case GANJA: return "GA";
            case GOBUSTAN: return "QOB";
            case GORANBOY: return "GOR";
            case GOYCHAY: return "GOY";
            case GOYGOL: return "GYG";
            case HAJIGABUL: return "HAC";
            case IMISHLI: return "IMI";
            case ISMAYILLI: return "ISM";
            case JABRAYIL: return "CAB";
            case JULFA: return "CUL";
            case KALBAJAR: return "KAL";
            case KANGARLI: return "KAN";
            case KHACHMAZ: return "XAC";
            case KHIZI: return "XIZ";
            case KHOJALY: return "XCI";
            case KHOJAVEND: return "XVD";
            case KURDAMIR: return "KUR";
            case LACHIN: return "LAC";
            case LANKARAN: return "LA";
            //case LANKARAN: return "LAN";
            case LERIK: return "LER";
            case MASALLY: return "MAS";
            case MINGACHEVIR: return "MI";
            case NAFTALAN: return "NA";
            case NAKHCHIVAN: return "NV";
            case NEFTCHALA: return "NEF";
            case OGHUZ: return "OGU";
            case ORDUBAD: return "ORD";
            case QAKH: return "QAX";
            case QAZAKH: return "QAZ";
            //case QBALA: return "QAB";
            case QUBA: return "QBA";
            case QUBADLI: return "QBI";
            case QUSAR: return "QUS";
            case SAATLY: return "SAT";
            case SABIRABAD: return "SAB";
            case SADARAK: return "SAD";
            case SALYAN: return "SAL";
            case SAMUKH: return "SMX";
            case SHABRAN: return "SBN";
            case SHAHBUZ: return "SAH";
            case SHAKI_CITY: return "SA";
            case SHAKI_DISTRICT: return "SAK";
            //case SHAMAKHI: return "SMI";
            //case SHAMKIR: return "SKR";
            case SHARUR: return "SAR";
            case SHIRVAN: return "SR";
            case SHUSHA: return "SUS";
            //case SIAZAN: return "SIY";
            //case STEPANAKERT: return "XA";
            case SUMQAYIT: return "SM";
            case TARTAR: return "TAR";
            case TOVUZ: return "TOV";
            case UJAR: return "UCA";
            case YARDIMLI: return "YAR";
            case YEVLAKH_CITY: return "YE";
            case YEVLAKH_DISTRICT: return "YEV";
            case ZANGILAN: return "ZAN";
            case ZAQATALA: return "ZAQ";
            case ZARDAB: return "ZAR";
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
