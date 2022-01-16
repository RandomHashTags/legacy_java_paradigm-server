package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.JokeOfTheDayEvent;
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
        final JSONObject json = requestJSONObject(url, RequestMethod.GET);
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
            final String[] textValues = text.substring(2).split("\\?\r\nA: ");
            final String question = textValues[0] + "?", answer = textValues[1];

            final String[] dateValues = jokeJSON.getString("date").split("-");
            final int year = Integer.parseInt(dateValues[0]), monthValue = Integer.parseInt(dateValues[1]), day = Integer.parseInt(dateValues[2]);
            final Month month = Month.of(monthValue);
            final EventDate date = new EventDate(month, day, year);
            final String dateString = date.getDateString();

            final String identifier = getEventDateIdentifier(dateString, title);

            final JokeOfTheDayEvent event = new JokeOfTheDayEvent(title, null, imageURL, copyright, question, answer, sources);
            putLoadedPreUpcomingEvent(identifier, event.toPreUpcomingEventJSON(type, identifier, "Copyright: " + copyright));
            putUpcomingEvent(identifier, event.toString());
        }
    }
}
