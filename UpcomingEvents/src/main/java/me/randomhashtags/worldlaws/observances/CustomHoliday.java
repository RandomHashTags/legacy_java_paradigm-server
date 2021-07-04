package me.randomhashtags.worldlaws.observances;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

public final class CustomHoliday extends HolidayObj {

    private final String celebrators, emoji;

    public CustomHoliday(String celebrators, String emoji, String englishName, String imageURL, String[] aliases, String learnMoreURL) {
        super(englishName, imageURL, aliases, null, learnMoreURL, null);
        this.celebrators = celebrators;
        this.emoji = StringEscapeUtils.escapeJava(emoji);
    }
    public CustomHoliday(String englishName, JSONObject json) {
        super(englishName, json);
        celebrators = json.getString("celebrators");
        emoji = StringEscapeUtils.escapeJava(json.getString("emoji"));
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
