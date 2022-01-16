package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.MLBEvent;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;

public final class MLB extends USAUpcomingEventController {

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SPORT_MLB;
    }

    @Override
    public void load() {
        final LocalDate now = LocalDate.now();
        final int month = now.getMonthValue(), day = now.getDayOfMonth(), year = now.getYear();
        final String mlbScheduleDateString = now.getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        final String mlbScheduleURL = "https://www.mlb.com/schedule/" + mlbScheduleDateString;
        final EventSources sources = new EventSources(new EventSource("MLB Schedule", mlbScheduleURL));
        final Document doc = getDocument(mlbScheduleURL);
        if(doc != null) {
            final Elements dates = doc.select("div.ScheduleCollectionGridstyle__SectionWrapper-sc-c0iua4-0");
            final int max = dates.size();
            if(max > 0) {
                ParallelStream.stream(dates, dateElementObj -> {
                    final Element dateElement = (Element) dateElementObj;
                    final String previousElementString = dateElement.previousElementSibling().text();
                    final String[] values = previousElementString.split(" ");
                    final Month targetMonth = Month.valueOf(values[1].toUpperCase());
                    final int targetDay = Integer.parseInt(values[2]);
                    final String dateString = getEventDateString(year, targetMonth, targetDay);
                    final Elements matches = dateElement.select("div.ScheduleGamestyle__DesktopScheduleGameWrapper-sc-b76vp3-0");
                    matches.parallelStream().forEach(matchElement -> {
                        final Element teamElement = matchElement.selectFirst("div.TeamMatchupLayerstyle__TeamMatchupLayerWrapper-sc-ouprud-0");
                        final Element awayTeamElement = teamElement.selectFirst("div.TeamMatchupLayerstyle__AwayWrapper-sc-ouprud-1"), homeTeamElement = teamElement.selectFirst("div.TeamMatchupLayerstyle__HomeWrapper-sc-ouprud-2");
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
                        final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, url, targetTimeET, null, customValues);
                        putPreUpcomingEvent(id, preUpcomingEvent);
                    });
                });
            }
        }
    }

    private JSONObject getTeamJSON(Element teamElement) {
        final JSONObject json = new JSONObject();
        final Element ahrefElement = teamElement.selectFirst("div div a[href]");
        final String scheduleURL = ahrefElement.attr("href");
        json.put("scheduleURL", scheduleURL);

        final Element teamLogoElement = ahrefElement.selectFirst("div img");
        final String teamLogoURL = teamLogoElement.attr("src");
        json.put("logoURL", teamLogoURL);

        final Elements nameElement = ahrefElement.select("div div div.TeamWrappersstyle__DesktopTeamWrapper-sc-uqs6qh-0");
        final String teamName = nameElement.text();
        json.put("name", teamName);

        return json;
    }

    @Override
    public String loadUpcomingEvent(String id) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String title = preUpcomingEvent.getTitle();
        final EventSources sources = (EventSources) preUpcomingEvent.getCustomValue("sources");
        final String awayTeam = (String) preUpcomingEvent.getCustomValue("awayTeam"), homeTeam = (String) preUpcomingEvent.getCustomValue("homeTeam");

        final MLBEvent event = new MLBEvent(title, awayTeam, homeTeam, null, sources);
        final String string = event.toString();
        putUpcomingEvent(id, string);
        return string;
    }
}
