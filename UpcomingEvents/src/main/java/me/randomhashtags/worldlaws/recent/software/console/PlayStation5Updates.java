package me.randomhashtags.worldlaws.recent.software.console;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.recent.PreRecentEvent;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

public final class PlayStation5Updates extends RecentEventController {
    @Override
    public RecentEventType getType() {
        return RecentEventType.SOFTWARE_UPDATES;
    }

    @Override
    public HashSet<PreRecentEvent> refreshHashSet(LocalDate startingDate) {
        final String url = "https://www.playstation.com/en-us/support/hardware/ps5/system-software/";
        final Elements box = getDocumentElements(Folder.OTHER, url, false, "div.gdk div.cmp-container div.gdk div.cmp-container div.section section.section--light div div.contentgrid div.content-grid div.box");
        HashSet<PreRecentEvent> updates = null;
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
                final Elements paragraphs = updateNotesElements.select("p");
                final String[] updateNotesValues = paragraphs.get(0).text().split(" ");
                final String updateNotesTitle = "PS5 " + updateNotesValues[1] + " system update";
                final StringBuilder description = new StringBuilder();
                boolean isFirst = true;
                final Elements updateNotesList = updateNotesElements.select("ul");
                if(!updateNotesList.isEmpty()) {
                    for(Element element : updateNotesElements.select("ul").get(0).select("li")) {
                        description.append(isFirst ? "" : "\n").append(element.text());
                        isFirst = false;
                    }
                } else {
                    description.append(paragraphs.get(1).text());
                }
                final PreRecentEvent event = new PreRecentEvent(date, updateNotesTitle, description.toString(), null, new EventSources(new EventSource("PlayStation Support: PS5 System Software", url)));
                updates = new HashSet<>() {{ add(event); }};
            }
        }
        return updates;
    }
}
