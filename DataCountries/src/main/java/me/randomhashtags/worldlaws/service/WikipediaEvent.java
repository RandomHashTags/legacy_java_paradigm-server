package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.service.wikipedia.WikipediaDocument;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Month;
import java.util.*;

public class WikipediaEvent extends JSONObject {

    private JSONArray images;

    public WikipediaEvent(String description, HashMap<String, String> hyperlinks, EventSources sources) {
        put("description", description);
        if(hyperlinks != null) {
            final JSONObject hyperlinksJSON = new JSONObject(hyperlinks);
            put("hyperlinks", hyperlinksJSON);
        }
        if(!sources.isEmpty()) {
            put("sources", sources.toJSONObject());
        }
    }

    public JSONArray getImages(int amountToLoad) {
        if(images == null) {
            final JSONObject hyperlinksJSON = optJSONObject("hyperlinks", null);
            if(hyperlinksJSON != null) {
                images = new JSONArray();
                int amountLoaded = 0;
                final String wikipediaKey = "https://en.wikipedia.org/wiki/";
                for(String text : hyperlinksJSON.keySet()) {
                    final String url = hyperlinksJSON.getString(text);
                    if(url.startsWith(wikipediaKey)) {
                        final WikipediaDocument doc = new WikipediaDocument(url);
                        final List<String> docImages = doc.getImages();
                        if(!docImages.isEmpty()) {
                            final String first = docImages.get(0);
                            images.put(first);
                            amountLoaded += 1;
                            if(amountLoaded == amountToLoad) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if(images != null && !images.isEmpty()) {
            put("images", images);
        }
        return images;
    }

    public final void updateMentionedCountries(WLCountry parentCountry) {
        final WLCountry[] countries = WLCountry.values();
        final String description = getString("description"), descriptionLowercase = description.toLowerCase();
        final JSONObject mentionedSovereignStates = new JSONObject();
        for(WLCountry country : countries) {
            final String countryBackendID = getMentionedCountryBackendID(description, descriptionLowercase, country);
            if(countryBackendID != null) {
                final JSONArray subdivisions = getMentionedSubdivisions(descriptionLowercase, country);
                mentionedSovereignStates.put(countryBackendID, subdivisions);
            }
        }
        if(parentCountry != null) {
            final JSONArray subdivisions = getMentionedSubdivisions(descriptionLowercase, parentCountry);
            final String countryBackendID = parentCountry.getBackendID();
            if(!mentionedSovereignStates.has(countryBackendID) && !subdivisions.isEmpty()) {
                mentionedSovereignStates.put(countryBackendID, subdivisions);
            }
        }
        if(!mentionedSovereignStates.isEmpty()) {
            put("mentionedSovereignStates", mentionedSovereignStates);
        }
    }
    private String getMentionedCountryBackendID(String description, String descriptionLowercase, WLCountry country) {
        final String isoAlpha2Official = country.getISOAlpha2Official();
        final String isoAlpha2Alias = country.getISOAlpha2Alias();
        final String isoAlpha2ParentGroup = country.getISOAlpha2ParentGroup();
        final String isoAlpha3 = country.getISOAlpha3();
        String countryBackendID = null;
        if(isoAlpha2Official != null && containsISO(description, isoAlpha2Official)
                || isoAlpha2Alias != null && containsISO(description, isoAlpha2Alias)
                || isoAlpha2ParentGroup != null && containsISO(description, isoAlpha2ParentGroup)
                || isoAlpha3 != null && containsISO(description, isoAlpha3)
                || descriptionLowercase.contains(country.getShortName().toLowerCase())
        ) {
            countryBackendID = country.getBackendID();
        } else {
            final HashSet<String> aliases = country.getAliases();
            if(aliases != null) {
                for(String alias : aliases) {
                    if(descriptionLowercase.contains(alias.toLowerCase())) {
                        countryBackendID = country.getBackendID();
                        break;
                    }
                }
            }
        }
        return countryBackendID;
    }
    private boolean containsISO(String input, String isoAlpha) {
        return containsString(input,
                " " + isoAlpha + " ",
                " " + isoAlpha + ",",
                " " + isoAlpha + ".",
                " " + isoAlpha + ";"
        );
    }
    private boolean containsString(String input, String...strings) {
        for(String string : strings) {
            if(input.contains(string)) {
                return true;
            }
        }
        return false;
    }
    private JSONArray getMentionedSubdivisions(String description, WLCountry country) {
        final SovereignStateSubdivision[] subdivisions = country.getSubdivisions();
        final JSONArray array = new JSONArray();
        if(subdivisions != null) {
            for(SovereignStateSubdivision subdivision : subdivisions) {
                final String realName = subdivision.getRealName(), shortName = subdivision.getShortName();
                if(realName != null && description.contains(realName.toLowerCase())
                        || shortName != null && description.contains(shortName.toLowerCase())
                        || description.contains(subdivision.getName().toLowerCase())
                ) {
                    array.put(subdivision.getBackendID());
                }
            }
        }
        return array;
    }

    public static HashMap<EventDate, List<WikipediaEvent>> parseMonthEvents(String identifier, int year, WLCountry parentCountry, Elements dayElements, HashMap<String, EventSource> references) {
        final HashMap<EventDate, List<WikipediaEvent>> map = new HashMap<>();
        for(Element dayElement : dayElements) {
            final String dayText = dayElement.text();
            final String[] textValues = dayText.split(" ");
            int dayIndex = -1, monthIndex = -1;
            final String regex = "[0-9]+";
            if(textValues.length > 1) {
                if(textValues[0].matches(regex)) {
                    dayIndex = 0;
                    monthIndex = 1;
                } else if(textValues[1].matches(regex)) {
                    dayIndex = 1;
                    monthIndex = 0;
                }
            }
            if(dayIndex != -1) {
                final Month month = WLUtilities.valueOfMonthFromInput(textValues[monthIndex]);
                if(month != null) {
                    final int day = Integer.parseInt(textValues[dayIndex]);
                    final EventDate date = new EventDate(month, day, year);
                    final List<WikipediaEvent> events = WikipediaEvent.parseEvents(identifier, dayElement, references);
                    if(!events.isEmpty()) {
                        map.put(date, events);
                    }
                }
            }
        }
        for(Map.Entry<EventDate, List<WikipediaEvent>> bruh : map.entrySet()) {
            for(WikipediaEvent event : bruh.getValue()) {
                event.updateMentionedCountries(parentCountry);
            }
        }
        return map;
    }

    private static List<WikipediaEvent> parseEvents(String identifier, Element listElement, HashMap<String, EventSource> references) {
        final List<WikipediaEvent> events = new ArrayList<>();
        final Element innerList = listElement.selectFirst("ul");
        if(innerList != null) {
            final Element thirdList = innerList.selectFirst("ul");
            final Elements targetElements = (thirdList != null ? thirdList : innerList).select("li");
            for(Element element : targetElements) {
                final String text = LocalServer.removeWikipediaReferences(element.text());
                if(!text.contains(" (b. ") && !text.contains(" (born ")) {
                    final HashMap<String, String> hyperlinks = getWikipediaHyperlinks(element);
                    final EventSources sources = getSources(references, element);
                    final WikipediaEvent event = new WikipediaEvent(text, hyperlinks, sources);
                    events.add(event);
                }
            }
        } else {
            String text = LocalServer.removeWikipediaReferences(listElement.text());
            if(!text.contains(" (b. ") && !text.contains(" (born ")) {
                if(text.contains(" – ")) {
                    final String targetDateString = text.split(" – ")[0];
                    final int length = targetDateString.length() + 3;
                    if(text.length() - length > 0) {
                        text = text.substring(length);
                        final HashMap<String, String> hyperlinks = getWikipediaHyperlinks(listElement);
                        final EventSources sources = getSources(references, listElement);
                        final WikipediaEvent event = new WikipediaEvent(text, hyperlinks, sources);
                        events.add(event);
                    } else {
                        WLLogger.logWarning("WikipediaEvent - parseEvents;identifier=" + identifier + ";text=" + text);
                    }
                } else if(text.contains(" - ")) {
                    WLLogger.logWarning("WikipediaEvent - parseEvents;identifier=" + identifier + ";wikipedia text needs fixing=\"" + text + "\"");
                }
            }
        }
        return events;
    }
    public static HashMap<String, String> getWikipediaHyperlinks(Element element) {
        final HashMap<String, String> hyperlinks = new HashMap<>();
        final Elements links = element.select("a[href]");
        links.removeIf(test -> test.attr("href").startsWith("#cite_note-"));
        final String wikipediaDomain = "https://en.wikipedia.org";
        for(Element href : links) {
            final String ahref = href.attr("href");
            final String text = href.text();
            final String homepageURL = ahref.startsWith("http") ? ahref : wikipediaDomain + ahref;
            hyperlinks.put(text, homepageURL);
        }
        return hyperlinks.isEmpty() ? null : hyperlinks;
    }
    private static EventSources getSources(HashMap<String, EventSource> references, Element element) {
        final EventSources sources = new EventSources();
        final Elements superscripts = element.select("sup.reference");
        for(Element superscript : superscripts) {
            final String number = superscript.text().replace("[", "").replace("]", "");
            if(references.containsKey(number)) {
                sources.add(references.get(number));
            }
        }
        return sources;
    }
}