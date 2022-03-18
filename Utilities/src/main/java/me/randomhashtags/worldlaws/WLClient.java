package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.Language;
import me.randomhashtags.worldlaws.locale.LanguageTranslator;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketException;

public final class WLClient {
    private final Socket httpClient;
    private String target, identifier, platform;
    private Language language;
    private LanguageTranslator languageType;

    public WLClient(Socket client) {
        httpClient = client;
        setupHeaders(httpClient);
    }

    public Socket getClient() {
        return httpClient;
    }
    public String getIdentifier() {
        return identifier;
    }
    public String getPlatform() {
        return platform;
    }
    public String getTarget() {
        return target;
    }
    public Language getLanguage() {
        return language;
    }
    public LanguageTranslator getLanguageType() {
        return languageType;
    }

    public void sendResponse(@NotNull String response) {
        final String path = DataValues.HTTP_SUCCESS_200 + (response == null || response.equals("null") ? WLUtilities.SERVER_EMPTY_JSON_RESPONSE : response);
        WLUtilities.writeClientOutput(httpClient, path);
    }

    private void setupHeaders(@NotNull Socket client) {
        try {
            final InputStream input = client.getInputStream();
            final Reader reader = new InputStreamReader(input);
            String headers = "";
            try {
                int c;
                while ((c = reader.read()) != -1) {
                    headers += (char) c;
                    if (headers.contains("\r\n\r\n")) {
                        break;
                    }
                }
            } catch (SocketException ignored) {
            } catch (Exception e) {
                WLUtilities.saveException(e);
            }
            loadHeaderData(headers.replaceAll("\r", "").split("\n"));
        } catch (Exception e) {
            WLUtilities.saveException(e);
        }
    }
    private void loadHeaderData(String[] headers) {
        identifier = getFirstHeaderThatStartsWith(headers, "***REMOVED***");
        platform = getFirstHeaderThatStartsWith(headers, "***REMOVED***");
        final String targetLanguage = getFirstHeaderThatStartsWith(headers, "***REMOVED***");
        if(!targetLanguage.equals("null")) {
            language = Language.valueOfString(targetLanguage);
        }
        if(language == null) {
            language = Language.ENGLISH;
        }
        final String targetLanguageType = getFirstHeaderThatStartsWith(headers, "***REMOVED***");
        if(!targetLanguageType.equals("null")) {
            languageType = LanguageTranslator.valueOfString(targetLanguageType);
        }
        if(languageType == null) {
            languageType = LanguageTranslator.ARGOS;
        }
        setupTarget(headers);
    }
    private String getFirstHeaderThatStartsWith(String[] headers, String key) {
        for(String string : headers) {
            if(string.startsWith(key)) {
                return string.substring(key.length());
            }
        }
        return "null";
    }
    private void setupTarget(String[] headers) {
        if(target == null) {
            final String httpVersion = DataValues.HTTP_VERSION;
            for(String string : headers) {
                if(string.endsWith(httpVersion)) {
                    if(string.startsWith("POST")) {
                        target = string.split("POST ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
                        break;
                    } else if(string.startsWith("GET")) {
                        target = string.split("GET ")[1].split(" " + httpVersion)[0].replaceFirst("/", "");
                        break;
                    }
                }
            }
        }
    }
}
