package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public interface YouTubeService extends RestAPI, Jsonable {
    default JSONArray getVideosJSONArray(YouTubeVideoType type, String title) {
        title = LocalServer.fixUnescapeValues(title);
        final String apiKey = Settings.PrivateValues.YouTube.getKey();
        final String url = "https://youtube.googleapis.com/youtube/v3/search";
        final int requestLimit = Settings.PrivateValues.YouTube.getRequestLimit();
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>(GET_CONTENT_HEADERS);
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("part", "snippet");
        query.put("maxResults", Integer.toString(requestLimit));
        query.put("order", "relevance");
        final String q = URLEncoder.encode((title + " " + type.getSearchSuffix()), StandardCharsets.UTF_8);
        query.put("q", q);
        query.put("type", "video");
        query.put("key", apiKey);
        final JSONObject json = requestJSONObject(url, headers, query);
        JSONArray array = null;
        if(json != null) {
            final JSONArray items = json.getJSONArray("items");
            array = filterResults(type, title, items);
        } else {
            WLUtilities.saveLoggedError("YouTubeService", "json==null;title=" + title + ";q=" + q + ";headers=" + headers.toString() + ";query=" + query.toString());
        }
        return array != null && array.isEmpty() ? null : array;
    }
    private JSONArray filterResults(YouTubeVideoType type, String title, JSONArray items) {
        final JSONArray array = new JSONArray();
        final String titleLowercase = title.toLowerCase();
        final String alternateTitle1 = titleLowercase.replace(":", "");
        final String alternateTitle2 = titleLowercase.replace(" – ", " ");
        final String alternateTitle3 = titleLowercase.replace(":", "").replace(" – ", " ");

        new CompletableFutures<JSONObject>().stream(items, itemJSON -> {
            final JSONObject snippet = itemJSON.getJSONObject("snippet");
            final String uploader = snippet.getString("channelTitle");
            if(type.isLegitUploader(uploader)) {
                final String videoTitle = snippet.getString("title"), videoTitleLowercase = videoTitle.toLowerCase();
                if(videoTitleLowercase.contains(titleLowercase)
                        || videoTitleLowercase.contains(alternateTitle1)
                        || videoTitleLowercase.contains(alternateTitle2)
                        || videoTitleLowercase.contains(alternateTitle3)
                ) {
                    final JSONObject idJSON = itemJSON.getJSONObject("id");
                    final String videoID = idJSON.getString("videoId");
                    array.put(videoID);
                }
            }
        });
        return array;
    }

    enum YouTubeVideoType {
        MOVIE,
        VIDEO_GAME,
        ;

        public String getSearchSuffix() {
            switch (this) {
                default: return "trailer";
            }
        }
        public boolean isLegitUploader(String username) {
            switch (this) {
                case MOVIE: return Settings.ServerValues.YouTube.isSupportedMovieUploader(username);
                case VIDEO_GAME: return Settings.ServerValues.YouTube.isSupportedVideoGameUploader(username);
                default: return false;
            }
        }
    }
}
