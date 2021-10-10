package me.randomhashtags.worldlaws.recent.software.videogames;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;

public enum Overwatch implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getUpdatePageURL() {
        return "https://playoverwatch.com/en-us/news/patch-notes/";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final String url = getUpdatePageURL();
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements patchElements = doc.select("section section.NotchPage-body div.Container div.PatchNotes-body div.PatchNotes-patch");
            final EventSources sources = new EventSources(new EventSource("Overwatch: Patch Notes", url));
            for(Element patchElement : patchElements) {
                final String[] values = patchElement.attr("id").substring("patch-".length()).split("-");
                final int year = Integer.parseInt(values[0]), monthValue = Integer.parseInt(values[1]), day = Integer.parseInt(values[2]);
                final LocalDate date = LocalDate.of(year, Month.of(monthValue), day);
                if(date.isAfter(startingDate)) {
                    final EventDate eventDate = new EventDate(date);
                    final StringBuilder description = new StringBuilder();
                    boolean isFirst = true;
                    for(TextNode textNode : patchElement.textNodes()) {
                        description.append(isFirst ? "" : "\n").append(textNode.text());
                        isFirst = false;
                    }
                    String month = date.getMonth().name();
                    month = month.charAt(0) + month.substring(1, 3).toLowerCase() + " " + day;
                    final VideoGameUpdate update = new VideoGameUpdate(eventDate, month + " Patch Notes", description.toString(), null, sources);
                    handler.handleObject(update);
                    return;
                }
            }
        }
        handler.handleObject(null);
    }
}
