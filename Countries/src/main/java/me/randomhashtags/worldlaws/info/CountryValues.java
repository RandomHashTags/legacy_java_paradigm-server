package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;

public enum CountryValues implements CountryValueService {
    HEALTH_CARE_SYSTEM(
            "https://en.wikipedia.org/wiki/Health_care_systems_by_country",
            2019
    ),
    MILITARY_ENLISTMENT_AGE(
            "https://en.wikipedia.org/wiki/List_of_enlistment_age_by_country",
            2020
    ),
    MINIMUM_DRIVING_AGE(
            "https://en.wikipedia.org/wiki/List_of_minimum_driving_ages",
            -1
    ),
    SYSTEM_OF_GOVERNMENT(
            "https://en.wikipedia.org/wiki/List_of_countries_by_system_of_government",
            -1
    ),
    TRAFFIC_SIDE(
            "https://en.wikipedia.org/wiki/Left-_and_right-hand_traffic",
            WLUtilities.getTodayYear()
    ),
    VOTING_AGE(
            "https://aceproject.org/epic-en/CDTable?view=country&question=VR001",
            -1
    ),
    ;

    private final String url;
    private final int yearOfData;

    CountryValues(String url, int yearOfData) {
        this.url = url;
        this.yearOfData = yearOfData;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.valueOf("VALUE_" + name());
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
    public String loadData() {
        switch (this) {
            case HEALTH_CARE_SYSTEM: return loadHealthCareSystem();
            case MILITARY_ENLISTMENT_AGE: return loadMilitaryEnlistmentAge();
            case MINIMUM_DRIVING_AGE: return loadMinimumDrivingAge();
            case SYSTEM_OF_GOVERNMENT: return loadSystemOfGovernment();
            case TRAFFIC_SIDE: return loadTrafficSide();
            case VOTING_AGE: return loadVotingAge();
            default: return null;
        }
    }

    private String loadHealthCareSystem() {
        final Element output = getValueDocumentElements(url, "div.mw-parser-output").get(0);
        final Elements trs = output.select("div.div-col");
        for(int i = 0; i < trs.size()-5; i++) {
            trs.remove(5);
        }
        final HashMap<Integer, String> types = new HashMap<>() {{
            put(0, "Universal Government-Funded");
            put(1, "Universal Public Insurance");
            put(2, "Universal Public-Private Insurance");
            put(3, "Universal Private Insurance");
            put(4, "Non-Universal Insurance");
        }};
        final HashMap<Integer, String> notes = new HashMap<>();
        int index = 0;
        final Elements allElements = output.getAllElements();;
        for(Element tr : trs) {
            final int indexOf = allElements.indexOf(tr);
            final Element three = allElements.get(indexOf-3);
            notes.put(index, (three.tagName().equals("p") ? three : allElements.get(indexOf-2)).text().replace(":", "."));
            index += 1;
        }
        index = 0;

        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements list = element.select("ul li");
            final String type = types.get(index), note = notes.get(index);
            for(Element li : list) {
                final List<TextNode> textNodes = li.textNodes();
                String description = textNodes.size() != 1 ? null : textNodes.get(0).text().split("\\)")[0];
                if(description != null && description.contains("(")) {
                    description = description.split("\\(")[1];
                }
                final CountrySingleValue value = new CountrySingleValue(note, type, description, -1);
                for(Element hrefElement : li.select("a")) {
                    final String href = hrefElement.attr("href");
                    if(!href.startsWith("#cite_note")) {
                        value.country = hrefElement.text().toLowerCase().split("\\[")[0].split("\\(")[0].replace(" ", "");
                        builder.append(isFirst ? "" : ",").append(value.toServerJSON());
                        isFirst = false;
                    }
                }
            }
            index += 1;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMilitaryEnlistmentAge() {
        final Elements lists = getValueDocumentElements(url, "h2 + ul li");
        lists.remove(lists.size()-1);
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : lists) {
            final String country = element.select("a").get(0).text().toLowerCase().replace(" ", "");
            final int substring = country.length() + 3;
            final String text = removeReferences(element.text().substring(substring));
            final CountrySingleValue value = new CountrySingleValue(null, text, null, -1);
            value.country = country;
            builder.append(isFirst ? "" : ",").append(value.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMinimumDrivingAge() {
        final Elements tables = getValueDocumentElements(url, "div.mw-parser-output table.wikitable");
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element table : tables) {
            String data = loadMinimumDrivingAgeData(table);
            if(isFirst) {
                data = data.substring(1);
            }
            builder.append(data);
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadMinimumDrivingAgeData(Element table) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);
        final StringBuilder builder = new StringBuilder();
        for(Element tr : trs) {
            final Elements tds = tr.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "");
            final String age = removeReferences(tds.get(1).text()), notes = getNotesFromElement(tds.get(2));
            final CountrySingleValue value = new CountrySingleValue(notes, age, null, -1);
            value.country = country;
            builder.append(",").append(value.toServerJSON());
        }
        return builder.toString();
    }
    private String loadSystemOfGovernment() {
        final Elements tables = getValueDocumentElements(url, "div.mw-parser-output table.wikitable");

        final HashMap<String, String> styles = new HashMap<>();
        styles.put("background:#CCDDEE", "Presidential Republic");
        styles.put("background:#FFFF66", "Semi-Presidential Republic");
        styles.put("background:#99FF99", "Republic with an executive presidency nominated by or elected by the legislature");
        styles.put("background:#FFD383", "Parliamentary Republic with a Ceremonial Presidency");
        styles.put("background:#FFD0EE", "Constitutional Monarchy");
        styles.put("background:#FFC0AA", "Constitutional Parliamentary Monarchy");
        styles.put("background:#DDAADD", "Absolute Monarchy");
        styles.put("background:#DDAA77", "One-party state");
        styles.put("background:#EEDDC3", "Unknown");

        final HashMap<String, String> styleDescriptions = new HashMap<>();
        styleDescriptions.put("Presidential Republic", "Head of state is also head of government and is independent of legislature");
        styleDescriptions.put("Semi-Presidential Republic", "Head of state has some executive powers and is independent of legislature; remaining executive power is vested in ministry that is subject to parliamentary confidence");
        styleDescriptions.put("Republic with an executive presidency nominated by or elected by the legislature", "President is both head of state and government; ministry, including the president, may or may not be subject to parliamentary confidence");
        styleDescriptions.put("Parliamentary Republic with a Ceremonial Presidency", "Head of state is ceremonial; ministry is subject to parliamentary confidence");
        styleDescriptions.put("Constitutional Monarchy", "Head of state is executive; Monarch personally exercises power in concert with other institutions");
        styleDescriptions.put("Constitutional Parliamentary Monarchy", "Head of state is ceremonial; ministry is subject to parliamentary confidence");
        styleDescriptions.put("Absolute Monarchy", "Head of state is executive; all authority vested in absolute monarch");
        styleDescriptions.put("One-party state", "Head of state is executive or ceremonial; power constitutionally linked to a single political movement");

        final StringBuilder builder = new StringBuilder("[");
        for(int i = 0; i <= 2; i++) {
            String data = loadSystemOfGovernmentData(tables.get(i), styles, styleDescriptions);
            if(i == 0) {
                data = data.substring(1);
            }
            builder.append(data);
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadSystemOfGovernmentData(Element table, HashMap<String, String> styles, HashMap<String, String> styleDescriptions) {
        final Elements trs = table.select("tbody tr");
        trs.remove(0);

        final StringBuilder builder = new StringBuilder();
        for(Element element : trs) {
            final String style = styles.getOrDefault(element.attr("style"), "Unknown"), styleDescription = styleDescriptions.getOrDefault(style, "Unknown");
            final Elements tds = element.select("td");
            final String country = tds.get(0).text().toLowerCase().replace(",", "").replace(" ", "").split("people'srepublicof")[0];
            final String notes = getNotesFromElement(tds.get(3));
            final CountrySingleValue value = new CountrySingleValue(notes, style, styleDescription, -1);
            value.country = country;
            builder.append(",").append(value.toServerJSON());
        }
        return builder.toString();
    }
    private String loadTrafficSide() {
        final Elements trs = getValueDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        String previousCountry = null;
        int rowspanCount = 0;
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final Element firstElement = tds.get(0), notesElement = tds.get(tds.size()-1);
            final String notes = getNotesFromElement(notesElement), secondElementText = tds.get(1).text();
            final Elements links = firstElement.select("a");
            final String data;
            if(rowspanCount != 0) {
                rowspanCount -= 1;
                final boolean isMainland = links.size() == 0 && firstElement.text().equalsIgnoreCase("Mainland");
                final String country = isMainland ? previousCountry : links.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "");
                data = loadTrafficSideData(country, secondElementText, notes);
            } else {
                final String country = links.get(0).text().toLowerCase().split("\\(")[0].replace(" ", "").replace(",", "").replace(".", "");
                if(firstElement.hasAttr("rowspan")) {
                    rowspanCount = Integer.parseInt(firstElement.attr("rowspan"));
                    if(secondElementText.equalsIgnoreCase("Mainland")) {
                        previousCountry = country;
                    }
                    data = loadTrafficSideData(country, tds.get(2).text(), notes);
                } else {
                    data = loadTrafficSideData(country, secondElementText, notes);
                }
            }
            builder.append(isFirst ? "" : ",").append(data);
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String loadTrafficSideData(String country, String value, String notes) {
        final String realValue = value.startsWith("RHT") ? "Right" : "Left";
        final CountrySingleValue singleValue = new CountrySingleValue(notes, realValue, null, -1);
        singleValue.country = country;
        return singleValue.toServerJSON();
    }
    private String loadVotingAge() {
        // https://en.wikipedia.org/wiki/Voting_age
        final Elements trs = getValueDocumentElements(url, "div.documentContent table.CDlisting tbody tr");
        trs.remove(0);
        trs.remove(0);

        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final String country = tds.get(0).select("a").get(0).text().toLowerCase().replace(" ", "").replace(",", "").split("\\(")[0]
                    .replace("korearepublicof", "northkorea")
                    .replace("koreademocraticpeople'srepublicof", "southkorea")
                    .replace("burma", "myanmar")
                    .replace("islamic", "")
                    .replace("republicof", "")
                    .replace("federatedstatesof", "")
                    .replace("theformeryugoslav", "")
                    .replace("unitedstatesofamerica", "unitedstates")
                    .replace("tanzaniaunited", "tanzania")
                    .replace("russianfederation", "russia")
                    .replace("netherlandsantilles", "netherlands")
                    .replace("unitedkingdomofgreatbritainandnorthernireland", "unitedkingdom")
                    ;
            final int yearOfData = Integer.parseInt(tds.get(3).text().split("/")[0]);
            final String value = removeReferences(tds.get(1).text().substring(3));
            String valueDescription = tds.get(2).text();
            if(valueDescription.contains(" Source:")) {
                valueDescription = valueDescription.split(" Source:")[0];
            }
            final CountrySingleValue singleValue = new CountrySingleValue(null, value, valueDescription, yearOfData);
            singleValue.country = country;
            builder.append(isFirst ? "" : ",").append(singleValue.toServerJSON());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
}
