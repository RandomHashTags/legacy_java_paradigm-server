package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum LegalityProstitution implements CountryLegalityService {
    INSTANCE;

    private HashMap<String, String> countries;
    private HashMap<Integer, String> descriptions, values;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_PROSTITUTION;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    private void loadValues() {
        descriptions = new HashMap<>();
        descriptions.put(0, "Prostitution itself (exchanging sex for money) is illegal. The punishment for prostitution varies considerably: in some countries, it can incur the death penalty, in other jurisdictions, it is a crime punishable with a prison sentence, while in others it is a lesser administrative offense punishable only with a fine.");
        descriptions.put(1, "Although prostitutes themselves commit no crime, clients and any third party involvement is criminalised. Also called the \"Swedish model\" or \"Nordic model\".");
        descriptions.put(2, "Prostitution is permitted, prohibited or regulated by local laws rather than national laws. For example, in Mexico, prostitution is prohibited in some states but regulated in others.");
        descriptions.put(3, "There is no specific law prohibiting the exchange of sex for money, but in general most forms of procuring (pimping) are illegal. These countries also generally have laws against soliciting in a public place (e.g., a street) or advertising prostitution, making it difficult to engage in prostitution without breaking any law. In countries like India, though prostitution is legal, it is illegal when committed in a hotel.");
        descriptions.put(4, "In some countries, prostitution is legal and regulated; although activities like pimping and street-walking are restricted or generally illegal. The degree of regulation varies by country.");
        descriptions.put(5, "The decriminalization of sex work is the removal of criminal penalties for sex work. Removing criminal prosecution for sex workers creates a safer and healthier environment and allows them to live with less social exclusion and stigma.");

        values = new HashMap<>();
        values.put(0, "Illegal");
        values.put(1, "Kinda Legal");
        values.put(2, "Kinda Legal");
        values.put(3, "Kinda Legal");
        values.put(4, "Legal");
        values.put(5, "Decriminalized");
    }

    @Override
    public void refresh(CompletionHandler handler) {
        loadValues();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Prostitution_law";
        final Elements hrefs = getLegalityDocumentElements(url, "div.mw-parser-output h3 + p + ul");
        final EventSource source = new EventSource("Wikipedia: Prostitution law", url);
        final EventSources sources = new EventSources(source);
        for(int i = 0; i < 6; i++) {
            final String description = descriptions.get(i), value = values.get(i);
            load(hrefs.get(i), value, description, sources);
        }
        handler.handle(null);
    }
    private void load(Element list, String value, String description, EventSources sources) {
        final Elements elements = list.select("li a");
        elements.removeIf(row -> !row.attr("href").startsWith("/wiki/Prostitution_in"));
        final String title = getInfo().getTitle();
        final int yearOfData = 2018;
        for(Element element : elements) {
            final String country = element.text().toLowerCase().replace(" ", "").replace(",", "")
                    .replace("republicofmacedonia", "macedonia")
                    .replace("prostitutioninthe", "")
                    ;

            final CountryInfoValue infoValue = new CountryInfoValue("Prostitution", value, null);

            final CountryInfoKey info = new CountryInfoKey(title, description, yearOfData, sources, infoValue);
            countries.put(country, info.toString());
        }
    }
}
