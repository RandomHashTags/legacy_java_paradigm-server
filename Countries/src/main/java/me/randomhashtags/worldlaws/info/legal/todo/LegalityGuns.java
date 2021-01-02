package me.randomhashtags.worldlaws.info.legal.todo;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.CountryInfo;
import me.randomhashtags.worldlaws.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum LegalityGuns implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LEGALITY_GUNS;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries != null) {
            final String value = getValue(countryBackendID);
            handler.handle(value);
        } else {
            refresh(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String value = getValue(countryBackendID);
                    handler.handle(value);
                }
            });
        }
    }
    private String getValue(String countryBackendID) {
        final String value = countries.getOrDefault(countryBackendID, "null");
        if(value.equals("null")) {
            WLLogger.log(Level.WARNING, "LegalityGuns - missing for country \"" + countryBackendID + "\"!");
        }
        return value;
    }

    private void refresh(CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/Overview_of_gun_laws_by_nation";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements trs = doc.select("div.mw-parser-output table.wikitable").get(1).select("tbody tr");
            trs.remove(0);
            trs.remove(0);
            trs.remove(0);
            trs.removeIf(row -> row.select("td").size() == 0);

            final String goodReasonText = "Is good reason required to obtain firearms (does not include self-defense)";
            final String personalProtectionText = "Personal protection/self-defense is a legitimate reason to acquire license/permit/firearm";
            final String longGunsText = "Are shotguns and semi/full automatic rifles allowed?";
            final String handgunsText = "Are handguns permitted?";
            final String semiAutoText = "Are semi-automatic rifles permitted?";
            final String fullAutoText = "Are fully automatic firearms allowed for civilians (including with a special permit)?";
            final String openCarryText = "Are private citizens allowed to carry guns openly (including with a special permit)?";
            final String concealedCarryText = "Is concealed carry allowed for private civilians (including with a special permit)?";
            final String magazineCapacityLimitText = "";
            final String registrationText = "Are firearms not required to be registered (\"yes\" means \"not required\")?";
            final String maxPenaltyText = "Maximum prison penalty for illicit firearm possession";

            final EventSources sources = new EventSources(new EventSource("Wikipedia: Overview of gun laws by nation", url));
            for(Element element : trs) {
                final Elements tds = element.select("td");
                final String country = tds.get(0).text().toLowerCase().split("\\(")[0].split("\\[")[0].replace(" ", "");
                final Element goodReasonElement = tds.get(1);
                final Element personalProtectionElement = tds.get(2);
                final Element longGunsElement = tds.get(3);
                final Element handgunsElement = tds.get(4);
                final Element semiAutoRiflesElement = tds.get(5);
                final Element fullAutoFirearmsElement = tds.get(6);
                final Element openCarryElement = tds.get(7);
                final Element concealedCarryElement = tds.get(8);
                final Element magazineCapacityElement = tds.get(9);
                final Element freeOfRegistrationElement = tds.get(10);
                final Element maxPenaltyElement = tds.get(11);

                final CountryInfoValue goodReasonRequiredValue = new CountryInfoValue("Good Reason Required", getValue(goodReasonElement), goodReasonText);
                final CountryInfoValue personalProtectionValue = new CountryInfoValue("Personal Protection", getValue(personalProtectionElement), personalProtectionText);
                final CountryInfoValue longGunsValue = new CountryInfoValue("Long Guns", getValue(longGunsElement), longGunsText);
                final CountryInfoValue handgunsValue = new CountryInfoValue("Handguns", getValue(handgunsElement), handgunsText);
                final CountryInfoValue semiAutomaticRiflesValue = new CountryInfoValue("Semi-automatic Riles", getValue(semiAutoRiflesElement), semiAutoText);
                final CountryInfoValue fullAutomaticFirearmsValue = new CountryInfoValue("Full Automatic Firearms", getValue(fullAutoFirearmsElement), fullAutoText);
                final CountryInfoValue openCarryValue = new CountryInfoValue("Open Carry", getValue(openCarryElement), openCarryText);
                final CountryInfoValue concealedCarryValue = new CountryInfoValue("Concealed Carry", getValue(concealedCarryElement), concealedCarryText);
                final CountryInfoValue magazineCapacityLimitsValue = new CountryInfoValue("Magazine Capacity Limits", getValue(magazineCapacityElement), magazineCapacityLimitText);
                final CountryInfoValue freeOfRegistrationValue = new CountryInfoValue("Registration Required", getValue(freeOfRegistrationElement), registrationText);
                final CountryInfoValue maxPenaltyValue = new CountryInfoValue("Max penalty (years)", getValue(maxPenaltyElement), maxPenaltyText);

                final CountryInfoKey info = new CountryInfoKey("Firearms", null, sources, goodReasonRequiredValue, personalProtectionValue, longGunsValue, handgunsValue, semiAutomaticRiflesValue, fullAutomaticFirearmsValue, openCarryValue, concealedCarryValue, magazineCapacityLimitsValue, freeOfRegistrationValue, maxPenaltyValue);
                countries.put(country, info.toString());
            }
            WLLogger.log(Level.INFO, "LegalityGuns - refreshed (took " + (System.currentTimeMillis()-started) + "ms)");
            handler.handle(null);
        }
    }

    private String getValue(Element element) {
        final String text = element.text();
        return text.isEmpty() ? "Unknown" : removeReferences(text);
    }
}
