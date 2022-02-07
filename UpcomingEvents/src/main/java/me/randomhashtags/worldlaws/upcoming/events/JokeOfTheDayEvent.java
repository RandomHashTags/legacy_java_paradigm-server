package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;

public final class JokeOfTheDayEvent extends UpcomingEvent {

    private final String copyright, question, answer;

    public JokeOfTheDayEvent(String title, String description, String imageURL, String copyright, String question, String answer, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.copyright = LocalServer.fixEscapeValues(copyright);
        this.question = LocalServer.fixEscapeValues(question);
        this.answer = LocalServer.fixEscapeValues(answer);
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.JOKE_OF_THE_DAY;
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"" + UpcomingEventValue.JOKE_OF_THE_DAY_COPYRIGHT.getKey() + "\":\"" + copyright + "\"," +
                (answer != null ? "\"" + UpcomingEventValue.JOKE_OF_THE_DAY_ANSWER.getKey() + "\":\"" + answer + "\"," : "") +
                "\"" + UpcomingEventValue.JOKE_OF_THE_DAY_QUESTION + "\":\"" + question + "\"" +
                "}";
    }
}
