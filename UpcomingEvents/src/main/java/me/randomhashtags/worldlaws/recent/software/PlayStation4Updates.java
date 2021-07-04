package me.randomhashtags.worldlaws.recent.software;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.recent.RecentEventController;
import me.randomhashtags.worldlaws.recent.RecentEventType;
import org.apache.logging.log4j.Level;
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
        final long started = System.currentTimeMillis();
        final String url = "https://www.playstation.com/en-us/support/hardware/ps4/system-software/";
        final Elements elements = getDocumentElements(FileType.OTHER, url, false, "div.gdk div.cmp-container div.gdk div.cmp-container div.section section.section--light div div.contentgrid div.content-grid div.box div.textblock div.text-block p");
        if(elements != null) {
            final String string = elements.get(0).text();
            final String[] values = string.split(" ");
            final String[] dateValues = values[values.length-1].replace(".", "").split("/");
            final Month month = Month.of(Integer.parseInt(dateValues[1]));
            final int day = Integer.parseInt(dateValues[0]), year = Integer.parseInt(dateValues[2]);
            final EventDate date = new EventDate(month, day, year);
            if(date.getLocalDate().isAfter(startingDate)) {
            }
            WLLogger.log(Level.INFO, "PlayStation4Updates;date=" + date.toString());
        }
    }
}
