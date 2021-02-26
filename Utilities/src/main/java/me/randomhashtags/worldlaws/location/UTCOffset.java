package me.randomhashtags.worldlaws.location;

public final class UTCOffset {
    private final int hour, minute;
    private final String regions;

    public UTCOffset(int hour, int minute, String regions) {
        this.hour = hour;
        this.minute = minute;
        this.regions = regions;
    }

    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public String getRegions() {
        return regions;
    }

    @Override
    public String toString() {
        return "{" +
                (hour != 0 ? "\"hour\":" + hour + "," : "") +
                (minute != 0 ? "\"minute\":" + minute + "," : "") +
                "\"regions\":\"" + regions + "\"" +
                "}";
    }
}
