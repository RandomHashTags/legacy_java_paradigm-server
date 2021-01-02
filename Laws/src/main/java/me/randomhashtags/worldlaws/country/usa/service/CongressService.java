package me.randomhashtags.worldlaws.country.usa.service;

import me.randomhashtags.worldlaws.CompletionHandler;

public interface CongressService {
    void getPolitician(String id, CompletionHandler handler);
}
