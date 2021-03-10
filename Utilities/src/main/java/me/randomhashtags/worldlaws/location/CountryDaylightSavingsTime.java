package me.randomhashtags.worldlaws.location;

import java.time.LocalDateTime;

public final class CountryDaylightSavingsTime {
    private final int startMonth, startDay, endMonth, endDay;

    public CountryDaylightSavingsTime(LocalDateTime startDate, LocalDateTime endDate) {
        final boolean hasDate = startDate != null && endDate != null;
        this.startMonth = hasDate ? startDate.getMonthValue() : -1;
        this.startDay = hasDate ? startDate.getDayOfMonth() : -1;
        this.endMonth = hasDate ? endDate.getMonthValue() : -1;
        this.endDay = hasDate ? endDate.getDayOfMonth() : -1;
    }
    public CountryDaylightSavingsTime(int startMonth, int startDay, int endMonth, int endDay) {
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    @Override
    public String toString() {
        return "{" +
                "\"startMonth\":" + startMonth + "," +
                "\"startDay\":" + startDay + "," +
                "\"endMonth\":" + endMonth + "," +
                "\"endDay\":" + endDay +
                "}";
    }
}
