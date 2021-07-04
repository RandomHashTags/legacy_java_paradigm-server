package me.randomhashtags.worldlaws.service.finance;

import me.randomhashtags.worldlaws.*;

import java.util.HashSet;

public interface StockService extends WLService {
    void getAutoComplete(APIVersion version, String input, CompletionHandler handler);
    void getMovers(APIVersion version, CompletionHandler handler);
    void getQuotes(APIVersion version, HashSet<String> symbols, CompletionHandler handler);
    void getChart(APIVersion version, String symbol, CompletionHandler handler);
}
