package me.randomhashtags.worldlaws.upcoming.entertainment.movies;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.List;

public final class MovieProductionCompany {
    private static final String IMAGE_URL_PREFIX = "https://upload.wikimedia.org/wikipedia/";

    private final String name, wikipediaName;
    private final JSONArray aliases;
    private String description, imageURL;
    private EventSources sources;

    public MovieProductionCompany(String name, JSONObject json) {
        this.name = name;
        wikipediaName = json.optString("wikipediaName", name).replace(" ", "_");
        aliases = json.optJSONArray("aliases");
        if(json.has("description")) {
            description = json.getString("description");
            imageURL = json.optString("imageURL", null);
            sources = new EventSources(json.getJSONObject("sources"));
        } else {
            loadDetails();
        }
    }

    public void loadDetails() {
        final String url = "https://en.wikipedia.org/wiki/" + wikipediaName.replace(" ", "_");
        final WikipediaDocument doc = new WikipediaDocument(url);
        final StringBuilder description = new StringBuilder();
        final List<Element> paragraphs = doc.getConsecutiveParagraphs();
        if(paragraphs != null) {
            boolean isFirst = true;
            for(Element paragraph : paragraphs) {
                final String text = paragraph.text();
                description.append(isFirst ? "" : "\n\n").append(text);
                isFirst = false;
            }
        }
        this.description = LocalServer.fixEscapeValues(LocalServer.removeWikipediaReferences(LocalServer.removeWikipediaTranslations(description.toString())));
        final List<String> images = doc.getImages();
        imageURL = !images.isEmpty() ? images.get(0) : null;
        if(imageURL != null) {
            if(imageURL.startsWith(IMAGE_URL_PREFIX)) {
                imageURL = imageURL.substring(IMAGE_URL_PREFIX.length());
            }
        }

        sources = doc.getExternalLinks();
        if(sources == null) {
            sources = new EventSources();
        }
        sources.add(new EventSource("Wikipedia: " + wikipediaName.replace("_", " "), url));
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name", "description", "aliases");
        json.put("name", name);
        json.put("description", description);
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
        if(aliases != null) {
            json.put("aliases", aliases);
        }
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
