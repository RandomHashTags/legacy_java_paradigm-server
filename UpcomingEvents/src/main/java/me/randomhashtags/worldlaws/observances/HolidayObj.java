package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class HolidayObj implements Holiday {
    private HashSet<String> countries;
    protected String[] aliases;
    protected String imageURL;
    private final String englishName, description, learnMoreURL;
    private final EventSources otherSources;

    public HolidayObj(String englishName, String imageURL, String[] aliases, String description, String learnMoreURL, EventSources otherSources) {
        this.englishName = LocalServer.fixEscapeValues(englishName);
        this.imageURL = imageURL;
        this.aliases = aliases;
        this.description = description;
        this.learnMoreURL = learnMoreURL;
        this.otherSources = otherSources;
    }
    public HolidayObj(String englishName, JSONObject json) {
        this.englishName = englishName;
        this.imageURL = json.has("imageURL") ? json.getString("imageURL") : null;
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
            builder.append(isFirst ? "" : ",").append("\"").append(alias).append("\"");
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
                (aliases != null ? "\"aliases\":" + getAliasesArray() + "," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (otherSources != null ? "\"otherSources\":" + otherSources.toString() : "") +
                "\"learnMoreURL\":\"" + learnMoreURL + "\"" +
                "}";
    }
}
