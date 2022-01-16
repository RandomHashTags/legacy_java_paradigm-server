package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum TwitterTrends implements RestAPI {
    INSTANCE;

    private HashMap<String, String> countries;

    TwitterTrends() {
        countries = new HashMap<>();
    }


    public void getWorldwideTrendingTopics(CompletionHandler handler) {
    }
    public void getTrendingTopics(String country, CompletionHandler handler) {
        if(countries.containsKey(country)) {
            handler.handleString(countries.get(country));
        } else {
            refreshTrendingTopics("1", handler);
        }
    }

    private void refreshTrendingTopics(String WOEID, CompletionHandler handler) {
        final String url = "https://api.twitter.com/1.1/trends/place.json?id=" + WOEID;
        final JSONArray array = requestJSONArray(url, RequestMethod.GET);
        final JSONObject obj = (JSONObject) array.get(0);
        final JSONArray trends = obj.getJSONArray("trends");
    }
}
