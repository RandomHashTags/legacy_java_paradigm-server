package me.randomhashtags.worldlaws.info.national;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.info.CountryNationalService;
import me.randomhashtags.worldlaws.info.CountrySingleValue;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public enum NationalAnthems implements CountryNationalService { // https://en.wikipedia.org/wiki/List_of_national_anthems | https://www.navyband.navy.mil/media/ceremonial
    INSTANCE;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.NATIONAL_ANTHEM;
    }

    @Override
    public void loadData(CompletionHandler handler) {
        final String url = "https://www.navyband.navy.mil/media/ceremonial";
        final Document doc = getDocument(Folder.COUNTRIES_NATIONAL, url, false);
        if(doc != null) {
            final String title = getInfo().getTitle();
            final HashMap<String, String> countries = new HashMap<>();
            final EventSources sources = new EventSources();
            final EventSource source = new EventSource("United States Navy Band: Ceremonial Music", "https://www.navyband.navy.mil/media/ceremonial");
            sources.append(source);

            final Elements elements = doc.select("body div div div.custom-page div div main div div div div.list-group a[href]");
            ParallelStream.stream(elements, elementObj -> {
                final Element element = (Element) elementObj;
                final Element targetElement = element.selectFirst("div.col-9");
                if(targetElement != null) {
                    final String targetCountry = targetElement.textNodes().get(0).text();
                    final String countryBackendID = targetCountry.toLowerCase().replace(" ", "");
                    final WLCountry wlcountry = WLCountry.valueOfBackendID(countryBackendID);
                    if(wlcountry != null) {
                        final String href = "https://www.navyband.navy.mil" + element.attr("href");
                        countries.put(countryBackendID, href);
                    }
                }
            });
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(Map.Entry<String, String> map : countries.entrySet()) {
                final String country = map.getKey(), audioURL = map.getValue();
                final CountrySingleValue value = new CountrySingleValue(null, audioURL, null, -1);
                value.setSources(sources);
                builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":{\"").append(title).append("\":").append(value.toString().substring("\"null\":".length())).append("}");
                isFirst = false;
            }
            builder.append("}");
            handler.handleString(builder.toString());
        } else {
            handler.handleString(null);
        }
    }
}
