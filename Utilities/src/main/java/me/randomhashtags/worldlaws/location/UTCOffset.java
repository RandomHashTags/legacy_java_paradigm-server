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
        final boolean hasMinute = minute != 0;
        return "\"" + regions + "\":{" +
                (hour != 0 ? "\"hour\":" + hour + (hasMinute ? "," : "") : "") +
                (hasMinute ? "\"minute\":" + minute : "") +
                "}";
    }
}
