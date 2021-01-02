package me.randomhashtags.worldlaws.info.rankings;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public enum MinimumWage implements CountryRankingService {
    INSTANCE;

    private String rankedJSON;
    private HashMap<String, String> wageHistory;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.RANKING_MINIMUM_WAGE_HISTORY;
    }

    @Override
    public void getRankedJSON(CompletionHandler handler) {
        if(rankedJSON != null) {
            handler.handle(rankedJSON);
        } else {
            handler.handle(null);
        }
    }

    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(wageHistory != null) {
            final String value = getHistory(countryBackendID);
            handler.handle(value);
        } else {
            load(new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String history = getHistory(countryBackendID);
                    handler.handle(history);
                }
            });
        }
    }
    private String getHistory(String countryName) {
        return wageHistory.getOrDefault(countryName, "null");
    }
    private void load(CompletionHandler handler) {
        wageHistory = new HashMap<>();

        final int startingYear = 1963, endingYear = 2020, years = endingYear-startingYear;
        final String url = "https://stats.oecd.org/sdmx-json/data/RMW/AUS+BEL+CAN+CHL+COL+CZE+EST+FRA+DEU+GRC+HUN+IRL+ISR+JPN+KOR+LVA+LTU+LUX+MEX+NLD+NZL+POL+PRT+SVK+SVN+ESP+TUR+GBR+USA+CRI+BRA+RUS.PPP.H/all?startTime=" + startingYear + "&endTime=" + endingYear;
        final HashMap<String, StringBuilder> wageHistories = new HashMap<>();
        requestJSON(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject obj = new JSONObject(object.toString());
                final JSONArray countries = obj.getJSONObject("structure").getJSONObject("dimensions").getJSONArray("series").getJSONObject(0).getJSONArray("values");
                final JSONObject dataSets = obj.getJSONArray("dataSets").getJSONObject(0).getJSONObject("series");
                int index = 0;
                for(Object country : countries) {
                    final JSONObject json = (JSONObject) country;
                    final String countryName = json.getString("name").toLowerCase().replace("n federation", "");
                    wageHistories.put(countryName, new StringBuilder("{"));
                    final JSONObject values = dataSets.getJSONObject(index + ":0:0").getJSONObject("observations");

                    boolean isFirst = true;
                    for(int i = 0; i < years; i++) {
                        final String key = Integer.toString(i);
                        if(values.has(key)) {
                            final float value = (float) values.getJSONArray(key).getDouble(0);
                            final int year = startingYear+i;
                            wageHistories.get(countryName).append(isFirst ? "" : ",").append("\"").append(year).append("\":").append(value);
                            isFirst = false;
                        }
                    }
                    index += 1;
                }
                for(Map.Entry<String, StringBuilder> builders : wageHistories.entrySet()) {
                    wageHistory.put(builders.getKey(), builders.getValue().append("}").toString());
                }
                if(handler != null) {
                    handler.handle(null);
                }
            }
        });
    }
}
