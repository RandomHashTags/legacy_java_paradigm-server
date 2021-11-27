package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public interface SpotifyService extends QuotaHandler, RestAPI, DataValues {
    ConcurrentHashMap<String, HashSet<CompletionHandler>> WAITING_FOR_ACCESS_TOKEN = new ConcurrentHashMap<>();

    default void getSpotifyAccessToken(CompletionHandler handler) {
        getSpotifyJSONValues(new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final String accessToken = json.getString("access_token"), tokenType = json.getString("token_type");
                final String string = tokenType + " " + accessToken;
                handler.handleString(string);
                final HashSet<CompletionHandler> completionHandlers = WAITING_FOR_ACCESS_TOKEN.getOrDefault("spotify", null);
                if(completionHandlers != null) {
                    for(CompletionHandler completionHandler : completionHandlers) {
                        completionHandler.handleString(string);
                    }
                }
            }
        });
    }
    private void getSpotifyJSONValues(CompletionHandler handler) {
        getJSONDataValue(JSONDataValue.SPOTIFY, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json.isEmpty()) {
                    if(WAITING_FOR_ACCESS_TOKEN.isEmpty()) {
                        WAITING_FOR_ACCESS_TOKEN.put("spotify", new HashSet<>());
                    } else {
                        WAITING_FOR_ACCESS_TOKEN.get("spotify").add(handler);
                        return;
                    }
                    requestSpotifyToken(getRequestSpotifyTokenCompletionHandler(handler));
                } else if(System.currentTimeMillis() >= json.getLong("expiration")) {
                    requestSpotifyToken(getRequestSpotifyTokenCompletionHandler(handler));
                } else {
                    handler.handleJSONObject(json);
                }
            }
        });
    }
    private CompletionHandler getRequestSpotifyTokenCompletionHandler(CompletionHandler handler) {
        return new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                setJSONDataValue(JSONDataValue.SPOTIFY, json);
                handler.handleJSONObject(json);
            }
        };
    }
    private void requestSpotifyToken(CompletionHandler handler) {
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
        requestJSONObject(url, RequestMethod.POST, headers, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final int expireDuration = json.getInt("expires_in") * 1_000;
                json.put("expiration", requestTime + expireDuration);
                json.remove("expires_in");
                WLLogger.logInfo("DataValues - refreshed Spotify Access Token (took " + (System.currentTimeMillis()-requestTime) + "ms)");
                handler.handleJSONObject(json);
            }
        });
    }

    default void getSpotifyPlaylistJSON(String id, CompletionHandler handler) {
        getSpotifyAccessToken(new CompletionHandler() {
            @Override
            public void handleString(String accessToken) {
                final String url = "https://api.spotify.com/v1/playlists/" + id;
                final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
                headers.put("Authorization", "Basic " + accessToken);
                final HashMap<String, String> query = new HashMap<>();
                query.put("market", "US");
                requestJSONObject(url, RequestMethod.GET, headers, query, handler);
            }
        });
    }
    default void getSpotifyAlbum(HashSet<String> artists, String album, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        tryRequesting(album, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final JSONObject albumsJSON = json.getJSONObject("albums");
                    final JSONArray itemsArray = albumsJSON.getJSONArray("items");
                    final JSONObject targetJSON = getAlbumFromItems(album, artists, itemsArray);
                    if(targetJSON != null) {
                        handler.handleJSONObject(targetJSON);
                        return;
                    }
                }
                WLLogger.logError("SpotifyService", "failed to load album with name \"" + album + "\" with artists " + artists.toString() + " (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleJSONObject(null);
            }
        });
    }
    private void tryRequesting(String album, CompletionHandler handler) {
        makeQuotaRequest(JSONDataValue.SPOTIFY, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                getSpotifyAccessToken(new CompletionHandler() {
                    @Override
                    public void handleString(String accessToken) {
                        final String url = "https://api.spotify.com/v1/search";
                        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
                        headers.put("Authorization", accessToken);
                        final HashMap<String, String> query = new HashMap<>();
                        query.put("q", album.replace(" ", "%20"));
                        query.put("limit", "50");
                        query.put("type", "album");
                        requestJSONObject(url, RequestMethod.GET, headers, query, handler);
                    }
                });
            }

            @Override
            public void handleFail() {
                handler.handleJSONObject(null);
            }
        });
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
}
