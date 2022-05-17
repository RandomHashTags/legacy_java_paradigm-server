package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

import java.time.format.DateTimeFormatter;

public final class WeatherAlertTime {
    private long sent, effective, expires, ends;

    public WeatherAlertTime(String sent, String effective, String expires, String ends) {
        final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        if(sent != null && !sent.isEmpty()) {
            this.sent = WLUtilities.parseDateFormatToMilliseconds(formatter, sent);
        }
        if(effective != null && !effective.isEmpty()) {
            this.effective = WLUtilities.parseDateFormatToMilliseconds(formatter, effective);
        }
        if(expires != null && !expires.isEmpty()) {
            this.expires = WLUtilities.parseDateFormatToMilliseconds(formatter, expires);
        }
        if(ends != null && !ends.isEmpty()) {
            this.ends = WLUtilities.parseDateFormatToMilliseconds(formatter, ends);
        }
    }

    public long getSent() {
        return sent;
    }
    public long getEffective() {
        return effective;
    }
    public long getExpires() {
        return expires;
    }
    public long getEnds() {
        return ends;
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
