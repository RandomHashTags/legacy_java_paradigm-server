package me.randomhashtags.worldlaws.service.finance;

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

    @Override
    public String toString() {
        return "\"" + symbol + "\":{" +
                (regular != null ? "\"regularMarket\":" + regular.toString() + "," : "") +
                (post != null ? "\"postMarket\":" + post.toString() + "," : "") +
                (!longName.equals(shortName) ? "\"longName\":\"" + longName + "\"," : "") +
                "\"shortName\":\"" + shortName + "\"" +
                "}";
    }
}
