package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.recent.software.videogames.NoMansSky;
import me.randomhashtags.worldlaws.recent.software.videogames.VideoGameUpdateController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public enum VideoGameUpdates implements RecentEventController {
    INSTANCE;

    public static VideoGameUpdateController[] getSupportedVideoGames() {
        return new VideoGameUpdateController[] {
                NoMansSky.INSTANCE
        };
    }

    @Override
    public RecentEventType getType() {
        return RecentEventType.VIDEO_GAME_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final HashSet<String> values = new HashSet<>();
        final VideoGameUpdateController[] controllers = getSupportedVideoGames();
        final int max = controllers.length;
        final AtomicInteger completed = new AtomicInteger(0);
        final CompletionHandler completionHandler = new CompletionHandler() {
            @Override
            public void handleStringValue(String key, String value) {
                if(value != null) {
                    values.add("\"" + key + "\":" + value);
                }
                if(completed.addAndGet(1) == max) {
                    handler.handleHashSetString(values);
                }
            }
        };
        Arrays.asList(controllers).parallelStream().forEach(controller -> {
            controller.refresh(startingDate, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    completionHandler.handleStringValue(controller.getName(), string);
                }
            });
        });
    }
}
