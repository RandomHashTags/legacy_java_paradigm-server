package me.randomhashtags.worldlaws.currency;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.WLLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public enum CurrencyExchange implements RestAPI {
    INSTANCE;

    private HashMap<Integer, String> exchangeRates;
    private HashMap<Integer, HashMap<String, String>> countryExchangeRates;

    CurrencyExchange() {
        exchangeRates = new HashMap<>();
        countryExchangeRates = new HashMap<>();
    }

    public void getExchangeRates(int year, CompletionHandler handler) {
        if(exchangeRates.containsKey(year)) {
            handler.handleString(exchangeRates.get(year));
        } else {
            refreshCurrencyExchange(year, handler);
        }
    }
    public void getExchangeRates(int year, String country, CompletionHandler handler) {
        if(countryExchangeRates.containsKey(year)) {
            final String value = getValue(year, country);
            handler.handleString(value);
        } else {
            refreshCurrencyExchange(year, new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final String value = getValue(year, country);
                    handler.handleString(value);
                }
            });
        }
    }
    private String getValue(int year, String country) {
        country = country.toLowerCase();
        return countryExchangeRates.containsKey(year) ? countryExchangeRates.get(year).getOrDefault(country.toLowerCase(), null) : null;
    }

    private void refreshCurrencyExchange(int year, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final String url = "https://www.transparency.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=record_date,country,currency,exchange_rate&filter=record_calendar_year:eq:" + year + "&sort=-record_date&page[size]=200";
        requestJSONObject(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONArray data = json.getJSONArray("data");
                final JSONObject first = data.getJSONObject(0);
                final int month = Integer.parseInt(first.getString("record_date").split("-")[1]);
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                countryExchangeRates.put(year, new HashMap<>());
                for(Object obj : data) {
                    final JSONObject currencyJSON = (JSONObject) obj;
                    final String recordDate = currencyJSON.getString("record_date");
                    final int recordMonth = Integer.parseInt(recordDate.split("-")[1]);
                    if(recordMonth == month) {
                        final String country = currencyJSON.getString("country");
                        final String currencyName = currencyJSON.getString("currency");
                        final String exchangeRate = currencyJSON.getString("exchange_rate");
                        final Currency currency = new Currency(recordDate, country, currencyName, exchangeRate);
                        final String string = currency.toString();
                        countryExchangeRates.get(year).put(country.toLowerCase().replace(" ", "_"), string);
                        builder.append(isFirst ? "" : ",").append(string);
                        isFirst = false;
                    }
                }
                builder.append("]");
                final String string = builder.toString();
                exchangeRates.put(year, string);
                WLLogger.logInfo("CurrencyExchange - refreshed rates for year " + year + " (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handleString(string);
            }
        });
    }
}
