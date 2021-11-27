package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public interface ITunesSearchAPI extends RestAPI {
    private void search(String term, String media, String entity, CompletionHandler handler) {
        final String url = "https://itunes.apple.com/search";
        final HashMap<String, String> headers = new HashMap<>(), query = new HashMap<>();
        query.put("term", term.replace(" ", "+"));
        query.put("country", "US");
        query.put("limit", "200");
        query.put("media", media);
        query.put("entity", entity);
        requestJSONObject(url, RequestMethod.GET, headers, query, handler);
    }

    default void getITunesAlbum(String term, String artist, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        search(term, "music", "album", new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final String termLowercase = term.toLowerCase();
                    final JSONArray array = json.getJSONArray("results");
                    for(Object obj : array) {
                        final JSONObject result = (JSONObject) obj;
                        final String artistName = result.getString("artistName"), collectionName = result.getString("collectionName");
                        if(artist.equalsIgnoreCase(artistName) && (collectionName.equalsIgnoreCase(term) || collectionName.toLowerCase().startsWith(termLowercase))) {
                            result.remove("wrapperType");
                            result.remove("collectionType");
                            result.remove("artistId");
                            result.remove("collectionId");
                            result.remove("artistName");
                            result.remove("collectionName");
                            result.remove("collectionCensoredName");
                            result.remove("collectionPrice");
                            result.remove("collectionExplicitness");
                            result.remove("contentAdvisoryRating");
                            result.remove("trackCount");
                            result.remove("copyright");
                            result.remove("currency");
                            result.remove("releaseDate");
                            result.remove("primaryGenreName");
                            result.remove("country");

                            final String imageURL = result.getString("artworkUrl100").replace("/100x100bb.jpg", "/1000x1000bb.jpg");
                            result.put("imageURL", imageURL);

                            result.remove("artworkUrl60");
                            result.remove("artworkUrl100");
                            handler.handleJSONObject(result);
                            return;
                        }
                    }
                }
                WLLogger.logError("ITunesSearchAPI", "failed to load album with name \"" + term + "\" with artist " + artist + " (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleJSONObject(null);
            }
        });
    }
}
