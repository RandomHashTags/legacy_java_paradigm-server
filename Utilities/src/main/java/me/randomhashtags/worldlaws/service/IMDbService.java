package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.WLUtilities;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface IMDbService extends DataValues {

    default JSONObject getIMDbMovieDetails(String title, int year) {
        final String lowercaseTitle = title.toLowerCase();
        final String url = "https://www.imdb.com/find?q=" + lowercaseTitle.replace(" ", "+");
        final Elements elements = Jsoupable.getStaticDocumentElements(Folder.OTHER, url, false, "div div.redesign div.pagecontent div div div.article div.findSection table.findList tr.findResult");
        JSONObject json = null;
        if(elements != null) {
            for(Element element : elements) {
                final Element resultText = element.selectFirst("td.result_text");
                if(resultText != null) {
                    final String text = resultText.text().toLowerCase();
                    if(hasMovieTitle(lowercaseTitle, year, text)) {
                        final String id = resultText.selectFirst("a[href]").attr("href").split("/")[2];
                        json = getMovie(id);
                        break;
                    }
                }
            }
        }
        return json;
    }
    private boolean hasMovieTitle(String title, int year, String text) {
        if(text.contains("(" + year + ")") && !text.contains("(video game)") && !text.contains("(tv series)")) {
            return text.contains(title)
                    || text.contains(title.replace("part one", "part 1"))
                    || text.contains(title.replace("part two", "part 2"))
                    || text.contains(title.replace("part three", "part 3"))
                    || text.contains(title.replace("part 1", "part one"))
                    || text.contains(title.replace("part 2", "part two"))
                    || text.contains(title.replace("part 3", "part three"));
        }
        return false;
    }
    private JSONObject getMovie(String id) {
        final String url = "https://www.imdb.com/title/" + id + "/";
        final Document doc = Jsoupable.getStaticDocument(Folder.OTHER, url, false);
        JSONObject json = null;
        if(doc != null) {
            //final String title = doc.selectFirst("h1.TitleHeader__TitleText-sc-1wu6n3d-0").text();
            final Element listElement = doc.selectFirst("ul.ipc-inline-list");
            if(listElement != null) {
                final Elements list = listElement.select("li.ipc-inline-list__item");
                if(list.size() >= 3) {
                    final String rating = list.get(1).selectFirst("a[href]").text();
                    int runtimeSeconds = 0;
                    final String[] runtimeValues = list.get(2).text().split(" ");
                    for(String string : runtimeValues) {
                        string = string.toLowerCase();
                        final boolean isHours = string.endsWith("h");
                        final String target = string.substring(0, string.length()-1);
                        if(target.matches("[0-9]+")) {
                            final int value = Integer.parseInt(target);
                            runtimeSeconds += value * 60 * (isHours ? 60 : 1);
                        }
                    }

                    final String[] images = doc.selectFirst("div.ipc-poster div.ipc-media img").attr("srcset").split(" ");
                    String imageURL = null;
                    for(String image : images) {
                        final String string = image.split(",")[0];
                        if(string.endsWith("_UX380_CR0")) {
                            imageURL = string;
                        }
                    }

                    final Element metadataList = doc.selectFirst("div.Storyline__StorylineWrapper-sc-1b58ttw-0 ul.ipc-metadata-list");
                    final JSONArray genres = new JSONArray();
                    Elements genreElements = metadataList.select("a.GenresAndPlot__GenreChip-cum89p-3");
                    String ratingReason = null;
                    if(!genreElements.isEmpty()) {
                        ratingReason = metadataList.selectFirst("li.ipc-metadata-list__item ul.ipc-inline-list li.ipc-inline-list__item").text();
                    } else {
                        final Elements elements = metadataList.select("li.ipc-metadata-list__item");
                        final boolean hasTaglines = elements.first().attr("data-testid").equalsIgnoreCase("storyline-taglines");
                        genreElements = elements.get(hasTaglines ? 1 : 0).select("a");
                        ratingReason = elements.get(hasTaglines ? 2 : 1).text().substring("Motion Picture Rating (MPAA) ".length());
                    }
                    for(Element element : genreElements) {
                        genres.put(element.text());
                    }

                    json = new JSONObject();
                    json.put("rating", rating);
                    json.put("ratingReason", ratingReason);
                    json.put("runtimeSeconds", runtimeSeconds);
                    json.put("imageURL", imageURL);
                    json.put("genres", genres);
                    json.put("source", url);
                }
            }
        }
        if(json == null) {
            WLUtilities.saveLoggedError("IMDbService", "failed to get movie details for movie with id \"" + id + "\"!");
        }
        return json;
    }

    private JSONObject parseMovie(String id) {
        final String url = "https://www.imdb.com/title/" + id + "/";
        final Document doc = Jsoupable.getStaticDocument(Folder.OTHER, url, false);
        if(doc != null) {
            final Elements scripts = doc.select("script");
            scripts.removeIf(script -> !script.hasAttr("id") || !script.attr("id").equals("__NEXT_DATA__"));
            for(Element script : scripts) {
                String scriptText = script.toString();
                scriptText = scriptText.substring("<script id=\"__NEXT_DATA__\" type=\"application/json\">".length(), scriptText.length() - ("</script>".length()));
                final JSONObject propsJSON = new JSONObject(scriptText).getJSONObject("props");
                if(propsJSON.has("urqlState")) {
                    final JSONObject json = propsJSON.getJSONObject("urqlState");

                    String primaryImageURL = null;
                    JSONObject certificateJSON = null, runtimeJSON = null;
                    JSONArray genresArray = null;

                    final int maximum = json.length();
                    int completed = 0;
                    for(String key : json.keySet()) {
                        completed += 1;
                        final JSONObject idJSON = json.getJSONObject(key);
                        if(idJSON.has("data")) {
                            final JSONObject dataJSON = idJSON.getJSONObject("data");
                            if(dataJSON.has("title")) {
                                final JSONObject titleJSON = dataJSON.getJSONObject("title");
                                if(titleJSON.has("id") && id.equals(titleJSON.getString("id")) && titleJSON.has("genres") && titleJSON.has("certificate") && titleJSON.has("primaryImage")) {
                                    final JSONObject primaryImageJSON = titleJSON.getJSONObject("primaryImage");
                                    if(primaryImageURL == null && primaryImageJSON.has("url")) {
                                        primaryImageURL = primaryImageJSON.getString("url");
                                    }

                                    if(genresArray == null) {
                                        genresArray = titleJSON.getJSONObject("genres").getJSONArray("genres");
                                    }

                                    if(certificateJSON == null && titleJSON.get("certificate") instanceof JSONObject) {
                                        final JSONObject test = titleJSON.getJSONObject("certificate");
                                        if(test.has("ratingReason")) {
                                            certificateJSON = test;
                                        }
                                    }

                                    if(runtimeJSON == null) {
                                        runtimeJSON = titleJSON.get("runtime") instanceof JSONObject ? titleJSON.getJSONObject("runtime") : null;
                                    }

                                    if(primaryImageURL != null && certificateJSON != null && runtimeJSON != null && genresArray != null) {
                                        return complete(url, primaryImageURL, runtimeJSON, certificateJSON, genresArray);
                                    }
                                }
                            }
                        }
                        if(completed == maximum) {
                            return complete(url, primaryImageURL, runtimeJSON, certificateJSON, genresArray);
                        }
                    }
                }
            }
        }
        return null;
    }

    private JSONObject complete(String url, String primaryImageURL, JSONObject runtimeJSON, JSONObject certificateJSON, JSONArray genresArray) {
        final boolean hasCertificate = certificateJSON != null;
        String rating = null, ratingReason = null;
        if(hasCertificate) {
            rating = certificateJSON.getString("rating");
            ratingReason = certificateJSON.get("ratingReason") instanceof String ? certificateJSON.getString("ratingReason") : "Unknown";
        }
        final int runtimeSeconds = runtimeJSON != null ? runtimeJSON.getInt("seconds") : 0;

        final JSONArray genres = new JSONArray();
        if(genresArray != null) {
            for(Object obj : genresArray) {
                final JSONObject genreJSON = (JSONObject) obj;
                genres.put(genreJSON.getString("text"));
            }
        }

        final JSONObject imdbJSON = new JSONObject();
        if(hasCertificate) {
            imdbJSON.put("rating", rating);
            imdbJSON.put("ratingReason", ratingReason);
        }
        imdbJSON.put("runtimeSeconds", runtimeSeconds);
        imdbJSON.put("genres", genres);
        imdbJSON.put("imageURL", primaryImageURL);
        imdbJSON.put("source", url);
        return imdbJSON;
    }
}