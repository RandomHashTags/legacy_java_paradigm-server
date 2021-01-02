package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public enum LegalityBitcoin implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_BITCOIN;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityBitcoin - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legality_of_bitcoin_by_country_or_territory";
        final Document doc = getDocument(url);
        if(doc != null) {
            final String title = getInfo().getTitle();
            final Elements tables = doc.select("div.mw-parser-output table.wikitable");
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Legality of bitcoin by country or territory", url));
            load(title, sources, tables.get(1)); // Northern Africa
            load(title, sources, tables.get(2)); // Western Africa
            load(title, sources, tables.get(3)); // Indian Ocean States
            load(title, sources, tables.get(4)); // Southern Africa
            load(title, sources, tables.get(5)); // North America
            load(title, sources, tables.get(6)); // Central America
            load(title, sources, tables.get(7)); // Caribbean
            load(title, sources, tables.get(8)); // South America
            load(title, sources, tables.get(9)); // Central Asia
            load(title, sources, tables.get(10)); // Eurasia
            load(title, sources, tables.get(11)); // West Asia
            load(title, sources, tables.get(12)); // South Asia
            load(title, sources, tables.get(13)); // East Asia
            load(title, sources, tables.get(14)); // Southeast Asia
            load(title, sources, tables.get(15)); // Central Europe
            load(title, sources, tables.get(16)); // Eastern Europe
            load(title, sources, tables.get(17)); // Northern Europe
            load(title, sources, tables.get(18)); // Southern Europe
            load(title, sources, tables.get(19)); // Western Europe
            load(title, sources, tables.get(20)); // Australasia
            WLLogger.log(Level.INFO, "LegalityBitcoin - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    private void load(String title, EventSources sources, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        final String legal = "/wiki/File:Yes_check.svg";
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "").replace(",", "");

            final Element secondElement = tds.get(1);
            final Elements links = secondElement.select("a");
            links.removeIf(link -> !link.attr("class").equals("image"));
            if(!links.isEmpty()) {
                final List<TextNode> textNodes = secondElement.textNodes();
                final Element legalElement = links.get(0);
                final boolean isLegal = legalElement.attr("href").equals(legal);
                final String legalityTextNodeText = textNodes.get(0).text().replace(" /", "").replace("/", "");
                final String legalityText = isLegal ? legalityTextNodeText.substring(1, legalityTextNodeText.length()-1) : "Illegal";

                final CountryInfoValue legality = new CountryInfoValue("Legality", legalityText, null);
                CountryInfoValue illegality = null;
                if(links.size() == 2) {
                    String text = textNodes.get(1).text().substring(1);
                    text = text.substring(0, text.length()-1);
                    illegality = new CountryInfoValue("Illegality", text, null);
                }

                final StringBuilder builder = new StringBuilder();
                boolean isFirst = true;
                for(Element paragraph : tds.get(1).select("p")) {
                    builder.append(isFirst ? "" : "\n").append(removeReferences(paragraph.text()));
                    isFirst = false;
                }
                final String notes = builder.toString();
                final CountryInfoKey info = new CountryInfoKey(title, notes, sources, legality, illegality);
                countries.put(country, info.toString());
            }
        }
    }
}
