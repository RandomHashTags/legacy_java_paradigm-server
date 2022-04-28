package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public interface ITunesSearchAPI extends RestAPI {
    private JSONObject search(String term, String media, String entity) {
        final String url = "https://itunes.apple.com/search";
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>(GET_CONTENT_HEADERS), query = new LinkedHashMap<>();
        term = URLEncoder.encode(term, StandardCharsets.UTF_8);
        query.put("term", term);
        query.put("country", "US");
        query.put("limit", "50");
        query.put("media", media);
        query.put("entity", entity);
        query.put("explicit", "yes");
        return requestJSONObject(url, headers, query);
    }

    default JSONObject getITunesAlbum(String term, String artist) {
        final long started = System.currentTimeMillis();
        final JSONObject json = search(term, "music", "album");
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
                    return result;
                }
            }
        }
        WLLogger.logError("ITunesSearchAPI", "failed to load album with name \"" + term + "\" with artist " + artist + " (took " + WLUtilities.getElapsedTime(started) + ")");
        return null;
    }
}
