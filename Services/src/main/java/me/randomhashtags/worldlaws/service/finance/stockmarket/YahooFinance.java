package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.SovereignStateInfo;
import me.randomhashtags.worldlaws.service.JSONDataValue;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

// https://rapidapi.com/apidojo/api/yahoo-finance1/
public enum YahooFinance implements StockService {
    INSTANCE;

    private HashMap<String, String> getHeaders() {
        final HashMap<String, String> headers = new HashMap<>();
        headers.put("x-rapidapi-key", YAHOO_FINANCE_RAPID_API_KEY);
        headers.put("x-rapidapi-host", "apidojo-yahoo-finance-v1.p.rapidapi.com");
        return headers;
    }

    @Override
    public JSONDataValue getJSONDataValue() {
        return JSONDataValue.FINANCE_YAHOO_FINANCE;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_YAHOO_FINANCE;
    }

    @Override
    public void getAutoComplete(APIVersion version, String input, CompletionHandler handler) {
        final String term = input.toLowerCase();
        getJSONObject(Folder.SERVICES_FINANCE_YAHOO_FINANCE_CHARTS, "_Auto completes", new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final HashMap<String, String> query = new HashMap<>();
                query.put("q", term);
                requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/auto-complete", RequestMethod.GET, getHeaders(), query, new CompletionHandler() {
                    @Override
                    public void handleJSONObject(JSONObject json) {
                        final JSONArray quotes = json.getJSONArray("quotes");
                        for(Object obj : quotes) {
                            final JSONObject quoteJSON = (JSONObject) obj;
                        }
                    }
                });
            }

            @Override
            public void handleJSONObject(JSONObject json) {
            }
        });
    }
    @Override
    public void getMovers(APIVersion version, CompletionHandler handler) {
        getMovers(version, 0, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONObject finance = json.getJSONObject("finance");
                final JSONArray results = finance.getJSONArray("result");
                final JSONArray dayGainers = results.getJSONObject(0).getJSONArray("quotes");
                final JSONArray dayLosers = results.getJSONObject(1).getJSONArray("quotes");
                final JSONArray mostActive = results.getJSONObject(2).getJSONArray("quotes");

                final HashSet<String> symbols = getQuoteSymbols(dayGainers);
                symbols.addAll(getQuoteSymbols(dayLosers));
                symbols.addAll(getQuoteSymbols(mostActive));
                getQuotes(version, symbols, handler);
            }
        });
    }
    private void getMovers(APIVersion version, int offset, CompletionHandler handler) {
        final HashMap<String, String> query = new HashMap<>();
        query.put("region", "US");
        query.put("count", "25");
        query.put("start", Integer.toString(offset));
        requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-movers", RequestMethod.GET, getHeaders(), query, handler);
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
    public void getQuotes(APIVersion version, HashSet<String> symbols, CompletionHandler handler) { // 50 symbols limit per request
        final long started = System.currentTimeMillis();
        final HashMap<String, String> headers = getHeaders();
        final HashMap<String, String> query = new HashMap<>();
        query.put("region", "US");
        final StringBuilder symbolBuilder = new StringBuilder();
        boolean isFirstSymbol = true;
        for(String symbol : symbols) {
            symbolBuilder.append(isFirstSymbol ? "" : ",").append(symbol);
            isFirstSymbol = false;
        }
        query.put("symbols", symbolBuilder.toString());

        final AtomicInteger completed = new AtomicInteger(0);
        final HashSet<Stock> stocks = new HashSet<>();
        requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/market/v2/get-quotes", RequestMethod.GET, headers, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONObject quoteResponse = json.getJSONObject("quoteResponse");
                final JSONArray results = quoteResponse.getJSONArray("result");
                final int max = results.length();
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
                    stocks.add(symbolStock);

                    if(completed.addAndGet(1) == max) {
                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirst = true;
                        for(Stock stock : stocks) {
                            builder.append(isFirst ? "" : ",").append(stock.toString());
                            isFirst = false;
                        }
                        builder.append("}");
                        final String string = builder.toString();
                        WLLogger.log(Level.INFO, "YahooFinance - loaded " + max + " quotes (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handleString(string);
                    }
                }
            }
        });
    }

    @Override
    public void getChart(APIVersion version, String symbol, CompletionHandler handler) {
        final Folder folder = Folder.SERVICES_FINANCE_YAHOO_FINANCE_CHARTS;
        getJSONObject(folder, symbol, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                requestChart(false, version, symbol, handler);
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                final long elapsedTime = System.currentTimeMillis() - json.getLong("request_epoch");
                if(elapsedTime >= 604_800) {
                    requestChart(true, version, symbol, new CompletionHandler() {
                        @Override
                        public void handleJSONObject(JSONObject json) {
                            setFileJSONObject(folder, symbol, json);
                            handler.handleString(json.toString());
                        }
                    });
                } else {
                    handler.handleString(json.toString());
                }
            }
        });
    }
    private void requestChart(boolean refresh, APIVersion version, String symbol, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final HashMap<String, String> headers = getHeaders();
        final HashMap<String, String> query = new HashMap<>();
        query.put("interval", "1d");
        query.put("symbol", symbol);
        query.put("range", "10y");
        final String url = "https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-chart";
        requestJSONObject(url, RequestMethod.GET, headers, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONObject chartJSON = json.getJSONObject("chart");
                final JSONObject resultsJSON = chartJSON.getJSONArray("result").getJSONObject(0);
                final JSONObject quotesJSON = resultsJSON.getJSONObject("indicators").getJSONArray("quote").getJSONObject(0);
                final JSONArray closeArray = quotesJSON.getJSONArray("close");
                final JSONArray timestampsArray = resultsJSON.getJSONArray("timestamp");
                final int max = timestampsArray.length()-1;
                final AtomicInteger completed = new AtomicInteger(0);
                final JSONObject jsonObject = new JSONObject();
                IntStream.range(0, max).parallel().forEach(index -> {
                    final long timestamp = timestampsArray.getLong(index);
                    final float price = (float) closeArray.getDouble(index);
                    jsonObject.put("" + timestamp, price);
                    if(completed.addAndGet(1) == max) {
                        jsonObject.put("request_epoch", started);
                        WLLogger.log(Level.INFO, "YahooFinance - " + (refresh ? "refreshed" : "loaded") + " chart for symbol \"" + symbol + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                        handler.handleJSONObject(jsonObject);
                    }
                });
            }
        });
    }

    private void getStockProfile(String symbol, CompletionHandler handler) {
        getJSONObject(Folder.SERVICES_FINANCE_YAHOO_FINANCE_CHARTS, symbol, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                final HashMap<String, String> query = new HashMap<>();
                query.put("symbol", symbol);
                query.put("region", "US");
                requestJSONObject("https://apidojo-yahoo-finance-v1.p.rapidapi.com/stock/v2/get-profile", RequestMethod.GET, getHeaders(), query, new CompletionHandler() {
                    @Override
                    public void handleJSONObject(JSONObject json) {
                        final JSONObject quoteType = json.getJSONObject("quoteType");
                        final String shortName = quoteType.getString("shortName"), longName = quoteType.getString("longName");

                        final JSONObject assetProfile = json.getJSONObject("assetProfile");
                    }
                });
            }

            @Override
            public void handleJSONObject(JSONObject json) {

            }
        });
    }

    private void requestYahooFinanceJSONObject(String url, HashMap<String, String> headers, HashMap<String, String> query, CompletionHandler handler) {
        requestJSONObject(url, RequestMethod.GET, headers, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleJSONObject(json);
            }
        });
    }
}
