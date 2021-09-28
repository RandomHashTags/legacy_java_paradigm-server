package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class HolidayObj implements Holiday {
    private HashSet<String> countries;
    protected String[] aliases;
    protected String imageURL;
    private final String englishName, description, learnMoreURL;
    private final EventSources otherSources;
    private String celebrators, emoji;

    public HolidayObj(String englishName, String imageURL, String[] aliases, String description, String learnMoreURL, EventSources otherSources) {
        this.englishName = LocalServer.fixEscapeValues(englishName);
        this.imageURL = imageURL;
        this.aliases = aliases;
        this.description = LocalServer.fixEscapeValues(description);
        this.learnMoreURL = learnMoreURL;
        this.otherSources = otherSources;
    }
    public HolidayObj(String celebrators, String emoji, String englishName, String imageURL, String[] aliases, String learnMoreURL) {
        this(englishName, imageURL, aliases, null, learnMoreURL, null);
        this.celebrators = celebrators;
        this.emoji = StringEscapeUtils.escapeJava(emoji);
    }
    public HolidayObj(String englishName, JSONObject json) {
        this.englishName = englishName;
        celebrators = json.has("celebrators") ? json.getString("celebrators") : null;
        emoji = json.has("emoji") ? StringEscapeUtils.escapeJava(json.getString("emoji")) : null;
        imageURL = json.has("imageURL") ? json.getString("imageURL") : null;
        if(json.has("aliases")) {
            final JSONArray array = json.getJSONArray("aliases");
            final HashSet<String> targetAliases = new HashSet<>();
            for(Object obj : array) {
                targetAliases.add((String) obj);
            }
            aliases = targetAliases.toArray(new String[targetAliases.size()]);
        }
        description = json.has("description") ? LocalServer.fixEscapeValues(json.getString("description")) : null;
        if(json.has("countries")) {
            final JSONArray array = json.getJSONArray("countries");
            countries = new HashSet<>();
            for(Object obj : array) {
                countries.add((String) obj);
            }
        }
        learnMoreURL = json.getString("learnMoreURL");
        otherSources = null;
    }

    @Override
    public HashSet<String> getCountries() {
        return countries;
    }

    public void addCountry(String country) {
        if(countries == null) {
            countries = new HashSet<>();
        }
        countries.add(country);
    }
    protected String getCountriesArray(HashSet<String> countries) {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String country : countries) {
            builder.append(isFirst ? "" : ",").append("\"").append(country).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getEnglishName() {
        return englishName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    protected String getAliasesArray() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String alias : aliases) {
            builder.append(isFirst ? "" : ",").append("\"").append(LocalServer.fixEscapeValues(alias)).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String getLearnMoreURL() {
        return learnMoreURL;
    }

    @Override
    public String toString() {
        final HashSet<String> countries = getCountries();
        final String learnMoreURL = getLearnMoreURL();
        return "\"" + getEnglishName() + "\":{" +
                (countries != null ? "\"countries\":" + getCountriesArray(countries) + "," : "") +
                (celebrators != null ? "\"celebrators\":\"" + celebrators + "\"," : "") +
                (emoji != null ? "\"emoji\":\"" + emoji + "\"," : "") +
                (aliases != null ? "\"aliases\":" + getAliasesArray() + "," : "") +
                (imageURL != null && !imageURL.equals("null") ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (otherSources != null ? "\"otherSources\":" + otherSources.toString() : "") +
                "\"learnMoreURL\":\"" + learnMoreURL + "\"" +
                "}";
    }
}
