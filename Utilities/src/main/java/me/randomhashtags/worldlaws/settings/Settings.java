package me.randomhashtags.worldlaws.settings;

import me.randomhashtags.worldlaws.Jsonable;
import org.json.JSONObject;

public enum Settings { // TODO: replace Jsonable.getSettingsJSON with this implementation
    ;

    private static JSONObject JSON = Jsonable.getSettingsJSON();
    public static void refresh() {
        JSON = Jsonable.getSettingsJSON();
    }

    public enum Server {
        ;

        private static JSONObject getParentJSON() {
            return JSON.getJSONObject("server");
        }
        public static int getProxyPort() {
            return getParentJSON().getInt("proxy_port");
        }
        public static String getDefaultAddress() {
            return getParentJSON().getString("default_address");
        }
        public static int getDefaultAPIVersion() {
            return getParentJSON().getInt("default_api_version");
        }
    }

    public enum PrivateValues {
        GOOGLE,
        NASA,
        TWITCH,
        YAHOO_FINANCE,
        YOUTUBE,
        ;
    }
}
