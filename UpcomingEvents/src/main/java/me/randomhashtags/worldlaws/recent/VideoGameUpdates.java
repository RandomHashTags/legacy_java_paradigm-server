package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.recent.software.videogames.NoMansSky;
import me.randomhashtags.worldlaws.recent.software.videogames.VideoGameUpdate;
import me.randomhashtags.worldlaws.recent.software.videogames.VideoGameUpdateController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum VideoGameUpdates implements RecentEventController {
    INSTANCE;

    public static VideoGameUpdateController[] getSupportedVideoGames() {
        return new VideoGameUpdateController[] {
                NoMansSky.INSTANCE,
                //Overwatch.INSTANCE
        };
    }

    @Override
    public RecentEventType getType() {
        return RecentEventType.VIDEO_GAME_UPDATES;
    }

    @Override
    public void refresh(LocalDate startingDate, CompletionHandler handler) {
        final VideoGameUpdateController[] controllers = getSupportedVideoGames();
        final int max = controllers.length;
        final AtomicInteger completed = new AtomicInteger(0);
        final HashMap<String, HashSet<String>> values = new HashMap<>();
        Arrays.asList(controllers).parallelStream().forEach(controller -> controller.refresh(startingDate, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                if(object != null) {
                    final VideoGameUpdate update = (VideoGameUpdate) object;
                    final String dateString = update.getDate().getDateString();
                    values.putIfAbsent(dateString, new HashSet<>());
                    values.get(dateString).add("\"" + controller.getName() + "\":" + update.toString());
                }
                if(completed.addAndGet(1) == max) {
                    final HashSet<String> set = new HashSet<>();
                    if(!values.isEmpty()) {
                        for(Map.Entry<String, HashSet<String>> map : values.entrySet()) {
                            final StringBuilder builder = new StringBuilder("\"" + map.getKey() + "\":{");
                            boolean isFirst = true;
                            for(String value : map.getValue()) {
                                builder.append(isFirst ? "" : ",").append(value);
                                isFirst = false;
                            }
                            builder.append("}");
                            set.add(builder.toString());
                        }
                    }
                    handler.handleHashSetString(set);
                }
            }
        }));
    }
}
