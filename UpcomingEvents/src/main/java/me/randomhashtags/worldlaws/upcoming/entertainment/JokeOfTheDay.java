package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.JokeOfTheDayEvent;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONObject;

import java.time.Month;

public final class JokeOfTheDay extends LoadedUpcomingEventController {
    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.JOKE_OF_THE_DAY;
    }

    @Override
    public void load() {
        final String url = "https://api.jokes.one/jod";
        final JSONObject json = requestJSONObject(url);
        if(json != null) {
            final UpcomingEventType type = getType();
            final String imageURL = "https://jokes.one/img/joke_of_the_day.jpg", copyright = "Jokes.One";
            final EventSources sources = new EventSources(
                    new EventSource(copyright, "https://jokes.one")
            );

            final JSONObject contents = json.getJSONObject("contents");

            final JSONObject jokeJSON = contents.getJSONArray("jokes").getJSONObject(0).getJSONObject("joke");
            final String title = jokeJSON.getString("title");

            final String text = jokeJSON.getString("text");
            final boolean isKnockKnockJoke = text.startsWith("Knock Knock - "), hasInheritedAnswer = text.contains("?\n\n\n ");
            final String[] textValues = (isKnockKnockJoke ? text.split("who\\? ") : hasInheritedAnswer ? text.split("\\?\n\n\n ") : text.substring(2).split("\\?\r\nA: "));
            String question = textValues[0] + (isKnockKnockJoke ? "who? " : "?"), answer = textValues.length > 1 ? textValues[1] : null;
            if(isKnockKnockJoke && !question.startsWith("K")) {
                question = "K" + question;
            }

            final String[] dateValues = jokeJSON.getString("date").split("-");
            final int year = Integer.parseInt(dateValues[0]), monthValue = Integer.parseInt(dateValues[1]), day = Integer.parseInt(dateValues[2]);
            final Month month = Month.of(monthValue);
            final EventDate date = new EventDate(month, day, year);
            final String dateString = date.getDateString();

            final String identifier = getEventDateIdentifier(dateString, title);

            final JokeOfTheDayEvent event = new JokeOfTheDayEvent(date, title, null, imageURL, copyright, question, answer, sources);
            putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(type, identifier, "Copyright: " + copyright));
            putUpcomingEvent(identifier, event);
        }
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new JokeOfTheDayEvent(json);
    }
}
