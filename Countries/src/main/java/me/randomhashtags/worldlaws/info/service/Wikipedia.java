package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum Wikipedia implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.SERVICE_WIKIPEDIA;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return null;
    }

    @Override
    public void refresh(CompletionHandler handler) {
    }

    @Override
    public void getValue(String tag, CompletionHandler handler) {
        if(countries == null) {
            countries = new HashMap<>();
        }
        if(countries .containsKey(tag)) {
            final String value = countries.get(tag);
            handler.handle(value);
        } else {
            final long started = System.currentTimeMillis();
            final String url = "https://en.wikipedia.org/wiki/" + tag.replace(" ", "_");
            final Document document;
            try {
                document = Jsoupable.getLocalDocument(FileType.COUNTRIES, url);
            } catch (Exception e) {
                e.printStackTrace();
                handler.handle(null);
                return;
            }
            final Elements paragraphs = document.select("div.mw-parser-output table.infobox + p");
            final Element firstParagraphElement = paragraphs.get(0);
            String firstParagraph = removeReferences(firstParagraphElement.text());
            final String[] split = firstParagraph.split(" ");
            if(split[1].startsWith("(")) {
                final String value = " (" + firstParagraph.split("\\(")[1].split("\\)")[0] + ")";
                firstParagraph = firstParagraph.replace(value, "");
            }
            if(firstParagraph.contains(" listen)")) {
                final String key = firstParagraph.split(" listen\\)")[0].split("\\(")[1];
                firstParagraph = firstParagraph.replace(" (" + key + " listen)", "");
            }
            while (firstParagraph.contains("(listen)")) {
                final String[] values = firstParagraph.split("\\(listen\\)");
                final String prefix = values[0].split("\\(")[1];
                final String suffix = values[1].split("\\)")[0];
                final String value = " (" + prefix + "(listen)" + suffix + ")";
                firstParagraph = firstParagraph.replace(value, "");
            }
            final String paragraph = LocalServer.fixEscapeValues(firstParagraph);
            final String string =
                    "{" +
                    "\"paragraph\":\"" + paragraph + "\"," +
                    "\"url\":\"" + url + "\"" +
                    "}";
            countries.put(tag, string);
            WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + tag + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(string);
        }
    }
}
