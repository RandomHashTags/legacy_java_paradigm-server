package me.randomhashtags.worldlaws.observances;

import org.json.JSONObject;

public final class CustomHoliday extends HolidayObj {

    private final String celebrators, emoji;

    public CustomHoliday(String celebrators, String emoji, String englishName, String imageURL, String[] aliases, String learnMoreURL) {
        super(englishName, imageURL, aliases, null, learnMoreURL, null);
        this.celebrators = celebrators;
        this.emoji = emoji;
    }
    public CustomHoliday(String englishName, JSONObject json) {
        super(englishName, json);
        celebrators = json.getString("celebrators");
        emoji = json.getString("emoji");
    }

    @Override
    public String toString() {
        return "\"" + getEnglishName() + "\":{" +
                (aliases != null ? "\"aliases\":" + getAliasesArray() + "," : "") +
                "\"celebrators\":\"" + celebrators + "\"," +
                "\"emoji\":\"" + emoji + "\"," +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"learnMoreURL\":\"" + getLearnMoreURL() + "\"" +
                "}";
    }
}
