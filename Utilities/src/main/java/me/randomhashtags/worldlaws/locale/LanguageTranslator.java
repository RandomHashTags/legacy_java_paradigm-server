package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum LanguageTranslator {
    ARGOS,
    ;

    private static final Random RANDOM = new Random();

    public static LanguageTranslator valueOfString(String string) {
        for(LanguageTranslator translator : values()) {
            if(translator.name().equalsIgnoreCase(string)) {
                return translator;
            }
        }
        return null;
    }

    public static ConcurrentHashMap<LanguageTranslator, HashMap<Language, HashMap<String, String>>> translateFromEnglish(HashMap<String, String> text, Language...toLanguages) {
        return translateFromEnglish(text, Arrays.asList(toLanguages));
    }
    public static ConcurrentHashMap<LanguageTranslator, HashMap<Language, HashMap<String, String>>> translateFromEnglish(HashMap<String, String> text, Collection<Language> toLanguages) {
        final ConcurrentHashMap<LanguageTranslator, HashMap<Language, HashMap<String, String>>> map = new ConcurrentHashMap<>();
        new CompletableFutures<LanguageTranslator>().stream(Arrays.asList(values()), translator -> {
            map.putIfAbsent(translator, new HashMap<>());
            for(Language toLanguage : toLanguages) {
                for(Map.Entry<String, String> test : text.entrySet()) {
                    final String key = test.getKey(), words = test.getValue();
                    final String string = translator.translateFromEnglish(words, toLanguage);
                    if(string != null) {
                        map.get(translator).putIfAbsent(toLanguage, new HashMap<>());
                        map.get(translator).get(toLanguage).put(key, string);
                    }
                }
            }
        });
        return map;
    }

    public String getID() {
        return name().toLowerCase();
    }

    public HashMap<String, String> translateFromEnglish(HashMap<String, String> words, Language toLanguage) {
        final HashMap<String, String> translated = new HashMap<>();
        new CompletableFutures<String>().stream(words.keySet(), key -> {
            final String text = words.get(key);
            final String translatedText = translateFromEnglish(text, toLanguage);
            translated.put(key, translatedText);
        });
        return translated;
    }
    public String translateFromEnglish(String text, Language toLanguage) {
        return translate(Language.ENGLISH, text, toLanguage);
    }
    public String translate(Language fromLanguage, String text, Language toLanguage) {
        if(text == null || fromLanguage.equals(toLanguage)) {
            return text;
        }
        switch (this) {
            case ARGOS: return translateArgos(fromLanguage, text, toLanguage);
            default: return null;
        }
    }

    private String translateArgos(Language fromLanguage, String text, Language toLanguage) {
        final List<String> fallbacks = new ArrayList<>(Arrays.asList(
                "https://libretranslate.de",
                "https://translate.argosopentech.com",
                "https://trans.zillyhuhn.com",
                "https://libretranslate.esmailelbob.xyz",
                "https://libretranslate.pussthecat.org"
        ));
        final LinkedHashMap<String, String> postData = new LinkedHashMap<>();
        postData.put("q", text);
        postData.put("source", fromLanguage.getID());
        postData.put("target", toLanguage.getID());
        postData.put("format", "text");
        final int size = fallbacks.size();
        String string = null;
        for(int i = 1; i <= size; i++) {
            final String targetURL = fallbacks.get(RANDOM.nextInt(fallbacks.size()));
            final String url = targetURL + "/translate";
            final JSONObject json = RestAPI.postStaticJSONObject(url, postData, true, false, null);
            final String key = "translatedText";
            if(json != null && json.has(key) && json.get(key) instanceof String) {
                string = json.getString(key);
                break;
            }
            fallbacks.remove(targetURL);
        }
        if(string == null) {
            string = translateArgosLocal(fromLanguage, text, toLanguage);
        }
        WLLogger.logInfo("LanguageTranslator;translateArgos;fromLanguage=" + fromLanguage.getID() + ";toLanguage=" + toLanguage.getID() + ";text=" + text + ";translatedText=" + string);
        return string;
    }
    private String translateArgosLocal(Language fromLanguage, String text, Language toLanguage) {
        final String command = "argos-translate --from-lang " + fromLanguage.getID() + " --to-lang " + toLanguage.getID() + " \"" + text + "\"";
        return WLUtilities.executeCommand(command, false);
    }
}
