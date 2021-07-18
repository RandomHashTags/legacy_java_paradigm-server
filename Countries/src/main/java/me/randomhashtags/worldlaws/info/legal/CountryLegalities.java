package me.randomhashtags.worldlaws.info.legal;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.info.CountryInfoKey;
import me.randomhashtags.worldlaws.info.CountryInfoValue;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
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
    public String loadData() {
        loadStyles();
        switch (this) {
            case ABORTION: return loadAbortion();
            case BITCOIN: return loadBitcoin();
            case CANNABIS: return loadCannabis();
            case DRINKING_AGE: return loadDrinkingAge();
            //case GUNS: return loadGuns();
            case INCEST: return loadIncest();
            case MARITAL_RAPE: return loadMaritalRape();
            //case PORNOGRAPHY: return loadPornography();
            case PROSTITUTION: return loadProstitution();
            case SMOKING_AGE: return loadSmokingAge();
            default: return null;
        }
    }

    private String getValue(Element element) {
        final String style = element.attr("style");
        return styles.getOrDefault(style, "Unknown");
    }

    private void loadStyles() {
        switch (this) {
            case ABORTION:
                styles = new HashMap<>() {{
                    put("background:#9F9;vertical-align:middle;text-align:center;", "Legal");
                    put("background: #D2FFD2; color: black; vertical-align: middle; text-align: center;", "Legal, with complex legality or practice");
                    put("background: #FFB; color: black; vertical-align: middle; text-align: center;", "Varies by subdivision");
                    put("background: #FFD2D2; color:black; vertical-align: middle; text-align: center;", "Illegal, with complex legality or practice");
                    put("background:#F99;vertical-align:middle;text-align:center;", "Illegal");
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

    private String loadAbortion() {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 2).select("tbody tr");
        trs.removeIf(row -> {
            return row.hasAttr("id") && row.attr("id").startsWith("mw-customcollapsible-") || row.select("td").size() == 0;
        });

        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().split("\\[")[0].replace(" ", "");
            final Element saveLifeElement = tds.get(1), preserveHealthElement = tds.get(2), rapeElement = tds.get(3), fetalImpairmentElement = tds.get(4), economicOrSocialElement = tds.get(5), onRequestElement = tds.get(6);

            final CountryInfoValue saveLifeValue = new CountryInfoValue("Risk to Life", getValue(saveLifeElement), null);
            final CountryInfoValue preserveHealthValue = new CountryInfoValue("Risk to Health", getValue(preserveHealthElement), null);
            final CountryInfoValue rapeValue = new CountryInfoValue("Rape", getValue(rapeElement), null);
            final CountryInfoValue fetalImpairmentValue = new CountryInfoValue("Fetal Impairment", getValue(fetalImpairmentElement), null);
            final CountryInfoValue economicOrSocialValue = new CountryInfoValue("Economic or Social", getValue(economicOrSocialElement), null);
            final CountryInfoValue onRequestValue = new CountryInfoValue("On Request", getValue(onRequestElement), null);

            final CountryInfoKey info = new CountryInfoKey(null, -1, saveLifeValue, preserveHealthValue, rapeValue, fetalImpairmentValue, economicOrSocialValue, onRequestValue);
            info.country = country;
            final String string = info.toServerJSON();
            builder.append(isFirst ? "" : ",").append(string);
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadBitcoin() {
        final StringBuilder builder = new StringBuilder("[");
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 1; i <= 20; i++) {
            String data = loadBitcoinData(tables.get(i));
            if(i == 1) {
                data = data.substring(1);
            }
            builder.append(data);
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadBitcoinData(Element table) {
        final StringBuilder builder = new StringBuilder();
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
                    noteBuilder.append(isFirst ? "" : "\n").append(removeReferences(paragraph.text()));
                    isFirst = false;
                }
                final String notes = noteBuilder.toString();
                final CountryInfoKey info = new CountryInfoKey(notes, -1, legality, illegality);
                info.country = country;
                builder.append(",").append(info.toServerJSON());
            }
        }
        return builder.toString();
    }
    private String loadCannabis() {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.hasAttr("class") && row.attr("class").equals("sortbottom"));
        trs.remove(trs.size()-1);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
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

            final CountryInfoKey info = new CountryInfoKey(notes, -1, recreationalValue, medicinalValue);
            info.country = country;
            builder.append(isFirst ? "" : ",").append(info.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadDrinkingAge() {
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");

        final StringBuilder builder = new StringBuilder("[");
        for(int i = 0; i < 5; i++) {
            String data = loadDrinkingAgeData(tables.get(i), i == 3);
            if(i == 0) {
                data = data.substring(1);
            }
            builder.append(data);
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadDrinkingAgeData(Element table, boolean propertyBased) {
        final StringBuilder builder = new StringBuilder();
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

            final String drinkingAgeText = removeReferences(targetElement.text());
            final CountryInfoValue drinkingAge = new CountryInfoValue("Drinking Age", drinkingAgeText, null);

            final String notes = getNotesFromElement(tds.get((hasStateRegionProvince ? 4 : 3)-(isSameAge ? 1 : 0)));
            final String purchasingAgeText;
            if(isSameAge) {
                purchasingAgeText = drinkingAgeText;
            } else {
                final Element purchaseAge = tds.get(hasStateRegionProvince ? 3 : 2);
                purchasingAgeText = removeReferences(purchaseAge.text());
            }
            final CountryInfoValue purchasingAge = new CountryInfoValue("Purchasing Age", purchasingAgeText, null);

            final CountryInfoKey info = new CountryInfoKey(notes, -1, drinkingAge, purchasingAge);
            info.country = country;
            builder.append(",").append(info.toServerJSON());
        }
        return builder.toString();
    }
    private String loadGuns() {
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

        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
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

            final CountryInfoKey info = new CountryInfoKey(null, -1, goodReasonRequiredValue, personalProtectionValue, longGunsValue, handgunsValue, semiAutomaticRiflesValue, fullAutomaticFirearmsValue, openCarryValue, concealedCarryValue, magazineCapacityLimitsValue, freeOfRegistrationValue, maxPenaltyValue);
            info.country = country;
            builder.append(isFirst ? "" : ",").append(info.toServerJSON());
            isFirst = false;
        }
        return builder.toString();
    }
    private String getGunValue(Element element) {
        final String text = element.text();
        return text.isEmpty() ? "Unknown" : removeReferences(text);
    }
    private String loadIncest() {
        final StringBuilder builder = new StringBuilder("[");
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 1; i <= 1; i++) {
            String data = loadIncestData(tables.get(i));
            if(i == 1) {
                data = data.substring(1);
            }
            builder.append(data);
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadIncestData(Element table) {
        final StringBuilder builder = new StringBuilder();
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
                if(links.size() == 2) {
                    String text = textNodes.get(1).text().substring(1);
                    text = text.substring(0, text.length()-1);
                    illegality = new CountryInfoValue("Illegality", text, null);
                }

                final String prohibitedRelationshipsText = tds.get(2).text(), penaltiesText = removeReferences(tds.get(3).text());
                CountryInfoValue prohibitedRelationships = null;
                if(!prohibitedRelationshipsText.isEmpty()) {
                    prohibitedRelationships = new CountryInfoValue("Prohibited Relationships", tds.get(2).text(), null);
                }
                final String penaltiesValue = penaltiesText.isEmpty() ? "Unknown" : penaltiesText;
                final CountryInfoValue penalties = new CountryInfoValue("Penalties", penaltiesValue, null);

                final CountryInfoKey info = new CountryInfoKey(null, -1, legality, illegality, prohibitedRelationships, penalties);
                info.country = country;
                builder.append(",").append(info.toServerJSON());
            }
        }
        return builder.toString();
    }
    private String loadMaritalRape() {
        final StringBuilder builder = new StringBuilder("[");
        final String title = getInfo().getTitle();
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable");
        for(int i = 0; i < 24; i++) {
            String data = loadMaritalRapeData(title, tables.get(i));
            if(i == 0) {
                data = data.substring(1);
            }
            builder.append(data);
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMaritalRapeData(String title, Element table) {
        final StringBuilder builder = new StringBuilder();
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = removeReferences(tds.get(0).text().toLowerCase().replace(" ", "").replace(",", ""));

            final Element legalElement = tds.get(1);
            final String notes = getNotesFromElement(tds.get(2));

            final CountryInfoValue value = new CountryInfoValue(title, getValue(legalElement), null);
            final CountryInfoKey info = new CountryInfoKey(notes, -1, value);
            info.country = country;
            builder.append(",").append(info.toServerJSON());
        }
        return builder.toString();
    }
    private String loadPornography() {
        final Elements trs = getLegalityDocumentElements(url, "div.mw-parser-output table.wikitable tbody tr");
        trs.remove(0);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final Element saleElement = tds.get(1), possessionElement = tds.get(2), internetElement = tds.get(3);
            final String saleString = getValue(saleElement), possessionString = getValue(possessionElement), internetString = getValue(internetElement);

            if(!saleString.equals("No data") && !possessionString.equals("No data") && !internetString.equals("No data")) {
                final String country = tds.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "");
                final String notes = null;

                final String saleText = removeReferences(saleElement.text());
                final CountryInfoValue saleValue = new CountryInfoValue("Sale", saleString, saleText);

                final String possessionText = removeReferences(possessionElement.text());
                final CountryInfoValue possessionValue = new CountryInfoValue("Possession", possessionString, possessionText);

                final String internetText = removeReferences(internetElement.text());
                final CountryInfoValue internetValue = new CountryInfoValue("Internet Pornography", internetString, internetText);

                final List<TextNode> textNodes = tds.get(4).textNodes();
                final String penaltyText = textNodes.isEmpty() || textNodes.get(0).text().equals(" ") ? "Unknown" : textNodes.get(0).text();
                final CountryInfoValue penaltyValue = new CountryInfoValue("Penalty", penaltyText, null);

                final CountryInfoKey info = new CountryInfoKey(notes, -1, saleValue, possessionValue, internetValue, penaltyValue);
                info.country = country;
                builder.append(isFirst ? "" : ",").append(info.toServerJSON());
                isFirst = false;
            }
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadProstitution() {
        final StringBuilder builder = new StringBuilder("[");

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
            String data = loadProstitutionData(hrefs.get(i), value, description);
            if(i == 0) {
                data = data.substring(1);
            }
            builder.append(data);
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadProstitutionData(Element list, String value, String description) {
        final StringBuilder builder = new StringBuilder();
        final Elements elements = list.select("li a");
        elements.removeIf(row -> !row.attr("href").startsWith("/wiki/Prostitution_in"));
        for(Element element : elements) {
            final String country = element.text().toLowerCase().replace(" ", "").replace(",", "")
                    .replace("republicofmacedonia", "macedonia")
                    .replace("prostitutioninthe", "")
                    ;

            final CountryInfoValue infoValue = new CountryInfoValue("Prostitution", value, null);
            final CountryInfoKey info = new CountryInfoKey(description, -1, infoValue);
            info.country = country;
            builder.append(",").append(info.toServerJSON());
        }
        return builder.toString();
    }
    private String loadSmokingAge() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        final Elements tables = getLegalityDocumentElements(url, "div.mw-parser-output div + table.wikitable");
        for(Element table : tables) {
            String data = loadSmokingAgeData(table);
            if(isFirst) {
                data = data.substring(1);
            }
            builder.append(data);
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadSmokingAgeData(Element table) {
        final StringBuilder builder = new StringBuilder();
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

                    final String smokingAgeText = removeReferences(secondElement.text());
                    final CountryInfoValue smokingAge = new CountryInfoValue("Smoking Age", smokingAgeText, null);

                    final Element targetNoteElement = tds.get(isSameAge ? 2 : 3);
                    final String notes = getNotesFromElement(targetNoteElement);
                    final String purchasingAgeText;
                    if(isSameAge) {
                        purchasingAgeText = smokingAgeText;
                    } else {
                        final Element purchaseAge = tds.get(2);
                        purchasingAgeText = removeReferences(purchaseAge.text());
                    }
                    final CountryInfoValue purchasingAge = new CountryInfoValue("Purchasing Age", purchasingAgeText, null);

                    final CountryInfoKey info = new CountryInfoKey(notes, -1, smokingAge, purchasingAge);
                    info.country = country;
                    builder.append(",").append(info.toServerJSON());
                }
            }
        }
        return builder.toString();
    }
}
