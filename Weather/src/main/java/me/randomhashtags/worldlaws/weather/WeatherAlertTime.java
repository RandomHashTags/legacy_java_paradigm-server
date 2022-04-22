package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

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
            final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            final TemporalAccessor time = formatter.parse(input);
            return time.query(Instant::from).toEpochMilli();
        } catch (Exception e) {
            final String trace = WLUtilities.getThrowableStackTrace(e);
            WLUtilities.saveLoggedError("WeatherAlertTime", "failed to parse date format for input \"" + input + "\"!\n\n" + trace);
            return 0;
        }
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        if(ends != 0) {
            json.put("ends", ends);
        }
        json.put("sent", sent);
        json.put("effective", effective);
        json.put("expires", expires);
        return json;
    }
}
