package me.randomhashtags.worldlaws.observances;

public enum HolidaySource {
    OTHER,
    TIME_AND_DATE_FUN,
    WIKIPEDIA,
    ;

    public String getURL(String holiday) {
        switch (this) {
            case WIKIPEDIA:
                return "https://en.wikipedia.org/wiki/" + holiday.replace(" ", "_");
            case TIME_AND_DATE_FUN:
                return "https://www.timeanddate.com/holidays/fun/" + holiday.toLowerCase().replace(" ", "-");
            default:
                return null;
        }
    }
}
