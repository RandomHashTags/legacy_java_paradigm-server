package me.randomhashtags.worldlaws.info.agriculture;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.rankings.NumberType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryService;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public interface CountryProductionService extends NewCountryService {
    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_RANKINGS_AGRICULTURE;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.AGRICULTURE;
    }

    String getURL();
    String getSuffix();

    @Override
    default JSONObjectTranslatable loadData() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title");

        final SovereignStateInfo info = getInfo();
        final String url = getURL();
        final Elements tables = getDocumentElements(Folder.COUNTRIES_RANKINGS_AGRICULTURE, url, "div.mw-parser-output table.wikitable");
        switch (info) {
            case AGRICULTURE_FOOD_APPLE_PRODUCTION:
                tables.remove(tables.size()-1);
                break;
            case AGRICULTURE_FOOD_PEAR_PRODUCTION:
                tables.remove(0);
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

        final HashMap<String, CountryAgricultureValue> values = new HashMap<>();
        for(Element table : tables) {
            final HashMap<String, CountryAgricultureValue> data = loadTable(table);
            values.putAll(data);
        }

        for(String country : values.keySet()) {
            final CountryAgricultureValue value = values.get(country);
            json.put(country, value.toJSONObject());
        }
        return json;
    }

    @Override
    default void insertCountryData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable countryJSON) {
        final String suffix = LocalServer.fixEscapeValues(getSuffix());
        if(suffix != null) {
            countryJSON.put("suffix", suffix);
        }
        countryJSON.put("maxWorldRank", dataJSON.keySet().size());
        countryJSON.put("valueType", NumberType.INTEGER.name());
        countryJSON.put("title", getInfo().getTitle());
        countryJSON.addTranslatedKey("title");

        final String url = getURL();
        final String siteName = url.split("/wiki/")[1].replace("_", " ");
        final EventSource source = new EventSource("Wikipedia: " + siteName, url);
        final EventSources sources = new EventSources(source);
        countryJSON.put("sources", sources.toJSONObject());
    }

    @Override
    default JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String country : json.keySet()) {
            final JSONObject valueJSON = json.getJSONObject(country);
            final CountryAgricultureValue value = CountryAgricultureValue.parse(valueJSON);
            final JSONObjectTranslatable test = value.toJSONObject();
            test.put("title", getInfo().getTitle());
            test.addTranslatedKey("title");
            translatable.put(country, test);
            translatable.addTranslatedKey(country);
        }
        return translatable;
    }

    private HashMap<String, CountryAgricultureValue> loadTable(Element table) {
        final HashMap<String, CountryAgricultureValue> map = new HashMap<>();
        final Elements trs = table.select("tbody tr");
        final int yearOfData = Integer.parseInt(trs.get(0).select("th").get(2).text());
        trs.remove(0);
        trs.removeIf(tr -> {
            final String text = tr.select("td").get(0).text();
            return text.equals("–") || text.equals("—");
        });
        int worldRank = 0;
        for(Element tr : trs) {
            final Elements tds = tr.select("td");
            final String targetWorldRank = tds.get(0).text();
            int countryIndex = 1, quantityIndex = 2;
            if(targetWorldRank.matches("[0-9]+")) {
                worldRank = Integer.parseInt(targetWorldRank);
            } else {
                worldRank += 1;
                quantityIndex = 1;
                countryIndex = 0;
            }
            final int quantity = Integer.parseInt(tds.get(quantityIndex).text().replace(",", ""));
            final Element countryElement = tds.get(countryIndex);
            final Element targetCountryElement = countryElement.selectFirst("a");
            final String country = (targetCountryElement != null ? targetCountryElement.text() : targetCountryElement.textNodes().get(0).text()).toLowerCase().replace(" ", "").replace(" ", "").replace("*", "");
            final CountryAgricultureValue value = new CountryAgricultureValue(worldRank, yearOfData, quantity);
            map.put(country, value);
            worldRank += 1;
        }
        return map;
    }
}
