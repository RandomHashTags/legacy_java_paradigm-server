package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface IHoliday extends Jsoupable, Jsonable {
    String WIKIPEDIA = "Wikipedia: ";

    Enum<? extends IHoliday> getEnum();
    default String getName() {
        final String wikipediaName = getOfficialName();
        return wikipediaName != null ? wikipediaName : LocalServer.toCorrectCapitalization(getEnum().name(), false, "of", "and", "de", "the", "it");
    }
    String getOfficialName();

    private JSONObject loadHolidayJSON() {
        final String url = getURL();
        String description = null, imageURL = "null";
        EventSources sources = new EventSources();
        if(url != null) {
            final WikipediaDocument doc = new WikipediaDocument(Folder.UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS, url, false);
            final String pageName = doc.getPageName();
            sources.addAll(doc.getExternalLinks());
            sources.add(new EventSource("Wikipedia: " + pageName, url));
            final List<Element> paragraphs = doc.getConsecutiveParagraphs();
            final StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(Element paragraph : paragraphs) {
                builder.append(isFirst ? "" : "\n\n").append(paragraph.text());
                isFirst = false;
            }
            description = LocalServer.fixEscapeValues(LocalServer.removeWikipediaTranslations(removeReferences(builder.toString())));
            final List<String> images = doc.getImages();
            if(!images.isEmpty()) {
                final String src = images.get(0);
                final String[] endingValues = src.split("/");
                final String endingValue = endingValues[endingValues.length-1];
                final String targetImageURL = src.contains("px-") ? src.split(endingValue)[0] + "%quality%px-" + endingValue.split("px-")[1] : src;
                imageURL = "https:" + targetImageURL;
            }
        }
        final JSONObject json = new JSONObject();
        json.put("description", description);
        json.put("imageURL", imageURL);
        json.put("sources", sources.toJSONObject());
        return json;
    }
    default JSONObject getHolidayJSON(HolidayType holidayType) {
        final String fileName = holidayType.name().replace("_EAST", "").replace("_WEST", "") + "_" + getName();
        return getJSONObject(Folder.UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS, fileName, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return loadHolidayJSON();
            }
        });
    }
    default HolidaySource getSource() { // TODO: delete this. Use getSources instead
        return HolidaySource.WIKIPEDIA;
    }
    default String getURL() {
        return getSource().getURL(getName());
    }
    default String[] getAliases() {
        return null;
    }
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
        return getDefaultSources();
    }
    default EventSources collectSources(EventSource...sources) {
        return new EventSources(sources);
    }

    default EventDate getFirst(DayOfWeek dayOfWeek, Month month, int year) {
        return get(1, dayOfWeek, year, month, 1);
    }
    default EventDate getSecond(DayOfWeek dayOfWeek, Month month, int year) {
        return get(2, dayOfWeek, year, month, 1);
    }
    default EventDate getThird(DayOfWeek dayOfWeek, Month month, int year) {
        return get(3, dayOfWeek, year, month, 1);
    }
    default EventDate getFourth(DayOfWeek dayOfWeek, Month month, int year) {
        return get(4, dayOfWeek, year, month, 1);
    }
    default EventDate getLast(DayOfWeek dayOfWeek, Month month, int year) {
        final EventDate fifth = get(5, dayOfWeek, year, month, 1);
        return fifth != null ? fifth : getFourth(dayOfWeek, month, year);
    }
    default EventDate getFirstAfter(DayOfWeek dayOfWeek, int year, Month month, int day) {
        return get(1, dayOfWeek, year, month, day);
    }
    private EventDate get(int amount, DayOfWeek dayOfWeek, int year, Month month, int day) {
        final LocalDate date = LocalDate.of(year, month, day);
        final int startingDay = amount == 1 ? 1 : (amount-1)*7;
        for(int i = startingDay; i <= startingDay+7; i++) {
            final LocalDate targetDate = date.plusDays(i);
            final Month targetMonth = targetDate.getMonth();
            final DayOfWeek weekday = targetDate.getDayOfWeek();
            if(dayOfWeek == weekday && targetMonth == month) {
                return new EventDate(month, targetDate.getDayOfMonth(), year);
            }
        }
        return null;
    }
}
