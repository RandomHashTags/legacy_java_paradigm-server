package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.stream.CustomConsumer;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;
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

    public static JSONObjectTranslatable parse(JSONObject json, Folder folder, String fileName, Collection<String> capturedKeys, CustomConsumer yoink) {
        final String[] array = capturedKeys != null ? capturedKeys.toArray(new String[capturedKeys.size()]) : new String[0];
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable(array);
        translatable.folder = folder;
        translatable.fileName = fileName;
        for(String key : json.keySet()) {
            final Object obj = capturedKeys != null && capturedKeys.contains(key) ? yoink.accept(key) : json.get(key);
            translatable.put(key, obj);
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
        for(String key : json.keySet()) {
            put(key, json.get(key));
        }
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

    public void updateIfNeeded(LanguageTranslator translator, Language clientLanguage) {
        final String localeKey = "locale", translatorID = translator.getID(), languageID = clientLanguage.getID();
        final boolean hasTranslations = JSONTranslatable.hasTranslations(this, translatorID, languageID);
        if(!hasTranslations) {
            final JSONObject localeJSON = optJSONObject(localeKey, new JSONObject());
            final JSONObject languageJSON = localeJSON.optJSONObject(translatorID, new JSONObject());
            final JSONObject translationsJSON = languageJSON.optJSONObject(languageID, new JSONObject());
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
}
