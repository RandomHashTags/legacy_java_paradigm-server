package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.WLUtilities;

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
        try {
            return WLUtilities.DATE_FORMAT.parse(input).getTime();
        } catch (Exception e) {
            WLUtilities.saveException(e);
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
