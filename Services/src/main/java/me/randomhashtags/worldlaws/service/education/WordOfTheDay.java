package me.randomhashtags.worldlaws.service.education;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

public enum WordOfTheDay implements WLService {
    INSTANCE;

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_WORD_OF_THE_DAY;
    }

    public void refresh(CompletionHandler handler) {
        final LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        final LocalDate leftEarth = now.minusHours(9).toLocalDate();
        final LocalDate rightEarth = now.plusHours(14).toLocalDate();
        final int max = leftEarth.equals(rightEarth) ? 1 : 2;
        final HashSet<String> values = new HashSet<>();
        for(int i = 1; i <= max; i++) {
            final LocalDate targetDate = i == 1 ? leftEarth : rightEarth;
            final int month = targetDate.getMonthValue(), day = targetDate.getDayOfMonth();
            final String dateString = targetDate.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
            final String wordOfTheDay = getWordOfTheDay(dateString);
            if(wordOfTheDay != null) {
                final String eventDateString = new EventDate(targetDate).getDateString();
                values.add("\"" + eventDateString + "\":" + wordOfTheDay);
            }
        }
        String value = null;
        if(!values.isEmpty()) {
            final StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(String stringValue : values) {
                builder.append(isFirst ? "" : ",").append(stringValue);
                isFirst = false;
            }
            value = builder.toString();
        }
        handler.handleString(value);
    }

    private String getWordOfTheDay(String dateString) {
        final String url = "https://www.merriam-webster.com/word-of-the-day/" + dateString;
        final Elements mainArticle = getDocumentElements(Folder.OTHER, url, "main article");
        String string = null;
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
            final WordOfTheDayObj obj = new WordOfTheDayObj(word, builder.toString());
            string = obj.toString();
        }
        return string;
    }

    private final class WordOfTheDayObj {
        private final String word, description;

        public WordOfTheDayObj(String word, String description) {
            this.word = LocalServer.fixEscapeValues(word);
            this.description = LocalServer.fixEscapeValues(description);
        }

        @Override
        public String toString() {
            return "{" +
                    "\"word\":\"" + word + "\"," +
                    "\"description\":\"" + description + "\"" +
                    "}";
        }
    }
}
