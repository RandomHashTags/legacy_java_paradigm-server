package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLUtilities;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public enum VideoGameUpdates implements RecentEventController {
    INSTANCE
    ;

    @Override
    public RecentEventType getType() {
        return RecentEventType.VIDEO_GAME_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final HashSet<String> values = new HashSet<>();
        final VideoGame[] videoGames = VideoGame.values();
        final int max = videoGames.length;
        final AtomicInteger completed = new AtomicInteger(0);
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleString(String string) {
                if(string != null) {
                    values.add(string);
                }
                if(completed.addAndGet(1) == max) {
                    handler.handleHashSetString(values);
                }
            }
        };
        Arrays.asList(videoGames).parallelStream().forEach(videoGame -> {
            videoGame.refresh(startingDate, completionHandler);
        });
    }

    private enum VideoGame implements Jsoupable {
        CALL_OF_DUTY(
                "https://www.callofduty.com/blog"
        ),
        DEAD_BY_DAYLIGHT(
                "https://forum.deadbydaylight.com/en/kb/patchnotes"
        ),
        MINECRAFT(
                "https://feedback.minecraft.net/hc/en-us/sections/360001186971-Release-Changelogs"
        ),
        NO_MANS_SKY(
                "No Man's Sky",
                "https://www.nomanssky.com/release-log/"
        ),
        OVERWATCH(
                "https://playoverwatch.com/en-us/news/patch-notes/"
        ),
        ;

        private final String name, url;

        VideoGame(String url) {
            this(null, url);
        }
        VideoGame(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name != null ? name : LocalServer.toCorrectCapitalization(name(), "by", "of");
        }

        public void refresh(LocalDate startingDate, CompletionHandler handler) {
            switch (this) {
                case NO_MANS_SKY:
                    refreshNoMansSky(startingDate, handler);
                    break;
                default:
                    handler.handleString(null);
                    break;
            }
        }

        private void refreshNoMansSky(LocalDate startingDate, CompletionHandler handler) {
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements elements = doc.select("main section.section div.section__content div.box div.grid div.grid__cell a.link");
                final Element first = elements.first();
                if(first != null) {
                    final String ahref = first.attr("href");
                    final Document targetDoc = getDocument(ahref);
                    if(targetDoc != null) {
                        final Elements box = targetDoc.select("main section.section div.section__content div.box");
                        final Elements targetElements = box.select("div.post-meta span.date");
                        final Element dateElement = targetElements.first();
                        if(dateElement != null) {
                            final String[] dateValues = dateElement.text().replace(",", "").split(" ");
                            final Month month = WLUtilities.valueOfMonthFromInput(dateValues[0]);
                            final int day = Integer.parseInt(dateValues[1]), year = Integer.parseInt(dateValues[2]);
                            final LocalDate targetDate = LocalDate.of(year, month, day);
                            if(startingDate.isBefore(targetDate)) {
                                final String update = box.select("h1").get(0).text();
                                final PreRecentEvent preRecentEvent = new PreRecentEvent(update, "No Man's Sky Update Description", null);
                                handler.handleString(preRecentEvent.toString());
                            } else {
                                handler.handleString(null);
                            }
                        } else {
                            handler.handleString(null);
                        }
                    } else {
                        handler.handleString(null);
                    }
                } else {
                    handler.handleString(null);
                }
            } else {
                handler.handleString(null);
            }
        }
    }
}
