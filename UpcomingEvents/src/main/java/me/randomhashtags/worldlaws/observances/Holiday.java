package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.UUID;

public interface Holiday {
    String WIKIPEDIA = "Wikipedia: ";

    String name();
    HolidayType getType();
    default String getName() {
        final String wikipediaName = getWikipediaName();
        return wikipediaName != null ? wikipediaName : LocalServer.toCorrectCapitalization(name(), false, "of", "and", "de", "the", "it", "in", "for", "to");
    }
    default String getWikipediaName() {
        return null;
    }
    String[] getAliases();
    default String[] collectAliases(String...aliases) {
        return aliases;
    }

    EventDate getDate(WLCountry country, int year);

    default EventSources getDefaultSources() {
        final HolidaySource source = getSource();
        if(source != null) {
            final String name = getName();
            final EventSource eventSource = new EventSource(source.getName() + ": " + name, getURL());
            return new EventSources(eventSource);
        } else {
            return new EventSources();
        }
    }
    default EventSources getSources(WLCountry country) {
        return null;
    }
    default EventSources getExternalLinks() {
        return null;
    }
    default EventSources collectSources(EventSource...sources) {
        return new EventSources(sources);
    }

    default String getIdentifier() {
        final JSONObjectTranslatable json = getJSONObjectTranslatable();
        return json != null ? json.getString("identifier") : null;
    }
    default JSONObjectTranslatable getJSONObjectTranslatable() {
        final String name = getName();
        final Folder folder = Folder.UPCOMING_EVENTS_HOLIDAYS_TYPE;
        final HolidayType type = getType();
        final String typeName = type.name().replace("_WEST", "").replace("_EAST", "");
        final String fileName = folder.getFolderName().replace("%type%", typeName);
        folder.setCustomFolderName(name, fileName);
        final JSONObject local = Jsonable.getStaticLocalFileJSONObject(folder, name);
        final int version = ResponseVersions.HOLIDAYS.getValue();
        final JSONObject json;
        if(local == null || local.optInt("version", -1) < version) {
            json = loadHolidayJSON();
        } else {
            json = local;
        }
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable("name", "description");
        translatable.setFolder(folder);
        translatable.setFileName(name);
        for(String key : json.keySet()) {
            translatable.put(key, json.get(key));
        }
        translatable.addRemovedClientKeys("identifier");
        translatable.addRemovedClientKeys("version");
        boolean edited = local == null;
        if(!translatable.has("version")) {
            translatable.put("version", version);
            edited = true;
        }
        if(!translatable.has("identifier")) {
            final String identifier = "***REMOVED***";
            translatable.put("identifier", identifier);
            edited = true;
        }
        if(!translatable.has("name")) {
            translatable.put("name", name);
            edited = true;
        }
        if(!translatable.has("aliases")) {
            final String[] aliases = getAliases();
            if(aliases != null) {
                translatable.put("aliases", new JSONArray(aliases));
                edited = true;
            }
        }
        if(!translatable.has("type")) {
            translatable.put("type", typeName);
            edited = true;
        }
        if(translatable.has("imageURL") && translatable.getString("imageURL").startsWith("https:https:")) {
            translatable.put("imageURL", translatable.getString("imageURL").substring("https:".length()));
            edited = true;
        }
        if(edited) {
            translatable.save();
        }

        return translatable;
    }

    default HolidaySource getSource() {
        return HolidaySource.WIKIPEDIA;
    }
    default String getURL() {
        final HolidaySource source = getSource();
        return source != null ? source.getURL(getName()) : null;
    }

    private JSONObjectTranslatable loadHolidayJSON() {
        final String url = getURL();
        String description = null, imageURL = null;
        EventSources sources = new EventSources();
        if(url != null) {
            final WikipediaDocument doc = new WikipediaDocument(Folder.UPCOMING_EVENTS_HOLIDAYS_TYPE, url, false);
            if(doc.exists()) {
                final String pageName = doc.getPageName();
                final EventSources externalLinks = doc.getExternalLinks();
                if(externalLinks != null) {
                    sources.addAll(externalLinks);
                }
                sources.add(new EventSource("Wikipedia: " + pageName, url));
                final StringBuilder builder = new StringBuilder();
                final List<Element> paragraphs = doc.getConsecutiveParagraphs();
                if(paragraphs != null) {
                    boolean isFirst = true;
                    for(Element paragraph : paragraphs) {
                        builder.append(isFirst ? "" : "\n\n").append(paragraph.text());
                        isFirst = false;
                    }
                }
                description = LocalServer.removeWikipediaTranslations(LocalServer.removeWikipediaReferences(builder.toString()));
                final List<String> images = doc.getImages();
                if(!images.isEmpty()) {
                    final String src = images.get(0);
                    final String[] endingValues = src.split("/");
                    final String endingValue = endingValues[endingValues.length-1];
                    final String targetImageURL = src.contains("px-") ? src.split(endingValue)[0] + "%quality%px-" + endingValue.split("px-")[1] : src;
                    imageURL = (targetImageURL.startsWith("https:") ? "" : "https:") + targetImageURL;
                }
            }
        } else {
            WLUtilities.saveLoggedError("Holiday", "url for holiday == null! name()=" + name() + ";type=" + getType());
        }
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name", "description");
        json.put("version", ResponseVersions.HOLIDAYS.getValue());
        json.put("name", getName());
        json.put("identifier", "***REMOVED***");
        json.put("description", description);
        if(imageURL != null) {
            json.put("imageURL", imageURL);
        }
        json.put("sources", sources.toJSONObject());
        return json;
    }
}
