package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.service.finance.StockService;
import me.randomhashtags.worldlaws.service.finance.YahooFinance;
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
        test();
        //load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.SERVICES;
    }

    private void test() {
        getStockMarketHomeResponse(new CompletionHandler() {
            @Override
            public void handle(Object object) {
                WLLogger.log(Level.INFO, "Services;test;object=" + object);
            }
        });
    }

    @Override
    public void getServerResponse(APIVersion version, String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
        switch (key) {
            case "stock_market":
                getStockMarketHomeResponse(handler);
                break;
            default:
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return new String[] {
                "stock_market"
        };
    }

    private void getStockMarketHomeResponse(CompletionHandler handler) {
        final HashSet<String> requests = new HashSet<>() {{
            add("movers");
        }};
        final int max = requests.size();
        final HashSet<String> values = new HashSet<>();
        final AtomicInteger completed = new AtomicInteger(0);
        requests.parallelStream().forEach(request -> {
            getStockMarketResponse(request, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    if(object != null) {
                        final String target = "\"" + request + "\":" + object.toString();
                        values.add(target);
                    }
                    if(completed.addAndGet(1) == max) {
                        String string = null;
                        if(!values.isEmpty()) {
                            final StringBuilder builder = new StringBuilder("\"stock_market\":{");
                            boolean isFirst = true;
                            for(String value : values) {
                                builder.append(isFirst ? "" : ",").append(value);
                                isFirst = false;
                            }
                            builder.append("}");
                            string = builder.toString();
                        }
                        handler.handle(string);
                    }
                }
            });
        });
    }
    private void getStockMarketResponse(String value, CompletionHandler handler) {
        final String[] values = value.split("/");
        final String key = values[0];
        switch (key) {
            case "movers":
                stockService.getMovers(handler);
                break;
            case "quotes":
                final HashSet<String> symbols = new HashSet<>(Arrays.asList(values[1].split(",")));
                stockService.getQuotes(symbols, handler);
                break;
            default:
                break;
        }
    }
}
