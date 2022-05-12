package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public interface RemoteNotificationDeviceTokenController extends RestAPI, Jsonable {
    RemoteNotificationCategory getCategory();
    DeviceTokenType getDeviceTokenType();
    Map<RemoteNotificationSubcategory, HashSet<String>> getDeviceTokens();
    default Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> getConditionalDeviceTokens() {
        return null;
    }

    default HashSet<String> getConditionalDeviceTokensThatContain(RemoteNotificationSubcategory subcategory, String conditionalValue) {
        if(!subcategory.isConditional()) {
            return null;
        }
        final Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> map = getConditionalDeviceTokens();
        if(map != null && !map.isEmpty() && map.containsKey(subcategory) && map.get(subcategory).containsKey(conditionalValue)) {
            return map.get(subcategory).get(conditionalValue);
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
        final Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> deviceTokens = getConditionalDeviceTokens();
        if(category != null && deviceTokens != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final String folderName = getCategoryFolderName();
            for(Map.Entry<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> entry : deviceTokens.entrySet()) {
                final RemoteNotificationSubcategory subcategory = entry.getKey();
                final String fileName = subcategory.name();
                final HashMap<String, HashSet<String>> conditionalTokens = entry.getValue();
                final JSONObject json = new JSONObject();
                for(Map.Entry<String, HashSet<String>> conditionalToken : conditionalTokens.entrySet()) {
                    final String conditionalValue = conditionalToken.getKey();
                    final JSONArray tokens = new JSONArray(conditionalToken.getValue());
                    json.put(conditionalValue, tokens);
                }
                folder.setCustomFolderName(fileName, folderName);
                Jsonable.setFileJSONObject(folder, fileName, json);
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
    default void insertConditionalDeviceTokens(Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> map) {
        final RemoteNotificationCategory category = getCategory();
        final RemoteNotificationSubcategory[] subcategories = category != null ? category.getSubcategories() : null;
        if(subcategories != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final String folderName = getCategoryFolderName();
            for(RemoteNotificationSubcategory subcategory : subcategories) {
                if(subcategory.isConditional()) {
                    map.put(subcategory, new HashMap<>());
                    final String fileName = subcategory.name();
                    folder.setCustomFolderName(fileName, folderName);
                    final JSONObject json = Jsonable.getStaticLocalFileJSONObject(folder, fileName);
                    if(json != null && !json.isEmpty()) {
                        final HashMap<String, HashSet<String>> tokens = new HashMap<>();
                        for(Map.Entry<String, Object> entry : json.toMap().entrySet()) {
                            final String conditionalValue = entry.getKey();
                            final JSONArray array = (JSONArray) entry.getValue();
                            final HashSet<String> deviceTokens = new HashSet<>();
                            for(Object obj : array) {
                                deviceTokens.add((String) obj);
                            }
                            tokens.put(conditionalValue, deviceTokens);
                        }
                        map.put(subcategory, tokens);
                    }
                    folder.removeCustomFolderName(fileName);
                }
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
        final Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> deviceTokens = getConditionalDeviceTokens();
        if(deviceTokens != null) {
            deviceTokens.putIfAbsent(subcategory, new HashMap<>());
            deviceTokens.get(subcategory).putIfAbsent(conditionalValue, new HashSet<>());
            deviceTokens.get(subcategory).get(conditionalValue).add(deviceToken);
        }
    }

    default void unregister(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(deviceTokens != null && deviceTokens.containsKey(subcategory)) {
            deviceTokens.get(subcategory).remove(deviceToken);
        }
        final Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> conditionalDeviceTokens = getConditionalDeviceTokens();
        if(conditionalDeviceTokens != null && conditionalDeviceTokens.containsKey(subcategory)) {
            final HashMap<String, HashSet<String>> hashmap = conditionalDeviceTokens.get(subcategory);
            for(String conditionalValue : hashmap.keySet()) {
                hashmap.get(conditionalValue).remove(deviceToken);
            }
            conditionalDeviceTokens.get(subcategory).remove(deviceToken);
        }
    }
    default boolean isRegistered(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        final Map<RemoteNotificationSubcategory, HashMap<String, HashSet<String>>> conditionalDeviceTokens = getConditionalDeviceTokens();
        return deviceTokens != null && deviceTokens.containsKey(subcategory) && deviceTokens.get(subcategory).contains(deviceToken)
                || conditionalDeviceTokens != null && conditionalDeviceTokens.containsKey(subcategory) && conditionalDeviceTokens.get(subcategory).containsKey(deviceToken)
                ;
    }
    default void update() {
    }
    boolean shouldSendNotification(RemoteNotification notification);
    void sendNotification(RemoteNotification notification);
}
