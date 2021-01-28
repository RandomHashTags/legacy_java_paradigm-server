package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum MinimumAnnualLeave implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.INFO_MINIMUM_ANNUAL_LEAVE;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_minimum_annual_leave_by_country";
        final EventSource source = new EventSource("Wikipedia: List of minimum annual leave by country", url);
        final EventSources sources = new EventSources(source);
        final Elements trs = getDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final String title = getInfo().getTitle();
        final int yearOfData = WLUtilities.getTodayYear();
        for(Element element : trs) {
            final Elements tds = element.select("td");
            if(tds.size() > 0 && tds.get(0).select("a").size() > 0) {
                final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
                final String annualLeaveText = removeReferences(tds.get(1).text());
                final CountryInfoValue annualLeave = new CountryInfoValue("Annual Leave", annualLeaveText, "");

                final String paidVacationDaysText = removeReferences(tds.get(2).text());
                final CountryInfoValue paidVacationDays = new CountryInfoValue("Paid Vacation Days", paidVacationDaysText, "");

                final String paidPublicHolidaysText = removeReferences(tds.get(3).text());
                final CountryInfoValue paidPublicHolidays = new CountryInfoValue("Paid Public Holidays", paidPublicHolidaysText, "");

                final String totalPaidLeaveText = removeReferences(tds.get(4).text());
                final CountryInfoValue totalPaidLeave = new CountryInfoValue("Total Paid Leave", totalPaidLeaveText, "");

                final CountryInfoKey info = new CountryInfoKey(title, null, yearOfData, sources, annualLeave, paidVacationDays, paidPublicHolidays, totalPaidLeave);
                countries.put(country, info.toString());
            }
        }
        handler.handle(null);
    }
}
