package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

// https://aceproject.org/epic-en/CDTable?view=country&question=VR001
// https://en.wikipedia.org/wiki/Voting_age
public enum VotingAge implements CountryValueService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.VALUE_VOTING_AGE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://aceproject.org/epic-en/CDTable?view=country&question=VR001";
        final Elements trs = getValueDocumentElements(url, "div.documentContent table.CDlisting tbody tr");
        trs.remove(0);
        trs.remove(0);
        final String title = getInfo().getTitle();
        final EventSource source = new EventSource("ACE Electoral Knowledge Network", url);
        final EventSources sources = new EventSources(source);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "").replace(",", "").split("\\(")[0]
                    .replace("korearepublicof", "northkorea")
                    .replace("koreademocraticpeople'srepublicof", "southkorea")
                    .replace("burma", "myanmar")
                    .replace("islamic", "")
                    .replace("republicof", "")
                    .replace("federatedstatesof", "")
                    .replace("theformeryugoslav", "")
                    .replace("unitedstatesofamerica", "unitedstates")
                    .replace("tanzaniaunited", "tanzania")
                    .replace("russianfederation", "russia")
                    .replace("netherlandsantilles", "netherlands")
                    .replace("unitedkingdomofgreatbritainandnorthernireland", "unitedkingdom")
                    ;
            final int yearOfData = Integer.parseInt(tds.get(3).text().split("/")[0]);
            final String value = removeReferences(tds.get(1).text().substring(3));
            String valueDescription = tds.get(2).text();
            if(valueDescription.contains(" Source:")) {
                valueDescription = valueDescription.split(" Source:")[0];
            }
            final CountrySingleValue singleValue = new CountrySingleValue(title, null, value, valueDescription, yearOfData, sources);
            countries.put(country, singleValue.toString());
        }
        handler.handle(null);
    }
}
