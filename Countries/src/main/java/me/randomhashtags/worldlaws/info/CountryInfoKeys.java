package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
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
    public String loadData() {
        switch (this) {
            case AGE_STRUCTURE: return loadAgeStructure();
            case BLOOD_TYPE_DISTRIBUTION: return loadBloodTypeDistribution();
            case MEDIAN_AGE: return loadMedianAge();
            case MINIMUM_ANNUAL_LEAVE: return loadMinimumAnnualLeave();
            case MINIMUM_WAGE: return loadMinimumWage();
            default: return null;
        }
    }

    private String loadAgeStructure() {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
            final CountryInfoValue zeroTo14 = new CountryInfoValue("0-14", tds.get(1).text().replace(" ", ""), "Children and adolescents");
            final CountryInfoValue fifteenTo64 = new CountryInfoValue("15-64", tds.get(2).text().replace(" ", ""), "Working population or population in education");
            final CountryInfoValue over65 = new CountryInfoValue("65+", tds.get(3).text().replace(" ", ""), "Retirees and elderly");

            final CountryInfoKey info = new CountryInfoKey(null, -1, zeroTo14, fifteenTo64, over65);
            info.country = country;
            builder.append(isFirst ? "" : ",").append(info.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadBloodTypeDistribution() {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(trs.size()-1);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
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

            final CountryInfoKey info = new CountryInfoKey(null, -1, oPositive, aPositive, bPositive, abPositive, oNegative, aNegative, bNegative, abNegative);
            info.country = country;
            builder.append(isFirst ? "" : ",").append(info.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMedianAge() {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        trs.remove(0);
        trs.removeIf(element -> {
            return element.select("a[href]").size() == 0;
        });
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = element.selectFirst("a[href]").text().toLowerCase().split("\\(")[0].replace(" ", "");
            final CountryInfoValue combined = new CountryInfoValue("Combined", tds.get(2).text().replace(" ", ""), null);
            final CountryInfoValue male = new CountryInfoValue("Male", tds.get(3).text().replace(" ", ""), null);
            final CountryInfoValue female = new CountryInfoValue("Female", tds.get(4).text().replace(" ", ""), null);

            final CountryInfoKey info = new CountryInfoKey(null, -1, combined, male, female);
            info.country = country;
            builder.append(isFirst ? "" : ",").append(info.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMinimumAnnualLeave() {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            if(tds.size() > 0 && tds.get(0).select("a").size() > 0) {
                final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
                final String annualLeaveText = removeReferences(tds.get(1).text());
                final CountryInfoValue annualLeave = new CountryInfoValue("Annual Leave", annualLeaveText, null);

                final String paidVacationDaysText = removeReferences(tds.get(2).text());
                final CountryInfoValue paidVacationDays = new CountryInfoValue("Paid Vacation Days", paidVacationDaysText, null);

                final String paidPublicHolidaysText = removeReferences(tds.get(3).text());
                final CountryInfoValue paidPublicHolidays = new CountryInfoValue("Paid Public Holidays", paidPublicHolidaysText, null);

                final String totalPaidLeaveText = removeReferences(tds.get(4).text());
                final CountryInfoValue totalPaidLeave = new CountryInfoValue("Total Paid Leave", totalPaidLeaveText, null);

                final CountryInfoKey info = new CountryInfoKey(null, -1, annualLeave, paidVacationDays, paidPublicHolidays, totalPaidLeave);
                info.country = country;
                builder.append(isFirst ? "" : ",").append(info.toServerJSON());
                isFirst = false;
            }
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMinimumWage() {
        final Elements trs = getInfoDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
            final String notes = getNotesFromElement(tds.get(1)), notesLowercase = notes.toLowerCase();
            final boolean isNone = notesLowercase.startsWith("none") || notesLowercase.contains("has no minimum wage");

            final String hoursValue = removeReferences(tds.get(4).text());
            final CountryInfoValue workweekHours = new CountryInfoValue("Workweek hours", hoursValue, null);

            final String annualValue = removeReferences(tds.get(2).text());
            final CountryInfoValue annual = new CountryInfoValue("Annual (USD)", annualValue.isEmpty() ? "Unknown" : isNone ? "None" : "$" + annualValue, null);

            final String hourlyValue = removeReferences(tds.get(5).text());
            final CountryInfoValue hourly = new CountryInfoValue("Hourly (USD)", hourlyValue.isEmpty() ? "Unknown" : isNone ? "None" : "$" + hourlyValue, null);

            final String effectivePer = tds.get(8).text();
            final String[] effectiveValues = effectivePer.split(" ");
            final int yearOfData = Integer.parseInt(effectiveValues[effectiveValues.length-1]);
            final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, workweekHours, annual, hourly);
            info.country = country;
            builder.append(isFirst ? "" : ",").append(info.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
}
