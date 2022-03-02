package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;

public interface SpotifyService extends QuotaHandler, RestAPI, DataValues {
    default String getSpotifyAccessToken() {
        final JSONObject json = getSpotifyJSONValues();
        final String accessToken = json.getString("access_token"), tokenType = json.getString("token_type");
        return tokenType + " " + accessToken;
    }
    private JSONObject getSpotifyJSONValues() {
        final JSONObject json = getJSONDataValue(JSONDataValue.SPOTIFY);
        if(json.isEmpty() || System.currentTimeMillis() >= json.getLong("expiration")) {
            return refreshSpotifyToken();
        } else {
            return json;
        }
    }
    private JSONObject requestSpotifyToken() {
        final String clientID = "***REMOVED***";
        final String clientSecret = "***REMOVED***";
        final String url = "https://accounts.spotify.com/api/token";
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Content-Length", "0");
        final String encodedString = Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes());
        headers.put("Authorization", "Basic " + encodedString);
        final HashMap<String, String> query = new HashMap<>();
        query.put("grant_type", "client_credentials");
        final long requestTime = System.currentTimeMillis();
        final JSONObject json = postJSONObject(url, new HashMap<>(), true, headers, query);// requestJSONObject(url, RequestMethod.POST, headers, query);
        final int expireDuration = json.getInt("expires_in") * 1_000;
        json.put("expiration", requestTime + expireDuration);
        json.remove("expires_in");
        WLLogger.logInfo("DataValues - refreshed Spotify Access Token (took " + WLUtilities.getElapsedTime(requestTime) + ")");
        return json;
    }
    private JSONObject refreshSpotifyToken() {
        final JSONObject json = requestSpotifyToken();
        setJSONDataValue(JSONDataValue.SPOTIFY, json);
        return json;
    }

    default JSONObject getSpotifyPlaylistJSON(String id) {
        final String accessToken = getSpotifyAccessToken();
        final String url = "https://api.spotify.com/v1/playlists/" + id;
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        headers.put("Authorization", "Basic " + accessToken);
        final HashMap<String, String> query = new HashMap<>();
        query.put("market", "US");
        return requestJSONObject(url, headers, query);
    }
    default JSONObject getSpotifyAlbum(HashSet<String> artists, String album) {
        final long started = System.currentTimeMillis();
        final JSONObject json = tryRequesting(album);
        if(json != null) {
            final JSONObject albumsJSON = json.getJSONObject("albums");
            final JSONArray itemsArray = albumsJSON.getJSONArray("items");
            final JSONObject targetJSON = getAlbumFromItems(album, artists, itemsArray);
            if(targetJSON != null) {
                return targetJSON;
            }
        }
        WLLogger.logError("SpotifyService", "failed to load album with name \"" + album + "\" with artists " + artists.toString() + " (took " + WLUtilities.getElapsedTime(started) + ")");
        return null;
    }
    private JSONObject tryRequesting(String album) {
        final boolean success = makeQuotaRequest(JSONDataValue.SPOTIFY);
        JSONObject json = null;
        if(success) {
            final String accessToken = getSpotifyAccessToken();
            final String url = "https://api.spotify.com/v1/search";
            final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
            headers.put("Authorization", accessToken);
            final HashMap<String, String> query = new HashMap<>();
            query.put("q", album.replace(" ", "%20"));
            query.put("limit", "50");
            query.put("type", "album");
            json = requestJSONObject(url, headers, query);
        }
        return json;
    }
    private JSONObject getAlbumFromItems(String album, HashSet<String> artists, JSONArray itemsArray) {
        // TODO: support accents on letters in the album or artist name
        album = album.toLowerCase();
        for(Object obj : itemsArray) {
            final JSONObject itemJSON = (JSONObject) obj;
            final String targetAlbumName = itemJSON.getString("name").toLowerCase();
            if(album.equals(targetAlbumName) || targetAlbumName.startsWith(album)) {
                final JSONArray artistsArray = itemJSON.getJSONArray("artists");
                for(Object artistObj : artistsArray) {
                    final JSONObject artistJSON = (JSONObject) artistObj;
                    final String artistName = artistJSON.getString("name");
                    if(hasArtist(artists, artistName)) {
                        String imageURL = null;
                        for(Object imageObj : itemJSON.getJSONArray("images")) {
                            final JSONObject imageJSON = (JSONObject) imageObj;
                            if(imageJSON.getInt("width") == 640) {
                                imageURL = imageJSON.getString("url");
                                break;
                            }
                        }
                        itemJSON.put("imageURL", imageURL);
                        itemJSON.remove("images");
                        itemJSON.remove("name");
                        itemJSON.remove("id");
                        itemJSON.remove("type");
                        itemJSON.remove("album_type");
                        itemJSON.remove("href");
                        itemJSON.remove("uri");
                        itemJSON.remove("release_date");
                        itemJSON.remove("release_date_precision");
                        itemJSON.remove("artists");
                        itemJSON.put("url", itemJSON.getJSONObject("external_urls").getString("spotify"));
                        itemJSON.remove("external_urls");
                        return itemJSON;
                    }
                }
            }
        }
        return null;
    }
    private boolean hasArtist(HashSet<String> artists, String artist) {
        final HashSet<String> lowercaseArtists = new HashSet<>();
        for(String targetArtist : artists) {
            lowercaseArtists.add(targetArtist.toLowerCase());
        }
        final String targetArtist = artist.toLowerCase();
        return lowercaseArtists.contains(targetArtist) || lowercaseArtists.contains(targetArtist.replace("...", "")) || lowercaseArtists.contains(targetArtist.replace(" & ", " and ")) || lowercaseArtists.contains(targetArtist.replace(" and ", " & "));
    }
    private String getFixedAlbum(String album) {
        return album.replace(":", "%3A");
    }
}
