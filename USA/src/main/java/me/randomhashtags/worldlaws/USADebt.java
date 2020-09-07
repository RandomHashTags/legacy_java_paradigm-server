package me.randomhashtags.worldlaws;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public enum USADebt implements RestAPI {
    INSTANCE;

    private String historic, current;

    public void update() {
        updateCurrent();
        updateHistoric();
        autoUpdate();
    }

    private void updateCurrent() {
        requestJSON("https://www.treasurydirect.gov/NP_WS/debt/current?format=json", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject obj = new JSONObject(object.toString());
                current = obj.toString();
            }
        });
    }
    public void updateHistoric() {
        requestJSON("https://www.treasurydirect.gov/NP_WS/debt/search?startdate=1993-01-01&enddate=9999-12-31&format=json", RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final JSONObject obj = new JSONObject(object.toString());
                final JSONArray array = obj.getJSONArray("entries");
                historic = array.toString();
            }
        });
    }
    private void autoUpdate() {
        final long twentyFourHrs = 1000*60*60*24;
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateCurrent();
                updateHistoric();
            }
        }, twentyFourHrs, twentyFourHrs);
    }

    public String getCurrent() {
        return current;
    }
    public String getHistoric() {
        return historic;
    }
}
