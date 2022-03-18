package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class Stock {
    private final String symbol, shortName, longName;
    private final StockQuote regular, post;

    public Stock(String symbol, String shortName, String longName, StockQuote regular, StockQuote post) {
        this.symbol = symbol;
        this.shortName = shortName;
        this.longName = longName;
        this.regular = regular;
        this.post = post;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("shortName", shortName);
        if(regular != null) {
            json.put("regularMarket", regular.toJSONObject());
        }
        if(post != null) {
            json.put("postMarket", post.toJSONObject());
        }
        if(longName != null && !longName.equals(shortName)) {
            json.put("longName", longName);
        }
        return json;
    }
}
