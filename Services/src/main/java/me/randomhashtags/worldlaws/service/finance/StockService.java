package me.randomhashtags.worldlaws.service.finance;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.DataValues;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.RestAPI;

import java.util.HashSet;

public interface StockService extends RestAPI, Jsonable, DataValues {
    void getAutoComplete(String input, CompletionHandler handler);
    void getMovers(CompletionHandler handler);
    void getQuotes(HashSet<String> symbols, CompletionHandler handler);
}
