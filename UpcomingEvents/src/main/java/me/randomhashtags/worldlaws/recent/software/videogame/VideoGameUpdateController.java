package me.randomhashtags.worldlaws.recent.software.videogame;

import me.randomhashtags.worldlaws.Jsoupable;
import org.json.JSONObject;

import java.time.LocalDate;

public interface VideoGameUpdateController extends Jsoupable {
    String getName();
    String getCoverArtURL();
    String getUpdatePageURL();
    VideoGameUpdate refresh(LocalDate startingDate);

    default JSONObject getDetailsJSONObject() {
        final JSONObject json = new JSONObject();
        final String coverArtURL = getCoverArtURL();
        if(coverArtURL != null) {
            json.put("imageURL", coverArtURL);
        }
        return json;
    }
}
