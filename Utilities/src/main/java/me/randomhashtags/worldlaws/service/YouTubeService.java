package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import org.json.JSONArray;
import org.json.JSONObject;

public interface YouTubeService extends RestAPI, Jsonable {
    default void getVideosJSONArray(YouTubeVideoType type, String title, CompletionHandler handler) {
        final String suffix = type.getSearchSuffix(), titleLowercase = title.toLowerCase();
        final String url = "http://youtube-scrape.herokuapp.com/api/search?q=" + titleLowercase.replace(" ", "+") + "+" + suffix;

        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final String alternateTitle1 = titleLowercase.replace(":", "");
                final String alternateTitle2 = titleLowercase.replace(" – ", " ");
                final String alternateTitle3 = titleLowercase.replace(":", "").replace(" – ", " ");

                final JSONArray resultsArray = json.getJSONArray("results");
                final JSONArray array = new JSONArray();
                for(Object obj : resultsArray) {
                    final JSONObject resultJSON = (JSONObject) obj;
                    if(resultJSON.has("video")) {
                        final JSONObject uploaderJSON = resultJSON.getJSONObject("uploader");
                        final boolean verified = uploaderJSON.getBoolean("verified");
                        if(type.isLegitUploader(verified, uploaderJSON.getString("username"))) {
                            final JSONObject videoJSON = resultJSON.getJSONObject("video");
                            final String videoTitle = videoJSON.getString("title"), videoTitleLowercase = videoTitle.toLowerCase();
                            if(videoTitleLowercase.contains(titleLowercase)
                                    || videoTitleLowercase.contains(alternateTitle1)
                                    || videoTitleLowercase.contains(alternateTitle2)
                                    || videoTitleLowercase.contains(alternateTitle3)
                            ) {
                                array.put(videoJSON.getString("id"));
                            }
                        }
                    }
                }
                handler.handleJSONArray(array.isEmpty() ? null : array);
            }
        });
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
        public boolean isLegitUploader(boolean verified, String username) {
            switch (this) {
                case MOVIE: return isLegitMovieUploader(username);
                case VIDEO_GAME: return isLegitVideoGameUploader(verified, username);
                default: return false;
            }
        }
        private boolean isLegitVideoGameUploader(boolean verified, String username) {
            switch (username.toLowerCase()) {
                case "7th beat games":
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
                case "fireflyworlds":
                case "fortnite":
                case "genshin impact":
                case "halo":
                case "hellogamestube":
                case "hitman":
                case "hytale":
                case "kingdom hearts":
                case "klei entertainment":
                case "life is strange":
                case "marvel entertainment":
                case "minecraft":
                case "modus games":
                case "mwm interactive":
                case "nintendo":
                case "oddworld inhabitants":
                case "outriders":
                case "overkill software":
                case "plants vs. zombies":
                case "playdigious":
                case "playoverwatch":
                case "playstation":
                case "resident evil":
                case "running with scissors":
                case "segaamerica":
                case "signalstudiosgames":
                case "skookum arts":
                case "skyforge":
                case "studio fow":
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
                case "a24":
                case "adult swim":
                case "amazon prime video":
                case "apple tv":
                case "disney plus":
                case "hulu":
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
                case "the fast saga":
                case "the witcher netflix":
                case "universal pictures":
                case "vertical entertainment us":
                case "warner bros. pictures":
                    return true;
                default:
                    return false;
            }
        }
    }
}
