package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public interface IMDbService extends DataValues {

    default JSONObject getIMDbMovieDetails(String title, int year) {
        title = LocalServer.fixUnescapeValues(title);
        final String lowercaseTitle = title.toLowerCase();
        final LinkedHashMap<String, Object> query = new LinkedHashMap<>() {{
            put("q", lowercaseTitle);
        }};
        final String queryString = RestAPI.parseQuery(query, false);
        final String url = "https://www.imdb.com/find?" + queryString;
        final Elements elements = Jsoupable.getStaticDocumentElements(Folder.OTHER, url, false, "div div.redesign div.pagecontent div div div.article div.findSection table.findList tr.findResult");
        JSONObject json = null;
        if(elements != null) {
            json = getFirst(elements, lowercaseTitle, year);
            if(json == null) {
                json = getFirst(elements, lowercaseTitle, year-1);
            }
        }
        if(json == null) {
            final String string = "title=\"" + title + "\"\n" +
                    "lowercaseTitle=\"" + lowercaseTitle + "\"\n" +
                    "elements==null=" + (elements == null) + "\n" +
                    "url=\"" + url + "\"\n" +
                    "year=" + year;
            WLUtilities.saveLoggedError("IMDbService", "failed to get movie details with\n\n" + string);
        }
        return json;
    }
    private JSONObject getFirst(Elements elements, String lowercaseTitle, int year) {
        JSONObject json = null;
        for(Element element : elements) {
            final Element resultTextElement = element.selectFirst("td.result_text");
            if(resultTextElement != null) {
                final String text = resultTextElement.text().toLowerCase();
                if(hasMovieTitle(lowercaseTitle, year, text)) {
                    final String id = resultTextElement.selectFirst("a[href]").attr("href").split("/")[2];
                    json = getMovie(id);
                    break;
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
                Element secondElement = null;
                String rating = null;
                boolean isValidRating = false;
                if(list.size() > 1) {
                    secondElement = list.get(1);
                    final Element secondElementHref = secondElement.selectFirst("a[href]");
                    rating = secondElementHref != null ? secondElementHref.text() : null;
                    isValidRating = isValidMovieRating(rating);
                }

                int runtimeSeconds = 0;
                if(list.size() > 2) {
                    final String[] runtimeValues = (isValidRating ? list.get(2) : secondElement).text().split(" ");
                    for(String string : runtimeValues) {
                        string = string.toLowerCase();
                        final boolean isHours = string.endsWith("h");
                        final String target = string.substring(0, string.length()-1);
                        if(target.matches("[0-9]+")) {
                            final int value = Integer.parseInt(target);
                            runtimeSeconds += value * 60 * (isHours ? 60 : 1);
                        }
                    }
                }

                final Element targetImageElement = doc.selectFirst("div.ipc-poster div.ipc-media img");
                final String[] images = targetImageElement != null ? targetImageElement.attr("srcset").split(" ") : null;
                String imageURL = null;
                if(images != null) {
                    for(String image : images) {
                        final String string = image.split(",")[0];
                        if(string.endsWith("_UX380_CR0")) {
                            imageURL = string;
                        }
                    }
                }

                final Element metadataList = doc.selectFirst("div.Storyline__StorylineWrapper-sc-1b58ttw-0 ul.ipc-metadata-list");
                final JSONArray genres = new JSONArray();
                String ratingReason = null;
                if(metadataList != null) {
                    Elements genreElements = metadataList.select("a.GenresAndPlot__GenreChip-cum89p-3"), taglineElements = null;
                    if(!genreElements.isEmpty()) {
                        final Element ratingElement = metadataList.selectFirst("li.ipc-metadata-list__item ul.ipc-inline-list li.ipc-inline-list__item");
                        ratingReason = ratingElement != null ? ratingElement.text() : null;
                    } else {
                        final Elements elements = metadataList.select("li.ipc-metadata-list__item");
                        for(Element element : elements) {
                            final String testID = element.attr("data-testid");
                            switch (testID) {
                                case "storyline-taglines":
                                    taglineElements = element.select("span.ipc-metadata-list-item__list-content-item");
                                    break;
                                case "storyline-genres":
                                    genreElements = element.select("a");
                                    break;
                                case "storyline-certificate":
                                    ratingReason = element.text().replace("Motion Picture Rating (MPAA) ", "").replace("Certificate ", "");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    for(Element element : genreElements) {
                        genres.put(element.text());
                    }
                }

                json = new JSONObject();
                if(rating != null) {
                    json.put("rating", rating);
                }
                if(ratingReason != null) {
                    json.put("ratingReason", ratingReason);
                }
                json.put("runtimeSeconds", runtimeSeconds);
                json.put("imageURL", imageURL);
                json.put("genres", genres);
                json.put("source", url);
            }
        }
        return json;
    }
    private boolean isValidMovieRating(String input) { // https://en.wikipedia.org/wiki/Motion_picture_content_rating_system
        if(input == null) {
            return false;
        }
        input = input.toLowerCase().replace(" ", "");
        final Set<String> list = Set.of(
                // Argentina
                "ATP",
                "+13",
                "+16",
                "+18",
                "C",

                // Australia
                "M",
                "MA 15+",
                "R 18+",
                "X 18+",
                "RC",

                // Bahamas
                "B",
                "T",
                "D",

                // Barbados
                "GA",
                "PG13",

                // Belgium
                "AL", "TOUS", "AL/TOUS",
                "9",
                "14",

                // Brazil
                "ER",
                "L",
                "10",

                // Bulgaria
                "X",

                // Canada
                "14A",
                "18A",
                "A",
                "E",
                "Prohibited",
                "13+",
                "16+",
                "18+",

                // Switzerland
                "0",
                "6",
                "12",
                "16",
                "18",
                "Unrated",

                // United States
                "G",
                "PG",
                "PG-13",
                "R",
                "NC-17",
                "Not rated"
        );
        final HashSet<String> ratings = new HashSet<>();
        for(String rating : list) {
            ratings.add(rating.replace(" ", "").toLowerCase());
        }
        return ratings.contains(input);
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