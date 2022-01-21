package me.randomhashtags.worldlaws.observances;

public enum HolidaySource {
    NATIONAL_TODAY,
    TIME_AND_DATE,
    UNITED_NATIONS,
    WIKIPEDIA,
    ;

    public String getName() {
        switch (this) {
            case NATIONAL_TODAY:
                return "National Today";
            case TIME_AND_DATE:
                return "Time and Date";
            case UNITED_NATIONS:
                return "United Nations";
            case WIKIPEDIA:
                return "Wikipedia";
            default:
                return "Unknown";
        }
    }
    public String getURL(String holiday) {
        switch (this) {
            case NATIONAL_TODAY:
                return "https://nationaltoday.com/" + holiday.toLowerCase().replace(" ", "-") + "/";
            case WIKIPEDIA:
                return "https://en.wikipedia.org/wiki/" + holiday.replace(" ", "_");
            case TIME_AND_DATE:
                return "https://www.timeanddate.com/holidays/fun/" + holiday.toLowerCase().replace(" ", "-");
            default:
                return null;
        }
    }
}
