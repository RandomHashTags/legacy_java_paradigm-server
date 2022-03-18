package me.randomhashtags.worldlaws.recent;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.recent.software.videogame.NoMansSky;
import me.randomhashtags.worldlaws.recent.software.videogame.VideoGameUpdate;
import me.randomhashtags.worldlaws.recent.software.videogame.VideoGameUpdateController;
import me.randomhashtags.worldlaws.settings.ResponseVersions;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

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

    @Override
    public RecentEventType getType() {
        return RecentEventType.VIDEO_GAME_UPDATES;
    }

    @Override
    public ConcurrentHashMap<String, HashSet<PreRecentEvent>> refreshHashMap(LocalDate startingDate) {
        final VideoGameUpdateController[] controllers = getSupportedVideoGames();
        final ConcurrentHashMap<String, HashSet<PreRecentEvent>> values = new ConcurrentHashMap<>();
        new CompletableFutures<VideoGameUpdateController>().stream(Arrays.asList(controllers), controller -> {
            final VideoGameUpdate update = controller.refresh(startingDate);
            if(update != null) {
                final String dateString = update.getDate().getDateString();
                values.putIfAbsent(dateString, new HashSet<>());
                values.get(dateString).add(update);
            }
        });
        return values;
    }

    public static JSONObjectTranslatable getTypesJSON() { // TODO: save to file
        final JSONObjectTranslatable json = new JSONObjectTranslatable("types");
        json.put("version", ResponseVersions.VIDEO_GAMES.getValue());
        final JSONObjectTranslatable typesJSON = new JSONObjectTranslatable();
        for(VideoGameUpdateController controller : getSupportedVideoGames()) {
            typesJSON.put(LocalServer.fixEscapeValues(controller.getName()), controller.getDetailsJSONObject());
        }
        json.put("types", typesJSON);
        return json;
    }
}
