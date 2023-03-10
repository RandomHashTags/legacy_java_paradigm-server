package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.JSONDataValue;
import me.randomhashtags.worldlaws.settings.Settings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

// https://rapidapi.com/apidojo/api/yahoo-finance1/
public enum YahooFinance implements StockService {
    INSTANCE;

    private LinkedHashMap<String, String> getHeaders() {
        final LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("x-rapidapi-key", Settings.PrivateValues.YahooFinance.getAPIKey());
        headers.put("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        return headers;
    }

    @Override
    public JSONDataValue getJSONDataValue() {
        return JSONDataValue.FINANCE_YAHOO_FINANCE;
    }

    /*@Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_YAHOO_FINANCE;
    }*/

    @Override
    public void getAutoComplete(APIVersion version, String input, CompletionHandler handler) {
        final String term = input.toLowerCase();
        final JSONObject json = getJSONObject(Folder.SERVICES_FINANCE_YAHOO_FINANCE_CHARTS, "_Auto completes", new CompletionHandler() {
            @Override
            public String loadJSONObjectString() {
                final LinkedHashMap<String, String> query = new LinkedHashMap<>();
                query.put("q", term);
                final JSONObject json = requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/auto-complete", getHeaders(), query);
                final JSONArray quotes = json.getJSONArray("quotes");
                for(Object obj : quotes) {
                    final JSONObject quoteJSON = (JSONObject) obj;
                }
                return null;
            }
        });
    }
    @Override
    public JSONObjectTranslatable getMovers(APIVersion version) {
        final JSONObject json = getMovers(version, 0);
        final JSONObject finance = json.getJSONObject("finance");
        final JSONArray results = finance.getJSONArray("result");
        final JSONArray dayGainers = results.getJSONObject(0).getJSONArray("quotes");
        final JSONArray dayLosers = results.getJSONObject(1).getJSONArray("quotes");
        final JSONArray mostActive = results.getJSONObject(2).getJSONArray("quotes");

        final HashSet<String> symbols = getQuoteSymbols(dayGainers);
        symbols.addAll(getQuoteSymbols(dayLosers));
        symbols.addAll(getQuoteSymbols(mostActive));
        return getQuotes(version, symbols);
    }
    private JSONObject getMovers(APIVersion version, int offset) {
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("region", "US");
        query.put("count", "25");
        query.put("start", Integer.toString(offset));
        return requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-movers", getHeaders(), query);
    }
    private HashSet<String> getQuoteSymbols(JSONArray array) {
        final HashSet<String> symbols = new HashSet<>();
        for(Object obj : array)  {
            final JSONObject json = (JSONObject) obj;
            final String symbol = json.getString("symbol");
            symbols.add(symbol);
        }
        return symbols;
    }

    @Override
    public JSONObjectTranslatable getQuotes(APIVersion version, HashSet<String> symbols) { // 50 symbols limit per request
        final long started = System.currentTimeMillis();
        final LinkedHashMap<String, String> headers = getHeaders();
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("region", "US");
        final StringBuilder symbolBuilder = new StringBuilder();
        boolean isFirstSymbol = true;
        for(String symbol : symbols) {
            symbolBuilder.append(isFirstSymbol ? "" : ",").append(symbol);
            isFirstSymbol = false;
        }
        query.put("symbols", symbolBuilder.toString());

        final JSONObject json = requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes", headers, query);
        final JSONObject quoteResponse = json.getJSONObject("quoteResponse");
        final JSONArray results = quoteResponse.getJSONArray("result");
        final int max = results.length();
        final JSONObjectTranslatable translatable = new JSONObjectTranslatable();
        for(Object obj : results) {
            final JSONObject targetQuote = (JSONObject) obj;
            final String symbol = targetQuote.getString("symbol");
            final String shortName = targetQuote.getString("shortName");
            final String longName = targetQuote.getString("longName");

            final StockQuote regularMarket = new StockQuote(
                    targetQuote.getFloat("regularMarketOpen"),
                    targetQuote.getFloat("regularMarketChange"),
                    targetQuote.getFloat("regularMarketChangePercent"),
                    targetQuote.getFloat("regularMarketPrice"),
                    targetQuote.getFloat("regularMarketDayHigh"),
                    targetQuote.getFloat("regularMarketDayLow")
            );

            StockQuote postMarket = null;
            if(targetQuote.has("postMarketPrice")) {
                postMarket = new StockQuote(
                        -1,
                        targetQuote.getFloat("postMarketChange"),
                        targetQuote.getFloat("postMarketChangePercent"),
                        targetQuote.getFloat("postMarketPrice"),
                        -1,
                        -1
                );
            }

            final Stock symbolStock = new Stock(symbol, shortName, longName, regularMarket, postMarket);
            translatable.put(symbol, symbolStock.toJSONObject());
        }
        WLLogger.logInfo("YahooFinance - loaded " + max + " quotes (took " + WLUtilities.getElapsedTime(started) + ")");
        return translatable;
    }

    @Override
    public JSONObjectTranslatable getChart(APIVersion version, String symbol) {
        final Folder folder = Folder.SERVICES_FINANCE_YAHOO_FINANCE_CHARTS;
        final JSONObject json = getJSONObject(folder, symbol, new CompletionHandler() {
            @Override
            public JSONObject loadJSONObject() {
                return requestChart(false, version, symbol);
            }
        });
        final long elapsedTime = System.currentTimeMillis() - json.getLong("request_epoch");
        if(elapsedTime >= 604_800) {
            final JSONObject chartJSON = requestChart(true, version, symbol);
            Jsonable.setFileJSONObject(folder, symbol, chartJSON);
            return JSONObjectTranslatable.copy(chartJSON);
        } else {
            return JSONObjectTranslatable.copy(json);
        }
    }
    private JSONObject requestChart(boolean refresh, APIVersion version, String symbol) {
        final long started = System.currentTimeMillis();
        final LinkedHashMap<String, String> headers = getHeaders();
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        query.put("interval", "1d");
        query.put("symbol", symbol);
        query.put("range", "10y");
        final String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-chart";
        final JSONObject json = requestJSONObject(url, headers, query);
        final JSONObject chartJSON = json.getJSONObject("chart");
        final JSONObject resultsJSON = chartJSON.getJSONArray("result").getJSONObject(0);
        final JSONObject quotesJSON = resultsJSON.getJSONObject("indicators").getJSONArray("quote").getJSONObject(0);
        final JSONArray closeArray = quotesJSON.getJSONArray("close");
        final JSONArray timestampsArray = resultsJSON.getJSONArray("timestamp");
        final int max = timestampsArray.length()-1;
        final JSONObject jsonObject = new JSONObject();
        IntStream.range(0, max).parallel().forEach(index -> {
            final long timestamp = timestampsArray.getLong(index);
            final float price = (float) closeArray.getDouble(index);
            jsonObject.put("" + timestamp, price);
        });

        jsonObject.put("request_epoch", started);
        WLLogger.logInfo("YahooFinance - " + (refresh ? "refreshed" : "loaded") + " chart for symbol \"" + symbol + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
        return jsonObject;
    }

    private void getStockProfile(String symbol, CompletionHandler handler) {
        final JSONObject json = getJSONObject(Folder.SERVICES_FINANCE_YAHOO_FINANCE_CHARTS, symbol, new CompletionHandler() {
            @Override
            public String loadJSONArrayString() {
                final LinkedHashMap<String, String> query = new LinkedHashMap<>();
                query.put("symbol", symbol);
                query.put("region", "US");
                final JSONObject json = requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-profile", getHeaders(), query);
                final JSONObject quoteType = json.getJSONObject("quoteType");
                final String shortName = quoteType.getString("shortName"), longName = quoteType.getString("longName");

                final JSONObject assetProfile = json.getJSONObject("assetProfile");
                return null;
            }
        });
    }

    private JSONObject requestYahooFinanceJSONObject(String url, LinkedHashMap<String, String> headers, LinkedHashMap<String, String> query) {
        return requestJSONObject(url, headers, query);
    }
}
