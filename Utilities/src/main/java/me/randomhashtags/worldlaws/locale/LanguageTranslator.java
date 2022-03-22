package me.randomhashtags.worldlaws.locale;

import me.randomhashtags.worldlaws.locale.translators.ArgosTranslator;
import me.randomhashtags.worldlaws.locale.translators.LanguageTranslatorController;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum LanguageTranslator {
    ARGOS, // TODO: remove this doo doo translator
    OPEN_NMT_PYTHON,
    ;

    public static LanguageTranslator valueOfString(String string) {
        for(LanguageTranslator translator : values()) {
            if(translator.name().equalsIgnoreCase(string)) {
                return translator;
            }
        }
        return null;
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

    public LanguageTranslatorController getController() {
        switch (this) {
            case ARGOS: return ArgosTranslator.INSTANCE;
            default: return null;
        }
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
        final LanguageTranslatorController controller = getController();
        return controller != null ? controller.tryTranslating(fromLanguage, text, toLanguage) : null;
    }
}
