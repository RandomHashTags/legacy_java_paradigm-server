package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

// https://en.wikipedia.org/wiki/Provinces_of_China
public enum CHTerritory implements Territory {
    ANHUI,
    BEIJING,
    CHONGQING,
    FUJIAN,
    GUANGDONG,
    GANSU,
    GUANGXI_ZHUANG,
    GUIZHOU,
    HENAN,
    HUBEI,
    HEBEI,
    HAINAN,
    HONG_KONG,
    HEILONGJIANG,
    HUNAN,
    JILIN,
    JIANGSU,
    JIANGXI,
    LIAONING,
    MACAU,
    INNER_MONGOLIA,
    NINGXIA_HUI,
    QINGHAI,
    SICHUAN,
    SHANDONG,
    SHANGHAI,
    SHAANXI,
    SHANXI,
    TIANJIN,
    TAIWAN,
    XINJIANG_UYGHUR,
    TIBET,
    TUNNAN,
    ZHEJIANG
    ;

    public static final CHTerritory[] TERRITORIES = CHTerritory.values();
    private String name;

    CHTerritory() {
        name = LocalServer.toCorrectCapitalization(name());
    }

    @Override
    public Country getCountry() {
        return Country.CHINA;
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
