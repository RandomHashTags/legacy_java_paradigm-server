package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

public enum LegalityBitcoin implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_BITCOIN;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Legality_of_bitcoin_by_country_or_territory";
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("Wikipedia: Legality of bitcoin by country or territory", url);
        final EventSources sources = new EventSources(source);
        for(int i = 1; i <= 20; i++) {
            load(title, sources, tables.get(i));
        }
        handler.handle(null);
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
                final CountryInfoKey info = new CountryInfoKey(title, notes, -1, sources, legality, illegality);
                countries.put(country, info.toString());
            }
        }
    }
}
