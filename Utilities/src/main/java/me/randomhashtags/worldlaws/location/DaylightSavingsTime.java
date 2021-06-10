package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.*;
import org.json.JSONObject;
import org.jsoup.select.Elements;

import java.time.*;
import java.util.HashMap;
import java.util.TimeZone;

import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static java.time.temporal.TemporalAdjusters.lastInMonth;

public enum DaylightSavingsTime implements Jsonable { // TODO: update to jsonable
    INSTANCE;

    private JSONObject json;

    private Elements ELEMENTS;
    private final HashMap<String, String> observations;

    private LocalDateTime startDate, endDate;

    DaylightSavingsTime() {
        observations = new HashMap<>();
        resetVariables();
    }
    private void resetVariables() {
        startDate = null;
        endDate = null;
    }

    private Elements getElements() {
        if(ELEMENTS == null) {
            ELEMENTS = Jsoupable.getStaticDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/Daylight_saving_time_by_country", true, "table.wikitable tbody tr");
            ELEMENTS.remove(0);
            ELEMENTS.removeIf(row -> row.select("td").get(3).text().equals("–"));
        }
        return ELEMENTS;
    }

    private void getJSONObject(CompletionHandler handler) {
        getJSONObject(FileType.COUNTRIES, "Daylight Savings Time", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                DaylightSavingsTime.INSTANCE.json = json;
                handler.handleJSONObject(json);
            }
        });
    }

    private void loadDaylightSavingsTimes() {
        final String url = "https://en.wikipedia.org/wiki/Daylight_saving_time_by_country";
    }

    public SovereignStateDST getFrom(String country, UTCOffset offset) {
        country = country.toLowerCase();
        final boolean observed = isObserved(country);
        if(observed) {
            try {
                isDaylightSavingsTime(country, offset);
            } catch (Exception e) {
                e.printStackTrace();
                resetVariables();
                return null;
            }
            final SovereignStateDST dst = new SovereignStateDST(startDate, endDate);
            resetVariables();
            return dst;
        } else {
            return null;
        }
    }
    private boolean isObserved(String country) {
        if(observations.containsKey(country)) {
            return true;
        } else {
            final Elements table = new Elements(getElements());
            table.removeIf(row -> !row.select("td").get(0).text().toLowerCase().equalsIgnoreCase(country));
            final boolean isObserved = !table.isEmpty();
            if(isObserved) {
                final Elements tds = table.get(0).select("td");
                final String daylightSavingsTime = tds.get(3).text() + "|" + tds.get(4).text();
                observations.put(country, daylightSavingsTime);
            }
            return isObserved;
        }
    }
    private void isDaylightSavingsTime(String country, UTCOffset offset) {
        if(isObserved(country)) {
            final Instant now = Instant.now();
            final long offsetEpoch = (offset.getHour()*60*60*1000) + (offset.getMinute()*60*1000);
            final long targetEpoch = now.toEpochMilli() + offsetEpoch;
            final Instant utcInstant = Instant.ofEpochMilli(targetEpoch);
            final LocalDateTime today = LocalDate.ofInstant(utcInstant, TimeZone.getTimeZone("UTC").toZoneId()).atStartOfDay();
            final int currentYear = today.getYear();

            final String[] values = observations.getOrDefault(country, "Unknown|Unknown").split("\\|");
            final String start = values[0].toLowerCase(), end = values[1].toLowerCase();
            final String[] startValues = start.split(" "), endValues = end.split(" ");
            Month startMonth = WLUtilities.valueOfMonthFromInput(start);
            if(startMonth != null) {
                final int startMonthValue = startMonth.getValue(), startDay = Integer.parseInt(startValues[1]);
                final LocalDateTime startDate = LocalDate.of(currentYear, startMonthValue, startDay).atStartOfDay();

                final int endMonthValue = WLUtilities.valueOfMonthFromInput(end).getValue(), endDay = Integer.parseInt(endValues[1]);
                final LocalDateTime endDate = LocalDate.of(currentYear+1, endMonthValue, endDay).atStartOfDay();

                this.startDate = startDate;
                this.endDate = endDate;
            } else {
                LocalDateTime startDate = null, endDate = null;
                final int startLength = startValues.length, endLength = endValues.length;
                switch (startLength) {
                    case 3:
                        startDate = getDateWith(currentYear, startValues[0], startValues[1], startValues[2]);
                        break;
                    case 6:
                        startDate = start.contains(" at ") ? getDateWith(currentYear, startValues[0], startValues[1], startValues[2]) : null;
                        break;
                    default:
                        break;
                }
                this.startDate = startDate;

                switch (endLength) {
                    case 3:
                        endDate = getDateWith(currentYear, endValues[0], endValues[1], endValues[2]);
                        break;
                    case 6:
                        endDate = end.contains(" at ") ? getDateWith(currentYear, endValues[0], endValues[1], endValues[2]) : null;
                        break;
                    default:
                        break;
                }
                this.endDate = endDate;
            }
        }
    }
    private LocalDateTime getDateWith(int year, String start, String weekday, String month) {
        final Month targetMonth = WLUtilities.valueOfMonthFromInput(month);
        return getDateWith(year, start, weekday, targetMonth);
    }
    private LocalDateTime getDateWith(int year, String start, String weekday, Month month) {
        final DayOfWeek dayOfWeek = DayOfWeek.valueOf(weekday.toUpperCase());
        LocalDate date = LocalDate.of(year, month, 1);
        switch (start.toLowerCase()) {
            case "first":
                date = date.with(firstInMonth(dayOfWeek));
                break;
            case "last":
                date = date.with(lastInMonth(dayOfWeek));
                break;
            default:
                break;
        }
        return date.atStartOfDay();
    }
}
