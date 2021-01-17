package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.location.CustomCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.logging.Level;

public enum Holidays implements Jsoupable {
    INSTANCE;

    private int COMPLETED_HANDLERS;
    private String json, nearHolidays;
    private final Elements list;
    private final HashMap<CustomCountry, String> countryJSON;
    private final HashMap<CustomCountry, Set<Holiday>> countryHolidays;

    private HashMap<String, String> countries;

    Holidays() {
        countryJSON = new HashMap<>();
        countryHolidays = new HashMap<>();
        list = getDocumentElements("https://en.wikipedia.org/wiki/List_of_holidays_by_country", "div.mw-body div.mw-body-content div.mw-content-ltr div.mw-parser-output div.div-col ul li a[href]");
    }

    @SuppressWarnings({ "unchecked" })
    public void getAllHolidays(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            json = "{}";
            final long time = System.currentTimeMillis();
            WLUtilities.getCustomCountryCollection(new CompletionHandler() {
                @Override
                public void handleCollection(Collection<?> collection) {
                    final Collection<CustomCountry> countries = (Collection<CustomCountry>) collection;
                    final int max = countries.size();
                    for(CustomCountry country : countries) {
                        new Thread(() -> getHolidaysFor(country, new CompletionHandler() {
                            @Override
                            public void handle(Object object) {
                                completedHandler();
                                if(getCompletedHandlers() == max) {
                                    updateJSON();
                                    WLLogger.log(Level.INFO, "Holidays - updated all holidays (took " + (System.currentTimeMillis()-time) + "ms)");
                                    handler.handle(json);
                                }
                            }
                        })).start();
                    }
                }
            });
        }
    }
    public void getNearHolidays(CompletionHandler handler) {
        if(nearHolidays != null) {
            handler.handle(nearHolidays);
        } else {
            getAllHolidays(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final long started = System.currentTimeMillis();
                    final StringBuilder builder = new StringBuilder("{");
                    final LocalDate date = LocalDate.now();
                    final Month todayMonth = date.getMonth();
                    final int todayDay = date.getDayOfMonth(), monthDays = todayMonth.maxLength(), nextMonthValue = todayMonth.getValue()+1;
                    boolean isFirstCountry = true;

                    for(Map.Entry<CustomCountry, Set<Holiday>> map : countryHolidays.entrySet()) {
                        final CustomCountry country = map.getKey();
                        final Set<Holiday> holidays = map.getValue();
                        holidays.removeIf(holiday -> {
                            final EventDate holidayDate = holiday.getDate();
                            final int targetDay = holidayDate.getDay();
                            final Month targetMonth = holidayDate.getMonth();
                            final boolean sameMonth = todayMonth.equals(targetMonth);
                            return targetDay == -1 || !(sameMonth ? targetDay >= todayDay : todayDay+7 >= monthDays && targetDay <= (todayDay+7-monthDays) && nextMonthValue == targetMonth.getValue());
                        });

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
                    WLLogger.log(Level.INFO, "Holidays - updated near holidays (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(nearHolidays);
                }
            });
        }
    }
    private void updateJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(CustomCountry country : countryJSON.keySet()) {
            final String string = "\"" + country.getName() + "\":" + countryJSON.get(country);
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
        }
        builder.append("}");
        json = builder.toString();
    }

    public void getHolidaysFor(CustomCountry country, CompletionHandler handler) {
        if(countryJSON.containsKey(country)) {
            handler.handle(countryJSON.get(country));
        } else {
            final String shortName = country.getShortName().replace("&", "and");
            final String name = country.getName();
            for(Element element : list) {
                String text = element.text();
                if(text.contains("Public holidays in ")) {
                    final String[] values = text.split("Public holidays in the ");
                    text = values.length == 1 ? text.split("Public holidays in ")[1] : values[1];
                    if(text.equalsIgnoreCase(name) || text.equalsIgnoreCase(shortName)) {
                        final String url = "https://en.wikipedia.org" + element.attr("href");
                        getHolidays(country, url, handler);
                        return;
                    }
                }
            }
            handler.handle(null);
        }
    }
    private void getHolidays(CustomCountry country, String url, CompletionHandler handler) {
        final Document doc = getDocument(url);
        final StringBuilder builder = new StringBuilder("[");
        final Elements table = doc.select("table.wikitable tbody tr");
        boolean isFirst = true;
        final HashSet<String> names = new HashSet<>();
        final HashSet<Holiday> holidays = new HashSet<>();
        for(Element row : table) {
            final Elements array = row.select("td");
            final int arraySize = array.size();
            if(!array.isEmpty() && arraySize >= 2) {
                final Element englishElement = array.get(1);
                final String englishName = englishElement.text();
                if(!names.contains(englishName)) {
                    names.add(englishName);
                    final boolean hasSuperScript = !array.select("sup").isEmpty();
                    String dateString = array.get(0).text();
                    if(hasSuperScript) {
                        final String superscript = array.select("sup").get(0).text();
                        final int length = dateString.length(), target = length-superscript.length();
                        dateString = dateString.substring(0, target < 0 ? length : target);
                    }
                    final Elements firstElements = englishElement.select("a[href]"), secondElements = arraySize >= 3 && firstElements.isEmpty() ? array.get(2).select("a[href]") : null;
                    final Elements hrefElement = (secondElements != null && !secondElements.isEmpty() ? secondElements : firstElements);
                    final String href = hrefElement.attr("href");
                    final String learnMoreURL = href.isEmpty() ? url : "https://wikipedia.org" + href;
                    final EventDate date = getDateFrom(country, dateString);
                    final Holiday holiday = new Holiday(date, englishName, learnMoreURL);
                    holidays.add(holiday);
                    builder.append(isFirst ? "" : ",").append(holiday.toString());
                    isFirst = false;
                }
            }
        }
        builder.append("]");
        final String string = builder.toString();
        countryJSON.put(country, string);
        countryHolidays.put(country, holidays);
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
    private EventDate getDateFrom(CustomCountry country, String input) {
        return getDateFrom(input);
        /*witch (country) {
            case UNITED_STATES_OF_AMERICA:

            default:
                return new EventDate(Month.JANUARY, -1, -1);
        }*/
    }

    private synchronized void completedHandler() {
        COMPLETED_HANDLERS += 1;
    }
    private synchronized int getCompletedHandlers() {
        return COMPLETED_HANDLERS;
    }

    private final class Holiday {
        private final EventDate date;
        private final String englishName, learnMoreURL;

        public Holiday(EventDate date, String englishName, String learnMoreURL) {
            this.date = date;
            this.englishName = LocalServer.fixEscapeValues(englishName);
            this.learnMoreURL = learnMoreURL;
        }

        public EventDate getDate() {
            return date;
        }
        public String getEnglishName() {
            return englishName;
        }
        public String getLearnMoreURL() {
            return learnMoreURL;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"date\":" + date.toString() + "," +
                    "\"englishName\":\"" + englishName + "\"," +
                    "\"learnMoreURL\":\"" + learnMoreURL + "\"" +
                    "}";
        }
    }
}