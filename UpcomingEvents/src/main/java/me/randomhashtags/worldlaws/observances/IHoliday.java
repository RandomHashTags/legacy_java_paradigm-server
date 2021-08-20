package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

public interface IHoliday extends Jsoupable, Jsonable {
    Enum<? extends IHoliday> getEnum();
    default String getName() {
        final String wikipediaName = getOfficialName();
        return wikipediaName != null ? wikipediaName : LocalServer.toCorrectCapitalization(getEnum().name(), false, "of", "and", "de", "the", "it");
    }
    String getOfficialName();

    private String loadHolidayJSON(HolidayType holidayType) {
        final String url = getURL(), name = getName();
        final String fileName = holidayType.name().replace("_EAST", "").replace("_WEST", "") + "_" + name;
        final Document doc = getDocument(Folder.UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS, fileName, url, false);
        String description = null, imageURL = null;
        if(doc != null) {
            final String mwParserOutput = "div.mw-content-ltr div.mw-parser-output ";
            final Elements elements = doc.getAllElements();
            final Elements paragraphs = elements.select(mwParserOutput + "p");
            final Elements headings = elements.select(mwParserOutput + "h2");
            final int firstHeadingIndex = elements.indexOf(headings.get(0));

            final StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(Element paragraph : paragraphs) {
                final int index = elements.indexOf(paragraph);
                if(index < firstHeadingIndex) {
                    String text = paragraph.text();
                    if(!text.isEmpty()) {
                        builder.append(isFirst ? "" : "\n\n").append(text);
                        isFirst = false;
                    }
                } else {
                    break;
                }
            }
            description = LocalServer.fixEscapeValues(LocalServer.removeWikipediaTranslations(removeReferences(builder.toString())));

            final Elements infoboxes = doc.select(mwParserOutput + "table.infobox");
            if(!infoboxes.isEmpty()) {
                final Element infobox = infoboxes.get(0);
                final Elements images = infobox.select("a.image img");
                if(!images.isEmpty()) {
                    final Element image = images.get(0);
                    final String src = image.attr("src");
                    final String[] endingValues = src.split("/");
                    final String endingValue = endingValues[endingValues.length-1];
                    final String targetImageURL = src.contains("px-") ? src.split(endingValue)[0] + "%quality%px-" + endingValue.split("px-")[1] : src;
                    imageURL = "https:" + targetImageURL;
                }
            }
        }
        return "{\"description\":\"" + description + "\",\"imageURL\":\"" + imageURL + "\"}";
    }
    private void getHolidayJSON(HolidayType holidayType, CompletionHandler handler) {
        final String fileName = holidayType.name().replace("_EAST", "").replace("_WEST", "") + "_" + getName();
        getJSONObject(Folder.UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                handler.handleString(loadHolidayJSON(holidayType));
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
            }
        });
    }
    default void getDescription(HolidayType holidayType, CompletionHandler handler) {
        getHolidayJSON(holidayType, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(json.getString("description"));
            }
        });
    }
    default void getImageURL(HolidayType holidayType, CompletionHandler handler) {
        getHolidayJSON(holidayType, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(json.getString("imageURL"));
            }
        });
    }
    default HolidaySource getSource() {
        return HolidaySource.WIKIPEDIA;
    }
    default String getURL() {
        return getSource().getURL(getName());
    }
    String[] getAliases();
    default String[] collectAliases(String...aliases) {
        return aliases;
    }
    EventDate getDate(WLCountry country, int year);
    default EventSources getOtherSources() {
        return null;
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
