package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsoupable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface IMDbService extends DataValues {

    default void getIMDbMovieDetails(String title, int year, CompletionHandler handler) {
        final String lowercaseTitle = title.toLowerCase();
        final String url = "https://www.imdb.com/find?q=" + lowercaseTitle.replace(" ", "+");
        final Elements elements = Jsoupable.getStaticDocumentElements(Folder.OTHER, url, false, "div div.redesign div.pagecontent div div div.article div.findSection table.findList tr.findResult");
        if(elements != null) {
            for(Element element : elements) {
                final Element resultText = element.selectFirst("td.result_text");
                final String text = resultText.text().toLowerCase();
                if(hasMovieTitle(lowercaseTitle, year, text)) {
                    final String id = resultText.selectFirst("a").attr("href").split("/")[2];
                    getMovie(id, handler);
                    return;
                }
            }
        }
        handler.handleString(null);
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
    private void getMovie(String id, CompletionHandler handler) {
        final String url = "https://www.imdb.com/title/" + id + "/";
        final Document doc = Jsoupable.getStaticDocument(Folder.OTHER, url, false);
        if(doc != null) {
            final Elements scripts = doc.select("script");
            scripts.removeIf(script -> !script.hasAttr("id") || !script.attr("id").equals("__NEXT_DATA__"));
            for(Element script : scripts) {
                String scriptText = script.toString();
                scriptText = scriptText.substring("<script id=\"__NEXT_DATA__\" type=\"application/json\">".length(), scriptText.length() - ("</script>".length()));
                final JSONObject json = new JSONObject(scriptText).getJSONObject("props").getJSONObject("urqlState");

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
                                    complete(url, primaryImageURL, runtimeJSON, certificateJSON, genresArray, handler);
                                    return;
                                }
                            }
                        }
                    }
                    if(completed == maximum) {
                        complete(url, primaryImageURL, runtimeJSON, certificateJSON, genresArray, handler);
                        return;
                    }
                }
            }
        }
        handler.handleJSONObject(null);
    }

    private void complete(String url, String primaryImageURL, JSONObject runtimeJSON, JSONObject certificateJSON, JSONArray genresArray, CompletionHandler handler) {
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
        handler.handleJSONObject(imdbJSON);
    }
}