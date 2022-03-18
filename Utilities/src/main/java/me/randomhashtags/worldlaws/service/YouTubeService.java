package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public interface YouTubeService extends RestAPI, Jsonable {
    default JSONArray getVideosJSONArray(YouTubeVideoType type, String title) {
        final String apiKey = Settings.PrivateValues.YouTube.getKey();
        final String url = "https://youtube.googleapis.com/youtube/v3/search";
        final int requestLimit = Settings.PrivateValues.YouTube.getRequestLimit();
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>(GET_CONTENT_HEADERS);
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("part", "snippet");
        query.put("maxResults", Integer.toString(requestLimit));
        query.put("order", "relevance");
        final String q = (title + " " + type.getSearchSuffix()).replace(" ", "%20");
        query.put("q", q);
        query.put("type", "video");
        query.put("key", apiKey);
        final JSONObject json = requestJSONObject(url, headers, query);
        JSONArray array = null;
        if(json != null) {
            final JSONArray items = json.getJSONArray("items");
            array = filterResults(type, title, items);
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
                case MOVIE: return isLegitMovieUploader(username);
                case VIDEO_GAME: return isLegitVideoGameUploader(username);
                default: return false;
            }
        }
        private boolean isLegitVideoGameUploader(String username) {
            switch (username.toLowerCase()) {
                case "7th beat games":
                case "amanita design":
                case "animu game":
                case "apex legends":
                case "bandai namco entertainment america":
                case "bandai namco entertainment europe":
                case "battlefield":
                case "bethesda softworks":
                case "borderlands":
                case "call of duty":
                case "capcom usa":
                case "chorus worldwide games":
                case "dead by daylight":
                case "deep silver":
                case "devolverdigital":
                case "doublefineprod":
                case "dying light":
                case "ea star wars":
                case "electronic arts":
                case "epic games":
                case "fatbot games":
                case "fireflyworlds":
                case "fortnite":
                case "futurlab":
                case "gamious":
                case "genshin impact":
                case "halo":
                case "hellogamestube":
                case "hitman":
                case "hytale":
                case "idea factory international":
                case "kingdom hearts":
                case "klei entertainment":
                case "life is strange":
                case "lowiro":
                case "mad mimic":
                case "magnus games":
                case "marmalade game studio":
                case "marvel entertainment":
                case "minecraft":
                case "modus games":
                case "mwm interactive":
                case "nintendo":
                case "nintendo uk":
                case "oddworld inhabitants":
                case "outriders":
                case "overkill software":
                case "plants vs. zombies":
                case "playdigious":
                case "playism":
                case "playoverwatch":
                case "playstation":
                case "pqube":
                case "resident evil":
                case "running with scissors":
                case "segaamerica":
                case "signalstudiosgames":
                case "skookum arts":
                case "skyforge":
                case "staxel":
                case "studio fow":
                case "studio hg":
                case "team meat":
                case "team woodsalt":
                case "total war":
                case "ubisoft":
                case "ubisoft north america":
                case "warcave":
                case "xbox":
                    return true;
                default:
                    return false;
            }
        }
        private boolean isLegitMovieUploader(String username) {
            switch (username.toLowerCase()) {
                case "20th century studios":
                case "a24":
                case "adult swim":
                case "amazon prime video":
                case "apple tv":
                case "disney plus":
                case "hulu":
                case "ifc films":
                case "lionsgate movies":
                case "marvel entertainment":
                case "movieclips":
                case "movieclips classic trailers":
                case "netflix":
                case "one media":
                case "paramount pictures":
                case "pixar":
                case "sony pictures classic":
                case "sony pictures entertainment":
                case "strand releasing":
                case "the fast saga":
                case "the witcher netflix":
                case "universal pictures":
                case "vertical entertainment us":
                case "walt disney studios":
                case "warner bros. pictures":
                    return true;
                default:
                    return false;
            }
        }
    }
}
