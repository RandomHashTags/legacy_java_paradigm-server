package me.randomhashtags.worldlaws.location.territories;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.PopulationEstimate;
import me.randomhashtags.worldlaws.location.Country;
import me.randomhashtags.worldlaws.location.Territory;

// https://en.wikipedia.org/wiki/Provinces_of_Afghanistan
public enum AFTerritory implements Territory {
    BADAKHSHAN,
    BADGHIS,
    BAGHLAN,
    BALKH,
    BAMYAN,
    DAYKUNDI,
    FARAH,
    FARYAB,
    GHAZNI,
    GHOR,
    HELMAND,
    HERAT,
    JOWZJAN,
    KABUL,
    KANDAHAR,
    KAPISA,
    KHOST,
    KUNAR,
    KUNDUZ,
    LAGHMAN,
    LOGAR,
    NANGARHAR,
    NIMRUZ,
    NURISTAN,
    PAKTIA,
    PAKTIKA,
    PANJSHIR,
    PARWAN,
    SAMANGAN,
    SAR_E_POL,
    TAKHAR,
    URUZGAN,
    WARDAK,
    ZABUL,
    ;

    public static final AFTerritory[] TERRITORIES = AFTerritory.values();
    private String name;

    AFTerritory() {
        name = LocalServer.toCorrectCapitalization(name());
    }

    @Override
    public Country getCountry() {
        return Country.AFGHANISTAN;
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
