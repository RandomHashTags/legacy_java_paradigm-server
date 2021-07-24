package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.WikipediaPicture;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import me.randomhashtags.worldlaws.location.SovereignStateInformationType;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Optional;

public enum Wikipedia implements CountryService {
    INSTANCE;

    private HashMap<String, String> countries;
    private Elements featuredPicturesElements, nationalAnimalsElements;

    @Override
    public Folder getFolder() {
        return Folder.COUNTRIES_SERVICES_WIKIPEDIA;
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
        if(countries == null) {
            countries = new HashMap<>();
        }
        if(countries.containsKey(tag)) {
            handler.handleString(countries.get(tag));
        } else {
            final long started = System.currentTimeMillis();
            getJSONObject(Folder.COUNTRIES_SERVICES_WIKIPEDIA, tag, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    loadWikipedia(tag, handler);
                }

                @Override
                public void handleJSONObject(JSONObject object) {
                    final String string = new CountryServiceValue(Wikipedia.INSTANCE, object.toString()).toString();
                    WLLogger.log(Level.INFO, getInfo().name() + " - loaded \"" + tag + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    countries.put(tag, string);
                    handler.handleString(string);
                }
            });
        }
    }

    private void loadWikipedia(String tag, CompletionHandler handler) {
        final String url = "https://en.wikipedia.org/wiki/" + tag.replace(" ", "_");
        final Folder folder = getFolder();
        Document document = null;
        try {
            document = Jsoupable.getLocalDocument(folder, url);
            if(document == null) {
                document = getDocument(folder, url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.handleString(null);
            return;
        }
        final Elements infoboxParagraphs = document.select("div.mw-parser-output table.infobox + p");
        final Elements metadataParagraphs = document.select("div.mw-parser-output table.metadata + p");
        final Element element = !infoboxParagraphs.isEmpty() ? infoboxParagraphs.get(0) : !metadataParagraphs.isEmpty() ? metadataParagraphs.get(0) : null;
        if(element == null) {
            WLLogger.log(Level.WARN, "Wikipedia - missing paragraph for country \"" + tag + "\"!");
            handler.handleString(null);
        } else {
            String firstParagraph = removeReferences(element.text()).replace("(listen)", "").replace("(listen to all)", "");
            firstParagraph = LocalServer.removeWikipediaTranslations(firstParagraph);
            final String paragraph = LocalServer.fixEscapeValues(firstParagraph);
            getPictures(tag, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String value =
                            "{" +
                            "\"pictures\":" + string + "," +
                            "\"paragraph\":\"" + paragraph + "\"," +
                            "\"url\":\"" + url + "\"" +
                            "}";
                    handler.handleString(value);
                }
            });
        }
    }

    private void getPictures(String tag, CompletionHandler handler) {
        final StringBuilder builder = new StringBuilder("{");
        getNationalAnimals(tag, new CompletionHandler() {
            @Override
            public void handleString(String nationalAnimals) {
                getFeaturedPictures(tag, new CompletionHandler() {
                    @Override
                    public void handleString(String featured) {
                        final String string =
                                (nationalAnimals != null ? "\"nationalAnimal\":" + nationalAnimals + (featured != null ? "," : "") : "") +
                                (featured != null ? "\"featured\":" + featured : "");
                        builder.append(string);
                        builder.append("}");
                        handler.handleString(builder.toString());
                    }
                });
            }
        });
    }

    private void getFeaturedPictures(String tag, CompletionHandler handler) {
        getJSONObject(Folder.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES, tag, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final String country = tag.toLowerCase();
                final Elements featuredElements = getFeaturedPicturesElements();
                final Optional<Element> target = new Elements(featuredElements).parallelStream().filter(element -> {
                    return element.text().toLowerCase().endsWith(country);
                }).findFirst();
                if(target.isPresent()) {
                    final StringBuilder builder = new StringBuilder("{");
                    final String prefix = "https://commons.wikimedia.org", url = prefix + target.get().attr("href");
                    final Elements pictures = getDocumentElements(Folder.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES, url, "div.mw-body div.mw-body-content div.mw-content-ltr div.mw-category-generated div ul.gallery li.gallerybox div div.thumb div a.image");
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
                                final String pictureURL = img.attr("src").replaceAll("/[0-9]+px-", "/%quality%px-");
                                if(!prefix.equals(pictureURL)) {
                                    /*final String mediaURL = prefix + "/wiki/File:" + imageTitle;
                                    final Elements descriptions = getDocumentElements(FileType.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES_MEDIA, mediaURL, "td.description");
                                    if(!descriptions.isEmpty()) {
                                        final Elements elements = descriptions.get(0).getAllElements(), descriptionElements = elements.select("div.description");
                                        final String text = (descriptionElements.isEmpty() ? elements : descriptionElements).textNodes().get(0).text();
                                    }*/
                                    final WikipediaPicture wikipediaPicture = new WikipediaPicture(imageTitle, null, pictureURL, "https://commons.wikimedia.org" + href);
                                    builder.append(isFirst ? "" : ",").append(wikipediaPicture);
                                    isFirst = false;
                                    added += 1;
                                }
                            }
                        }
                    }
                    builder.append("}");
                    handler.handleString(builder.toString());
                } else {
                    WLLogger.log(Level.WARN, "Wikipedia - missing Featured Pictures for country \"" + country + "\"");
                    handler.handleString("{}");
                }
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(json.toString());
            }
        });
    }
    private Elements getFeaturedPicturesElements() {
        if(featuredPicturesElements == null) {
            featuredPicturesElements = getDocumentElements(Folder.COUNTRIES_SERVICES_WIKIPEDIA_FEATURED_PICTURES, "https://commons.wikimedia.org/wiki/Category:Featured_pictures_by_country", "div.mw-body div.mw-body-content div.mw-content-ltr div.mw-category-generated div div.mw-content-ltr div.mw-category div.mw-category-group ul li a");
        }
        return featuredPicturesElements;
    }
    private Elements getNationalAnimalsElements() {
        if(nationalAnimalsElements == null) {
            nationalAnimalsElements = getDocumentElements(Folder.COUNTRIES_SERVICES_WIKIPEDIA, "https://en.wikipedia.org/wiki/List_of_national_animals", "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        }
        return nationalAnimalsElements;
    }

    private void getNationalAnimals(String tag, CompletionHandler animalHandler) {
        getJSONObject(Folder.COUNTRIES_SERVICES_WIKIPEDIA, "_National Animals", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final Elements nationalAnimalsElements = getNationalAnimalsElements();
                final Elements trs = new Elements(nationalAnimalsElements);
                trs.removeIf(row -> row.select("td").isEmpty());

                final String prefix = "https://en.wikipedia.org";
                final StringBuilder builder = new StringBuilder("{");
                boolean isFirst = true;
                for(int index = 0; index < trs.size(); index++) {
                    final Element realElement = trs.get(index);
                    final Elements tds = realElement.select("td");
                    final Element firstElement = tds.get(0);
                    final int rowspan = firstElement.hasAttr("rowspan") ? Integer.parseInt(firstElement.attr("rowspan")) : 0;
                    final String country = firstElement.selectFirst("a").text();
                    final StringBuilder animalBuilder = new StringBuilder("\"" + country + "\":{");

                    final Element ahref = tds.get(3).selectFirst("a.image");
                    final Element firstImage = ahref.selectFirst("img");
                    final WikipediaPicture animal = getNationalAnimal(tds.get(1), firstImage, prefix + ahref.attr("href"));
                    animalBuilder.append(animal);

                    if(rowspan != 0) {
                        final int nationalAnimalIndex = nationalAnimalsElements.indexOf(realElement);
                        for(int i = 1; i < rowspan; i++) {
                            final Element targetElement = nationalAnimalsElements.get(nationalAnimalIndex+i);
                            final Elements targetElementTDs = targetElement.select("td");
                            final Element targetHref = targetElementTDs.get(2).selectFirst("a.image");
                            final WikipediaPicture value = getNationalAnimal(targetElementTDs.get(0), targetHref.selectFirst("img"), prefix + targetHref.attr("href"));
                            animalBuilder.append(",").append(value);
                        }
                        index += rowspan-1;
                    }
                    animalBuilder.append("}");

                    builder.append(isFirst ? "" : ",").append(animalBuilder);
                    isFirst = false;
                }
                builder.append("}");
                handler.handleString(builder.toString());
            }

            @Override
            public void handleJSONObject(JSONObject object) {
                final JSONObject json = object.has(tag) ? object.getJSONObject(tag) : null;
                if(json != null) {
                    animalHandler.handleString(json.toString());
                } else {
                    WLLogger.log(Level.WARN, "Wikipedia - missing National Animals for country \"" + tag + "\"!");
                    animalHandler.handleString("{}");
                }
            }
        });
    }
    private WikipediaPicture getNationalAnimal(Element nameElement, Element thumbnailElement, String url) {
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
            title = "National Animal";
        }
        final String href = "https:" + thumbnailElement.attr("src");
        final String[] values = href.split("/");
        final String suffix = "/" + values[values.length-1], imageURL = href.replace("thumb/", "").split(suffix)[0];
        return new WikipediaPicture(name, title, imageURL, url);
    }
}
