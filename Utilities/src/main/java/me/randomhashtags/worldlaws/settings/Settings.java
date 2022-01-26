package me.randomhashtags.worldlaws.settings;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.TargetServer;
import org.json.JSONObject;

public enum Settings {
    ;

    private static JSONObject JSON = refresh();
    public static JSONObject refresh() {
        JSON = Jsonable.getStaticLocalFileJSONObject(Folder.OTHER, "settings");
        if(JSON == null) {
            JSON = new JSONObject();
        }
        return JSON;
    }

    private static Object getOrDefault(JSONObject json, String key, Object defaultValue) {
        return json.has(key) ? json.get(key) : defaultValue;
    }
    private static JSONObject getOrDefaultJSONObject(JSONObject json, String key, JSONObject defaultValue) {
        return (JSONObject) getOrDefault(json, key, defaultValue);
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

    public enum Performance {
        ;

        private static JSONObject getPerformanceJSON() {
            return getOrDefaultJSONObject(JSON, "performance", new JSONObject());
        }
        public static int getMaximumParallelThreads() {
            return getOrDefaultInt(getPerformanceJSON(), "maximum_parallel_threads", 25);
        }
    }

    public enum Server {
        ;

        private static JSONObject getServersJSON() {
            return getOrDefaultJSONObject(JSON, "server", new JSONObject());
        }
        private static JSONObject getServerJSON(TargetServer server) {
            return getOrDefaultJSONObject(getServersJSON(), server.getBackendID(), new JSONObject());
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
            return getOrDefaultJSONObject(JSON, "private_values", new JSONObject());
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
            public static String getSharedSecret() {
                return getOrDefaultString(getPrivateValuesApple(), "app_specific_shared_secret", null);
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
            public static String getKeyIdentifier() {
                return getOrDefaultString(getPrivateValuesYouTube(), "key_identifier", "***REMOVED***");
            }
            public static String getKeyValue() {
                return getOrDefaultString(getPrivateValuesYouTube(), "key_value", null);
            }
        }
    }
}
