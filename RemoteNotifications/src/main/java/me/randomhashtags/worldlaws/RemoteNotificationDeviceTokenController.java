package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.notifications.RemoteNotification;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationCategory;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationConditionalValue;
import me.randomhashtags.worldlaws.notifications.RemoteNotificationSubcategory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface RemoteNotificationDeviceTokenController extends RestAPI, Jsonable {
    RemoteNotificationCategory getCategory();
    DeviceTokenType getDeviceTokenType();
    Map<RemoteNotificationSubcategory, HashSet<String>> getDeviceTokens();
    default Map<RemoteNotificationSubcategory, DeviceTokenPairs> getConditionalDeviceTokens() {
        return null;
    }
    Set<RemoteNotificationDeviceTokenController> UPDATED_CONTROLLERS = ConcurrentHashMap.newKeySet();
    Set<RemoteNotificationDeviceTokenController> UPDATED_CONDITIONAL_CONTROLLERS = ConcurrentHashMap.newKeySet();

    default HashSet<String> getConditionalDeviceTokensThatContain(RemoteNotificationSubcategory subcategory, RemoteNotificationConditionalValue conditionalValue) {
        if(conditionalValue == null || !subcategory.isConditional()) {
            return null;
        }
        final Map<RemoteNotificationSubcategory, DeviceTokenPairs> map = getConditionalDeviceTokens();
        if(map != null && !map.isEmpty() && map.containsKey(subcategory)) {
            final HashSet<String> tokens = new HashSet<>();
            final String value = conditionalValue.getFormattedValue();
            for(DeviceTokenPair pair : map.get(subcategory)) {
                if(pair.getValues().contains(value)) {
                    tokens.add(pair.getDeviceToken());
                }
            }
            return tokens.isEmpty() ? null : tokens;
        }
        return null;
    }
    default HashSet<String> getConditionalValuesForDeviceToken(RemoteNotificationSubcategory subcategory, String deviceToken) {
        if(!subcategory.isConditional()) {
            return null;
        }
        final Map<RemoteNotificationSubcategory, DeviceTokenPairs> map = getConditionalDeviceTokens();
        if(map != null && map.containsKey(subcategory)) {
            final DeviceTokenPair pair = map.get(subcategory).valueOfToken(deviceToken);
            return pair != null ? pair.getValues() : null;
        }
        return null;
    }

    default String getCategoryFolderName(RemoteNotificationCategory category) {
        final DeviceTokenType deviceTokenType = getDeviceTokenType();
        if(category != null && deviceTokenType != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            return folder.getFolderName().replace("%type%", deviceTokenType.getID()).replace("%category%", category.getID());
        }
        return null;
    }

    default void save() {
        if(UPDATED_CONTROLLERS.contains(this)) {
            final RemoteNotificationCategory category = getCategory();
            final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
            if(category != null && deviceTokens != null) {
                UPDATED_CONTROLLERS.remove(this);
                final Folder folder = Folder.DEVICE_TOKENS;
                for(Map.Entry<RemoteNotificationSubcategory, HashSet<String>> entry : deviceTokens.entrySet()) {
                    final RemoteNotificationSubcategory subcategory = entry.getKey();
                    final String folderName = getCategoryFolderName(subcategory.getCategory());
                    final String fileName = subcategory.name();
                    final HashSet<String> tokens = entry.getValue();
                    folder.setCustomFolderName(fileName, folderName);
                    Jsonable.setFileJSONArray(folder, fileName, new JSONArray(tokens));
                }
            }
        }
    }
    default void saveConditional() {
        if(UPDATED_CONDITIONAL_CONTROLLERS.contains(this)) {
            final RemoteNotificationCategory category = getCategory();
            final Map<RemoteNotificationSubcategory, DeviceTokenPairs> deviceTokens = getConditionalDeviceTokens();
            if(category != null && deviceTokens != null) {
                UPDATED_CONDITIONAL_CONTROLLERS.remove(this);
                final Folder folder = Folder.DEVICE_TOKENS;
                for(Map.Entry<RemoteNotificationSubcategory, DeviceTokenPairs> entry : deviceTokens.entrySet()) {
                    final RemoteNotificationSubcategory subcategory = entry.getKey();
                    final String folderName = getCategoryFolderName(subcategory.getCategory());
                    final String fileName = subcategory.name();
                    final HashSet<DeviceTokenPair> conditionalTokens = entry.getValue();
                    final JSONObject json = new JSONObject();
                    for(DeviceTokenPair pair : conditionalTokens) {
                        json.put(pair.getDeviceToken(), pair.toJSONArray());
                    }
                    folder.setCustomFolderName(fileName, folderName);
                    Jsonable.setFileJSONObject(folder, fileName, json);
                }
            }
        }
    }
    default void insertDeviceTokens(Map<RemoteNotificationSubcategory, HashSet<String>> map) {
        final RemoteNotificationCategory category = getCategory();
        final RemoteNotificationSubcategory[] subcategories = category != null ? category.getSubcategories() : null;
        if(subcategories != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            for(RemoteNotificationSubcategory subcategory : subcategories) {
                map.put(subcategory, new HashSet<>());
                final String fileName = subcategory.name();
                final String folderName = getCategoryFolderName(subcategory.getCategory());
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
    default void insertConditionalDeviceTokens(Map<RemoteNotificationSubcategory, DeviceTokenPairs> map) {
        final RemoteNotificationCategory category = getCategory();
        final RemoteNotificationSubcategory[] subcategories = category != null ? category.getSubcategories() : null;
        if(subcategories != null) {
            final Folder folder = Folder.DEVICE_TOKENS;
            for(RemoteNotificationSubcategory subcategory : subcategories) {
                if(subcategory.isConditional()) {
                    final String folderName = getCategoryFolderName(subcategory.getCategory());
                    final String fileName = subcategory.name();
                    folder.setCustomFolderName(fileName, folderName);
                    JSONObject json = Jsonable.getStaticLocalFileJSONObject(folder, fileName);
                    if(json == null) {
                        json = new JSONObject();
                    }
                    map.put(subcategory, new DeviceTokenPairs(json));
                    folder.removeCustomFolderName(fileName);
                }
            }
        }
    }

    default void register(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(deviceTokens != null) {
            UPDATED_CONTROLLERS.add(this);
            deviceTokens.get(subcategory).add(deviceToken);
        }
    }
    default void registerConditionalValue(RemoteNotificationSubcategory subcategory, String deviceToken, String conditionalValue) {
        final Map<RemoteNotificationSubcategory, DeviceTokenPairs> deviceTokens = getConditionalDeviceTokens();
        if(deviceTokens != null) {
            UPDATED_CONDITIONAL_CONTROLLERS.add(this);
            deviceTokens.putIfAbsent(subcategory, new DeviceTokenPairs());
            final DeviceTokenPair existing = deviceTokens.get(subcategory).valueOfToken(deviceToken);
            if(existing != null) {
                existing.addValue(conditionalValue);
            } else {
                final DeviceTokenPair pair = new DeviceTokenPair(deviceToken, conditionalValue);
                deviceTokens.get(subcategory).add(pair);
            }
        }
    }

    default void unregister(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        if(deviceTokens != null && deviceTokens.containsKey(subcategory) && deviceTokens.get(subcategory).contains(deviceToken)) {
            UPDATED_CONTROLLERS.add(this);
            deviceTokens.get(subcategory).remove(deviceToken);
        }
        final Map<RemoteNotificationSubcategory, DeviceTokenPairs> conditionalDeviceTokens = getConditionalDeviceTokens();
        if(conditionalDeviceTokens != null && conditionalDeviceTokens.containsKey(subcategory) && conditionalDeviceTokens.get(subcategory).removePairWithDeviceToken(deviceToken)) {
            UPDATED_CONDITIONAL_CONTROLLERS.add(this);
        }
    }
    default void unregisterConditionalValue(RemoteNotificationSubcategory subcategory, String deviceToken, String conditionalValue) {
        final Map<RemoteNotificationSubcategory, DeviceTokenPairs> conditionalDeviceTokens = getConditionalDeviceTokens();
        if(conditionalDeviceTokens != null && conditionalDeviceTokens.containsKey(subcategory)) {
            final DeviceTokenPairs pairs = conditionalDeviceTokens.get(subcategory);
            final DeviceTokenPair pair = pairs.valueOfToken(deviceToken);
            if(pair != null) {
                UPDATED_CONDITIONAL_CONTROLLERS.add(this);
                pair.removeValue(conditionalValue);
                if(pair.getValues().isEmpty()) {
                    pairs.remove(pair);
                }
            }
        }
    }
    default boolean isRegistered(RemoteNotificationSubcategory subcategory, String deviceToken) {
        final Map<RemoteNotificationSubcategory, HashSet<String>> deviceTokens = getDeviceTokens();
        final Map<RemoteNotificationSubcategory, DeviceTokenPairs> conditionalDeviceTokens = getConditionalDeviceTokens();
        return deviceTokens != null && deviceTokens.containsKey(subcategory) && deviceTokens.get(subcategory).contains(deviceToken)
                || conditionalDeviceTokens != null && conditionalDeviceTokens.containsKey(subcategory) && conditionalDeviceTokens.get(subcategory).valueOfToken(deviceToken) != null
                ;
    }
    default void update() {
    }
    boolean shouldSendNotification(RemoteNotification notification);
    void sendNotification(RemoteNotification notification);
}
