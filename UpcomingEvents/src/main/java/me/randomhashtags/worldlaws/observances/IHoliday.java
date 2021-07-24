package me.randomhashtags.worldlaws.observances;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

public interface IHoliday extends Jsoupable {
    Enum<? extends IHoliday> getEnum();
    default String getName() {
        final String wikipediaName = getWikipediaName();
        return wikipediaName != null ? wikipediaName : LocalServer.toCorrectCapitalization(getEnum().name(), false, "of", "and", "de", "the", "it");
    }
    String getWikipediaName();

    private Document getDocument(HolidayType holidayType) {
        final String url = getWikipediaURL(), name = getName();
        final String fileName = holidayType.name().replace("_EAST", "").replace("_WEST", "") + "_" + name;
        return getDocument(Folder.UPCOMING_EVENTS_HOLIDAYS_DESCRIPTIONS, fileName, url, true);
    }
    default void getDescription(HolidayType holidayType, CompletionHandler handler) {
        final Document doc = getDocument(holidayType);
        if(doc != null) {
            final Elements elements = doc.getAllElements();
            final String prefix = "div.mw-content-ltr div.mw-parser-output ";
            final Elements paragraphs = elements.select(prefix + "p");
            final Elements headings = elements.select(prefix + "h2");
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
            final String string = LocalServer.removeWikipediaTranslations(removeReferences(builder.toString()));
            handler.handleString(string);
        }
    }
    default void getImageURL(HolidayType holidayType, CompletionHandler handler) {
        final Document doc = getDocument(holidayType);
        if(doc != null) {
            final Elements infoboxes = doc.select("div.mw-content-ltr div.mw-parser-output table.infobox");
            if(!infoboxes.isEmpty()) {
                final Element infobox = infoboxes.get(0);
                final Elements images = infobox.select("a.image img");
                if(!images.isEmpty()) {
                    final Element image = images.get(0);
                    final String src = image.attr("src");
                    final String[] endingValues = src.split("/");
                    final String endingValue = endingValues[endingValues.length-1];
                    final String url = "https:" + src.split(endingValue)[0] + "%quality%px-" + endingValue.split("px-")[1];
                    handler.handleString(url);
                } else {
                    handler.handleString(null);
                }
            } else {
                handler.handleString(null);
            }
        }
    }
    default String getWikipediaURL() {
        final String name = getName().replace(" ", "_");
        return "https://en.wikipedia.org/wiki/" + name;
    }
    String[] getAliases();
    EventDate getDate(WLCountry country, int year);
    default EventSources getOtherSources() {
        return null;
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
