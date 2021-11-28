package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class WikipediaCountryService implements CountryService {

    private final Folder folder, wikiFolder, featuredPicturesFolder;
    private HashMap<String, String> sovereignStates;
    private Elements featuredPicturesElements, nationalAnimalsElements;

    public WikipediaCountryService(boolean isCountries) {
        this.folder = isCountries ? Folder.COUNTRIES_SERVICES_WIKIPEDIA : Folder.COUNTRIES_SUBDIVISIONS_SERVICES_WIKIPEDIA;
        this.wikiFolder = isCountries ? Folder.COUNTRIES_WIKIPEDIA_PAGES : Folder.COUNTRIES_SUBDIVISIONS_WIKIPEDIA_PAGES;
        this.featuredPicturesFolder = isCountries ? Folder.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES : null;
    }

    @Override
    public Folder getFolder() {
        return folder;
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_WIKIPEDIA;
    }

    @Override
    public void loadData(CompletionHandler handler) {
    }

    @Override
    public void getCountryValue(String tag, CompletionHandler handler) {
        if(sovereignStates == null) {
            sovereignStates = new HashMap<>();
        }
        final WikipediaCountryService self = this;
        if(sovereignStates.containsKey(tag)) {
            handler.handleServiceResponse(self, sovereignStates.get(tag));
        } else {
            final long started = System.currentTimeMillis();
            getJSONObject(folder, tag, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadWikipedia(tag, handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final String string = json != null ? new CountryServiceValue(self, json.toString()).toString() : null;
                    WLLogger.logInfo(getInfo().name() + " - loaded \"" + tag + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    sovereignStates.put(tag, string);
                    handler.handleServiceResponse(self, string);
                }
            });
        }
    }

    private void loadWikipedia(String tag, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/" + tag.replace(" ", "_");
        Document document = Jsoupable.getLocalDocument(wikiFolder, url);
        if(document == null) {
            document = getDocument(wikiFolder, url, true);
            if(document == null) {
                handler.handleString(null);
                return;
            }
        }
        final WikipediaDocument wikiDoc = new WikipediaDocument(url, document);
        final List<Element> paragraphs = wikiDoc.getConsecutiveParagraphs();
        if(paragraphs != null && !paragraphs.isEmpty()) {
            String firstParagraph = removeReferences(paragraphs.get(0).text()).replace(" (listen)", "").replace("(listen)", "").replace(" (listen to all)", "").replace("(listen to all)", "");
            firstParagraph = LocalServer.removeWikipediaTranslations(firstParagraph);
            final String paragraph = LocalServer.fixEscapeValues(firstParagraph);
            getPictures(tag, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String value =
                            "{" +
                            (string != null ? "\"pictures\":" + string + "," : "") +
                            "\"paragraph\":\"" + paragraph + "\"," +
                            "\"url\":\"" + url + "\"" +
                            "}";
                    handler.handleString(value);
                }
            });
        } else {
            WLLogger.logError(this, "missing paragraph for country \"" + tag + "\"!");
            handler.handleString(null);
        }
    }

    private void getPictures(String tag, CompletionHandler handler) {
        if(featuredPicturesFolder != null) {
            final String[] types = new String[] { "nationalAnimal", "nationalTree", "featured" };
            final int max = types.length;
            final AtomicInteger completed = new AtomicInteger(0);
            final HashSet<String> values = new HashSet<>();
            Arrays.stream(types).parallel().forEach(type -> {
                getPictures(type, tag, new CompletionHandler() {
                    @Override
                    public void handleJSONObject(JSONObject json) {
                        handleString(json != null ? json.toString() : null);
                    }

                    @Override
                    public void handleString(String string) {
                        if(string != null) {
                            values.add("\"" + type + "\":" + string);
                        }
                        if(completed.addAndGet(1) == max) {
                            String value = null;
                            if(!values.isEmpty()) {
                                final StringBuilder builder = new StringBuilder("{");
                                boolean isFirst = true;
                                for(String valueString : values) {
                                    builder.append(isFirst ? "" : ",").append(valueString);
                                    isFirst = false;
                                }
                                builder.append("}");
                                value = builder.toString();
                            }
                            handler.handleString(value);
                        }
                    }
                });
            });
        } else {
            handler.handleString(null);
        }
    }
    private void getPictures(String type, String tag, CompletionHandler handler) {
        switch (type) {
            case "nationalAnimal":
            case "nationalTree":
                final WikipediaPictureType pictureType = WikipediaPictureType.valueOf(type.substring("national".length()).toUpperCase() + "S");
                loadFromTable(pictureType, tag, handler);
                break;
            case "featured":
                getFeaturedPictures(tag, handler);
                break;
            default:
                break;
        }
    }

    private void getFeaturedPictures(String tag, CompletionHandler handler) {
        getJSONObject(featuredPicturesFolder, tag, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String country = tag.toLowerCase();
                final Elements featuredElements = getFeaturedPicturesElements();
                final Optional<Element> target = new Elements(featuredElements).parallelStream().filter(element -> element.text().toLowerCase().endsWith(country)).findFirst();
                if(target.isPresent()) {
                    final StringBuilder builder = new StringBuilder("{");
                    final String prefix = "https://commons.wikimedia.org", url = prefix + target.get().attr("href");
                    final Elements pictures = getDocumentElements(featuredPicturesFolder, url, "div.mw-body div.mw-body-content div.mw-content-ltr div.mw-category-generated div ul.gallery li.gallerybox div div.thumb div a.image");
                    boolean isFirst = true;
                    int added = 0;
                    for(Element picture : pictures) {
                        if(added == 12) {
                            break;
                        } else {
                            final String href = picture.attr("href");
                            if(!href.isEmpty()) {
                                final Element img = picture.selectFirst("img");
                                final String imageTitle = img.attr("alt").replace(" ", "_");
                                final String pictureURL = WikipediaService.getPictureThumbnailImageURL(img);
                                if(!prefix.equals(pictureURL)) {
                                    /*final String mediaURL = prefix + "/wiki/File:" + imageTitle;
                                    final Elements descriptions = getDocumentElements(FileType.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA, mediaURL, "td.description");
                                    if(!descriptions.isEmpty()) {
                                        final Elements elements = descriptions.get(0).getAllElements(), descriptionElements = elements.select("div.description");
                                        final String text = (descriptionElements.isEmpty() ? elements : descriptionElements).textNodes().get(0).text();
                                    }*/
                                    final WikipediaPicture wikipediaPicture = new WikipediaPicture(imageTitle, null, pictureURL);
                                    builder.append(isFirst ? "" : ",").append(wikipediaPicture.toString());
                                    isFirst = false;
                                    added += 1;
                                }
                            }
                        }
                    }
                    builder.append("}");
                    handler.handleString(builder.toString());
                } else {
                    handler.handleString(null);
                }
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
            }
        });
    }
    private Elements getFeaturedPicturesElements() {
        if(featuredPicturesElements == null) {
            featuredPicturesElements = getDocumentElements(featuredPicturesFolder, "https://commons.wikimedia.org/wiki/Category:Featured_pictures_by_country", "div.mw-body div.mw-body-content div.mw-content-ltr div.mw-category-generated div div.mw-content-ltr div.mw-category div.mw-category-group ul li a");
        }
        return featuredPicturesElements;
    }

    private void loadFromTable(WikipediaPictureType type, String tag, CompletionHandler completionHandler) {
        final String fileName = type.getFileName();
        getJSONObject(folder, fileName, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements elements = type.getElements(folder);
                final Elements trs = new Elements(elements);
                trs.removeIf(row -> row.select("td").isEmpty());

                final String prefix = "https://en.wikipedia.org", defaultTitle = type.getDefaultTitle();
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(int index = 0; index < trs.size(); index++) {
                    final Element realElement = trs.get(index);
                    final Elements tds = realElement.select("td");
                    final Elements ahrefImages = tds.get(3).select("a.image");
                    if(!ahrefImages.isEmpty()) {
                        final Element firstElement = tds.get(0);
                        final String country = firstElement.selectFirst("a").text();
                        final StringBuilder pictureBuilder = new StringBuilder("\"" + country + "\":{");

                        final Element ahref = ahrefImages.get(0).selectFirst("a.image");
                        final Element firstImage = ahref.selectFirst("img");
                        final WikipediaPicture picture = getWikipediaPictureFromTable(tds.get(1), firstImage, defaultTitle);
                        pictureBuilder.append(picture);

                        final int rowspan = firstElement.hasAttr("rowspan") ? Integer.parseInt(firstElement.attr("rowspan")) : 0;
                        if(rowspan != 0) {
                            final int parentIndex = realElement.elementSiblingIndex();;
                            for(int i = 1; i < rowspan; i++) {
                                final Element targetElement = elements.get(parentIndex+i);
                                final Elements targetElementTDs = targetElement.select("td");
                                final Element targetHref = targetElementTDs.get(2).selectFirst("a.image");
                                final WikipediaPicture value = getWikipediaPictureFromTable(targetElementTDs.get(0), targetHref.selectFirst("img"), defaultTitle);
                                pictureBuilder.append(",").append(value);
                            }
                            index += rowspan-1;
                        }
                        pictureBuilder.append("}");

                        builder.append(isFirst ? "" : ",").append(pictureBuilder);
                        isFirst = false;
                    }
                }
                builder.append("}");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONObject(JSONObject object) {
                final JSONObject json = object.has(tag) ? object.getJSONObject(tag) : null;
                completionHandler.handleJSONObject(json);
            }
        });
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
        final String suffix = "/" + values[values.length-1], imageURL = href.replace("thumb/", "").split(suffix)[0];
        return new WikipediaPicture(name, title, imageURL);
    }

    private enum WikipediaPictureType implements Jsoupable {
        ANIMALS,
        //DISH, // https://en.wikipedia.org/wiki/National_dish
        //SPORT, // https://en.wikipedia.org/wiki/National_sport
        TREES,
        ;

        private static final HashMap<WikipediaPictureType, Elements> CACHE_ELEMENTS = new HashMap<>();

        public String getDefaultTitle() {
            switch (this) {
                case ANIMALS: return "National Animal";
                default: return null;
            }
        }
        public String getFileName() {
            switch (this) {
                case ANIMALS: return "_National Animals";
                case TREES: return "_National Trees";
                default: return null;
            }
        }

        public Elements getElements(Folder folder) {
            if(CACHE_ELEMENTS.containsKey(this)) {
                return CACHE_ELEMENTS.get(this);
            }
            final Elements elements;
            switch (this) {
                case ANIMALS:
                    elements = getDocumentElements(folder, "https://en.wikipedia.org/wiki/List_of_national_animals", "div.mw-parser-output table.wikitable", 0).select("tbody tr");
                    break;
                case TREES:
                    elements = getDocumentElements(folder, "https://en.wikipedia.org/wiki/List_of_national_trees", "div.mw-parser-output table.wikitable", 0).select("tbody tr");
                    break;
                default:
                    return null;
            }
            CACHE_ELEMENTS.put(this, elements);
            return elements;
        }
    }
}
