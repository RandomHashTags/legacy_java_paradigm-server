package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryServiceCentralData;
import me.randomhashtags.worldlaws.service.WikipediaPicture;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface CountryNationalService extends NewCountryServiceCentralData {

    EventSources getSources();

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_NATIONAL;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.NATIONAL;
    }

    default Elements getNationalDocumentElements(String url, String targetElements) {
        return getNationalDocumentElements(url, targetElements, -1);
    }
    default Elements getNationalDocumentElements(String url, String targetElements, int index) {
        return getDocumentElements(getFolder(), url, targetElements, index);
    }

    @Override
    default JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(String country : json.keySet()) {
            final JSONObject countryJSON = json.getJSONObject(country);
            final CountrySingleValue value = CountrySingleValue.parse(countryJSON);
            final JSONObjectTranslatable valueJSON = value.toJSONObject();
            if(countryJSON.has("pictures")) {
                valueJSON.put("pictures", countryJSON.getJSONArray("pictures"), true);
            }
            translatable.put(country, valueJSON, true);
        }
        return translatable;
    }

    @Override
    default void insertCountryData(JSONObjectTranslatable dataJSON, JSONObjectTranslatable countryJSON) {
        final EventSources sources = getSources();
        countryJSON.put("sources", sources.toJSONObject());
    }


    default JSONObjectTranslatable getNationalArrayJSON(Elements elements, String defaultTitle) {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        final Elements trs = new Elements(elements);
        trs.removeIf(row -> row.select("td").isEmpty());
        for(int index = 0; index < trs.size(); index++) {
            final Element realElement = trs.get(index);
            final Elements tds = realElement.select("td");
            final int imageIndex = tds.size()-2;
            final Elements ahrefImages = tds.get(imageIndex).select("a.image");
            if(!ahrefImages.isEmpty()) {
                final Element firstElement = tds.get(0);
                final String country = firstElement.selectFirst("a[href]").text().toLowerCase().replace(" ", "");
                final JSONArrayTranslatable pictures = new JSONArrayTranslatable();

                final Element ahref = ahrefImages.get(0).selectFirst("a.image");
                final Element firstImage = ahref.selectFirst("img");
                final WikipediaPicture picture = getWikipediaPictureFromTable(tds.get(1), firstImage, defaultTitle);
                pictures.put(picture.toJSONObject());

                final int rowspan = firstElement.hasAttr("rowspan") ? Integer.parseInt(firstElement.attr("rowspan")) : 0;
                if(rowspan != 0) {
                    final int parentIndex = realElement.elementSiblingIndex();;
                    for(int i = 1; i < rowspan; i++) {
                        final Element targetElement = elements.get(parentIndex+i);
                        final Elements targetElementTDs = targetElement.select("td");
                        final Element targetHref = targetElementTDs.get(2).selectFirst("a.image");
                        if(targetHref != null) {
                            final WikipediaPicture value = getWikipediaPictureFromTable(targetElementTDs.get(0), targetHref.selectFirst("img"), defaultTitle);
                            pictures.put(value.toJSONObject());
                        }
                    }
                    index += rowspan-1;
                }
                final JSONObjectTranslatable countryJSON = new JSONObjectTranslatable("pictures");
                countryJSON.put("pictures", pictures);
                json.put(country, countryJSON, true);
            }
        }
        return json;
    }

    private WikipediaPicture getWikipediaPictureFromTable(Element nameElement, Element thumbnailElement, String defaultTitle) {
        final String text = nameElement.text();
        final boolean hasParenthesis = text.contains(" (");
        final String name = hasParenthesis ? text.split(" \\(")[0] : text;
        final String[] titleValues = hasParenthesis ? text.split("\\(")[1].split("\\)")[0].split(" ") : null;
        String title = null;
        if(hasParenthesis) {
            title = "";
            boolean isFirst = true;
            for(String value : titleValues) {
                if(!value.isEmpty()) {
                    final String string = value.equalsIgnoreCase("of") ? value : value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
                    title = title.concat(isFirst ? "" : " ").concat(string);
                    isFirst = false;
                }
            }
            if(title.isEmpty()) {
                title = null;
            }
        } else {
            title = defaultTitle;
        }
        final String href = "https:" + thumbnailElement.attr("src");
        final String[] values = href.split("/");
        final String suffix = values[values.length-1];
        final String resolution = suffix.split("px-")[0], imageURL = href.replace("/" + resolution + "px-", "/%quality%px-");
        return new WikipediaPicture(name, title, imageURL);
    }
}
