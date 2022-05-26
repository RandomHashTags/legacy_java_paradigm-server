package me.randomhashtags.worldlaws.notifications;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.notifications.subcategory.RemoteNotificationSubcategoryApple;
import me.randomhashtags.worldlaws.notifications.subcategory.RemoteNotificationSubcategoryPlayStation;
import me.randomhashtags.worldlaws.notifications.subcategory.RemoteNotificationSubcategoryVideoGame;
import me.randomhashtags.worldlaws.notifications.subcategory.RemoteNotificationSubcategoryWeather;

public enum RemoteNotificationCategory {
    APPLE,
    PLAYSTATION,
    VIDEO_GAME,
    WEATHER,
    ;

    public static RemoteNotificationCategory valueOfString(String input) {
        for(RemoteNotificationCategory category : RemoteNotificationCategory.values()) {
            if(category.getID().equalsIgnoreCase(input)) {
                return category;
            }
        }
        return null;
    }

    public String getID() {
        return name().toLowerCase();
    }

    public String getName() {
        switch (this) {
            case APPLE: return "Apple";
            case PLAYSTATION: return "PlayStation";
            case VIDEO_GAME: return "Video Games";
            case WEATHER: return "Weather";
            default: return "Unknown";
        }
    }

    public RemoteNotificationSubcategory[] getSubcategories() {
        switch (this) {
            case APPLE: return RemoteNotificationSubcategoryApple.values();
            case PLAYSTATION: return RemoteNotificationSubcategoryPlayStation.values();
            case VIDEO_GAME: return RemoteNotificationSubcategoryVideoGame.values();
            case WEATHER: return RemoteNotificationSubcategoryWeather.values();
            default: return null;
        }
    }
    public RemoteNotificationSubcategory valueOfSubcategory(String id) {
        final RemoteNotificationSubcategory[] subcategories = getSubcategories();
        if(subcategories != null) {
            for(RemoteNotificationSubcategory subcategory : subcategories) {
                if(subcategory.getID().equalsIgnoreCase(id)) {
                    return subcategory;
                }
            }
        }
        return null;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title", "subcategories");
        json.put("title", getName());
        final RemoteNotificationSubcategory[] subcategories = getSubcategories();
        if(subcategories != null) {
            final JSONObjectTranslatable subcategoriesJSON = new JSONObjectTranslatable();
            for(RemoteNotificationSubcategory subcategory : subcategories) {
                final String id = subcategory.getID();
                subcategoriesJSON.put(id, subcategory.toJSONObject(), true);
            }
            json.put("subcategories", subcategoriesJSON);
        }
        return json;
    }
}
