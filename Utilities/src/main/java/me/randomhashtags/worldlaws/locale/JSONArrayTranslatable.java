package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class JSONArrayTranslatable extends JSONArray implements JSONTranslatable {

    private Folder folder;
    private String fileName;

    public JSONArrayTranslatable() {
        super();
    }
    public JSONArrayTranslatable(JSONArray array) {
        super(array);
    }
    public JSONArrayTranslatable(String source) {
        super(source);
    }

    @Override
    public void setTranslatedKeys(String...keys) {
    }
    @Override
    public void addTranslatedKey(String key) {
    }

    @Override
    public void setRemovedClientKeys(String...keys) {
    }
    @Override
    public void addRemovedClientKeys(String key) {
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
        return null;
    }

    @Override
    public HashSet<String> getRemovedClientKeys() {
        return null;
    }


    public static JSONArray getTranslations(JSONArray array, LanguageTranslator translator, Language toLanguage) {
        if(array.length() == 0) {
            return array;
        }
        final Object first = array.get(0);
        final JSONArray translatedArray = new JSONArray();
        if(first instanceof String) {
            new CompletableFutures<String>().stream(array, text -> {
                final String translatedText = translator.translateFromEnglish(text, toLanguage);
                translatedArray.put(translatedText);
            });
        } else if(first instanceof JSONObjectTranslatable) {
            final String translatorID = translator.getID(), toLanguageID = toLanguage.getID();
            new CompletableFutures<JSONObjectTranslatable>().stream(array, translatable -> {
                final boolean hasTranslations = JSONTranslatable.hasTranslations(translatable, translatorID, toLanguageID);
                if(!hasTranslations) {
                    final JSONObject translated = translatable.getTranslations(translatable, translator, toLanguage);
                    translatedArray.put(translated);
                } else {
                    translatedArray.put(translatable);
                }
            });
        } else {
            translatedArray.putAll(array);
        }
        return translatedArray;
    }
}
