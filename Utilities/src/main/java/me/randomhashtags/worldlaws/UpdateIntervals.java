package me.randomhashtags.worldlaws;

import java.util.concurrent.TimeUnit;

public enum UpdateIntervals {
    ;

    public static final long SAVE_STATISTICS = TimeUnit.HOURS.toMillis(1);

    public enum Proxy {
        ;
        public static final long PING = TimeUnit.MINUTES.toMillis(10);
        public static final long HOME = TimeUnit.MINUTES.toMillis(10);
    }

    public enum Countries {
        ;
        public static final long NON_STATIC_VALUES = TimeUnit.DAYS.toMillis(3);
    }

    public enum Laws {
        ;
        public static final long RECENT_ACTIVITY = TimeUnit.HOURS.toMillis(3);
    }

    public enum Services {
        ;
        public static final long HOME = TimeUnit.HOURS.toMillis(1);
        public static final long TWITCH_CLIPS = TimeUnit.HOURS.toMillis(3);
    }

    public enum UpcomingEvents {
        ;
        public static final long HOME = TimeUnit.HOURS.toMillis(1);
        public static final long WEEKLY_EVENTS = TimeUnit.DAYS.toMillis(1);
        public static final long TV_SHOW_SCHEDULE = TimeUnit.DAYS.toMillis(1);
    }

    public enum Weather {
        ;
        public static final long HOME = TimeUnit.MINUTES.toMillis(10);
        public static final long ALERTS = TimeUnit.MINUTES.toMillis(10);
        public static final long EARTHQUAKES = TimeUnit.MINUTES.toMillis(30);
        public static final long EARTHQUAKES_CLEAR_CACHE = TimeUnit.HOURS.toMillis(6);
        public static final long NASA_WEATHER_EVENT_TRACKER = TimeUnit.HOURS.toMillis(1);
        public static final long NASA_WEATHER_VOLCANOS = TimeUnit.DAYS.toMillis(3);
    }
}
