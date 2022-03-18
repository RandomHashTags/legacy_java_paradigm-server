package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class JokeOfTheDayEvent extends UpcomingEvent {

    private final String copyright, question, answer;

    public JokeOfTheDayEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        copyright = properties.getString(UpcomingEventValue.JOKE_OF_THE_DAY_COPYRIGHT.getKey());
        question = properties.getString(UpcomingEventValue.JOKE_OF_THE_DAY_QUESTION.getKey());
        answer = properties.getString(UpcomingEventValue.JOKE_OF_THE_DAY_ANSWER.getKey());
        insertProperties();
    }
    public JokeOfTheDayEvent(EventDate date, String title, String description, String imageURL, String copyright, String question, String answer, EventSources sources) {
        super(date, title, description, imageURL, null, null, sources);
        this.copyright = LocalServer.fixEscapeValues(copyright);
        this.question = LocalServer.fixEscapeValues(question);
        this.answer = LocalServer.fixEscapeValues(answer);
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.JOKE_OF_THE_DAY;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable(
                UpcomingEventValue.JOKE_OF_THE_DAY_ANSWER.getKey(),
                UpcomingEventValue.JOKE_OF_THE_DAY_QUESTION.getKey()
        );
        json.put(UpcomingEventValue.JOKE_OF_THE_DAY_COPYRIGHT.getKey(), copyright);
        if(answer != null) {
            json.put(UpcomingEventValue.JOKE_OF_THE_DAY_ANSWER.getKey(), answer);
        }
        json.put(UpcomingEventValue.JOKE_OF_THE_DAY_QUESTION.getKey(), question);
        return json;
    }
}
