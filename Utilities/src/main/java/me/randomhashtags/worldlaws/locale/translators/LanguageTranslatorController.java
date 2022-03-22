package me.randomhashtags.worldlaws.locale.translators;

import me.randomhashtags.worldlaws.locale.Language;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public interface LanguageTranslatorController {
    HashMap<Language, HashSet<Language>> getSupportedLanguages();
    default HashMap<Language, HashSet<Language>> collectEnglishSupportedTranslationLanguages(Language...languages) {
        final HashMap<Language, HashSet<Language>> map = new HashMap<>();
        map.put(Language.ENGLISH, new HashSet<>(Arrays.asList(languages)));
        return map;
    }

    default String tryTranslating(Language fromLanguage, String text, Language toLanguage) {
        final HashMap<Language, HashSet<Language>> supported = getSupportedLanguages();
        return supported.containsKey(fromLanguage) && supported.get(fromLanguage).contains(toLanguage) ? translate(fromLanguage, text, toLanguage) : null;
    }

    String translate(Language fromLanguage, String text, Language toLanguage);
}
