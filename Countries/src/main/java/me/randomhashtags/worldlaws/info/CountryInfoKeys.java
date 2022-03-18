package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public enum CountryInfoKeys implements CountryInfoService {
    AGE_STRUCTURE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_age_structure",
            2017
    ),
    BLOOD_TYPE_DISTRIBUTION(
            "https://en.wikipedia.org/wiki/Blood_type_distribution_by_country",
            WLUtilities.getTodayYear()
    ),
    MEDIAN_AGE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_median_age",
            2020
    ),
    MINIMUM_ANNUAL_LEAVE(
            "https://en.wikipedia.org/wiki/List_of_minimum_annual_leave_by_country",
            WLUtilities.getTodayYear()
    ),
    MINIMUM_WAGE(
            "https://en.wikipedia.org/wiki/List_of_countries_by_minimum_wage",
            -1
    ),
    ;

    private final String url;
    private final int yearOfData;

    CountryInfoKeys(String url, int yearOfData) {
        this.url = url;
        this.yearOfData = yearOfData;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.valueOf("INFO_" + name());
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getYearOfData() {
        return yearOfData;
    }


    @Override
    public JSONObjectTranslatable loadData() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        loadJSONData(json);
        for(String country : json.keySet()) {
            json.put(country, json.get(country));
            json.addTranslatedKey(country);
        }
        return json;
    }

    @Override
    public JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String country : json.keySet()) {
            final JSONObject countryJSON = json.getJSONObject(country);
            final CountryInfoKey value = CountryInfoKey.parse(countryJSON);
            translatable.put(country, value.toJSONObject());
            translatable.addTranslatedKey(country);
        }
        return translatable;
    }

    private void loadJSONData(JSONObjectTranslatable json) {
        switch (this) {
            case AGE_STRUCTURE:
                loadAgeStructure(json);
                break;
            case BLOOD_TYPE_DISTRIBUTION:
                loadBloodTypeDistribution(json);
                break;
            case MEDIAN_AGE:
                loadMedianAge(json);
                break;
            case MINIMUM_ANNUAL_LEAVE:
                loadMinimumAnnualLeave(json);
                break;
            case MINIMUM_WAGE:
                loadMinimumWage(json);
                break;
            default:
                break;
        }
    }

    private void loadAgeStructure(JSONObjectTranslatable json) {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
            final CountryInfoValue zeroTo14 = new CountryInfoValue("0-14", tds.get(1).text().replace(" ", ""), "Children and adolescents");
            final CountryInfoValue fifteenTo64 = new CountryInfoValue("15-64", tds.get(2).text().replace(" ", ""), "Working population or population in education");
            final CountryInfoValue over65 = new CountryInfoValue("65+", tds.get(3).text().replace(" ", ""), "Retirees and elderly");

            final CountryInfoKey info = new CountryInfoKey(null, yearOfData, zeroTo14, fifteenTo64, over65);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadBloodTypeDistribution(JSONObjectTranslatable json) {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(trs.size()-1);
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

            final CountryInfoKey info = new CountryInfoKey(null, yearOfData, oPositive, aPositive, bPositive, abPositive, oNegative, aNegative, bNegative, abNegative);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadMedianAge(JSONObjectTranslatable json) {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        trs.remove(0);
        trs.removeIf(element -> element.select("a[href]").size() == 0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = element.selectFirst("a[href]").text().toLowerCase().split("\\(")[0].replace(" ", "");
            final CountryInfoValue combined = new CountryInfoValue("Combined", tds.get(2).text().replace(" ", ""), null);
            final CountryInfoValue male = new CountryInfoValue("Male", tds.get(3).text().replace(" ", ""), null);
            final CountryInfoValue female = new CountryInfoValue("Female", tds.get(4).text().replace(" ", ""), null);

            final CountryInfoKey info = new CountryInfoKey(null, yearOfData, combined, male, female);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadMinimumAnnualLeave(JSONObjectTranslatable json) {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            if(tds.size() > 0 && tds.get(0).select("a").size() > 0) {
                final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
                final String annualLeaveText = tds.get(1).text();
                final CountryInfoValue annualLeave = new CountryInfoValue("Annual Leave", annualLeaveText, null);

                final String paidVacationDaysText = tds.get(2).text();
                final CountryInfoValue paidVacationDays = new CountryInfoValue("Paid Vacation Days", paidVacationDaysText, null);

                final String paidPublicHolidaysText = tds.get(3).text();
                final CountryInfoValue paidPublicHolidays = new CountryInfoValue("Paid Public Holidays", paidPublicHolidaysText, null);

                final String totalPaidLeaveText = tds.get(4).text();
                final CountryInfoValue totalPaidLeave = new CountryInfoValue("Total Paid Leave", totalPaidLeaveText, null);

                final CountryInfoKey info = new CountryInfoKey(null, yearOfData, annualLeave, paidVacationDays, paidPublicHolidays, totalPaidLeave);
                json.put(country, info.toJSONObject());
            }
        }
    }
    private void loadMinimumWage(JSONObjectTranslatable json) {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
            final String notes = getNotesFromElement(tds.get(1)), notesLowercase = notes.toLowerCase();
            final boolean isNone = notesLowercase.startsWith("none") || notesLowercase.contains("has no minimum wage");

            final String hoursValue = tds.get(4).text();
            final CountryInfoValue workweekHours = new CountryInfoValue("Workweek hours", hoursValue, null);

            final String annualValue = tds.get(2).text();
            final CountryInfoValue annual = new CountryInfoValue("Annual (USD)", annualValue.isEmpty() ? "Unknown" : isNone ? "None" : "$" + annualValue, null);

            final String hourlyValue = tds.get(5).text();
            final CountryInfoValue hourly = new CountryInfoValue("Hourly (USD)", hourlyValue.isEmpty() ? "Unknown" : isNone ? "None" : "$" + hourlyValue, null);

            final String effectivePer = tds.get(8).text();
            final String[] effectiveValues = effectivePer.split(" ");
            final int yearOfData = Integer.parseInt(effectiveValues[effectiveValues.length-1]);
            final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, workweekHours, annual, hourly);
            json.put(country, info.toJSONObject());
        }
    }
}
