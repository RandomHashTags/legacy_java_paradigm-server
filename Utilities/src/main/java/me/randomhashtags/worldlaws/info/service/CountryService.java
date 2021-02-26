package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public interface CountryService extends RestAPI, Jsoupable {
    CountryInfo getInfo();
    HashMap<String, String> getCountries();

    default void getValue(String countryBackendID, CompletionHandler handler) {
        if(getCountries() != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            final long started = System.currentTimeMillis();
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    WLLogger.log(Level.INFO, getInfo().name() + " - loaded " + getCountries().size() + " countries/territories (took " + (System.currentTimeMillis()-started) + "ms)");
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    default String getValue(String countryBackendID) {
        return getCountries().getOrDefault(countryBackendID, "null");
    }

    void refresh(CompletionHandler handler);

    default HashSet<String> getCountriesFromText(String text) {
        final HashSet<String> countries = new HashSet<>();
        switch (text) {
            case "carribean":
                // https://en.wikipedia.org/wiki/Caribbean
                countries.addAll(Arrays.asList(
                        "antiguaandbarbuda",
                        "bahamas",
                        "barbados",
                        "cuba",
                        "dominica",
                        "dominicanrepublic",
                        "grenada",
                        "haiti",
                        "jamaica",
                        "saintkittsandnevis",
                        "saintlucia",
                        "saintvincentandthegrenadines",
                        "trinidadandtobago"
                ));
                break;
            case "easterneurope":
                // https://en.wikipedia.org/wiki/Eastern_Europe
                countries.addAll(Arrays.asList(
                        "bulgaria",
                        "croatia",
                        "cyprus",
                        "czechrepublic",
                        "estonia",
                        "hungary",
                        "latvia",
                        "lithuania",
                        "malta",
                        "poland",
                        "romania",
                        "slovakia",
                        "slovenia"
                ));
                break;
            default:
                countries.add(text);
                break;
        }
        return countries;
    }

    default String getNotesFromElement(Element noteElement) {
        String notes = null;
        final Elements children = noteElement.children();
        final String text = noteElement.text(), firstText = children.size() > 0 ? children.get(0).text() : "";
        if(firstText.startsWith("Main article") || firstText.startsWith("See also")) {
            notes = text.equals(firstText) ? "" : text.substring(firstText.length()+1);
        } else {
            notes = text;
        }
        notes = removeReferences(notes);
        return notes;
    }

    private String getClassSimpleName() {
        return getClass().getSimpleName();
    }
}
