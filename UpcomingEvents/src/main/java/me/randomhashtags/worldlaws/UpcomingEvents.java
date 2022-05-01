package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.service.YouTubeService;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import me.randomhashtags.worldlaws.upcoming.science.WikipediaTodaysFeaturedPicture;

import java.util.Map;

public final class UpcomingEvents implements WLServer, YouTubeService {
    public static final UpcomingEvents INSTANCE = new UpcomingEvents();

    public static void main(String[] args) {
        INSTANCE.initialize();
    }

    private void initialize() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.UPCOMING_EVENTS;
    }

    private void test() {
        final long started = System.currentTimeMillis();
        final WikipediaTodaysFeaturedPicture wikipedia = new WikipediaTodaysFeaturedPicture();
        wikipedia.refresh();
        for(Map.Entry<String, UpcomingEvent> entry : wikipedia.getUpcomingEvents().entrySet()) {
            WLLogger.logInfo("UpcomingEvents;test;entry.key=" + entry.getKey() + ";entry.value=" + entry.getValue().toString());
        }
        WLLogger.logInfo("UpcomingEvents;took " + WLUtilities.getElapsedTime(started));
    }

    @Override
    public WLHttpHandler getDefaultHandler() {
        return ServerRequestTypeUpcomingEvents.getDefaultHandler();
    }

    @Override
    public ServerRequestType[] getRequestTypes() {
        return ServerRequestTypeUpcomingEvents.values();
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                //new ServerRequest(ServerRequestTypeUpcomingEvents.ELECTIONS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.EVENT_TYPES),
                //new ServerRequest(ServerRequestTypeUpcomingEvents.HAPPENING_NOW),
                new ServerRequest(ServerRequestTypeUpcomingEvents.HOLIDAYS, "near"),
                new ServerRequest(ServerRequestTypeUpcomingEvents.MOVIE_PRODUCTION_COMPANIES),
                //new ServerRequest(ServerRequestTypeUpcomingEvents.MUSIC_ARTISTS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.PRESENTATIONS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.RECENT_EVENTS),
                new ServerRequest(ServerRequestTypeUpcomingEvents.WEEKLY_EVENTS),
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        registerFixedTimer(UpdateIntervals.UpcomingEvents.RECENT_EVENTS, () -> {
            final JSONObjectTranslatable test = RecentEvents.INSTANCE.refresh(7);
            insertInHomeResponse(APIVersion.v1, ServerRequestTypeUpcomingEvents.RECENT_EVENTS.name().toLowerCase(), test);
        });
        return 0;
    }
}
