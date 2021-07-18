package me.randomhashtags.worldlaws.service.finance.stockmarket;

public final class StockChart {
    private final long requestTime;
    private final String timestampCloseValues;

    public StockChart(long requestTime, String timestampCloseValues) {
        this.requestTime = requestTime;
        this.timestampCloseValues = timestampCloseValues;
    }
    @Override
    public String toString() {
        return "{" +
                "\"request_epoch\":" + requestTime + "," +
                timestampCloseValues +
                "}";
    }
}
