package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.service.JSONDataValue;
import me.randomhashtags.worldlaws.service.QuotaHandler;

import java.util.HashSet;

public interface StockService extends WLService, QuotaHandler {
    JSONDataValue getJSONDataValue();
    void getAutoComplete(APIVersion version, String input, CompletionHandler handler);
    void getMovers(APIVersion version, CompletionHandler handler);
    void getQuotes(APIVersion version, HashSet<String> symbols, CompletionHandler handler);
    void getChart(APIVersion version, String symbol, CompletionHandler handler);
}
