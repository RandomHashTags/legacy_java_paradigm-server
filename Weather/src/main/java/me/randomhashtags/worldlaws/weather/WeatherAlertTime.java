package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.WLUtilities;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class WeatherAlertTime {
    private long sent, effective, expires, ends;

    public WeatherAlertTime(String sent, String effective, String expires, String ends) {
        if(sent != null && !sent.isEmpty()) {
            this.sent = getTimeFrom(sent);
        }
        if(effective != null && !effective.isEmpty()) {
            this.effective = getTimeFrom(effective);
        }
        if(expires != null && !expires.isEmpty()) {
            this.expires = getTimeFrom(expires);
        }
        if(ends != null && !ends.isEmpty()) {
            this.ends = getTimeFrom(ends);
        }
    }

    private long getTimeFrom(String input) {
        if(input.isEmpty()) {
            return 0;
        }
        try {
            final LocalDateTime date = LocalDateTime.parse(input, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return date.toEpochSecond(ZoneOffset.UTC) * 1000;
        } catch (Exception e) {
            final String trace = WLUtilities.getThrowableStackTrace(e);
            WLUtilities.saveLoggedError("WeatherAlertTime", "failed to parse date format for input \"" + input + "\"!\n\n" + trace);
            return 0;
        }
    }

    @Override
    public String toString() {
        return "{" +
                (ends != 0 ? "\"ends\":" + ends + "," : "") +
                "\"sent\":" + sent + "," +
                "\"effective\":" + effective + "," +
                "\"expires\":" + expires +
                "}";
    }
}
