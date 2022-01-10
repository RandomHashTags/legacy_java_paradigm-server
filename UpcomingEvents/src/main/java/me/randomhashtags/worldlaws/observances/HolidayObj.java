package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public final class HolidayObj implements Holiday {
    private HashSet<String> countries;
    protected String[] aliases;
    protected String imageURL;
    private final String englishName, description;
    private final EventSources sources;
    private String celebrators, emoji;

    public HolidayObj(String englishName, String imageURL, String[] aliases, String description, EventSources sources) {
        this.englishName = LocalServer.fixEscapeValues(englishName);
        this.imageURL = imageURL;
        this.aliases = aliases;
        this.description = LocalServer.fixEscapeValues(description);
        this.sources = sources;
    }
    public HolidayObj(String celebrators, String emoji, String englishName, String imageURL, String[] aliases, EventSources sources) {
        this(englishName, imageURL, aliases, null, sources);
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
        final EventSources sources = new EventSources();
        if(json.has("sources")) {
            final JSONObject sourcesJSON = json.getJSONObject("sources");
            for(String key : sourcesJSON.keySet()) {
                final JSONObject sourceJSON = sourcesJSON.getJSONObject(key);
                final String url = sourceJSON.getString("homepageURL");
                final EventSource source = new EventSource(key, url);
                sources.add(source);
            }
        }
        this.sources = sources;
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
    private String getCountriesArray(HashSet<String> countries) {
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

    private String getAliasesArray() {
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
    public EventSources getSources() {
        return sources;
    }

    @Override
    public String toString() {
        return "\"" + getEnglishName() + "\":{" +
                (countries != null ? "\"countries\":" + getCountriesArray(countries) + "," : "") +
                (celebrators != null ? "\"celebrators\":\"" + celebrators + "\"," : "") +
                (emoji != null ? "\"emoji\":\"" + emoji + "\"," : "") +
                (aliases != null ? "\"aliases\":" + getAliasesArray() + "," : "") +
                (imageURL != null && !imageURL.equals("null") ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                "\"sources\":" + (sources != null ? sources : new EventSources()).toString() +
                "}";
    }
}
