package me.randomhashtags.worldlaws.info.national;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.info.CountryNationalService;
import me.randomhashtags.worldlaws.info.CountrySingleValue;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public enum NationalAnthems implements CountryNationalService { // https://en.wikipedia.org/wiki/List_of_national_anthems | https://www.navyband.navy.mil/media/ceremonial
    INSTANCE;

    @Override
    public EventSources getSources() {
        return new EventSources(
                new EventSource("United States Navy Band: Ceremonial Music", "https://www.navyband.navy.mil/media/ceremonial")
        );
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.NATIONAL_ANTHEM;
    }

    @Override
    public JSONObjectTranslatable loadData() {
        final String url = "https://www.navyband.navy.mil/media/ceremonial";
        final Document doc = getDocument(Folder.COUNTRIES_NATIONAL, url, false);
        JSONObjectTranslatable json = null;
        if(doc != null) {
            final HashMap<String, String> countries = new HashMap<>();
            final Elements elements = doc.select("div.container div.margin-bottom-50 div.row div.list-group a.list-group-item");
            for(Element element : elements) {
                final Element targetElement = element.selectFirst("div.col-9");
                if(targetElement != null) {
                    final String targetCountry = targetElement.textNodes().get(0).text();
                    final String countryBackendID = targetCountry.toLowerCase().replace(" ", "");
                    final WLCountry wlcountry = WLCountry.valueOfString(countryBackendID);
                    if(wlcountry != null) {
                        final String href = "https://www.navyband.navy.mil" + element.attr("href");
                        countries.put(countryBackendID, href);
                    }
                }
            }

            if(!countries.isEmpty()) {
                json = new JSONObjectTranslatable();
                for(Map.Entry<String, String> map : countries.entrySet()) {
                    final String country = map.getKey(), audioURL = map.getValue();
                    final CountrySingleValue value = new CountrySingleValue(null, audioURL, null, -1);
                    json.put(country, value.toJSONObject(), true);
                }
            }
        }
        return json;
    }
}
