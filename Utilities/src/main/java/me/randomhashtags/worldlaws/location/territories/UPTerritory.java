package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

public enum UPTerritory implements Territory {
    CHERKASY,
    CHERNIHIV,
    CHERNIVTSI,
    DNIPROPETROVSK,
    DONETSK,
    IVANO_FRANKIVSK,
    KHARKIV,
    KHERSON,
    KHMELNYTSKYI,
    KIEV,
    KIROVOHRAD,
    LUHANSK,
    LVIV,
    MYKOLAIV,
    ODESSA,
    POLTAVA,
    RIVNE,
    SUMY,
    TERNOPIL,
    VINNYTSIA,
    VOLYN,
    ZAKARPATTIA,
    ZAPORIZHIA,
    ZHYTOMYR,
    ;

    public static final UPTerritory[] TERRITORIES = UPTerritory.values();
    private String name;

    UPTerritory() {
        name = LocalServer.toCorrectCapitalization(name());
    }

    @Override
    public Country getCountry() {
        return Country.UKRAINE;
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
