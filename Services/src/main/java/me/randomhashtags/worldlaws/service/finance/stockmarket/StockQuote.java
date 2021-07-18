package me.randomhashtags.worldlaws.service.finance.stockmarket;

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

    @Override
    public String toString() {
        return "{" +
                (open != -1 ? "\"open\":" + open + "," : "") +
                (high != -1 ? "\"high\":" + high + "," : "") +
                (low != -1 ? "\"low\":" + low + "," : "") +
                (!isChart ? "\"change\":" + change + "," : "") +
                (!isChart ? "\"changePercent\":" + changePercent + "," : "") +
                "\"price\":" + price +
                "}";
    }
}
