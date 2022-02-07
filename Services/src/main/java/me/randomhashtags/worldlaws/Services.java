package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.entertainment.TwitchClips;
import me.randomhashtags.worldlaws.service.finance.stockmarket.StockService;
import me.randomhashtags.worldlaws.service.finance.stockmarket.YahooFinance;
import me.randomhashtags.worldlaws.stream.ParallelStream;

import java.util.Arrays;
import java.util.HashSet;

public final class Services implements WLServer {

    private final StockService stockService;

    public static void main(String[] args) {
        new Services();
    }

    private Services() {
        stockService = YahooFinance.INSTANCE;
        test();
        //load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SERVICES;
    }

    private void test() {
        final String string = TwitchClips.INSTANCE.refresh();
        WLLogger.logInfo("Services;test;string=" + string);
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, String value) {
        final String[] values = value.split("/");
        final String key = values[0];
        switch (key) {
            case "stock_market":
                if(value.equals(key)) {
                    return getStockMarketHomeResponse(version);
                } else {
                    return getStockMarketResponse(version, value.substring(key.length()+1));
                }
            default:
                return null;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                //"stock_market"
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        return 0;
    }

    private String getStockMarketResponse(APIVersion version, String value) {
        final boolean success = stockService.makeQuotaRequest(stockService.getJSONDataValue());
        if(success) {
            final String[] values = value.split("/");
            final String key = values[0];
            switch (key) {
                case "chart":
                    return stockService.getChart(version, values[1]);
                case "movers":
                    return stockService.getMovers(version);
                case "quotes":
                    final HashSet<String> symbols = new HashSet<>(Arrays.asList(values[1].split(",")));
                    return stockService.getQuotes(version, symbols);
                default:
                    return null;
            }
        }
        return null;
    }
    private String getStockMarketHomeResponse(APIVersion version) {
        final long started = System.currentTimeMillis();
        final HashSet<String> requests = new HashSet<>() {{
            add("movers");
        }};
        final HashSet<String> values = new HashSet<>();
        new ParallelStream<String>().stream(requests, request -> {
            final String string = getStockMarketResponse(version, request);
            if(string != null) {
                final String target = "\"" + request + "\":" + string;
                values.add(target);
            }
        });

        String value = null;
        if(!values.isEmpty()) {
            final StringBuilder builder = new StringBuilder("{");
            boolean isFirst = true;
            for(String s : values) {
                builder.append(isFirst ? "" : ",").append(s);
                isFirst = false;
            }
            builder.append("}");
            value = builder.toString();
        }
        WLLogger.logInfo("Services - loaded stock market home response (took " + WLUtilities.getElapsedTime(started) + ")");
        return value;
    }
}
