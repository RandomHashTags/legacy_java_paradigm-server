package me.randomhashtags.worldlaws.upcoming.education;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.WordOfTheDayEvent;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;

public final class WordOfTheDay extends LoadedUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.WORD_OF_THE_DAY;
    }

    @Override
    public void load() {
        final UpcomingEventType type = getType();
        final String imageURL = "https://www.trendingpod.com/wp-content/uploads/2017/12/1200px-Merriam-Webster_logo-1024x1024.png";

        final LocalDate targetDate = LocalDate.now();
        final int month = targetDate.getMonthValue(), day = targetDate.getDayOfMonth();
        final String targetDateString = targetDate.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        final String url = "https://www.merriam-webster.com/word-of-the-day/" + targetDateString;
        final WordOfTheDayObj wordOfTheDay = getWordOfTheDay(url);
        if(wordOfTheDay != null) {
            final EventSources sources = new EventSources(
                    new EventSource("Merriam-Webster", url)
            );
            final String dateString = new EventDate(targetDate).getDateString(), word = wordOfTheDay.word;
            final String identifier = getEventDateIdentifier(dateString, word);
            final WordOfTheDayEvent event = new WordOfTheDayEvent(word, wordOfTheDay.description, imageURL, sources);
            putLoadedPreUpcomingEvent(identifier, event.toPreUpcomingEventJSON(type, identifier, null));
            putUpcomingEvent(identifier, event.toString());
        }
    }

    private WordOfTheDayObj getWordOfTheDay(String url) { // TODO: more in-depth properties (+"see entry")
        final Elements mainArticle = getDocumentElements(Folder.OTHER, url, "main article");
        WordOfTheDayObj obj = null;
        if(mainArticle != null) {
            final Elements elements = mainArticle.select("div.article-header-container div.quick-def-box div.word-header div.word-and-pronunciation h1");
            final String word = elements.get(0).text();
            final Elements definitionElements = mainArticle.select("div.lr-cols-area div.left-content div.wod-article-container div.wod-definition-container").get(0).children();
            definitionElements.removeIf(node -> !node.nodeName().equals("p"));
            final StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(Element paragraph : definitionElements) {
                builder.append(isFirst ? "" : "\n").append(paragraph.text());
                isFirst = false;
            }
            obj = new WordOfTheDayObj(word, builder.toString());
        }
        return obj;
    }

    private final class WordOfTheDayObj {
        private final String word, description;

        public WordOfTheDayObj(String word, String description) {
            this.word = LocalServer.toCorrectCapitalization(word);
            this.description = description;
        }
    }
}
