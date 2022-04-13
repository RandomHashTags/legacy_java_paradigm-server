package me.randomhashtags.worldlaws.locale.translators;

import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.locale.Language;
import org.json.JSONObject;

import java.util.*;

public enum ArgosTranslator implements LanguageTranslatorController {
    INSTANCE;

    private static final Random RANDOM = new Random();


    @Override
    public HashMap<Language, HashSet<Language>> getSupportedLanguages() {
        return collectEnglishSupportedTranslationLanguages(
                Language.ENGLISH,
                Language.AZERBAIJANI,
                Language.CHINESE,
                Language.CZECH,
                Language.DUTCH,
                Language.FINNISH,
                Language.FRENCH,
                Language.GERMAN,
                Language.GREEK,
                Language.HINDI,
                Language.HUNGARIAN,
                Language.INDONESIAN,
                Language.IRISH,
                Language.ITALIAN,
                Language.JAPANESE,
                Language.KOREAN,
                Language.PERSIAN,
                Language.POLISH,
                Language.PORTUGUESE,
                Language.RUSSIAN,
                Language.SLOVAK,
                Language.SPANISH,
                Language.SWEDISH,
                Language.TURKISH,
                Language.UKRAINIAN,
                Language.VIETNAMESE
        );
    }

    @Override
    public String translate(Language fromLanguage, String text, Language toLanguage) {
        final List<String> fallbacks = new ArrayList<>(Arrays.asList(
                "https://libretranslate.de",
                "https://translate.argosopentech.com",
                "https://trans.zillyhuhn.com",
                "https://libretranslate.esmailelbob.xyz",
                "https://libretranslate.pussthecat.org"
        ));
        final LinkedHashMap<String, Object> postData = new LinkedHashMap<>();
        postData.put("q", text);
        postData.put("source", fromLanguage.getID());
        postData.put("target", toLanguage.getID());
        postData.put("format", "text");
        final int size = fallbacks.size();
        String string = null;
        for(int i = 1; i <= size; i++) {
            final String targetURL = fallbacks.get(RANDOM.nextInt(fallbacks.size()));
            final String url = targetURL + "/translate";
            final JSONObject json = RestAPI.postStaticJSONObject(url, postData, false, null);
            final String key = "translatedText";
            if(json != null && json.has(key) && json.get(key) instanceof String) {
                string = json.getString(key);
                break;
            }
            fallbacks.remove(targetURL);
        }
        if(string == null) {
            string = translateLocal(fromLanguage, text, toLanguage);
        }
        WLLogger.logInfo("LanguageTranslator;translateArgos;fromLanguage=" + fromLanguage.getID() + ";toLanguage=" + toLanguage.getID() + ";text=" + text + ";translatedText=" + string);
        return string;
    }
    private String translateLocal(Language fromLanguage, String text, Language toLanguage) {
        final String command = "argos-translate --from-lang " + fromLanguage.getID() + " --to-lang " + toLanguage.getID() + " \"" + text + "\"";
        return WLUtilities.executeCommand(command, false);
    }
}
