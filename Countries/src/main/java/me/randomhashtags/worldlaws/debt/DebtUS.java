package me.randomhashtags.worldlaws.debt;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.location.WLCountry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public enum DebtUS implements ICountryDebt {
    INSTANCE;

    private String current;
    private HashMap<Integer, String> years;

    @Override
    public WLCountry getCountryBackendID() {
        return WLCountry.UNITED_STATES;
    }

    @Override
    public void getCurrentJSON(CompletionHandler handler) {
        if(current != null) {
            handler.handle(current);
        } else {
            updateCurrent(handler);
            autoUpdate(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    updateCurrent(null);
                }
            });
        }
    }

    @Override
    public void getFromYear(int year, CompletionHandler handler) {
        if(years == null) {
            years = new HashMap<>();
        }
        if(years.containsKey(year)) {
            handler.handle(years.get(year));
        } else {
            updateYear(year, handler);

            autoUpdate(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    updateYear(year, null);
                }
            });
        }
    }

    private void updateCurrent(CompletionHandler handler) {
        requestJSONObject("https://www.treasurydirect.gov/NP_WS/debt/current?format=json", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final CountryDebt debt = getCountryDebt(json);
                current = debt.toString();
                if(handler != null) {
                    handler.handle(current);
                }
            }
        });
    }
    private void updateYear(int year, CompletionHandler handler) {
        final int targetYear = Math.max(1993, year);
        requestJSONObject("https://www.treasurydirect.gov/NP_WS/debt/search?startdate=" + targetYear + "-01-01&enddate=" + targetYear + "-12-31&format=json", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject entriesJSON) {
                final JSONArray array = entriesJSON.getJSONArray("entries");
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Object o : array) {
                    final JSONObject json = (JSONObject) o;
                    final CountryDebt debt = getCountryDebt(json);
                    builder.append(isFirst ? "" : ",").append(debt.toString());
                    isFirst = false;
                }
                builder.append("]");
                final String string = builder.toString();
                years.put(targetYear, string);
                if(handler != null) {
                    handler.handle(string);
                }
            }
        });
    }

    private CountryDebt getCountryDebt(JSONObject json) {
        final double governmentHoldings = json.getDouble("governmentHoldings");
        final double publicDebt = json.getDouble("publicDebt");
        final double totalDebt = json.getDouble("totalDebt");
        return new CountryDebt(governmentHoldings, publicDebt, totalDebt);
    }

    private void autoUpdate(CompletionHandler handler) {
        final long twentyFourHrs = 1000*60*60*24;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.handle(null);
            }
        }, twentyFourHrs, twentyFourHrs);
    }
}
