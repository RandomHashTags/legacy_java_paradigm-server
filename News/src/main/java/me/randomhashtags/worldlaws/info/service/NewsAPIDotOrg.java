package me.randomhashtags.worldlaws.info.service;

import me.randomhashtags.worldlaws.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public enum NewsAPIDotOrg implements NewsService {
    INSTANCE;

    private HashMap<String, String> countryHeadlines;
    private HashMap<String, NewsArticle> ARTICLES;

    // https://newsapi.org/docs/get-started | https://newsapi.org/account
    private final HashMap<String, String> NEWSAPI_HEADERS = new HashMap<>() {{
        putAll(RestAPI.CONTENT_HEADERS);
        put("X-Api-Key", "***REMOVED***"); // max of 500 requests per day
    }};

    NewsAPIDotOrg() {
        countryHeadlines = new HashMap<>();
        ARTICLES = new HashMap<>();
    }

    @Override
    public void getResponseJSON(String target, CompletionHandler handler) {
        switch (target.toLowerCase()) {
            default:
                if(countryHeadlines.containsKey(target)) {
                    handler.handle(countryHeadlines.get(target));
                } else {
                    getTopHeadlines(target, handler);
                }
                break;
        }
    }

    private void getTopHeadlines(String countryISOAlpha2, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        countryHeadlines.remove(countryISOAlpha2);
        final HashMap<String, String> query = new HashMap<>();
        query.put("country", countryISOAlpha2);
        query.put("pageSize", "100");
        requestJSONObject("https://newsapi.org/v2/top-headlines", RequestMethod.GET, NEWSAPI_HEADERS, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject response) {
                final JSONArray articles = response.getJSONArray("articles");
                final List<NewsArticle> headlines = new ArrayList<>();
                for(Object obj : articles) {
                    final JSONObject json = new JSONObject(obj.toString());
                    final Object author = json.get("author"), description = json.get("description"), urlToImage = json.get("urlToImage");
                    final String title = json.getString("title").replace("|", "");
                    final String authorString = author != JSONObject.NULL ? author.toString().startsWith("[") ? new JSONArray(author.toString()).getJSONObject(0).getString("name") : json.getString("author") : null;
                    final String descriptionString = description != JSONObject.NULL ? json.getString("description") : null;
                    final String urlToImageString = urlToImage != JSONObject.NULL ? json.getString("urlToImage") : null;
                    final NewsArticle article = new NewsArticle(authorString, title, descriptionString, json.getString("url"), urlToImageString);
                    ARTICLES.put(title.toLowerCase().replace(" ", ""), article);
                    headlines.add(article);
                }
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(NewsArticle article : headlines) {
                    builder.append(isFirst ? "" : ",").append(article.toString());
                    isFirst = false;
                }
                builder.append("]");
                final String json = builder.toString();
                countryHeadlines.put(countryISOAlpha2, json);
                WLLogger.log(Level.INFO, "NewsAPIDotOrg - refreshed top headlines" + (countryISOAlpha2 != null ? " for country ISO Alpha2 \"" + countryISOAlpha2 + "\"" : "") + " (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handle(json);
            }
        });
    }
}
