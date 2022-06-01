package me.randomhashtags.worldlaws.upcoming.sports;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.PreUpcomingEvent;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.USAUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.MLBEvent;
import me.randomhashtags.worldlaws.upcoming.events.MLBTeamObj;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.sports.teams.MLBTeam;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.Month;

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
                new CompletableFutures<Element>().stream(dates, dateElement -> {
                    final String previousElementString = dateElement.previousElementSibling().text();
                    final String[] values = previousElementString.split(" ");
                    final Month targetMonth = Month.valueOf(values[1].toUpperCase());
                    final int targetDay = Integer.parseInt(values[2]);
                    final String dateString = EventDate.getDateString(year, targetDay, targetMonth);
                    final Elements matches = dateElement.select("div.ScheduleGamestyle__DesktopScheduleGameWrapper-sc-b76vp3-0");
                    for(Element matchElement : matches) {
                        final Element teamElement = matchElement.selectFirst("div.TeamMatchupLayerstyle__TeamMatchupLayerWrapper-sc-ouprud-0");
                        if(teamElement != null) {
                            final Element awayTeamElement = teamElement.selectFirst("div.TeamMatchupLayerstyle__AwayWrapper-sc-ouprud-1"), homeTeamElement = teamElement.selectFirst("div.TeamMatchupLayerstyle__HomeWrapper-sc-ouprud-2");
                            if(awayTeamElement != null && homeTeamElement != null) {
                                final MLBTeamObj awayTeam = getTeamJSON(awayTeamElement), homeTeam = getTeamJSON(homeTeamElement);
                                final String title = awayTeam.getName() + " @ " + homeTeam.getName();

                                final Element timeElement = matchElement.selectFirst("div.GameInfoLayerstyle__GameInfoLayerWrapper-sc-1xxsnoa-0").selectFirst("div.GameInfoLayerstyle__GameInfoTextWrapper-sc-1xxsnoa-1").selectFirst("a[href]");
                                final String url = timeElement.attr("href"), targetTimeET = timeElement.text();

                                final String id = getEventDateIdentifier(dateString, title);
                                final JSONObjectTranslatable customValues = new JSONObjectTranslatable("awayTeam", "homeTeam");
                                customValues.put("sources", sources.toJSONObject());
                                customValues.put("awayTeam", awayTeam.toJSONObject());
                                customValues.put("homeTeam", homeTeam.toJSONObject());
                                final PreUpcomingEvent preUpcomingEvent = new PreUpcomingEvent(id, title, url, targetTimeET, null, customValues);
                                putPreUpcomingEvent(id, preUpcomingEvent);
                            }
                        }
                    }
                });
            }
        }
    }

    private MLBTeamObj getTeamJSON(Element teamElement) {
        final Element ahrefElement = teamElement.selectFirst("div div a[href]");
        final String scheduleURL = ahrefElement.attr("href");
        final Elements nameElement = ahrefElement.select("div div div.TeamWrappersstyle__DesktopTeamWrapper-sc-uqs6qh-0");
        final String teamName = nameElement.text();

        final MLBTeam team = MLBTeam.valueOfInput(teamName);
        final String teamLogoURL = team != null ? team.getLogoURL() : null, wikipediaURL = team != null ? team.getWikipediaURL() : null;
        return new MLBTeamObj(teamName, scheduleURL, teamLogoURL, wikipediaURL);
    }

    @Override
    public UpcomingEvent loadUpcomingEvent(String id) {
        final PreUpcomingEvent preUpcomingEvent = getPreUpcomingEvent(id);
        final String title = preUpcomingEvent.getTitle();
        final JSONObject sourcesJSON = (JSONObject) preUpcomingEvent.getCustomValue("sources"), awayTeamJSON = (JSONObject) preUpcomingEvent.getCustomValue("awayTeam"), homeTeamJSON = (JSONObject) preUpcomingEvent.getCustomValue("homeTeam");
        final EventSources sources = new EventSources(sourcesJSON);
        final MLBTeamObj awayTeam = new MLBTeamObj(awayTeamJSON), homeTeam = new MLBTeamObj(homeTeamJSON);
        return new MLBEvent(preUpcomingEvent.getEventDate(), title, awayTeam, homeTeam, null, sources);
    }

    @Override
    public boolean isExactTime() {
        return false; // TODO: update
    }

    @Override
    public UpcomingEvent parseUpcomingEvent(JSONObject json) {
        return new MLBEvent(json);
    }
}
