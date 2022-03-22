package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryService;
import me.randomhashtags.worldlaws.service.WikipediaPicture;
import me.randomhashtags.worldlaws.service.WikipediaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public enum WikipediaFeaturedPictures implements NewCountryService {
    INSTANCE;

    @Override
    public Folder getFolder() {
        return Folder.COUNTRIES_WIKIPEDIA_FEATURED_PICTURES;
    }

    @Override
    public String getServiceFileName(WLCountry country) {
        return country.getBackendID();
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_STATIC;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_WIKIPEDIA_FEATURED_PICTURES;
    }

    @Override
    public JSONObjectTranslatable loadData() {
        return null;
    }

    @Override
    public JSONObjectTranslatable loadData(WLCountry country) {
        return loadFeaturedPictures(country);
    }

    @Override
    public JSONObjectTranslatable getJSONObject(WLCountry country) {
        return getJSONObject(country, true);
    }

    @Override
    public JSONObjectTranslatable parseData(JSONObject json) {
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        if(json.has("pictures")) {
            final JSONArray array = new JSONArray();
            final JSONArray picturesArray = json.getJSONArray("pictures");
            for(Object obj : picturesArray) {
                final JSONObject pictureJSON = (JSONObject) obj;
                final WikipediaPicture picture = WikipediaPicture.parse(pictureJSON);
                array.put(picture.toJSONObject());
            }
            translatable.put("pictures", array);
            translatable.put("imageURLPrefix", getImageURLPrefix());
        }
        return translatable;
    }

    private String getImageURLPrefix() {
        return "https://upload.wikimedia.org/wikipedia/commons/thumb/";
    }

    @Override
    public void insertCountryData(JSONObjectTranslatable countryJSON, WLCountry country) {
        final String shortName = country.getShortName();
        final String url = "https://commons.wikimedia.org/wiki/Category:Featured_pictures_of_" + shortName.replace(" ", "_");
        final EventSources sources = new EventSources(
                new EventSource("Wikimedia: Featured pictures of " + shortName, url)
        );
        countryJSON.put("sources", sources.toJSONObject());
    }

    private JSONObjectTranslatable loadFeaturedPictures(WLCountry country) {
        final String shortName = country.getShortName();
        final Folder folder = getFolder();
        final JSONObjectTranslatable json = new JSONObjectTranslatable("pictures");

        final Elements featuredElements = getDocumentElements(folder, "https://commons.wikimedia.org/wiki/Category:Featured_pictures_by_country", "li div.CategoryTreeItem a");
        Element target = null;
        for(Element element : featuredElements) {
            if(element.text().endsWith(shortName)) {
                target = element;
                break;
            }
        }
        if(target != null) {
            final String prefix = "https://commons.wikimedia.org", url = prefix + target.attr("href"), imageURLPrefix = getImageURLPrefix();
            final Elements pictureElements = getDocumentElements(folder, url, "ul.gallery li.gallerybox div div.thumb div a.image");
            if(!pictureElements.isEmpty()) {
                final JSONArrayTranslatable pictures = new JSONArrayTranslatable();
                int added = 0;
                for(Element picture : pictureElements) {
                    if(added == 20) {
                        break;
                    } else {
                        final String href = picture.attr("href");
                        if(!href.isEmpty()) {
                            final Element img = picture.selectFirst("img");
                            if(img != null) {
                                String pictureURL = WikipediaService.getPictureThumbnailImageURL(img);
                                if(!prefix.equals(pictureURL)) {
                                    if(pictureURL.startsWith(imageURLPrefix)) {
                                        pictureURL = pictureURL.substring(imageURLPrefix.length());
                                    }
                                    final String imageTitle = img.attr("alt");
                                    /*final String mediaURL = prefix + "/wiki/File:" + imageTitle;
                                    final Elements descriptions = getDocumentElements(FileType.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA, mediaURL, "td.description");
                                    if(!descriptions.isEmpty()) {
                                        final Elements elements = descriptions.get(0).getAllElements(), descriptionElements = elements.select("div.description");
                                        final String text = (descriptionElements.isEmpty() ? elements : descriptionElements).textNodes().get(0).text();
                                    }*/
                                    final WikipediaPicture wikipediaPicture = new WikipediaPicture(imageTitle, null, pictureURL);
                                    pictures.put(wikipediaPicture.toJSONObject());
                                    added += 1;
                                }
                            }
                        }
                    }
                }
                json.put("pictures", pictures);
                json.put("imageURLPrefix", imageURLPrefix);
            }
        }
        return json.isEmpty() ? null : json;
    }
}
