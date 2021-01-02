package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum BloodTypeDistribution implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.INFO_BLOOD_TYPE_DISTRIBUTION;
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
            WLLogger.log(Level.WARNING, "BloodTypeDistribution - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Blood_type_distribution_by_country";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(0).select("tbody tr");
            trs.remove(0);
            trs.remove(trs.size()-1);
            final EventSources sources = new EventSources(new EventSource("Wikipedia: Blood type distribution by country", url));
            for(Element element : trs) {
                final String country = element.select("th a").get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");

                final Elements tds = element.select("td");
                final CountryInfoValue oPositive = new CountryInfoValue("O+", tds.get(1).text(), "");
                final CountryInfoValue aPositive = new CountryInfoValue("A+", tds.get(2).text(), "");
                final CountryInfoValue bPositive = new CountryInfoValue("B+", tds.get(3).text(), "");
                final CountryInfoValue abPositive = new CountryInfoValue("AB+", tds.get(4).text(), "");
                final CountryInfoValue oNegative = new CountryInfoValue("O-", tds.get(5).text(), "");
                final CountryInfoValue aNegative = new CountryInfoValue("A-", tds.get(6).text(), "");
                final CountryInfoValue bNegative = new CountryInfoValue("B-", tds.get(7).text(), "");
                final CountryInfoValue abNegative = new CountryInfoValue("AB-", tds.get(8).text(), "");

                final CountryInfoKey info = new CountryInfoKey("Blood Type Distribution", null, sources, oPositive, aPositive, bPositive, abPositive, oNegative, aNegative, bNegative, abNegative);
                countries.put(country, info.toString());
            }
            WLLogger.log(Level.INFO, "BloodTypeDistribution - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }
}
