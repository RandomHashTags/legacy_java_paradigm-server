package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeServices;
import me.randomhashtags.worldlaws.service.entertainment.TwitchClips;
import me.randomhashtags.worldlaws.service.finance.stockmarket.StockService;
import me.randomhashtags.worldlaws.service.finance.stockmarket.YahooFinance;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

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
        final String string = TwitchClips.INSTANCE.getResponse("getAll");
        WLLogger.logInfo("Services;test;string=" + string);
    }

    @Override
    public String getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeServices type = (ServerRequestTypeServices) request.getType();
        final String target = request.getTarget();
        switch (type) {
            case STOCK_MARKET:
                if(target == null) {
                    return getStockMarketHomeResponse(version);
                } else {
                    return getStockMarketResponse(version, target);
                }
            case TWITCH_CLIPS:
                final TwitchClips clips = TwitchClips.INSTANCE;
                return clips.getResponse(target);
            default:
                return null;
        }
    }

    @Override
    public ServerRequest[] getHomeRequests() {
        return new ServerRequest[] {
                //"stock_market"
                new ServerRequest(ServerRequestTypeServices.TWITCH_CLIPS, "getAll")
        };
    }

    @Override
    public long getHomeResponseUpdateInterval() {
        registerFixedTimer(UpdateIntervals.Services.TWITCH_CLIPS, TwitchClips.INSTANCE::refresh);
        return UpdateIntervals.Services.HOME;
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
        new CompletableFutures<String>().stream(requests, request -> {
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
