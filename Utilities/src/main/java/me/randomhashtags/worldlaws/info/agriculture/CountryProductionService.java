package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public interface CountryProductionService extends CountryService {
    String getURL();
    String getSuffix();
    void setCountries(HashMap<String, String> countries);

    @Override
    default void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final CountryInfo info = getInfo();
        final String url = getURL();
        final EventSource source = new EventSource("Wikipedia", url);
        final EventSources sources = new EventSources(source);
        final Elements tables = getDocumentElements(url, "div.mw-parser-output table.wikitable");
        switch (info) {
            case AGRICULTURE_FOOD_APPLE_PRODUCTION:
                tables.remove(tables.size()-1);
                break;
            default:
                break;
        }

        int maxWorldRank = 0;
        for(Element table : tables) {
            final Elements trs = table.select("tbody tr");
            trs.removeIf(tr -> {
                final Elements tds = tr.select("td");
                return tds.isEmpty() || tds.get(0).text().equals("–");
            });
            maxWorldRank += trs.size();
        }

        final HashMap<String, String> countries = new HashMap<>();
        final String title = info.getTitle(), suffix = getSuffix();
        for(Element table : tables) {
            loadTable(countries, table, maxWorldRank, title, suffix, sources);
        }
        setCountries(countries);
        WLLogger.log(Level.INFO, info.name() + " - loaded " + getCountries().size() + " countries/territories (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(null);
    }

    private void loadTable(HashMap<String, String> countries, Element table, int maxWorldRank, String title, String suffix, EventSources sources) {
        final Elements trs = table.select("tbody tr");
        final int yearOfData = Integer.parseInt(trs.get(0).select("th").get(2).text());
        trs.remove(0);
        trs.removeIf(tr -> {
            final String text = tr.select("td").get(0).text();
            return text.equals("–") || text.equals("—");
        });
        for(Element tr : trs) {
            final Elements tds = tr.select("td");
            final int worldRank = Integer.parseInt(tds.get(0).text()), quantity = Integer.parseInt(tds.get(2).text().replace(",", ""));
            final String country = tds.get(1).select("a").get(0).text().toLowerCase().replace(" ", "");
            final CountryAgricultureValue value = new CountryAgricultureValue(maxWorldRank, worldRank, yearOfData, quantity, NumberType.INTEGER, true, title, suffix, sources);
            countries.put(country, value.toString());
        }
    }
}
