package me.randomhashtags.worldlaws.holiday;

import me.randomhashtags.worldlaws.event.EventDate;

public final class Holiday {
    private EventDate date;
    private String englishName, learnMoreURL;

    public Holiday(EventDate date, String englishName, String learnMoreURL) {
        this.date = date;
        this.englishName = englishName;
        this.learnMoreURL = learnMoreURL;
    }

    public EventDate getDate() {
        return date;
    }
    public String getEnglishName() {
        return englishName;
    }
    public String getLearnMoreURL() {
        return learnMoreURL;
    }

    @Override
    public String toString() {
        return "{" +
                "\"date\":" + date.toString() + "," +
                "\"englishName\":\"" + englishName + "\"," +
                "\"learnMoreURL\":\"" + learnMoreURL + "\"" +
                "}";
    }
}
