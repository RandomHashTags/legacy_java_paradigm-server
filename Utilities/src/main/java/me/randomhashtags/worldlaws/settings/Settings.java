package me.randomhashtags.worldlaws.settings;

import me.randomhashtags.worldlaws.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public enum Settings {
    ;

    private static JSONObject DATA_JSON = refreshDataValues();
    private static JSONObject SETTINGS_JSON = refreshSettings();
    private static JSONObject PRIVATE_VALUES_JSON = refreshPrivateValues();
    private static JSONObject SERVER_VALUES_JSON = refreshServerValues();

    public static void refresh() {
        final long started = System.currentTimeMillis();
        refreshDataValues();
        refreshSettings();
        refreshPrivateValues();
        refreshServerValues();
        WLLogger.logInfo("Settings - refreshed (took " + WLUtilities.getElapsedTime(started) + ")");
    }
    private static JSONObject refreshDataValues() {
        DATA_JSON = Jsonable.getStaticLocalFileJSONObject(Folder.OTHER, "data values");
        if(DATA_JSON == null) {
            DATA_JSON = new JSONObject();
        }
        return DATA_JSON;
    }
    private static JSONObject refreshSettings() {
        SETTINGS_JSON = Jsonable.getStaticLocalFileJSONObject(Folder.OTHER, "settings");
        if(SETTINGS_JSON == null) {
            SETTINGS_JSON = new JSONObject();
        }
        return SETTINGS_JSON;
    }
    private static JSONObject refreshPrivateValues() {
        PRIVATE_VALUES_JSON = Jsonable.getStaticLocalFileJSONObject(Folder.OTHER, "private values");
        if(PRIVATE_VALUES_JSON == null) {
            PRIVATE_VALUES_JSON = new JSONObject();
        }
        return PRIVATE_VALUES_JSON;
    }
    private static JSONObject refreshServerValues() {
        SERVER_VALUES_JSON = Jsonable.getStaticLocalFileJSONObject(Folder.OTHER, "server values");
        if(SERVER_VALUES_JSON == null) {
            SERVER_VALUES_JSON = new JSONObject();
        }
        return SERVER_VALUES_JSON;
    }

    private static Object getOrDefault(JSONObject json, String key, Object defaultValue) {
        return json.has(key) ? json.get(key) : defaultValue;
    }
    private static JSONObject getOrDefaultJSONObject(JSONObject json, String key, JSONObject defaultValue) {
        return (JSONObject) getOrDefault(json, key, defaultValue);
    }
    private static JSONArray getOrDefaultJSONArray(JSONObject json, String key, JSONArray defaultValue) {
        return (JSONArray) getOrDefault(json, key, defaultValue);
    }
    private static int getOrDefaultInt(JSONObject json, String key, int defaultValue) {
        return (int) getOrDefault(json, key, defaultValue);
    }
    private static String getOrDefaultString(JSONObject json, String key, String defaultValue) {
        return (String) getOrDefault(json, key, defaultValue);
    }
    private static boolean getOrDefaultBoolean(JSONObject json, String key, boolean defaultValue) {
        return (boolean) getOrDefault(json, key, defaultValue);
    }

    private static List<Integer> getListInteger(JSONArray array) {
        final List<Integer> list = new ArrayList<>();
        for(Object obj : array) {
            list.add((Integer) obj);
        }
        return list;
    }
    private static List<String> getListString(JSONArray array) {
        final List<String> list = new ArrayList<>();
        for(Object obj : array) {
            list.add((String) obj);
        }
        return list;
    }
    private static HashMap<String, String> getMap(JSONObject json) {
        final HashMap<String, String> map = new HashMap<>();
        for(String key : json.keySet()) {
            map.put(key, json.getString(key));
        }
        return map;
    }

    public enum DataValues {
        ;

        public static boolean isProductionMode() {
            return getOrDefaultBoolean(DATA_JSON, "production_mode", false);
        }
    }

    public enum Performance {
        ;

        private static JSONObject getPerformanceJSON() {
            return getOrDefaultJSONObject(SETTINGS_JSON, "performance", new JSONObject());
        }
        public static int getMaximumParallelThreads() {
            return getOrDefaultInt(getPerformanceJSON(), "maximum_parallel_threads", 100);
        }
    }

    public enum Server {
        ;

        private static JSONObject getServersJSON() {
            return getOrDefaultJSONObject(SETTINGS_JSON, "server", new JSONObject());
        }
        private static JSONObject getHttpsJSON() {
            return getOrDefaultJSONObject(getServersJSON(), "https", new JSONObject());
        }
        private static JSONObject getHttpsCertbotJSON() {
            return getOrDefaultJSONObject(getHttpsJSON(), "certbot", new JSONObject());
        }
        private static JSONObject getHttpsKeystoreJSON() {
            return getOrDefaultJSONObject(getHttpsJSON(), "keystore", new JSONObject());
        }
        private static JSONObject getServerJSON(TargetServer server) {
            return getOrDefaultJSONObject(getServersJSON(), server.getBackendID(), new JSONObject());
        }

        public static String getUUID() {
            return getOrDefaultString(getServersJSON(), "uuid", "***REMOVED***");
        }
        public static String getSandboxUUID() {
            return getOrDefaultString(getServersJSON(), "sandbox_uuid", "***REMOVED***");
        }
        public static String getRunServersCommand() {
            return getOrDefaultString(getServersJSON(), "runServersCommand", "bash runServers.sh");
        }
        public static int getServerRebootFrequencyInDays() {
            return getOrDefaultInt(getServersJSON(), "serverRebootFrequencyInDays", 3);
        }
        public static String getProxyLocalAddress() {
            return getOrDefaultString(getServersJSON(), "proxy_local_address", "localhost");
        }
        public static int getProxyPort() {
            return getOrDefaultInt(getServersJSON(), "proxy_port", 0);
        }
        public static String getDefaultAddress() {
            return getOrDefaultString(getServersJSON(), "default_address", "http://localhost");
        }
        public static int getDefaultAPIVersion() {
            return getOrDefaultInt(getServersJSON(), "default_api_version", 1);
        }
        public static List<String> getSupportedHomeRequestServers() {
            final JSONArray array = getOrDefaultJSONArray(getServersJSON(), "supported_home_request_servers", new JSONArray(Arrays.asList("countries", "remote_notifications", "services", "upcoming_events", "weather")));
            return getListString(array);
        }

        public enum Https {
            ;
            public static boolean isEnabled() {
                return getOrDefaultBoolean(getHttpsJSON(), "enabled", false);
            }
            public static JSONArray getDomains() {
                return getOrDefaultJSONArray(getHttpsJSON(), "domains", new JSONArray());
            }

            public enum Certbot {
                ;
                public static String getFolderName() {
                    return getOrDefaultString(getHttpsCertbotJSON(), "folderName", "certbot");
                }
                public static String getEmail() {
                    return getOrDefaultString(getHttpsCertbotJSON(), "email", "***REMOVED***");
                }
            }

            public enum Keystore {
                ;
                public static String getFileName() {
                    return getOrDefaultString(getHttpsKeystoreJSON(), "fileName", "***REMOVED***");
                }
                public static String getPassword() {
                    return getOrDefaultString(getHttpsKeystoreJSON(), "password", "***REMOVED***");
                }
            }

        }


        public static int getPort(TargetServer server) {
            return getOrDefaultInt(getServerJSON(server), "port", server.getDefaultPort());
        }
        public static String getAddress(TargetServer server) {
            return getOrDefaultString(getServerJSON(server), "address", getDefaultAddress());
        }
        public static int getAPIVersion(TargetServer server) {
            return getOrDefaultInt(getServerJSON(server), "api_version", getDefaultAPIVersion());
        }
    }

    public enum PrivateValues {
        ;

        private static JSONObject getPrivateValuesJSON() {
            return getOrDefaultJSONObject(PRIVATE_VALUES_JSON, "values", new JSONObject());
        }
        private static JSONObject getPrivateValuesJSON(String key) {
            return getOrDefaultJSONObject(getPrivateValuesJSON(), key, new JSONObject());
        }
        private static JSONObject getPrivateValuesApple() {
            return getPrivateValuesJSON("apple");
        }
        private static JSONObject getPrivateValuesGoogle() {
            return getPrivateValuesJSON("google");
        }
        private static JSONObject getPrivateValuesNASA() {
            return getPrivateValuesJSON("nasa");
        }
        private static JSONObject getPrivateValuesTicketmaster() {
            return getPrivateValuesJSON("ticketmaster");
        }
        private static JSONObject getPrivateValuesTwelveData() {
            return getPrivateValuesJSON("twelve_data");
        }
        private static JSONObject getPrivateValuesTwitch() {
            return getPrivateValuesJSON("twitch");
        }
        private static JSONObject getPrivateValuesYahooFinance() {
            return getPrivateValuesJSON("yahoo_finance");
        }
        private static JSONObject getPrivateValuesYouTube() {
            return getPrivateValuesJSON("youtube");
        }

        public enum Apple {
            ;

            public static boolean isProductionMode() {
                return getOrDefaultBoolean(getPrivateValuesApple(), "production_mode", false);
            }
            public static String getVerifySubscriptionSharedSecret() {
                return getOrDefaultString(getPrivateValuesApple(), "verify_subscription_shared_secret", null);
            }
            public static String getRemoteNotificationsEncryptionKeyID() {
                return getOrDefaultString(getPrivateValuesApple(), "remote_notifications_encryption_key_id", null);
            }
            public static String getRemoteNotificationsIssuerKey() {
                return getOrDefaultString(getPrivateValuesApple(), "remote_notifications_issuer_key", null);
            }
        }

        public enum Google {
            ;

            public static String getCivicAPIKey() {
                return getOrDefaultString(getPrivateValuesGoogle(), "civic_api_key", null);
            }
        }

        public enum NASA {
            ;

            public static String getAPIKey() {
                return getOrDefaultString(getPrivateValuesNASA(), "api_key", "***REMOVED***");
            }
        }

        public enum Ticketmaster {
            ;

            public static String getAPIKey() {
                return getOrDefaultString(getPrivateValuesTicketmaster(), "api_key", null);
            }
        }

        public enum TwelveData {
            ;

            public static String getAPIKey() {
                return getOrDefaultString(getPrivateValuesTwelveData(), "api_key", null);
            }
        }

        public enum Twitch {
            ;

            public static int getRequestLimit() {
                return getOrDefaultInt(getPrivateValuesTwitch(), "request_limit", 100);
            }
            public static String getClientID() {
                return getOrDefaultString(getPrivateValuesTwitch(), "client_id", null);
            }
            public static String getAccessToken() {
                return getOrDefaultString(getPrivateValuesTwitch(), "access_token", null);
            }
        }

        public enum YahooFinance {
            ;

            public static String getAPIKey() {
                return getOrDefaultString(getPrivateValuesYahooFinance(), "rapid_api_key", null);
            }
        }

        public enum YouTube {
            ;

            public static int getRequestLimit() {
                return getOrDefaultInt(getPrivateValuesYouTube(), "request_limit", 49);
            }
            public static String getKey() {
                return getOrDefaultString(getPrivateValuesYouTube(), "key", null);
            }
        }
    }

    public enum ServerValues {
        ;

        private static JSONObject getServerResponseVersionsJSON() {
            return getOrDefaultJSONObject(SERVER_VALUES_JSON, "response_versions", new JSONObject());
        }
        private static JSONObject getServerResponseVersionsClientSideJSON() {
            return getOrDefaultJSONObject(getServerResponseVersionsJSON(), "client_side", new JSONObject());
        }
        private static JSONObject getServerResponseVersionsServerSideJSON() {
            return getOrDefaultJSONObject(getServerResponseVersionsJSON(), "server_side", new JSONObject());
        }
        public enum ResponseVersions {
            ;

            public enum ClientSide {
                ;
                public static int getCountries() {
                    return getOrDefaultInt(getServerResponseVersionsClientSideJSON(), "countries", 0);
                }
                public static int getMovieProductionCompanies() {
                    return getOrDefaultInt(getServerResponseVersionsClientSideJSON(), "movie_production_companies", 0);
                }
                public static int getMusicArtists() {
                    return getOrDefaultInt(getServerResponseVersionsClientSideJSON(), "music_artists", 0);
                }
                public static int getUpcomingEventTypes() {
                    return getOrDefaultInt(getServerResponseVersionsClientSideJSON(), "upcoming_event_types", 0);
                }
                public static int getUpdateNotes() {
                    return getOrDefaultInt(getServerResponseVersionsClientSideJSON(), "update_notes", 0);
                }
            }

            public enum ServerSide {
                ;

            }
        }

        private static JSONObject getServerValuesJSON() {
            return getOrDefaultJSONObject(SERVER_VALUES_JSON, "values", new JSONObject());
        }
        private static JSONObject getServerValuesJSON(TargetServer server) {
            final String key = server.name().toLowerCase();
            return getOrDefaultJSONObject(getServerValuesJSON(), key, new JSONObject());
        }

        public static List<String> getWikipediaSupportedExternalLinkSources() {
            final JSONArray array = getOrDefaultJSONArray(getServerValuesJSON(), "wikipedia_supported_external_link_sources", new JSONArray());
            return getListString(array);
        }

        public enum YouTube {
            ;

            private static JSONObject getYouTubeJSON() {
                return getOrDefaultJSONObject(getServerValuesJSON(), "youtube", new JSONObject());
            }

            public static boolean isSupportedMovieUploader(String uploader) {
                return isSupported(uploader, getSupportedMovieUploaders());
            }
            public static boolean isSupportedVideoGameUploader(String uploader) {
                return isSupported(uploader, getSupportedVideoGameUploaders());
            }
            private static boolean isSupported(String input, List<String> list) {
                for(String string : list) {
                    if(string.equalsIgnoreCase(input)) {
                        return true;
                    }
                }
                return false;
            }
            private static List<String> getSupportedMovieUploaders() {
                final JSONArray array = getOrDefaultJSONArray(getYouTubeJSON(), "supported_movie_uploaders", new JSONArray());
                return getListString(array);
            }
            private static List<String> getSupportedVideoGameUploaders() {
                final JSONArray array = getOrDefaultJSONArray(getYouTubeJSON(), "supported_video_game_uploaders", new JSONArray());
                return getListString(array);
            }
        }

        public enum UpcomingEvents {
            ;

            private static JSONObject getUpcomingEventsJSON() {
                return getServerValuesJSON(TargetServer.UPCOMING_EVENTS);
            }
            private static JSONObject getMusicAlbumsJSON() {
                return getOrDefaultJSONObject(getUpcomingEventsJSON(), "music_albums", new JSONObject());
            }
            private static JSONObject getScienceJSON() {
                return getOrDefaultJSONObject(getUpcomingEventsJSON(), "science", new JSONObject());
            }
            private static JSONObject getVideoGamesJSON() {
                return getOrDefaultJSONObject(getUpcomingEventsJSON(), "video_games", new JSONObject());
            }

            public static JSONObject getMovieProductionCompanies() {
                return getOrDefaultJSONObject(getUpcomingEventsJSON(), "movie_production_companies", new JSONObject());
            }
            public static List<Integer> getMusicAlbumMultiPageYears() {
                final JSONArray array = getOrDefaultJSONArray(getMusicAlbumsJSON(), "multi-page_years", new JSONArray());
                return getListInteger(array);
            }
            public static List<Integer> getScienceYearReviewYears() {
                final JSONArray array = getOrDefaultJSONArray(getScienceJSON(), "year_review_years", new JSONArray());
                return getListInteger(array);
            }
            public static List<String> getVideoGameWikipediaHeadlineIDs() {
                final JSONArray array = getOrDefaultJSONArray(getVideoGamesJSON(), "wikipedia_headline_ids", new JSONArray());
                return getListString(array);
            }
            public static HashMap<String, String> getVideoGamePlatforms() {
                final JSONObject json = getOrDefaultJSONObject(getVideoGamesJSON(), "platforms", new JSONObject());
                return getMap(json);
            }
        }

        public enum Weather {
            ;

            public static HashMap<String, String> getVolcanoWikipediaPages() {
                final JSONObject json = getServerValuesJSON(TargetServer.WEATHER).getJSONObject("volcano_wikipedia_pages");
                return getMap(json);
            }
        }
    }
}
