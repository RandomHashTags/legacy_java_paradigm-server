package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

public interface JSONTranslatable {
    boolean isEmpty();

    void setTranslatedKeys(String...keys);
    void addTranslatedKey(String key);

    void setRemovedClientKeys(String...keys);
    void addRemovedClientKeys(String key);

    Folder getFolder();
    void setFolder(Folder folder);
    String getFileName();
    void setFileName(String fileName);
    HashSet<String> getTranslatedKeys();
    HashSet<String> getRemovedClientKeys();
    default HashSet<String> collectKeys(String...keys) {
        return new HashSet<>(Set.of(keys));
    }

    default void save() {
        final Folder folder = getFolder();
        final String fileName = getFileName();
        if(folder != null && fileName != null) {
            if(this instanceof JSONObjectTranslatable) {
                final String id = folder.getFolderName(fileName);
                Jsonable.setFileJSONObject(folder, fileName, (JSONObjectTranslatable) this, false);
                folder.setCustomFolderName(id, fileName);
            }
        }
    }

    static boolean insertTranslations(JSONObject json, String translatorID, String languageID) {
        final String localeKey = "locale";
        boolean inserted = false;
        if(json.has(localeKey)) {
            final JSONObject localeJSON = json.getJSONObject(localeKey);
            if(localeJSON.has(translatorID)) {
                final JSONObject languagesJSON = localeJSON.getJSONObject(translatorID);
                if(languagesJSON.has(languageID)) {
                    inserted = true;
                    final JSONObject translations = languagesJSON.getJSONObject(languageID);
                    for(String key : translations.keySet()) {
                        json.put(key, translations.get(key));
                    }
                }
            }
        }
        return inserted;
    }
}
