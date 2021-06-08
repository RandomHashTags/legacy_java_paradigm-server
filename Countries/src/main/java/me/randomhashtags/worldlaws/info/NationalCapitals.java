package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public enum NationalCapitals implements CountryNationalService {
    INSTANCE;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.NATIONAL_CAPITAL;
    }

    @Override
    public void loadData(CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/List_of_national_capitals", title = getInfo().getTitle();
        final Elements trs = getNationalDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.select("td").size() < 2);
        final EventSource source = new EventSource("Wikipedia: List of national capitals", url);
        final EventSources sources = new EventSources(source);
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(1).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "").replace("\"", "");
            if(!country.isEmpty()) {
                final String text = tds.size() > 2 ? tds.get(2).text() : null;
                final CountrySingleValue value = new CountrySingleValue(null, tds.get(0).text(), text, -1);
                value.setSources(sources);
                builder.append(isFirst ? "" : ",").append("\"").append(country).append("\":{\"").append(title).append("\":").append(value.toString().substring("\"null\":".length())).append("}");
                isFirst = false;
            }
        }
        builder.append("}");
        handler.handle(builder.toString());
    }
}
