package me.randomhashtags.worldlaws.service.finance;

public final class StockQuote {
    private final float open, change, changePercent;
    private final float price, high, low;

    public StockQuote(float open, float change, float changePercent, float price, float high, float low) {
        this.open = open;
        this.change = change;
        this.changePercent = changePercent;
        this.price = price;
        this.high = high;
        this.low = low;
    }

    @Override
    public String toString() {
        return "{" +
                (open != -1 ? "\"open\":" + open + "," : "") +
                (high != -1 ? "\"high\":" + high + "," : "") +
                (low != -1 ? "\"low\":" + low + "," : "") +
                "\"change\":" + change + "," +
                "\"changePercent\":" + changePercent + "," +
                "\"price\":" + price +
                "}";
    }
}
