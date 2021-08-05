package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum MLB implements USAUpcomingEventController {
    INSTANCE;

    private HashMap<String, PreUpcomingEvent> preUpcomingEvents;
    private HashMap<String, String> upcomingEvents;

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_MLB;
    }

    @Override
    public HashMap<String, PreUpcomingEvent> getPreUpcomingEvents() {
        return preUpcomingEvents;
    }

    @Override
    public HashMap<String, String> getUpcomingEvents() {
        return upcomingEvents;
    }

    @Override
    public void load(CompletionHandler handler) {
        refresh(handler);
    }

    private void refresh(CompletionHandler handler) {
        preUpcomingEvents = new HashMap<>();
        upcomingEvents = new HashMap<>();

        final LocalDate now = LocalDate.now();
        final int month = now.getMonthValue(), day = now.getDayOfMonth(), year = now.getYear();
        final String mlbScheduleDateString = now.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        final String mlbScheduleURL = "https://www.mlb.com/schedule/" + mlbScheduleDateString;
        final EventSources sources = new EventSources(new EventSource("MLB Schedule", mlbScheduleURL));
        final Document doc = getDocument(mlbScheduleURL);
        if(doc != null) {
            final Elements dates = doc.select("body main div div section section div div + div.ScheduleCollectionGridstyle__SectionWrapper-c0iua4-0");
            final AtomicInteger completed = new AtomicInteger(0);
            final int max = dates.size();
            dates.parallelStream().forEach(dateElement -> {
                final String previousElementString = dateElement.previousElementSibling().text();
                final String[] values = previousElementString.split(" ");
                final Month targetMonth = Month.valueOf(values[1].toUpperCase());
                final int targetDay = Integer.parseInt(values[2]);
                final String dateString = getEventDateString(year, targetMonth, targetDay);
                final Elements matches = dateElement.select("div.ScheduleGamestyle__DesktopScheduleGameWrapper-b76vp3-0");
                matches.parallelStream().forEach(matchElement -> {
                    final Element teamElement = matchElement.selectFirst("div.TeamMatchupLayerstyle__TeamMatchupLayerWrapper-ouprud-0");
                    final Element awayTeamElement = teamElement.selectFirst("div.TeamMatchupLayerstyle__AwayWrapper-ouprud-1"), homeTeamElement = teamElement.selectFirst("div.TeamMatchupLayerstyle__HomeWrapper-ouprud-2");
                    final JSONObject awayTeamJSON = getTeamJSON(awayTeamElement), homeTeamJSON = getTeamJSON(homeTeamElement);
                    final String title = awayTeamJSON.getString("name") + " @ " + homeTeamJSON.getString("name");

                    final Element timeElement = matchElement.selectFirst("div.GameInfoLayerstyle__GameInfoLayerWrapper-sc-1xxsnoa-0").selectFirst("div.GameInfoLayerstyle__GameInfoTextWrapper-sc-1xxsnoa-1").selectFirst("a[href]");
                    final String url = timeElement.attr("href"), targetTimeET = timeElement.text();

                    final String id = getEventDateIdentifier(dateString, title);
                    final HashMap<String, Object> customValues = new HashMap<>() {{
                        put("sources", sources);
                        put("awayTeam", awayTeamJSON.toString());
                        put("homeTeam", homeTeamJSON.toString());
                    }};
                    final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, url, targetTimeET, customValues);
                    preUpcomingEvents.put(id, preUpcomingEvent);
                });
                if(completed.addAndGet(1) == max) {
                    handler.handleString(null);
                }
            });
        } else {
            handler.handleString(null);
        }
    }

    private JSONObject getTeamJSON(Element teamElement) {
        final JSONObject json = new JSONObject();
        final Element ahrefElement = teamElement.selectFirst("div div a[href]");
        final String scheduleURL = ahrefElement.attr("href");
        json.put("scheduleURL", scheduleURL);

        final Elements nameElement = ahrefElement.select("div div div.TeamWrappersstyle__DesktopTeamWrapper-uqs6qh-0");
        final String teamName = nameElement.text();
        json.put("name", teamName);

        return json;
    }

    @Override
    public void loadUpcomingEvent(String id, CompletionHandler handler) {
        final PreUpcomingEvent preUpcomingEvent = preUpcomingEvents.get(id);
        final String title = preUpcomingEvent.getTitle();
        final EventSources sources = (EventSources) preUpcomingEvent.getCustomValue("sources");
        final String awayTeam = (String) preUpcomingEvent.getCustomValue("awayTeam"), homeTeam = (String) preUpcomingEvent.getCustomValue("homeTeam");

        final MLBEvent event = new MLBEvent(title, awayTeam, homeTeam, null, sources);
        final String string = event.toJSON();
        upcomingEvents.put(id, string);
        handler.handleString(string);
    }
}
