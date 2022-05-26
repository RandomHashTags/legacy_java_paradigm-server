package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;
import me.randomhashtags.worldlaws.service.entertainment.TwitchClips;
import me.randomhashtags.worldlaws.service.finance.stockmarket.StockService;
import me.randomhashtags.worldlaws.service.finance.stockmarket.YahooFinance;
import me.randomhashtags.worldlaws.stream.CompletableFutures;

import java.util.Arrays;
import java.util.HashSet;

public enum ServerRequestTypeServices implements ServerRequestType {
    STOCK_MARKET,
    TWITCH_CLIPS,
    ;

    private static final StockService STOCK_SERVICE = YahooFinance.INSTANCE;

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (version) {
            case v1: return getV1Handler(version);
            default: return null;
        }
    }

    private WLHttpHandler getV1Handler(APIVersion version) {
        switch (this) {
            case STOCK_MARKET:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return values.length == 0 ? getStockMarketHomeResponse(version) : getStockMarketResponse(version, values);
                };
            case TWITCH_CLIPS:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    return TwitchClips.INSTANCE.getResponse(values);
                };
            default:
                return null;
        }
    }


    private JSONObjectTranslatable getStockMarketResponse(APIVersion version, String[] values) {
        final boolean success = STOCK_SERVICE.makeQuotaRequest(STOCK_SERVICE.getJSONDataValue());
        if(success) {
            final String key = values[0];
            switch (key) {
                case "chart":
                    return STOCK_SERVICE.getChart(version, values[1]);
                case "movers":
                    return STOCK_SERVICE.getMovers(version);
                case "quotes":
                    final HashSet<String> symbols = new HashSet<>(Arrays.asList(values[1].split(",")));
                    return STOCK_SERVICE.getQuotes(version, symbols);
                default:
                    return null;
            }
        }
        return null;
    }
    private JSONObjectTranslatable getStockMarketHomeResponse(APIVersion version) {
        final long started = System.currentTimeMillis();
        final HashSet<String> requests = new HashSet<>() {{
            add("movers");
        }};
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        new CompletableFutures<String>().stream(requests, request -> {
            final String[] values = request.split("/");
            final JSONObjectTranslatable string = getStockMarketResponse(version, values);
            if(string != null) {
                json.put(request, string, true);
            }
        });
        WLLogger.logInfo("Services - loaded stock market home response (took " + WLUtilities.getElapsedTime(started) + ")");
        return json;
    }
}
