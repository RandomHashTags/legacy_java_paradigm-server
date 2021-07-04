package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.finance.StockService;
import me.randomhashtags.worldlaws.service.finance.YahooFinance;
import me.randomhashtags.worldlaws.service.science.astronomy.APOD;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

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
        stockService.getChart(APIVersion.v1, "AAPL", new CompletionHandler() {
            @Override
            public void handleString(String string) {
                WLLogger.log(Level.INFO, "Services;object=");
                WLLogger.log(Level.INFO, string);
            }
        });
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
            case "apod":
                APOD.INSTANCE.get(version, handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "stock_market",
                "apod"
        };
    }

    private void getStockMarketResponse(APIVersion version, String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
        switch (key) {
            case "movers":
                stockService.getMovers(version, handler);
                break;
            case "quotes":
                final HashSet<String> symbols = new HashSet<>(Arrays.asList(values[1].split(",")));
                stockService.getQuotes(version, symbols, handler);
                break;
            default:
                switch (values.length) {
                    case 2:
                        final String target = values[1];
                        switch (target) {
                            case "chart":
                                stockService.getChart(version, key, handler);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    private void getStockMarketHomeResponse(APIVersion version, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final HashSet<String> requests = new HashSet<>() {{
            add("movers");
        }};
        final int max = requests.size();
        final HashSet<String> values = new HashSet<>();
        final AtomicInteger completed = new AtomicInteger(0);
        requests.parallelStream().forEach(request -> {
            getStockMarketResponse(version, request, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    if(string != null) {
                        final String target = "\"" + request + "\":" + string;
                        values.add(target);
                    }
                    if(completed.addAndGet(1) == max) {
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
                        WLLogger.log(Level.INFO, "Services - loaded stock market home response (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handleString(value);
                    }
                }
            });
        });
    }
}
