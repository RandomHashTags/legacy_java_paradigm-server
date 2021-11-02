package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.WLCountry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public interface SpotifyService extends QuotaHandler, RestAPI, DataValues {
    ConcurrentHashMap<String, HashSet<CompletionHandler>> WAITING_FOR_ACCESS_TOKEN = new ConcurrentHashMap<>();

    default void getSpotifyAccessToken(CompletionHandler handler) {
        getSpotifyJSONValues(new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final String accessToken = json.getString("access_token"), tokenType = json.getString("token_type");
                final String string = tokenType.substring(0, 1).toUpperCase() + tokenType.substring(1) + " " + accessToken;
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

    default void getSpotifyAlbum(HashSet<String> artists, String album, CompletionHandler handler) {
        // TODO: support accents on letters in the album or artist name
        final long started = System.currentTimeMillis();
        makeQuotaRequest(JSONDataValue.SPOTIFY, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                getSpotifyAccessToken(new CompletionHandler() {
                    @Override
                    public void handleString(String accessToken) {
                        final String url = "https://api.spotify.com/v1/search";
                        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
                        final HashMap<String, String> query = new HashMap<>();
                        query.put("query", album.toLowerCase().replace(" ", "+"));
                        query.put("limit", "10");
                        query.put("type", "album");
                        headers.put("Authorization", accessToken);
                        requestJSONObject(url, RequestMethod.GET, headers, query, new CompletionHandler() {
                            @Override
                            public void handleJSONObject(JSONObject json) {
                                if(json != null) {
                                    final JSONObject albumsJSON = json.getJSONObject("albums");
                                    final JSONArray itemsArray = albumsJSON.getJSONArray("items");
                                    JSONObject targetJSON = null;
                                    outer: for(Object obj : itemsArray) {
                                        final JSONObject itemJSON = (JSONObject) obj;
                                        final String albumName = itemJSON.getString("name");
                                        if(album.equalsIgnoreCase(albumName)) {
                                            final JSONArray artistsArray = itemJSON.getJSONArray("artists");
                                            for(Object artistObj : artistsArray) {
                                                final JSONObject artistJSON = (JSONObject) artistObj;
                                                final String artistName = artistJSON.getString("name");
                                                artistJSON.remove("id");
                                                artistJSON.remove("type");
                                                if(hasArtist(artists, artistName)) {
                                                    String imageURL = null;
                                                    for(Object imageObj : artistJSON.getJSONArray("images")) {
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
                                                    targetJSON = itemJSON;
                                                    break outer;
                                                }
                                            }
                                        }
                                    }
                                    final String completedString = "%status% album with name \"" + album + "\" with artists " + artists.toString() + " (took %time%ms)";
                                    if(targetJSON != null) {
                                        final String availableMarketsKey = "available_markets";
                                        final JSONArray availableMarketsArray = targetJSON.getJSONArray(availableMarketsKey);
                                        final int max = availableMarketsArray.length();
                                        final JSONArray availableCountries = new JSONArray();
                                        final AtomicInteger completed = new AtomicInteger(0);
                                        final JSONObject finalTargetJSON = targetJSON;
                                        StreamSupport.stream(availableMarketsArray.spliterator(), true).forEach(marketObj -> {
                                            final String countryAbbreviation = (String) marketObj;
                                            final WLCountry country = WLCountry.valueOfISOAlpha2(countryAbbreviation);
                                            if(country != null) {
                                                availableCountries.put(country.getBackendID());
                                            }
                                            if(completed.addAndGet(1) == max) {
                                                finalTargetJSON.remove(availableMarketsKey);
                                                finalTargetJSON.put("available_countries", availableCountries);
                                                WLLogger.logInfo(completedString.replace("%time%", "" + (System.currentTimeMillis()-started)).replace("%status%", "loaded"));
                                                handler.handleJSONObject(finalTargetJSON);
                                            }
                                        });
                                    } else {
                                        WLLogger.logError("SpotifyService", completedString.replace("%time%", "" + (System.currentTimeMillis()-started)).replace("%status%", "failed to load"));
                                        handler.handleJSONObject(null);
                                    }
                                } else {
                                    handler.handleJSONObject(null);
                                }
                            }
                        });
                    }
                });
            }
        });

    }
    private boolean hasArtist(HashSet<String> artists, String artist) {
        final String targetArtist = artist.toLowerCase();
        return artists.contains(targetArtist) || artists.contains(targetArtist.replace("...", "")) || artists.contains(targetArtist.replace(" & ", " and ")) || artists.contains(targetArtist.replace(" and ", " & "));
    }
}
