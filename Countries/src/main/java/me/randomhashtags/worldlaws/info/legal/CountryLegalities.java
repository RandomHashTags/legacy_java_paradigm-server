package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

public enum CountryLegalities implements CountryLegalityService {
    ABORTION(
            "https://en.wikipedia.org/wiki/Abortion_law",
            -1
    ),
    BITCOIN(
            "https://en.wikipedia.org/wiki/Legality_of_bitcoin_by_country_or_territory",
            -1
    ),
    CANNABIS(
            "https://en.wikipedia.org/wiki/Legality_of_cannabis",
            -1
    ),
    DRINKING_AGE(
            "https://en.wikipedia.org/wiki/Legal_drinking_age",
            -1
    ),
    /*GUNS(
            "https://en.wikipedia.org/wiki/Overview_of_gun_laws_by_nation",
            -1
    ),*/
    INCEST(
            "https://en.wikipedia.org/wiki/Legality_of_incest",
            -1
    ),
    MARITAL_RAPE(
            "https://en.wikipedia.org/wiki/Marital_rape_laws_by_country",
            -1
    ),
    /*PORNOGRAPHY(
            "https://en.wikipedia.org/wiki/Pornography_laws_by_region",
            WLUtilities.getTodayYear()
    ),*/
    PROSTITUTION(
            "https://en.wikipedia.org/wiki/Prostitution_law",
            2018
    ),
    SMOKING_AGE(
            "https://en.wikipedia.org/wiki/Smoking_age",
            WLUtilities.getTodayYear()
    ),
    ;

    private final String url;
    private final int yearOfData;

    private HashMap<String, String> styles;

    CountryLegalities(String url, int yearOfData) {
        this.url = url;
        this.yearOfData = yearOfData;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getYearOfData() {
        return yearOfData;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.valueOf("LEGALITY_" + name());
    }

    @Override
    public JSONObjectTranslatable loadData() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        loadJSONData(json);
        for(String country : json.keySet()) {
            json.put(country, json.get(country));
            json.addTranslatedKey(country);
        }
        return json;
    }

    private void loadJSONData(JSONObjectTranslatable json) {
        loadStyles();
        switch (this) {
            case ABORTION:
                loadAbortion(json);
                break;
            case BITCOIN:
                loadBitcoin(json);
                break;
            case CANNABIS:
                loadCannabis(json);
                break;
            case DRINKING_AGE:
                loadDrinkingAge(json);
                break;
            /*case GUNS:
                loadGuns(json);
                break;*/
            case INCEST:
                loadIncest(json);
                break;
            case MARITAL_RAPE:
                loadMaritalRape(json);
                break;
            /*case PORNOGRAPHY:
                loadPornography(json);
                break;*/
            case PROSTITUTION:
                loadProstitution(json);
                break;
            case SMOKING_AGE:
                loadSmokingAge(json);
                break;
            default:
                break;
        }
    }

