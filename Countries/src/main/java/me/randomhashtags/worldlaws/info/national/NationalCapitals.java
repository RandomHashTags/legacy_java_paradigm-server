package me.randomhashtags.worldlaws.info.national;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.info.CountryNationalService;
import me.randomhashtags.worldlaws.info.CountrySingleValue;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public enum NationalCapitals implements CountryNationalService {
    INSTANCE;

    @Override
    public EventSources getSources() {
        final String url = "https://en.wikipedia.org/wiki/List_of_national_capitals";
        final EventSource source = new EventSource("Wikipedia: List of national capitals", url);
        return new EventSources(source);
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.NATIONAL_CAPITAL;
    }

    @Override
    public JSONObjectTranslatable loadData() {
        final String url = "https://en.wikipedia.org/wiki/List_of_national_capitals";
        final Elements trs = getNationalDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.select("td").size() < 2);
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(1).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "").replace("\"", "");
            if(!country.isEmpty()) {
                final String text = tds.size() > 2 ? tds.get(2).text() : null;
                final CountrySingleValue value = new CountrySingleValue(null, tds.get(0).text(), text, -1);
                json.put(country, value.toJSONObject(), true);
            }
        }
        return json;
    }
}
