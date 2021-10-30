package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;

public enum NoMansSky implements VideoGameUpdateController {
    INSTANCE;

    @Override
    public String getName() {
        return "No Man's Sky";
    }

    @Override
    public String getCovertArtURL() {
        return "https://nmswp.azureedge.net/wp-content/uploads/2021/09/nms-frontiers-book-cover-opt.png";
    }

    @Override
    public String getUpdatePageURL() {
        return "https://www.nomanssky.com/release-log/";
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final String url = getUpdatePageURL();
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements elements = doc.select("main section.section div.section__content div.box div.grid div.grid__cell a.link");
            final Element first = elements.get(0);
            if(first != null) {
                final String name = getName();
                String ahref = first.attr("href");
                final EventSources sources = new EventSources();
                sources.append(new EventSource(name + ": Release log", url));

                if(ahref.startsWith("/") && ahref.endsWith("/")) { // new major release
                    final String slug = ahref.substring(1, ahref.length()-1).replace("-", " ");
                    ahref = "https://www.nomanssky.com" + ahref;
                    final Document targetDoc = getDocument(ahref);
                    if(targetDoc != null) {
                        final String imageURL = targetDoc.selectFirst("section.section div.section__content img.no-lazy").attr("src");
                        final String description = targetDoc.selectFirst("section.section div.section__content div.box div.icons-header p.text--narrow").text();
                        final String title = LocalServer.toCorrectCapitalization(slug.replace(" ", "_"));
                        sources.append(new EventSource(name + ": " + title, ahref));
                        final LocalDate now = WLUtilities.getNowUTC();
                        final EventDate date = new EventDate(now.getMonth(), 1, now.getYear());
                        final VideoGameUpdate update = new VideoGameUpdate(date, title.replace(" Update", ""), description, imageURL, sources);
                        handler.handleObject(update);
                        return;
                    }
                } else {
                    final Document targetDoc = getDocument(ahref);
                    if(targetDoc != null) {
                        final Elements box = targetDoc.select("main section.section div.section__content div.box");
                        final Elements targetElements = box.select("div.post-meta span.date");
                        final Element dateElement = targetElements.first();
                        if(dateElement != null) {
                            final String[] dateValues = dateElement.text().replace(",", "").split(" ");
                            final Month month = WLUtilities.valueOfMonthFromInput(dateValues[0]);
                            final int day = Integer.parseInt(dateValues[1]), year = Integer.parseInt(dateValues[2]);
                            final EventDate eventDate = new EventDate(month, day, year);
                            if(startingDate.isBefore(eventDate.getLocalDate())) {
                                final String title = box.select("h1").get(0).text();
                                final String description = first.select("div.grid__cell-content p").get(0).text().replace(". Read more", "");
                                sources.append(new EventSource(name + ": " + title, ahref));
                                final VideoGameUpdate update = new VideoGameUpdate(eventDate, title, description, getLatestCoverArtURL(doc), sources);
                                handler.handleObject(update);
                                return;
                            }
                        }
                    }
                }
            }
        }
        handler.handleObject(null);
    }

    private String getLatestCoverArtURL(Document doc) {
        final String href = "https://www.nomanssky.com" + doc.selectFirst("nav.nav").selectFirst("ul.menu").selectFirst("li a[href]").attr("href");
        final Document latest = getDocument(href);
        return latest.selectFirst("section.section div.section__content img.no-lazy").attr("src");
    }
}
