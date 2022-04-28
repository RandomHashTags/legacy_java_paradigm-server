package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import org.json.JSONArray;

import java.util.HashSet;
import java.util.Map;

public interface RemoteNotificationDeviceTokenController extends RestAPI, Jsonable {
    RemoteNotificationCategory getCategory();
    DeviceTokenType getDeviceTokenType();
    Map<RemoteNotificationSubcategory, HashSet<String>> getDeviceTokens();

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
        if(category != null && deviceTokens != null && !deviceTokens.isEmpty()) {
            final Folder folder = Folder.DEVICE_TOKENS;
            final String folderName = getCategoryFolderName();
            for(Map.Entry<RemoteNotificationSubcategory, HashSet<String>> entry : deviceTokens.entrySet()) {
                final String fileName = entry.getKey().name();
                final HashSet<String> tokens = entry.getValue();
                folder.setCustomFolderName(fileName, folderName);
                Jsonable.setFileJSONArray(folder, fileName, new JSONArray(tokens));
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

    default void unregister(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(deviceTokens != null) {
            deviceTokens.get(subcategory).remove(deviceToken);
        }
    }
    default boolean isRegistered(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        return deviceTokens != null && deviceTokens.containsKey(subcategory) && deviceTokens.get(subcategory).contains(deviceToken);
    }
    void sendNotification(RemoteNotification notification);
}
