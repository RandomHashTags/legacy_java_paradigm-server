package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.recent.software.videogame.NoMansSky;
import me.randomhashtags.worldlaws.recent.software.videogame.VideoGameUpdate;
import me.randomhashtags.worldlaws.recent.software.videogame.VideoGameUpdateController;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public final class VideoGameUpdates extends RecentEventController {
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
    public ConcurrentHashMap<String, HashSet<String>> refreshHashMap(LocalDate startingDate) {
        final VideoGameUpdateController[] controllers = getSupportedVideoGames();
        final ConcurrentHashMap<String, HashSet<String>> values = new ConcurrentHashMap<>();
        new CompletableFutures<VideoGameUpdateController>().stream(Arrays.asList(controllers), controller -> {
            final VideoGameUpdate update = controller.refresh(startingDate);
            if(update != null) {
                final String dateString = update.getDate().getDateString();
                values.putIfAbsent(dateString, new HashSet<>());
                values.get(dateString).add("\"" + controller.getName() + "\":" + update.toString());
            }
        });
        return values;
    }

    public String getAllVideoGames() {
        if(allVideoGames == null) {
            final JSONObject json = new JSONObject();
            for(VideoGameUpdateController controller : getSupportedVideoGames()) {
                json.put(LocalServer.fixEscapeValues(controller.getName()), controller.getDetailsJSONObject());
            }
            allVideoGames = json.toString();
        }
        return allVideoGames;
    }
}
