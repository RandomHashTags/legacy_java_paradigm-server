package me.randomhashtags.worldlaws.recent.software;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;

public enum PlayStation4Updates implements RecentEventController, Jsoupable {
    INSTANCE;

    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final String url = "https://www.playstation.com/en-us/support/hardware/ps4/system-software/";
        final Elements box = getDocumentElements(FileType.OTHER, url, false, "div.gdk div.cmp-container div.gdk div.cmp-container div.section section.section--light div div.contentgrid div.content-grid div.box");
        if(box != null) {
            final Elements elements = box.select("div.textblock div.text-block p");
            final String string = elements.get(0).text();
            final String[] values = string.split(" ");
            final String[] dateValues = values[values.length-1].replace(".", "").split("/");
            final Month month = Month.of(Integer.parseInt(dateValues[1]));
            final int day = Integer.parseInt(dateValues[0]), year = Integer.parseInt(dateValues[2]);
            final EventDate date = new EventDate(month, day, year);
            if(date.getLocalDate().isAfter(startingDate)) {
                final Elements updateNotesElements = box.select("div.inlineAccordion div.accordion div.accordion__item-description div div.textblock").get(0).select("div.text-block");
                final String[] updateNotesValues = updateNotesElements.select("p").text().split(" ");
                final String updateNotesTitle = updateNotesValues[0] + " " + updateNotesValues[1] + " system update";
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : updateNotesElements.select("ul").select("li")) {
                    description.append(isFirst ? "" : "\n").append(element.text());
                    isFirst = false;
                }
                final PreRecentEvent event = new PreRecentEvent("ps4Update", updateNotesTitle, description.toString(), null);
                handler.handleString(event.toString());
            } else {
                handler.handleString(null);
            }
        } else {
            handler.handleString(null);
        }
    }
}