    private String getValue(Element element) {
        return getValue(element, "");
    }
    private String getValue(Element element, String suffix) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown") + suffix;
    }

    private void loadStyles() {
        switch (this) {
            case ABORTION:
                styles = new HashMap<>() {{
                    put("background:#FFC7C7;vertical-align:middle;text-align:center;", "Illegal");
                    put("background: #FFE3E3; color: black; vertical-align: middle; text-align: center;", "Illegal, but Legal under certain conditions");
                    put("background:#9EFF9E;vertical-align:middle;text-align:center;", "Legal");
                    put("background:#bfd; color:black; vertical-align:middle; text-align:center;", "Legal, only under certain conditions");
                    put("background:#FFB;vertical-align:middle;text-align:center;", "Varies");
                    put("background: #ececec; color: #2C2C2C; vertical-align: middle; text-align: center;", "Unclear");
                }};
                break;
            case CANNABIS:
                styles = new HashMap<>() {{
                    put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
                    put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
                    put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
                }};
                break;
            case MARITAL_RAPE:
                styles = new HashMap<>() {{
                    put("background: #ececec; color: #2C2C2C; font-size: smaller; vertical-align: middle; text-align: center;", "Unclear");
                    put("background:#9F9;vertical-align:middle;text-align:center;", "Illegal");
                    put("background:#F99;vertical-align:middle;text-align:center;", "Legal");
                    put("background: #FFD; color: black; vertical-align: middle; text-align: center;", "Legal and Illegal");
                }};
                break;
            /*case PORNOGRAPHY:
                styles = new HashMap<>() {{
                    put("background:#FFFFFF;vertical-align:middle;text-align:center;", "Unknown");
                    put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
                    put("background:#FFB;vertical-align:middle;text-align:center;", "Kinda Legal");
                    put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
                }};
                break;*/
            default:
                break;
        }
    }

    private void loadAbortion(JSONObjectTranslatable json) {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 2).select("tbody tr");
        trs.removeIf(row -> {
            return row.hasAttr("id") && row.attr("id").startsWith("mw-customcollapsible-") || row.select("td").size() == 0;
        });

        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].replace(" ", "");
            final Element saveLifeElement = tds.get(1), preserveHealthElement = tds.get(2), rapeElement = tds.get(3), fetalImpairmentElement = tds.get(4), economicOrSocialElement = tds.get(5), onRequestElement = tds.get(6);

            final CountryInfoValue saveLifeValue = new CountryInfoValue("Risk to Life", getValue(saveLifeElement), getAbortionDescription(saveLifeElement));
            final CountryInfoValue preserveHealthValue = new CountryInfoValue("Risk to Health", getValue(preserveHealthElement), getAbortionDescription(preserveHealthElement));
            final CountryInfoValue rapeValue = new CountryInfoValue("Rape", getValue(rapeElement), getAbortionDescription(rapeElement));
            final CountryInfoValue fetalImpairmentValue = new CountryInfoValue("Fetal Impairment", getValue(fetalImpairmentElement), getAbortionDescription(fetalImpairmentElement));
            final CountryInfoValue economicOrSocialValue = new CountryInfoValue("Economic or Social", getValue(economicOrSocialElement), getAbortionDescription(economicOrSocialElement));
            final CountryInfoValue onRequestValue = new CountryInfoValue("On Request", getValue(onRequestElement), getAbortionDescription(onRequestElement));

            final CountryInfoKey info = new CountryInfoKey(null, yearOfData, saveLifeValue, preserveHealthValue, rapeValue, fetalImpairmentValue, economicOrSocialValue, onRequestValue);
            json.put(country, info.toJSONObject());
        }
    }
    private String getAbortionDescription(Element element) {
        final String text = element.text();
        return text.equals("no limit") ? "Legal, with no limit"
                : text.endsWith("days") || text.endsWith("weeks") || text.endsWith("months") ? "Legal, only up to " + text + " of gestation"
                : text.contains("varies") ? "Varies by subdivision"
                : null;
    }
    private void loadBitcoin(JSONObjectTranslatable json) {
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 1; i <= 20; i++) {
            loadBitcoinData(json, tables.get(i));
        }
    }
    private void loadBitcoinData(JSONObjectTranslatable json, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        final String legal = "/wiki/File:Yes_check.svg";
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "").replace(",", "");

            final Element secondElement = tds.get(1);
            final Elements links = secondElement.select("a");
            links.removeIf(link -> !link.attr("class").equals("image"));
            if(!links.isEmpty()) {
                final List<TextNode> textNodes = secondElement.textNodes();
                final Element legalElement = links.get(0);
                final boolean isLegal = legalElement.attr("href").equals(legal);
                final String legalityTextNodeText = textNodes.get(0).text().replace(" /", "").replace("/", "");
                final String legalityText = isLegal ? legalityTextNodeText.substring(1, legalityTextNodeText.length()-1) : "Illegal";

                final CountryInfoValue legality = new CountryInfoValue("Legality", legalityText, null);
                CountryInfoValue illegality = null;
                if(links.size() == 2) {
                    String text = textNodes.get(1).text().substring(1);
                    text = text.substring(0, text.length()-1);
                    illegality = new CountryInfoValue("Illegality", text, null);
                }

                final StringBuilder noteBuilder = new StringBuilder();
                boolean isFirst = true;
                for(Element paragraph : tds.get(1).select("p")) {
                    final String string = LocalServer.fixEscapeValues(paragraph.text());
                    noteBuilder.append(isFirst ? "" : "\n").append(string);
                    isFirst = false;
                }
                final String notes = noteBuilder.toString();
                final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, legality, illegality);
                json.put(country, info.toJSONObject());
            }
        }
    }
    private void loadCannabis(JSONObjectTranslatable json) {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.hasAttr("class") && row.attr("class").equals("sortbottom"));
        trs.remove(trs.size()-1);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
            final String notes = getNotesFromElement(tds.get(3));

            final Element recreationalElement = tds.get(1), medicalElement = tds.get(2);
            final String recreationalText = recreationalElement.text().split(" See also")[0].split("\\[")[0], medicalText = medicalElement.text().split(" See also")[0].split("\\[")[0];

            final String recreationalString = getValue(recreationalElement);
            final CountryInfoValue recreationalValue = new CountryInfoValue("Recreational Use", recreationalString, recreationalText);

            final String medicinalString = getValue(medicalElement);
            final CountryInfoValue medicinalValue = new CountryInfoValue("Medicinal Use", medicinalString, medicalText);

            final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, recreationalValue, medicinalValue);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadDrinkingAge(JSONObjectTranslatable json) {
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 0; i < 5; i++) {
            loadDrinkingAgeData(json, tables.get(i), i == 3);
        }
    }
    private void loadDrinkingAgeData(JSONObjectTranslatable json, Element table, boolean propertyBased) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        if(propertyBased) {
            trs.remove(0);
            trs.remove(trs.size()-1);
        }
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final Element firstElement = tds.get(0);
            final String country = firstElement.text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
            final boolean hasStateRegionProvince = firstElement.hasAttr("colspan") && Integer.parseInt(firstElement.attr("colspan")) == 1;

            final Element targetElement = tds.get(hasStateRegionProvince ? 2 : 1);
            final boolean isSameAge = targetElement.hasAttr("colspan") && Integer.parseInt(targetElement.attr("colspan")) > 1;

            final String drinkingAgeText = targetElement.text();
            final CountryInfoValue drinkingAge = new CountryInfoValue("Drinking Age", drinkingAgeText, null);

            final String notes = getNotesFromElement(tds.get((hasStateRegionProvince ? 4 : 3)-(isSameAge ? 1 : 0)));
            final String purchasingAgeText;
            if(isSameAge) {
                purchasingAgeText = drinkingAgeText;
            } else {
                final Element purchaseAge = tds.get(hasStateRegionProvince ? 3 : 2);
                purchasingAgeText = purchaseAge.text();
            }
            final CountryInfoValue purchasingAge = new CountryInfoValue("Purchasing Age", purchasingAgeText, null);

            final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, drinkingAge, purchasingAge);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadGuns(JSONObjectTranslatable json) {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
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

            final CountryInfoValue goodReasonRequiredValue = new CountryInfoValue("Good Reason Required", getGunValue(goodReasonElement), goodReasonText);
            final CountryInfoValue personalProtectionValue = new CountryInfoValue("Personal Protection", getGunValue(personalProtectionElement), personalProtectionText);
            final CountryInfoValue longGunsValue = new CountryInfoValue("Long Guns", getGunValue(longGunsElement), longGunsText);
            final CountryInfoValue handgunsValue = new CountryInfoValue("Handguns", getGunValue(handgunsElement), handgunsText);
            final CountryInfoValue semiAutomaticRiflesValue = new CountryInfoValue("Semi-automatic Riles", getGunValue(semiAutoRiflesElement), semiAutoText);
            final CountryInfoValue fullAutomaticFirearmsValue = new CountryInfoValue("Full Automatic Firearms", getGunValue(fullAutoFirearmsElement), fullAutoText);
            final CountryInfoValue openCarryValue = new CountryInfoValue("Open Carry", getGunValue(openCarryElement), openCarryText);
            final CountryInfoValue concealedCarryValue = new CountryInfoValue("Concealed Carry", getGunValue(concealedCarryElement), concealedCarryText);
            final CountryInfoValue magazineCapacityLimitsValue = new CountryInfoValue("Magazine Capacity Limits", getGunValue(magazineCapacityElement), magazineCapacityLimitText);
            final CountryInfoValue freeOfRegistrationValue = new CountryInfoValue("Registration Required", getGunValue(freeOfRegistrationElement), registrationText);
            final CountryInfoValue maxPenaltyValue = new CountryInfoValue("Max penalty (years)", getGunValue(maxPenaltyElement), maxPenaltyText);

            final CountryInfoKey info = new CountryInfoKey(null, yearOfData, goodReasonRequiredValue, personalProtectionValue, longGunsValue, handgunsValue, semiAutomaticRiflesValue, fullAutomaticFirearmsValue, openCarryValue, concealedCarryValue, magazineCapacityLimitsValue, freeOfRegistrationValue, maxPenaltyValue);
            json.put(country, info.toJSONObject());
        }
    }
    private String getGunValue(Element element) {
        final String text = element.text();
        return text.isEmpty() ? "Unknown" : text;
    }
    private void loadIncest(JSONObjectTranslatable json) {
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 1; i <= 1; i++) {
            loadIncestData(json, tables.get(i));
        }
    }
    private void loadIncestData(JSONObjectTranslatable json, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        final String legal = "/wiki/File:Yes_check.svg";
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(1).text().toLowerCase().replace(" ", "").replace(",", "");

            final Element secondElement = tds.get(1);
            final Elements links = secondElement.select("a");
            links.removeIf(link -> !link.attr("class").equals("image"));
            if(!links.isEmpty()) {
                final List<TextNode> textNodes = secondElement.textNodes();
                final Element legalElement = links.get(0);
                final boolean isLegal = legalElement.attr("href").equals(legal);
                final String legalityTextNodeText = textNodes.get(0).text().replace(" /", "").replace("/", "");
                final String legalityText = isLegal ? legalityTextNodeText.substring(1, legalityTextNodeText.length()-1) : "Illegal";

                final CountryInfoValue legality = new CountryInfoValue("Legality", legalityText, null);
                CountryInfoValue illegality = null;
                if(links.size() == 2 && textNodes.size() > 1) {
                    String text = textNodes.get(1).text().substring(1);
                    text = text.substring(0, text.length()-1);
                    illegality = new CountryInfoValue("Illegality", text, null);
                }

                final String prohibitedRelationshipsText = tds.get(2).text(), penaltiesText = tds.get(3).text();
                CountryInfoValue prohibitedRelationships = null;
                if(!prohibitedRelationshipsText.isEmpty()) {
                    prohibitedRelationships = new CountryInfoValue("Prohibited Relationships", tds.get(2).text(), null);
                }
                final String penaltiesValue = penaltiesText.isEmpty() ? "Unknown" : penaltiesText;
                final CountryInfoValue penalties = new CountryInfoValue("Penalties", penaltiesValue, null);

                final CountryInfoKey info = new CountryInfoKey(null, yearOfData, legality, illegality, prohibitedRelationships, penalties);
                json.put(country, info.toJSONObject());
            }
        }
    }
    private void loadMaritalRape(JSONObjectTranslatable json) {
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 0; i < 24; i++) {
            loadMaritalRapeData(json, tables.get(i));
        }
    }
    private void loadMaritalRapeData(JSONObjectTranslatable json, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(" ", "").replace(",", "");

            final Element legalElement = tds.get(1);
            final String notes = getNotesFromElement(tds.get(2));

            final CountryInfoValue value = new CountryInfoValue("Criminalised", getValue(legalElement), null);
            final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, value);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadPornography(JSONObjectTranslatable json) {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable tbody tr");
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final Element saleElement = tds.get(1), possessionElement = tds.get(2), internetElement = tds.get(3);
            final String saleString = getValue(saleElement), possessionString = getValue(possessionElement), internetString = getValue(internetElement);

            if(!saleString.equals("No data") && !possessionString.equals("No data") && !internetString.equals("No data")) {
                final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
                final String notes = null;

                final String saleText = saleElement.text();
                final CountryInfoValue saleValue = new CountryInfoValue("Sale", saleString, saleText);

                final String possessionText = possessionElement.text();
                final CountryInfoValue possessionValue = new CountryInfoValue("Possession", possessionString, possessionText);

                final String internetText = internetElement.text();
                final CountryInfoValue internetValue = new CountryInfoValue("Internet Pornography", internetString, internetText);

                final List<TextNode> textNodes = tds.get(4).textNodes();
                final String penaltyText = textNodes.isEmpty() || textNodes.get(0).text().equals(" ") ? "Unknown" : textNodes.get(0).text();
                final CountryInfoValue penaltyValue = new CountryInfoValue("Penalty", penaltyText, null);

                final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, saleValue, possessionValue, internetValue, penaltyValue);
                json.put(country, info.toJSONObject());
            }
        }
    }
    private void loadProstitution(JSONObjectTranslatable json) {
        final HashMap<Integer, String> descriptions = new HashMap<>(), values = new HashMap<>();
        descriptions.put(0, "Prostitution itself (exchanging sex for money) is illegal. The punishment for prostitution varies considerably: in some countries, it can incur the death penalty, in other jurisdictions, it is a crime punishable with a prison sentence, while in others it is a lesser administrative offense punishable only with a fine.");
        descriptions.put(1, "Although prostitutes themselves commit no crime, clients and any third party involvement is criminalised. Also called the \"Swedish model\" or \"Nordic model\".");
        descriptions.put(2, "Prostitution is permitted, prohibited or regulated by local laws rather than national laws. For example, in Mexico, prostitution is prohibited in some states but regulated in others.");
        descriptions.put(3, "There is no specific law prohibiting the exchange of sex for money, but in general most forms of procuring (pimping) are illegal. These countries also generally have laws against soliciting in a public place (e.g., a street) or advertising prostitution, making it difficult to engage in prostitution without breaking any law. In countries like India, though prostitution is legal, it is illegal when committed in a hotel.");
        descriptions.put(4, "In some countries, prostitution is legal and regulated; although activities like pimping and street-walking are restricted or generally illegal. The degree of regulation varies by country.");
        descriptions.put(5, "The decriminalization of sex work is the removal of criminal penalties for sex work. Removing criminal prosecution for sex workers creates a safer and healthier environment and allows them to live with less social exclusion and stigma.");
        values.put(0, "Illegal");
        values.put(1, "Kinda Legal");
        values.put(2, "Kinda Legal");
        values.put(3, "Kinda Legal");
        values.put(4, "Legal");
        values.put(5, "Decriminalized");

        final Elements hrefs = getLegalityDocumentElements(url, "div.mw-parser-output h3 + p + ul");
        for(int i = 0; i < 6; i++) {
            final String description = descriptions.get(i), value = values.get(i);
            loadProstitutionData(json, hrefs.get(i), value, description);
        }
    }
    private void loadProstitutionData(JSONObject json, Element list, String value, String description) {
        final Elements elements = list.select("li a");
        elements.removeIf(row -> !row.attr("href").startsWith("/wiki/Prostitution_in"));
        for(Element element : elements) {
            final String country = element.text().toLowerCase().replace(" ", "").replace(",", "")
                    .replace("republicofmacedonia", "macedonia")
                    .replace("prostitutioninthe", "")
                    ;

            final CountryInfoValue infoValue = new CountryInfoValue("Prostitution", value, null);
            final CountryInfoKey info = new CountryInfoKey(description, yearOfData, infoValue);
            json.put(country, info.toJSONObject());
        }
    }
    private void loadSmokingAge(JSONObjectTranslatable json) {
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output div + table.wikitable");
        for(Element table : tables) {
            loadSmokingAgeData(json, table);
        }
    }
    private void loadSmokingAgeData(JSONObject json, Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.children();
            if(tds.size() >= 3) {
                final Elements links = tds.get(0).select("a");
                if(links.size() > 0) {
                    final String country = links.get(0).text().toLowerCase().split("\\(")[0].replace(".", "").replace(" ", "").replace(",", "").replace("federalgovernment", "canada");
                    final Element secondElement = tds.get(1);
                    final boolean isSameAge = secondElement.hasAttr("colspan") && Integer.parseInt(secondElement.attr("colspan")) == 2;

                    final String smokingAgeText = secondElement.text();
                    final CountryInfoValue smokingAge = new CountryInfoValue("Smoking Age", smokingAgeText, null);

                    final Element targetNoteElement = tds.get(isSameAge ? 2 : 3);
                    final String notes = getNotesFromElement(targetNoteElement);
                    final String purchasingAgeText;
                    if(isSameAge) {
                        purchasingAgeText = smokingAgeText;
                    } else {
                        final Element purchaseAge = tds.get(2);
                        purchasingAgeText = purchaseAge.text();
                    }
                    final CountryInfoValue purchasingAge = new CountryInfoValue("Purchasing Age", purchasingAgeText, null);

                    final CountryInfoKey info = new CountryInfoKey(notes, yearOfData, smokingAge, purchasingAge);
                    json.put(country, info.toJSONObject());
                }
            }
        }
    }
}
