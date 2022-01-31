package me.randomhashtags.worldlaws.country;

import org.json.JSONObject;

public enum WLCountrySocialMedia {
    ;

    public static JSONObject get(WLCountry country) {
        final CountrySocialMediaController[] controllers = new CountrySocialMediaController[] {
                Twitter.INSTANCE,
                YouTube.INSTANCE
        };
        final JSONObject json = new JSONObject();
        for(CountrySocialMediaController controller : controllers) {
            final String value = controller.get(country);
            if(value != null) {
                json.put(controller.getName(), controller.getURLPrefix() + value);
            }
        }
        return json.isEmpty() ? null : json;
    }

    private interface CountrySocialMediaController {
        String getName();
        String getURLPrefix();
        String get(WLCountry country);
    }

    private enum Twitter implements CountrySocialMediaController {
        INSTANCE;

        @Override
        public String getName() {
            return "Twitter";
        }

        @Override
        public String getURLPrefix() {
            return "https://twitter.com/";
        }

        @Override
        public String get(WLCountry country) {
            switch (country) {
                case CANADA: return "Canada";
                default: return null;
            }
        }
    }
    private enum YouTube implements CountrySocialMediaController {
        INSTANCE;

        @Override
        public String getName() {
            return "YouTube";
        }

        @Override
        public String getURLPrefix() {
            return "https://www.youtube.com/c/";
        }

        @Override
        public String get(WLCountry country) {
            switch (country) {
                default: return null;
            }
        }
    }
}
