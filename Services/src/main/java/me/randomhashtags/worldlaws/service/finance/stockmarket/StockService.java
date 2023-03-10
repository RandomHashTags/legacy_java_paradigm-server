package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLService;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.JSONDataValue;
import me.randomhashtags.worldlaws.service.QuotaHandler;

import java.util.HashSet;

public interface StockService extends WLService, QuotaHandler {
    JSONDataValue getJSONDataValue();
    void getAutoComplete(APIVersion version, String input, CompletionHandler handler);
    JSONObjectTranslatable getMovers(APIVersion version);
    JSONObjectTranslatable getQuotes(APIVersion version, HashSet<String> symbols);
    JSONObjectTranslatable getChart(APIVersion version, String symbol);
}
