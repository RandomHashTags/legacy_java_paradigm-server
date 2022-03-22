package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
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

    static boolean hasTranslations(JSONObject json, String translatorID, String languageID) {
        final String localeKey = "locale";
        if(json.has(localeKey)) {
            final JSONObject localeJSON = json.getJSONObject(localeKey);
            if(localeJSON.has(translatorID)) {
                final JSONObject languagesJSON = localeJSON.getJSONObject(translatorID);
                return languagesJSON.has(languageID);
            }
        }
        return false;
    }
    static void insertTranslations(JSONObject json, String translatorID, String languageID) {
        final String localeKey = "locale";
        if(json.has(localeKey)) {
            final JSONObject localeJSON = json.getJSONObject(localeKey);
            if(localeJSON.has(translatorID)) {
                final JSONObject languagesJSON = localeJSON.getJSONObject(translatorID);
                if(languagesJSON.has(languageID)) {
                    final JSONObject translations = languagesJSON.getJSONObject(languageID);
                    for(String key : translations.keySet()) {
                        json.put(key, translations.get(key));
                    }
                }
            }
        }
    }

    default JSONObject getTranslations(JSONObjectTranslatable json, LanguageTranslator translator, Language toLanguage) {
        final HashSet<String> keys = json.getTranslatedKeys();
        final JSONObject translations = new JSONObject();
        if(keys != null && keys.size() > 0) {
            new CompletableFutures<String>().stream(keys, key -> {
                if(json.has(key)) {
                    final Object obj = json.get(key);
                    if(obj instanceof String) {
                        final String text = (String) obj;
                        final String translatedText = translator.translateFromEnglish(text, toLanguage);
                        translations.put(key, translatedText);
                    } else if(obj instanceof JSONArray) {
                        final JSONArray array = (JSONArray) obj;
                        final JSONArray translatedArray = JSONArrayTranslatable.getTranslations(array, translator, toLanguage);
                        translations.put(key, translatedArray);
                    } else if(obj instanceof JSONObjectTranslatable) {
                        final JSONObjectTranslatable translatableObj = (JSONObjectTranslatable) obj;
                        final JSONObjectTranslatable translatable = new JSONObjectTranslatable(translatableObj);
                        final JSONObject translatableTranslations = getTranslations(translatable, translator, toLanguage);
                        if(!translatable.isEmpty()) {
                            for(String translatableKey : translatableTranslations.keySet()) {
                                translatable.put(translatableKey, translatableTranslations.get(translatableKey));
                            }
                            translations.put(key, translatable);
                        }
                    }
                }
            });
        }
        return translations;
    }
}
