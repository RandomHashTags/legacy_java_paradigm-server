package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.recent.software.videogame.NoMansSky;
import me.randomhashtags.worldlaws.recent.software.videogame.VideoGameUpdate;
import me.randomhashtags.worldlaws.recent.software.videogame.VideoGameUpdateController;
import me.randomhashtags.worldlaws.stream.ParallelStream;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public enum VideoGameUpdates implements RecentEventController {
    INSTANCE;

    public static VideoGameUpdateController[] getSupportedVideoGames() {
        return new VideoGameUpdateController[] {
                NoMansSky.INSTANCE,
                //Overwatch.INSTANCE
        };
    }

    private String allVideoGames;

    @Override
    public RecentEventType getType() {
        return RecentEventType.VIDEO_GAME_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final VideoGameUpdateController[] controllers = getSupportedVideoGames();
        final int max = controllers.length;
        final AtomicInteger completed = new AtomicInteger(0);
        final ConcurrentHashMap<String, HashSet<String>> values = new ConcurrentHashMap<>();
        ParallelStream.stream(Arrays.asList(controllers), controllerObj -> {
            final VideoGameUpdateController controller = (VideoGameUpdateController) controllerObj;
            controller.refresh(startingDate, new CompletionHandler() {
                @Override
                public void handleObject(Object object) {
                    if(object != null) {
                        final VideoGameUpdate update = (VideoGameUpdate) object;
                        final String dateString = update.getDate().getDateString();
                        values.putIfAbsent(dateString, new HashSet<>());
                        values.get(dateString).add("\"" + controller.getName() + "\":" + update.toString());
                    }
                    if(completed.addAndGet(1) == max) {
                        handler.handleConcurrentHashMapHashSetString(values);
                    }
                }
            });
        });
    }

    public String getAllVideoGames() {
        if(allVideoGames == null) {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(VideoGameUpdateController controller : getSupportedVideoGames()) {
                builder.append(isFirst ? "" : ",").append(controller.toVideoGameJSON());
                isFirst = false;
            }
            builder.append("}");
            allVideoGames = builder.toString();
        }
        return allVideoGames;
    }
}
