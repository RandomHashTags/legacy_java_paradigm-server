package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum BloodTypeDistribution implements CountryInfoService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.INFO_BLOOD_TYPE_DISTRIBUTION;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Blood_type_distribution_by_country";
        final EventSource source = new EventSource("Wikipedia: Blood type distribution by country", url);
        final EventSources sources = new EventSources(source);
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(trs.size()-1);
        final int yearOfData = WLUtilities.getTodayYear();
        for(Element element : trs) {
            final String country = element.select("th a").get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");

            final Elements tds = element.select("td");
            final CountryInfoValue oPositive = new CountryInfoValue("O+", tds.get(1).text(), null);
            final CountryInfoValue aPositive = new CountryInfoValue("A+", tds.get(2).text(), null);
            final CountryInfoValue bPositive = new CountryInfoValue("B+", tds.get(3).text(), null);
            final CountryInfoValue abPositive = new CountryInfoValue("AB+", tds.get(4).text(), null);
            final CountryInfoValue oNegative = new CountryInfoValue("O-", tds.get(5).text(), null);
            final CountryInfoValue aNegative = new CountryInfoValue("A-", tds.get(6).text(), null);
            final CountryInfoValue bNegative = new CountryInfoValue("B-", tds.get(7).text(), null);
            final CountryInfoValue abNegative = new CountryInfoValue("AB-", tds.get(8).text(), null);

            final CountryInfoKey info = new CountryInfoKey("Blood Type Distribution", null, yearOfData, sources, oPositive, aPositive, bPositive, abPositive, oNegative, aNegative, bNegative, abNegative);
            countries.put(country, info.toString());
        }
        handler.handle(null);
    }
}
