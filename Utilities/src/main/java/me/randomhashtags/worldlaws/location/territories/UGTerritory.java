package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

// https://www.nationsonline.org/oneworld/map/uganda-administrative-map.htm
// https://en.wikipedia.org/wiki/Geography_of_Uganda
public enum UGTerritory implements Territory {
    ABIM,
    ADJUMANI,
    AGAGO,
    ALEBTONG,
    AMOLATAR,
    AMUDAT,
    AMURU,
    APAC,
    ARUA,
    DOKOLO,
    GULU,
    KAABONG,
    KITGUM,
    KOBOKO,
    KOLE,
    KOTIDO,
    LAMWO,
    LIRA,
    MARACHA,
    MOROTO,
    MOYO,
    NAKAPIRIPIRIT,
    NAPAK,
    NEBBI,
    NWOYA,
    OTUKE,
    OYAM,
    PADER,
    YUMBE,
    ZOMBO,

    BUIKWE,
    BUKOMANSIMBI,
    BUTAMBALA,
    BUVUMA,
    GOMBA,
    KALANGALA,
    KALUNGU,
    KAMPALA,
    KAYUNGA,
    KIBOGA,
    KYANKWANZI,
    LUWEERO,
    LWENGO,
    LYANTONDE,
    MASAKA,
    MITYANA,
    MPIGI,
    MUBENDE,
    MUKONO,
    NAKASONGOLA,
    RAKAI,
    SEMBABULE,
    WAKISO,

    AMURIA,
    BUDAKA,
    BUDUDA,
    BUGIRI,
    BUKEDEA,
    BUKWA,
    BULAMBULI,
    BUSIA,
    BUTALEJA,
    BUYENDE,
    IGANGA,
    JINJA,
    KABERAMAIDO,
    KALIRO,
    KAMULI,
    KAPCHORWA,
    KATAKWI,
    KIBUKU,
    KUMI,
    KWEEN,
    LUUKA,
    MANAFWA,
    MAYUGE,
    MBALE,
    NAMAYINGO,
    NAMUTUMBA,
    NGORA,
    PALLISA,
    SERERE,
    SIRONKO,
    SOROTI,
    TORORO,

    BUHWEJU,
    BULIISA,
    BUNDIBUGYO,
    BUSHENYI,
    HOIMA,
    IBANDA,
    ISINGIRO,
    KABALE,
    KABAROLE,
    KAMWENGE,
    KANUNGU,
    KASESE,
    KIBAALE,
    KIRUHURA,
    KIRYANDONGO,
    KISORO,
    KYEGEGWA,
    KYENJOJO,
    MASINDI,
    MBARARA,
    MITOOMA,
    NTOROKO,
    NTUNGAMO,
    RUBIRIZI,
    RUKUNGIRI,
    SHEEMA,
    ;

    public static final UGTerritory[] TERRITORIES = UGTerritory.values();
    private String name;

    UGTerritory() {
        name = LocalServer.toCorrectCapitalization(name());
    }

    @Override
    public Country getCountry() {
        return Country.UGANDA;
    }
    @Override
    public String getBackendID() {
        return null;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getAbbreviation() {
        return null;
    }
    @Override
    public String getFlagURL() {
        return null;
    }
    @Override
    public PopulationEstimate getPopulationEstimate() {
        return null;
    }
    @Override
    public String getGovernmentURL() {
        return null;
    }
}
