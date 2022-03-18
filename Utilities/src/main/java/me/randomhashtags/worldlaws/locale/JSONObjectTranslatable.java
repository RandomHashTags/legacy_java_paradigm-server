package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;

public class JSONObjectTranslatable extends JSONObject implements JSONTranslatable {

    // TODO: have a cache of frequent words translated stored in a file

    public static JSONObjectTranslatable copy(JSONObject json) {
        if(json == null || json.isEmpty()) {
            return null;
        }
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String key : json.keySet()) {
            translatable.put(key, json.get(key));
        }
        return translatable;
    }


    private HashSet<String> translatedKeys, removedClientKeys;
    private Folder folder;
    private String fileName;

    public JSONObjectTranslatable(String...keys) {
        super();
        setTranslatedKeys(keys);
    }
    public JSONObjectTranslatable(JSONObjectTranslatable json) {
        super();
        translatedKeys = json.getTranslatedKeys();
        removedClientKeys = json.getRemovedClientKeys();
        folder = json.getFolder();
        fileName = json.getFileName();
    }

    @Override
    public void setTranslatedKeys(String...keys) {
        this.translatedKeys = new HashSet<>(Arrays.asList(keys));
    }
    @Override
    public void addTranslatedKey(String key) {
        if(translatedKeys == null) {
            translatedKeys = new HashSet<>();
        }
        translatedKeys.add(key);
    }

    @Override
    public void setRemovedClientKeys(String...keys) {
        this.removedClientKeys = new HashSet<>(Arrays.asList(keys));
    }
    @Override
    public void addRemovedClientKeys(String key) {
        if(removedClientKeys == null) {
            removedClientKeys = new HashSet<>();
        }
        removedClientKeys.add(key);
    }

    public JSONTranslatable getJSONTranslatable(String key) {
        final Object obj = opt(key);
        return obj instanceof JSONTranslatable ? (JSONTranslatable) obj : null;
    }
    public JSONObjectTranslatable getJSONObjectTranslatable(String key) {
        return optJSONObjectTranslatable(key, null);
    }
    public JSONObjectTranslatable optJSONObjectTranslatable(String key, JSONObjectTranslatable defaultValue) {
        final JSONObject json = optJSONObject(key, null);
        return json instanceof JSONObjectTranslatable ? (JSONObjectTranslatable) json : defaultValue;
    }

    @Override
    public Folder getFolder() {
        return folder;
    }

    @Override
    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public HashSet<String> getTranslatedKeys() {
        return translatedKeys;
    }

    @Override
    public HashSet<String> getRemovedClientKeys() {
        return removedClientKeys;
    }

    public void update(LanguageTranslator translator, Language clientLanguage) {
        final String localeKey = "locale", translatorID = translator.getID(), languageID = clientLanguage.getID();
        final boolean inserted = JSONTranslatable.insertTranslations(this, translatorID, languageID);
        if(!inserted) {
            final JSONObject localeJSON = has(localeKey) ? getJSONObject(localeKey) : new JSONObject();
            final JSONObject languageJSON = localeJSON.has(translatorID) ? localeJSON.getJSONObject(translatorID) : new JSONObject();
            final JSONObject translationsJSON = languageJSON.has(languageID) ? languageJSON.getJSONObject(languageID) : new JSONObject();
            final JSONObject translations = getTranslations(this, translator, clientLanguage);
            if(!translations.isEmpty()) {
                for(String key : translations.keySet()) {
                    translationsJSON.put(key, translations.get(key));
                }
                languageJSON.put(languageID, translationsJSON);
                localeJSON.put(translatorID, languageJSON);
                put(localeKey, localeJSON);
                save();
            }
        }
    }
    public JSONObject getTranslations(JSONObjectTranslatable json, LanguageTranslator translator, Language toLanguage) {
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
                        final JSONObjectTranslatable translatable = (JSONObjectTranslatable) obj;
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
