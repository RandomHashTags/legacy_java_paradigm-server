package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public enum NewsAPIDotOrg implements NewsService {
    INSTANCE;

    private HashMap<String, JSONObjectTranslatable> countryHeadlines;
    private HashMap<String, NewsArticle> ARTICLES;

    // https://newsapi.org/docs/get-started | https://newsapi.org/account
    private final LinkedHashMap<String, String> NEWSAPI_HEADERS = new LinkedHashMap<>() {{
        putAll(GET_CONTENT_HEADERS);
        put("X-Api-Key", "***REMOVED***"); // max of 500 requests per day
    }};

    NewsAPIDotOrg() {
        countryHeadlines = new HashMap<>();
        ARTICLES = new HashMap<>();
    }

    @Override
    public JSONObjectTranslatable getResponseJSON(String target) {
        switch (target.toLowerCase()) {
            default:
                if(countryHeadlines.containsKey(target)) {
                    return countryHeadlines.get(target);
                } else {
                    return getTopHeadlines(target);
                }
        }
    }

    private JSONObjectTranslatable getTopHeadlines(String countryISOAlpha2) {
        final long started = System.currentTimeMillis();
        countryHeadlines.remove(countryISOAlpha2);
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("country", countryISOAlpha2);
        query.put("pageSize", "100");
        final JSONObject response = requestJSONObject("https://newsapi.org/v2/top-headlines", NEWSAPI_HEADERS, query);
        final JSONArray articles = response.getJSONArray("articles");
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(Object obj : articles) {
            final JSONObject articleJSON = new JSONObject(obj.toString());
            final Object author = articleJSON.get("author"), description = articleJSON.get("description"), urlToImage = articleJSON.get("urlToImage");
            final String title = articleJSON.getString("title").replace("|", "");
            final String authorString = author != JSONObject.NULL ? author.toString().startsWith("[") ? new JSONArray(author.toString()).getJSONObject(0).getString("name") : articleJSON.getString("author") : null;
            final String descriptionString = description != JSONObject.NULL ? articleJSON.getString("description") : null;
            final String urlToImageString = urlToImage != JSONObject.NULL ? articleJSON.getString("urlToImage") : null;
            final NewsArticle article = new NewsArticle(authorString, title, descriptionString, articleJSON.getString("url"), urlToImageString);
            final String id = title.toLowerCase().replace(" ", "");
            ARTICLES.put(id, article);
            articleJSON.put(id, article.toJSONObject());
        }
        countryHeadlines.put(countryISOAlpha2, json);
        WLLogger.logInfo("NewsAPIDotOrg - refreshed top headlines" + (countryISOAlpha2 != null ? " for country ISO Alpha2 \"" + countryISOAlpha2 + "\"" : "") + " (took " + WLUtilities.getElapsedTime(started) + ")");
        return json;
    }
}
