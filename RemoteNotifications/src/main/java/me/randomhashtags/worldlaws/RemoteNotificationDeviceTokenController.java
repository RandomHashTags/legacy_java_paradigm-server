package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public interface RemoteNotificationDeviceTokenController extends RestAPI, Jsonable {
    RemoteNotificationCategory getCategory();
    DeviceTokenType getDeviceTokenType();
    Map<RemoteNotificationSubcategory, HashSet<String>> getDeviceTokens();
    default Map<RemoteNotificationSubcategory, HashMap<String, String>> getConditionalDeviceTokens() {
        return null;
    }

    default HashSet<String> getConditionalDeviceTokensThatContain(RemoteNotificationSubcategory subcategory, String conditionalValue) {
        if(!subcategory.isConditional()) {
            return null;
        }
        final Map<RemoteNotificationSubcategory, HashMap<String, String>> map = getConditionalDeviceTokens();
        if(map != null && !map.isEmpty()) {
        }
        return null;
    }

    default String getCategoryFolderName() {
        final DeviceTokenType deviceTokenType = getDeviceTokenType();
        if(deviceTokenType != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final RemoteNotificationCategory category = getCategory();
            return category != null ? folder.getFolderName().replace("%type%", deviceTokenType.getID()).replace("%category%", category.getID()) : null;
        }
        return null;
    }

    default void save() {
        final RemoteNotificationCategory category = getCategory();
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(category != null && deviceTokens != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final String folderName = getCategoryFolderName();
            for(Map.Entry<RemoteNotificationSubcategory, HashSet<String>> entry : deviceTokens.entrySet()) {
                final RemoteNotificationSubcategory subcategory = entry.getKey();
                final String fileName = subcategory.name();
                final HashSet<String> tokens = entry.getValue();
                folder.setCustomFolderName(fileName, folderName);
                Jsonable.setFileJSONArray(folder, fileName, new JSONArray(tokens));
            }
        }
    }
    default void saveConditional() {
        final RemoteNotificationCategory category = getCategory();
        final Map<RemoteNotificationSubcategory, HashMap<String, String>> deviceTokens = getConditionalDeviceTokens();
        if(category != null && deviceTokens != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final String folderName = getCategoryFolderName();
            for(Map.Entry<RemoteNotificationSubcategory, HashMap<String, String>> entry : deviceTokens.entrySet()) {
                final RemoteNotificationSubcategory subcategory = entry.getKey();
                final String fileName = subcategory.name();
                final HashMap<String, String> conditionalTokens = entry.getValue();
                for(Map.Entry<String, String> conditionalToken : conditionalTokens.entrySet()) {
                    final String deviceToken = conditionalToken.getKey();
                    final String conditionalValue = conditionalToken.getValue();
                }
            }
        }
    }
    default void insertDeviceTokens(Map<RemoteNotificationSubcategory, HashSet<String>> map) {
        final RemoteNotificationCategory category = getCategory();
        final RemoteNotificationSubcategory[] subcategories = category != null ? category.getSubcategories() : null;
        if(subcategories != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final String folderName = getCategoryFolderName();
            for(RemoteNotificationSubcategory subcategory : subcategories) {
                map.put(subcategory, new HashSet<>());
                final String fileName = subcategory.name();
                folder.setCustomFolderName(fileName, folderName);
                final JSONArray array = Jsonable.getStaticFileJSONArray(folder, fileName);
                if(array != null && !array.isEmpty()) {
                    final HashSet<String> tokens = new HashSet<>();
                    for(Object obj : array) {
                        tokens.add((String) obj);
                    }
                    map.put(subcategory, tokens);
                }
                folder.removeCustomFolderName(fileName);
            }
        }
    }

    default void register(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(deviceTokens != null) {
            deviceTokens.get(subcategory).add(deviceToken);
        }
    }
    default void register(RemoteNotificationSubcategory subcategory, String deviceToken, String conditionalValue) {
        final Map<RemoteNotificationSubcategory, HashMap<String, String>> deviceTokens = getConditionalDeviceTokens();
        if(deviceTokens != null) {
            deviceTokens.get(subcategory).put(deviceToken, conditionalValue);
        }
    }

    default void unregister(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(deviceTokens != null) {
            deviceTokens.get(subcategory).remove(deviceToken);
        }
        final Map<RemoteNotificationSubcategory, HashMap<String, String>> conditionalDeviceTokens = getConditionalDeviceTokens();
        if(conditionalDeviceTokens != null) {
            conditionalDeviceTokens.get(subcategory).remove(deviceToken);
        }
    }
    default boolean isRegistered(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        final Map<RemoteNotificationSubcategory, HashMap<String, String>> conditionalDeviceTokens = getConditionalDeviceTokens();
        return deviceTokens != null && deviceTokens.containsKey(subcategory) && deviceTokens.get(subcategory).contains(deviceToken)
                || conditionalDeviceTokens != null && conditionalDeviceTokens.containsKey(subcategory) && conditionalDeviceTokens.get(subcategory).containsKey(deviceToken)
                ;
    }
    void sendNotification(RemoteNotification notification);
}
