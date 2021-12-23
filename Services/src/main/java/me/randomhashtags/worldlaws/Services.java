package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.education.WordOfTheDay;
import me.randomhashtags.worldlaws.service.finance.stockmarket.StockService;
import me.randomhashtags.worldlaws.service.finance.stockmarket.YahooFinance;
import me.randomhashtags.worldlaws.service.science.astronomy.AstronomyPictureOfTheDay;
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
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SERVICES;
    }

    private void test() {
    }

    @Override
    public void getServerResponse(APIVersion version, String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
        switch (key) {
            case "stock_market":
                if(value.equals(key)) {
                    getStockMarketHomeResponse(version, handler);
                } else {
                    getStockMarketResponse(version, value.substring(key.length()+1), handler);
                }
                break;
            case "astronomy_picture_of_the_day":
                AstronomyPictureOfTheDay.INSTANCE.get(version, handler);
                break;
            case "word_of_the_day":
                WordOfTheDay.INSTANCE.refresh(handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                //"stock_market",
                "astronomy_picture_of_the_day",
                "word_of_the_day"
        };
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
    }

    private void getStockMarketResponse(APIVersion version, String value, CompletionHandler handler) {
        stockService.makeQuotaRequest(stockService.getJSONDataValue(), new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                final String[] values = value.split("/");
                final String key = values[0];
                switch (key) {
                    case "chart":
                        stockService.getChart(version, values[1], handler);
                        break;
                    case "movers":
                        stockService.getMovers(version, handler);
                        break;
                    case "quotes":
                        final HashSet<String> symbols = new HashSet<>(Arrays.asList(values[1].split(",")));
                        stockService.getQuotes(version, symbols, handler);
                        break;
                    default:
                        handler.handleString(null);
                        break;
                }
            }
        });
    }
    private void getStockMarketHomeResponse(APIVersion version, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final HashSet<String> requests = new HashSet<>() {{
            add("movers");
        }};
        final HashSet<String> values = new HashSet<>();
        ParallelStream.stream(requests, requestObj -> {
            final String request = (String) requestObj;
            getStockMarketResponse(version, request, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        final String target = "\"" + request + "\":" + string;
                        values.add(target);
                    }
                }
            });
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
        WLLogger.logInfo("Services - loaded stock market home response (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handleString(value);
    }
}
