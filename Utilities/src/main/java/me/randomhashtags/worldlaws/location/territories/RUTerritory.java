package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

public enum RUTerritory implements Territory {
    ADYGEA,
    ALTAI_KRAI,
    ALTAI_REPUBLIC,
    AMUR,
    ARKHANGELSK,
    ASTRAKHAN,
    BASHKORTOSTAN,
    BELGOROD,
    BRYANSK,
    BURYATIA,
    CHECHEN,
    CHELYABINSK,
    CHUKOTKA,
    CHUVASH,
    DAGESTAN,
    INGUSHETIA,
    IRKUTSK,
    IVANOVO,
    JEWISH_AUTONOMOUS_OBLAST,
    KABARDINO_BALKAR,
    KALININGRAD,
    KALMYKIA,
    KALUGA,
    KAMCHATKA,
    KARACHAY_CHERKESS,
    KARELIA,
    KEMEROVO,
    KHABAROVSK,
    KHAKASSIA,
    KHANTY_MANSI_AUTONOMOUS_OKRUG_YUGRA,
    KIROV,
    KOMI,
    KOSTROMA,
    KRASNODAR,
    KRASNOYARSK,
    KURGAN,
    KURSK,
    LENINGRAD,
    LIPETSK,
    MAGADAN,
    MARI_EL_REPUBLIC,
    MORDOVIA,
    MOSCOW_OBLAST,
    MURMANSK_OBLAST,
    NENETS_AUTONOMOUS_OKRUG,
    NIZHNY_NOVGOROD_OBLAST,
    NORTH_OSSETIA_ALANIA,
    NOVGOROD,
    NOVOSIBIRSK,
    OMSK,
    ORENBURG,
    ORYOL,
    PENZA,
    PERM,
    PRIMORSKY,
    PSKOV,
    CRIMEA,
    ROSTOV,
    RYANZAN,
    SAINT_PETERSBURG,
    SAKHA,
    SAKHALIN,
    SAMARA,
    SARATOV,
    SEVASTOPOL,
    SMOLENSK,
    STAVROPOL,
    SVERDLOVSK,
    TAMBOV,
    TATARSTAN,
    TOMSK,
    TULA,
    TUVA,
    TVER,
    TYUMEN,
    UDMURT,
    ULYANOVSK,
    VLADIMIR,
    VOLGOGRAD,
    VOLOGDA,
    VORONEZH,
    YAMALO_NENETS_AUTONOMOUS_OKRUG,
    YAROSLAVL,
    ZABAYKALSKY,
    ;

    public static final RUTerritory[] TERRITORIES = RUTerritory.values();
    private String backendID, name, abbreviation, flagURL, governmentURL;

    RUTerritory() {
        name = LocalServer.toCorrectCapitalization(name());
    }

    @Override
    public Country getCountry() {
        return Country.RUSSIA;
    }
    @Override
    public String getBackendID() {
        return backendID;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getAbbreviation() {
        return abbreviation;
    }
    @Override
    public String getFlagURL() {
        return flagURL;
    }
    @Override
    public PopulationEstimate getPopulationEstimate() {
        return null;
    }
    @Override
    public String getGovernmentURL() {
        return governmentURL;
    }
}
