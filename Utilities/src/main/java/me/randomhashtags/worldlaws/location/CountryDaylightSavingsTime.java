package me.randomhashtags.worldlaws.location;

import java.time.LocalDateTime;

public final class CountryDaylightSavingsTime {
    private final boolean observed;
    private final int startMonth, startDay, endMonth, endDay;

    public CountryDaylightSavingsTime(boolean observed, LocalDateTime startDate, LocalDateTime endDate) {
        this.observed = observed;
        final boolean hasDate = observed && startDate != null && endDate != null;
        this.startMonth = hasDate ? startDate.getMonthValue() : -1;
        this.startDay = hasDate ? startDate.getDayOfMonth() : -1;
        this.endMonth = hasDate ? endDate.getMonthValue() : -1;
        this.endDay = hasDate ? endDate.getDayOfMonth() : -1;
    }

    public boolean isObserved() {
        return observed;
    }
    public int getStartMonth() {
        return startMonth;
    }
    public int getStartDay() {
        return startDay;
    }
    public int getEndMonth() {
        return endMonth;
    }
    public int getEndDay() {
        return endDay;
    }

    @Override
    public String toString() {
        return "{" +
                "\"observed\":" + observed +
                (observed ?
                        "," +
                        "\"startMonth\":" + startMonth + "," +
                        "\"startDay\":" + startDay + "," +
                        "\"endMonth\":" + endMonth + "," +
                        "\"endDay\":" + endDay
                        : ""
                ) +
                "}";
    }
}
