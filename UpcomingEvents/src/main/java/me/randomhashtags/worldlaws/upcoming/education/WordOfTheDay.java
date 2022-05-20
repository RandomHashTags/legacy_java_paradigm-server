package me.randomhashtags.worldlaws.upcoming.education;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.events.WordOfTheDayEvent;
import org.json.JSONObject;
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

        final LocalDate now = LocalDate.now();
        final int month = now.getMonthValue(), day = now.getDayOfMonth();
        final String targetDateString = now.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        final String url = "https://www.merriam-webster.com/word-of-the-day/" + targetDateString;
        final WordOfTheDayObj wordOfTheDay = getWordOfTheDay(url);
        if(wordOfTheDay != null) {
            final EventSources sources = new EventSources(
                    new EventSource("Merriam-Webster", url)
            );
            final EventDate date = new EventDate(now);
            final String dateString = date.getDateString(), word = wordOfTheDay.word;
            final String identifier = getEventDateIdentifier(dateString, word);
            final WordOfTheDayEvent event = new WordOfTheDayEvent(date, word, wordOfTheDay.description, imageURL, wordOfTheDay.pronunciationURL, wordOfTheDay.type, wordOfTheDay.syllables, wordOfTheDay.examples, sources);
            putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(type, identifier, null));
            putUpcomingEvent(identifier, event);
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new WordOfTheDayEvent(json);
    }

    private WordOfTheDayObj getWordOfTheDay(String url) { // TODO: more in-depth properties (+"see entry")
        final Elements mainArticle = getDocumentElements(Folder.OTHER, url, "main article");
        WordOfTheDayObj obj = null;
        if(mainArticle != null) {
            final Element quickDefBox = mainArticle.select("div.article-header-container div.quick-def-box").get(0);
            final Element attributes = quickDefBox.selectFirst("div.word-attributes");
            String type = null, syllables = null;
            final Element typeElement = attributes.selectFirst("span.main-attr"), syllablesElement = attributes.selectFirst("span.word-syllables");
            if(typeElement != null) {
                type = typeElement.text();
            }
            if(syllablesElement != null) {
                syllables = syllablesElement.text();
            }

            final Elements elements = quickDefBox.select("div.word-header div.word-and-pronunciation h1");
            final String word = elements.get(0).text();
            final Elements definitionElements = mainArticle.select("div.lr-cols-area div.left-content div.wod-article-container div.wod-definition-container").get(0).children();
            definitionElements.removeIf(node -> !node.nodeName().equals("p"));

            final StringBuilder descriptionBuilder = new StringBuilder(), examplesBuilder = new StringBuilder();
            boolean isFirstDescription = true, isFirstExample = true;
            for(Element paragraph : definitionElements) {
                final String text = paragraph.text();
                if(text.startsWith("// ")) {
                    examplesBuilder.append(isFirstExample ? "" : "\n\n").append(text.substring(3));
                    isFirstExample = false;
                } else if(text.equals("See the entry >")) {
                } else {
                    descriptionBuilder.append(isFirstDescription ? "" : "\n").append(text);
                    isFirstDescription = false;
                }
            }
            obj = new WordOfTheDayObj(word, descriptionBuilder.toString(), null, type, syllables, examplesBuilder.toString());
        }
        return obj;
    }

    private final class WordOfTheDayObj {
        private final String word, description, pronunciationURL, type, syllables, examples;

        public WordOfTheDayObj(String word, String description, String pronunciationURL, String type, String syllables, String examples) {
            this.word = LocalServer.toCorrectCapitalization(word);
            this.description = description;
            this.pronunciationURL = pronunciationURL;
            this.type = type;
            this.syllables = syllables;
            this.examples = examples;
        }
    }
}
