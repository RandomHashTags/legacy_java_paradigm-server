package me.randomhashtags.worldlaws.holiday;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.location.Country;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum Holidays implements Jsoupable {
    INSTANCE;

    private volatile byte completedHandlers;
    private volatile String json;
    private volatile Elements list;
    private volatile HashMap<Country, String> countryJSON;
    private volatile HashMap<Country, Set<Holiday>> countryHolidays;
    private String nearHolidays;

    Holidays() {
        countryJSON = new HashMap<>();
        countryHolidays = new HashMap<>();
        list = getDocument("https://en.wikipedia.org/wiki/List_of_holidays_by_country").select("div.mw-body div.mw-body-content div.mw-content-ltr div.mw-parser-output div.div-col ul li a[href]");
    }

    public void getAllHolidays(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            final Country[] countries = Country.values();
            final int max = countries.length;
            for(Country country : countries) {
                new Thread(() -> getHolidaysFor(country, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        completedHandlers += 1;

                        if(completedHandlers == max) {
                            updateJSON();
                            handler.handle(json);
                        }
                    }
                })).start();
            }
        }
    }
    public void getNearHolidays(CompletionHandler handler) {
        if(nearHolidays != null) {
            handler.handle(nearHolidays);
        } else {
            getAllHolidays(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final long time = System.currentTimeMillis();
                    final StringBuilder builder = new StringBuilder("{");
                    final LocalDate date = LocalDate.now();
                    final Month month = date.getMonth();
                    final int day = date.getDayOfMonth();
                    boolean isFirstCountry = true;
                    for(Country country : countryHolidays.keySet()) {
                        final Set<Holiday> holidays = new HashSet<>(countryHolidays.get(country));
                        holidays.removeIf(holiday -> !month.equals(holiday.getDate().getMonth()));

                        if(!holidays.isEmpty()) {
                            final StringBuilder array = new StringBuilder("[");
                            boolean isFirstArray = true;
                            for(Holiday holiday : holidays) {
                                array.append(isFirstArray ? "" : ",").append(holiday.toString());
                                isFirstArray = false;
                            }
                            array.append("]");
                            builder.append(isFirstCountry ? "" : ",").append("\"").append(country.getBackendID()).append("\":").append(array.toString());
                            isFirstCountry = false;
                        }
                    }
                    builder.append("}");
                    nearHolidays = builder.toString();
                    System.out.println("Holidays - updated near holidays (took " + (System.currentTimeMillis()-time) + "ms)");
                    handler.handle(nearHolidays);
                }
            });
        }
    }
    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Country country : countryJSON.keySet()) {
            final String string = "\"" + country.getName() + "\":" + countryJSON.get(country);
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
        }
        builder.append("}");
        json = builder.toString();
    }

    public void getHolidaysFor(Country country, CompletionHandler handler) {
        if(countryJSON.containsKey(country)) {
            handler.handle(countryJSON.get(country));
        } else {
            final String name = country.getName();
            final HashSet<String> aliases = country.getAliases();
            for(Element element : list) {
                String text = element.text();
                if(text.contains("Public holidays in ")) {
                    text = text.split("Public holidays in ")[1];
                    if(text.equalsIgnoreCase(name) || aliases.contains(text)) {
                        final String url = "https://en.wikipedia.org" + element.attr("href");
                        getHolidays(country, url, handler);
                        return;
                    }
                }
            }
            handler.handle(null);
        }
    }
    private void getHolidays(Country country, String url, CompletionHandler handler) {
        final long time = System.currentTimeMillis();
        final Document doc = getDocument(url);
        final StringBuilder builder = new StringBuilder("[");
        final Elements table = doc.select("table.wikitable tbody tr");
        boolean isFirst = true;
        final HashSet<Holiday> holidays = new HashSet<>();
        for(Element row : table) {
            final Elements array = row.select("td");
            if(!array.isEmpty() && array.size() >= 2) {
                final Element englishElement = array.get(1);
                final String dateString = array.get(0).text(), englishName = englishElement.text(), learnMoreURL = "https://wikipedia.org" + englishElement.select("a[href]").attr("href");
                final EventDate date = getDateFrom(country, dateString);
                final Holiday holiday = new Holiday(date, englishName, learnMoreURL);
                holidays.add(holiday);
                builder.append(isFirst ? "" : ",").append(holiday.toString());
                isFirst = false;
            }
        }
        builder.append("]");
        final String string = builder.toString();
        countryJSON.put(country, string);
        countryHolidays.put(country, holidays);
        System.out.println("Holidays - updated holidays for country \"" + country.getName() + "\" (took " + (System.currentTimeMillis()-time) + "ms)");
        handler.handle(string);
    }
    private EventDate getDateFrom(String input) {
        input = input.toLowerCase().split(" - ")[0].split("-")[0].split(", ")[0].split(" and ")[0].split(" \\(")[0];
        final String[] values = input.split(" ");
        try {
            final Month month = Month.valueOf(values[0].toUpperCase());
            final int day = Integer.parseInt(values[1]);
            return new EventDate(month, day, -2);
        } catch (Exception e) {
            return new EventDate(Month.JANUARY, -1, -1);
        }
    }
    private EventDate getDateFrom(Country country, String input) {
        return getDateFrom(input);
        /*witch (country) {
            case UNITED_STATES_OF_AMERICA:

            default:
                return new EventDate(Month.JANUARY, -1, -1);
        }*/
    }
}
