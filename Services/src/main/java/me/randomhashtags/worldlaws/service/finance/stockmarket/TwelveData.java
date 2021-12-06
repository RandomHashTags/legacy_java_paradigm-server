package me.randomhashtags.worldlaws.service.finance.stockmarket;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.service.JSONDataValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.stream.StreamSupport;

// https://twelvedata.com
public enum TwelveData implements StockService {
    INSTANCE;

    @Override
    public JSONDataValue getJSONDataValue() {
        return JSONDataValue.FINANCE_TWELVE_DATA;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_TWELVE_DATA;
    }

    private String getAPIKey() {
        return "***REMOVED***";
    }

    @Override
    public void getAutoComplete(APIVersion version, String input, CompletionHandler handler) {
    }

    @Override
    public void getMovers(APIVersion version, CompletionHandler handler) {
    }

    @Override
    public void getQuotes(APIVersion version, HashSet<String> symbols, CompletionHandler handler) {
        // TODO: every symbol = 1 quota request (doo doo; total quota required to use this = symbols*2)
        makeQuotaRequest(JSONDataValue.FINANCE_TWELVE_DATA, 2, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                final StringBuilder builder = new StringBuilder();
                boolean isFirst = true;
                for(String symbol : symbols) {
                    builder.append(isFirst ? "" : ",").append(symbol);
                    isFirst = false;
                }
                final String typeURL = "https://api.twelvedata.com/%type%?symbol=" + builder.toString() + "&type=Stock&format=JSON&apikey=" + getAPIKey();
                final String priceURL = typeURL.replace("%type%", "price"), quoteURL = typeURL.replace("%type%", "quote");
                requestJSONObject(priceURL, RequestMethod.GET, new CompletionHandler() {
                    @Override
                    public void handleJSONObject(JSONObject json) {
                        if(json != null) {
                            final HashMap<String, Float> prices = new HashMap<>();
                            json.keySet().parallelStream().forEach(symbol -> {
                                final JSONObject symbolJSON = json.getJSONObject(symbol);
                                final float price = symbolJSON.getFloat("price");
                                prices.put(symbol, price);
                            });
                            getQuotes(version, quoteURL, symbols, prices, handler);
                        } else {
                            handler.handleString(null);
                        }
                    }
                });
            }
        });
    }
    private void getQuotes(APIVersion version, String quoteURL, HashSet<String> symbols, HashMap<String, Float> prices, CompletionHandler handler) {
        requestJSONObject(quoteURL, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                if(json != null) {
                    final HashSet<Stock> stocks = new HashSet<>();
                    symbols.parallelStream().forEach(symbol -> {
                        final JSONObject stockJSON = json.getJSONObject(symbol);
                        final String name = stockJSON.getString("name");
                        final float open = stockJSON.getFloat("open");
                        final float change = stockJSON.getFloat("change");
                        final float changePercent = stockJSON.getFloat("percent_change");
                        final float high = stockJSON.getFloat("high");
                        final float low = stockJSON.getFloat("low");
                        final float price = prices.get(symbol);
                        final StockQuote quote = new StockQuote(open, change, changePercent, price, high, low);
                        final Stock stock = new Stock(symbol, name, null, quote, null);
                        stocks.add(stock);
                    });
                    final StringBuilder builder = new StringBuilder();
                    boolean isFirst = true;
                    for(Stock targetStock : stocks) {
                        builder.append(isFirst ? "" : ",").append(targetStock.toString());
                        isFirst = false;
                    }
                    handler.handleString(builder.toString());
                } else {
                    handler.handleString(null);
                }
            }
        });
    }

    @Override
    public void getChart(APIVersion version, String symbol, CompletionHandler handler) {
        getJSONObject(Folder.SERVICES_FINANCE_TWELVE_DATA_CHARTS, symbol, new CompletionHandler() {
            @Override
            public void load(CompletionHandler handler) {
                makeQuotaRequest(JSONDataValue.FINANCE_TWELVE_DATA, new CompletionHandler() {
                    @Override
                    public void handleObject(Object object) {
                        requestChart(symbol, handler);
                    }
                });
            }

            @Override
            public void handleJSONObject(JSONObject json) {
                handler.handleString(json.toString());
            }
        });
    }

    private void requestChart(String symbol, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final int outputsize = 3650;
        final String interval = "1day", format = "JSON";
        final String url = "https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=" + interval + "&outputsize=" + outputsize + "&format=" + format + "&apikey=" + getAPIKey();
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONArray valuesArray = json.getJSONArray("values");
                final TimeZone timezone = TimeZone.getTimeZone(json.getJSONObject("meta").getString("exchange_timezone"));
                final Calendar calendar = Calendar.getInstance(timezone);
                final HashSet<String> values = new HashSet<>();
                StreamSupport.stream(valuesArray.spliterator(), true).forEach(obj -> {
                    final JSONObject valueJSON = (JSONObject) obj;
                    final String[] datetimeValues = valueJSON.getString("datetime").split("-");
                    final int year = Integer.parseInt(datetimeValues[0]), month = Integer.parseInt(datetimeValues[1]), day = Integer.parseInt(datetimeValues[2]);
                    calendar.set(year, month, day);
                    final long timestamp = calendar.getTimeInMillis();
                    final double close = valueJSON.getDouble("close");
                    values.add("\"" + timestamp + "\":" + close);
                });
                final StringBuilder builder = new StringBuilder();
                boolean isFirst = true;
                for(String string : values) {
                    builder.append(isFirst ? "" : ",").append(string);
                    isFirst = false;
                }
                final StockChart chart = new StockChart(started, builder.toString());
                WLLogger.logInfo("TwelveData - loaded chart for symbol \"" + symbol + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleString(chart.toString());
            }
        });
    }
}
