package me.randomhashtags.worldlaws.news;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.StoryController;
import me.randomhashtags.worldlaws.StoryObj;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.WLSubdivisions;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.stories.Story;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public enum NewsCBS implements StoryController {
    INSTANCE;

    @Override
    public void refresh(CompletionHandler handler) {
        final HashSet<Story> stories = new HashSet<>();
        refreshWorld(new CompletionHandler() {
            @Override
            public void handleStories(HashSet<Story> usStories) {
                stories.addAll(usStories);
                handler.handleStories(stories);
            }
        });
    }

    private HashSet<Story> getStoriesFrom(Elements imageElements, Elements titleElements, Elements descriptionElements, HashMap<String, SovereignStateSubdivision> subdivisions, EventSources sources) {
        final HashSet<String> titles = new HashSet<>();
        final HashSet<Story> stories = new HashSet<>();
        final Set<String> subdivisionKeys = subdivisions.keySet();
        titleElements.parallelStream().forEach(element -> {
            final String text = element.text();
            if(!titles.contains(text)) {
                final String textLowercase = text.toLowerCase().replace("'s", "");
                for(String key : subdivisionKeys) {
                    if(textLowercase.contains(" " + key + " ") || textLowercase.startsWith(key + " ") || textLowercase.contains(" " + key + ".") || textLowercase.endsWith(" " + key)) {
                        titles.add(text);
                        final SovereignStateSubdivision subdivision = subdivisions.get(key);
                        final int titleIndex = titleElements.indexOf(element);
                        final String imageURL = imageElements.get(titleIndex).attr("src");
                        final String description = descriptionElements.get(titleIndex).text();
                        final Story story = new StoryObj(subdivision.getCountry(), subdivision, text, description, imageURL, null, sources);
                        stories.add(story);
                        break;
                    }
                }
            }
        });
        return stories;
    }
    private HashMap<String, SovereignStateSubdivision> getSubdivisions(SovereignStateSubdivision...sovereignStateSubdivisions) {
        final HashMap<String, SovereignStateSubdivision> subdivisions = new HashMap<>();
        if(sovereignStateSubdivisions == null || sovereignStateSubdivisions.length == 0) {
            for(WLCountry country : WLSubdivisions.getSupportedCountries()) {
                putSubdivisions(subdivisions, WLSubdivisions.get(country));
            }
        } else {
            putSubdivisions(subdivisions, sovereignStateSubdivisions);
        }
        return subdivisions;
    }
    private void putSubdivisions(HashMap<String, SovereignStateSubdivision> subdivisions, SovereignStateSubdivision...sovereignStateSubdivisions) {
        if(sovereignStateSubdivisions != null) {
            for(SovereignStateSubdivision subdivision : sovereignStateSubdivisions) {
                subdivisions.put(subdivision.getName().toLowerCase(), subdivision);
                final String postalCode = subdivision.getISOAlpha2();
                if(postalCode != null) {
                    subdivisions.put(postalCode, subdivision);
                    subdivisions.put(postalCode + ".", subdivision);
                }
                final String shortName = subdivision.getShortName();
                if(shortName != null) {
                    subdivisions.put(shortName.toLowerCase(), subdivision);
                }
            }
        }
    }

    private HashSet<Story> refresh(String url, SovereignStateSubdivision...sovereignStateSubdivisions) {
        HashSet<Story> stories = new HashSet<>();
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements imageElements = doc.select("article span.img img");
            final Elements wrapper = doc.select("div.item__title-wrapper");
            final Elements titles = wrapper.select("h4.item__hed"), descriptions = wrapper.select("p.item__dek");
            final EventSources sources = new EventSources();
            final HashMap<String, SovereignStateSubdivision> subdivisions = getSubdivisions(sovereignStateSubdivisions);
            stories = getStoriesFrom(imageElements, titles, descriptions, subdivisions, sources);
        }
        return stories;
    }

    private void refreshWorld(CompletionHandler handler) {
        final HashSet<Story> stories = refresh("https://www.cbsnews.com/world/");
        stories.addAll(refresh("https://www.cbsnews.com/politics/"));
        stories.addAll(refresh("https://www.cbsnews.com/science/"));
        handler.handleStories(stories);
    }
    private void refreshUSA(CompletionHandler handler) {
        final SovereignStateSubdivision[] states = SubdivisionsUnitedStates.values();
        final HashSet<Story> stories = refresh("https://www.cbsnews.com/us/", states);
        stories.addAll(refresh("https://www.cbsnews.com/politics/", states));
        handler.handleStories(stories);
    }
}
