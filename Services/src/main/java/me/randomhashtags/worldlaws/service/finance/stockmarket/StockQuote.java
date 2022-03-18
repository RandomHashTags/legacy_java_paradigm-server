package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class StockQuote {
    private final float open, change, changePercent;
    private final float price, high, low;
    private final boolean isChart;

    public StockQuote(float open, float change, float changePercent, float price, float high, float low) {
        this(false, open, change, changePercent, price, high, low);
    }
    public StockQuote(boolean isChart, float open, float change, float changePercent, float price, float high, float low) {
        this.isChart = isChart;
        this.open = open;
        this.change = change;
        this.changePercent = changePercent;
        this.price = price;
        this.high = high;
        this.low = low;
    }
    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put("price", price);
        if(open != -1) {
            json.put("open", open);
        }
        if(high != -1) {
            json.put("high", high);
        }
        if(low != -1) {
            json.put("low", low);
        }
        if(!isChart) {
            json.put("change", change);
            json.put("changePercent", changePercent);
        }
        return json;
    }
}
