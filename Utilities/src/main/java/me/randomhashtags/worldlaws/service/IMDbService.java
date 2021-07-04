package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.Jsoupable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface IMDbService extends DataValues {

    default void getIMDbMovieDetails(String title, int year, CompletionHandler handler) {
        final String lowercaseTitle = title.toLowerCase();
        final String url = "https://www.imdb.com/find?q=" + title.toLowerCase().replace(" ", "+");
        final Elements elements = Jsoupable.getStaticDocumentElements(FileType.OTHER, url, false, "div div.redesign div.pagecontent div div div.article div.findSection table.findList tr.findResult");
        if(elements != null) {
            for(Element element : elements) {
                final Element resultText = element.selectFirst("td.result_text");
                final String text = resultText.text().toLowerCase();
                if(text.contains(lowercaseTitle) && text.contains("(" + year + ")")) {
                    final String id = resultText.selectFirst("a").attr("href").split("/")[2];
                    getMovie(id, handler);
                    break;
                }
            }
        }
    }
    private void getMovie(String id, CompletionHandler handler) {
        final String url = "https://www.imdb.com/title/" + id + "/";
        final Document doc = Jsoupable.getStaticDocument(FileType.OTHER, url, false);
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

                for(String key : json.keySet()) {
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

                                if(certificateJSON == null) {
                                    final JSONObject test = titleJSON.getJSONObject("certificate");
                                    if(test.has("ratingReason")) {
                                        certificateJSON = test;
                                    }
                                }

                                if(runtimeJSON == null) {
                                    runtimeJSON = titleJSON.getJSONObject("runtime");
                                }

                                if(primaryImageURL != null && certificateJSON != null && runtimeJSON != null && genresArray != null) {
                                    final String rating = certificateJSON.getString("rating"), ratingReason = certificateJSON.getString("ratingReason");
                                    final int runtimeSeconds = runtimeJSON.getInt("seconds");

                                    final JSONArray genres = new JSONArray();
                                    for(Object obj : genresArray) {
                                        final JSONObject genreJSON = (JSONObject) obj;
                                        genres.put(genreJSON.getString("text"));
                                    }

                                    final JSONObject imdbJSON = new JSONObject();
                                    imdbJSON.put("rating", rating);
                                    imdbJSON.put("ratingReason", ratingReason);
                                    imdbJSON.put("runtimeSeconds", runtimeSeconds);
                                    imdbJSON.put("genres", genres);
                                    imdbJSON.put("primaryImageURL", primaryImageURL);
                                    imdbJSON.put("source", url);
                                    handler.handleJSONObject(imdbJSON);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            handler.handleJSONObject(null);
        } else {
            handler.handleJSONObject(null);
        }
    }
}
