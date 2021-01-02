package me.randomhashtags.worldlaws;

public interface NewsService extends RestAPI {
    void getResponseJSON(String target, CompletionHandler handler);
}
