package me.randomhashtags.worldlaws.recent.software;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

public enum PlayStation5Updates implements RecentEventController {
    INSTANCE;

    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final String url = "https://www.playstation.com/en-us/support/hardware/ps5/system-software/";
        final Elements box = getDocumentElements(Folder.OTHER, url, false, "div.gdk div.cmp-container div.gdk div.cmp-container div.section section.section--light div div.contentgrid div.content-grid div.box");
        if(box != null) {
            final Elements elements = box.select("div.textblock div.text-block p");
            final String string = elements.get(0).text();
            final String[] values = string.split(" ");
            final String[] dateValues = values[values.length-1].replace(".", "").split("/");
            final Month month = Month.of(Integer.parseInt(dateValues[0]));
            final int day = Integer.parseInt(dateValues[1]), year = Integer.parseInt(dateValues[2]);
            final EventDate date = new EventDate(month, day, year);
            if(date.getLocalDate().isAfter(startingDate)) {
                final Elements updateNotesElements = box.select("div.inlineAccordion div.accordion div.accordion__item-description div div.textblock").get(1).select("div.text-block");
                final String[] updateNotesValues = updateNotesElements.select("p").get(0).text().split(" ");
                final String updateNotesTitle = "PS5 " + updateNotesValues[1] + " system update";
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                for(Element element : updateNotesElements.select("ul").get(0).select("li")) {
                    description.append(isFirst ? "" : "\n").append(element.text());
                    isFirst = false;
                }
                final PreRecentEvent event = new PreRecentEvent(date, updateNotesTitle, description.toString(), null, new EventSources(new EventSource("PlayStation Support: PS5 System Software", url)));
                final HashSet<PreRecentEvent> hashset = new HashSet<>() {{ add(event); }};
                handler.handleObject(hashset);
            } else {
                handler.handleObject(null);
            }
        } else {
            handler.handleObject(null);
        }
    }
}
