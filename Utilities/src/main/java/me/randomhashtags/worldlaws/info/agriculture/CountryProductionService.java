package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryProductionService extends CountryService {
    @Override
    default FileType getFileType() {
        return FileType.COUNTRIES_RANKINGS_AGRICULTURE;
    }

    String getURL();
    String getSuffix();
    void setCountries(HashMap<String, String> countries);

    @Override
    default void refresh(CompletionHandler handler) {
        getJSONArray(this, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final CountryInfo info = getInfo();
                final String url = getURL();
                final Elements tables = getDocumentElements(FileType.COUNTRIES_RANKINGS_AGRICULTURE, url, "div.mw-parser-output table.wikitable");
                switch (info) {
                    case AGRICULTURE_FOOD_APPLE_PRODUCTION:
                        tables.remove(tables.size()-1);
                        break;
                    default:
                        break;
                }

                for(Element table : tables) {
                    final Elements trs = table.select("tbody tr");
                    trs.removeIf(tr -> {
                        final Elements tds = tr.select("td");
                        return tds.isEmpty() || tds.get(0).text().equals("–");
                    });
                }

                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Element table : tables) {
                    String data = loadTable(table);
                    if(isFirst) {
                        data = data.substring(1);
                        isFirst = false;
                    }
                    builder.append(data);
                }
                builder.append("]");
                handler.handle(builder.toString());
            }

            @Override
            public void handleJSONArray(JSONArray array) {
                final int maxWorldRank = array.length();
                final String url = getURL(), title = getInfo().getTitle(), suffix = getSuffix();
                final String siteName = url.split("/wiki/")[1].replace("_", " ");
                final EventSource source = new EventSource("Wikipedia: " + siteName, url);
                final EventSources sources = new EventSources(source);
                final HashMap<String, String> countries = new HashMap<>();
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String country = json.getString("country");
                    final CountryAgricultureValue value = new CountryAgricultureValue(json);
                    value.setMaxWorldRank(maxWorldRank);
                    value.setDescription(title);
                    value.setSuffix(suffix);
                    value.setSources(sources);
                    countries.put(country, value.toString());
                }
                setCountries(countries);
                handler.handle(null);
            }
        });
    }

    private String loadTable(Element table) {
        final StringBuilder builder = new StringBuilder();
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
            final CountryAgricultureValue value = new CountryAgricultureValue(worldRank, yearOfData, quantity);
            value.country = country;
            builder.append(",").append(value.toServerJSON());
        }
        return builder.toString();
    }
}
