package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.elections.Election;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Month;
import java.util.HashMap;

public enum Elections implements RestAPI {
    INSTANCE;

    private final String GOOGLE_CIVIC_API_KEY;
    private String upcomingElectionsJSON;

    Elections() {
        GOOGLE_CIVIC_API_KEY = "***REMOVED***";
    }

    public void getResponse(String value, CompletionHandler handler) {
        switch (value) {
            default:
                getAll(handler);
                break;
        }
    }

    private void getAll(CompletionHandler handler) {
        if(upcomingElectionsJSON != null) {
            handler.handle(upcomingElectionsJSON);
        } else {
            refreshAll(handler);
        }
    }

    private void refreshAll(CompletionHandler handler) {
        // https://developers.google.com/civic-information/docs/v2
        final long started = System.currentTimeMillis();
        final HashMap<String, String> query = new HashMap<>();
        query.put("key", GOOGLE_CIVIC_API_KEY);

        requestJSONObject("https://www.googleapis.com/civicinfo/v2/elections", RequestMethod.GET, CONTENT_HEADERS, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject jsonobject) {
                final JSONArray elections = jsonobject.getJSONArray("elections");
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(Object obj : elections) {
                    final JSONObject json = new JSONObject(obj.toString());
                    final long electionId = json.getLong("id");
                    final String name = json.getString("name"), electionDay = json.getString("electionDay"), ocdDivisionId = json.getString("ocdDivisionId");
                    final String[] electionDayValues = electionDay.split("-");
                    final int year = Integer.parseInt(electionDayValues[0]), month = Integer.parseInt(electionDayValues[1]), day = Integer.parseInt(electionDayValues[2]);
                    final EventDate date = new EventDate(Month.of(month), day, year);
                    final Election election = new Election(electionId, name, date, ocdDivisionId);
                    builder.append(isFirst ? "" : ",").append(election.toString());
                    isFirst = false;
                    //getRepresentatives(ocdDivisionId.replace("/", "%2F").replace(":", "%3A"));
                }
                builder.append("]");
                upcomingElectionsJSON = builder.toString();
                WLLogger.log(Level.INFO, "Elections - refreshed upcoming elections (took " + (System.currentTimeMillis()-started) + "ms)");
                handler.handle(upcomingElectionsJSON);
            }
        });
    }
    private void getRepresentatives(String ocdDivisionId) {
        final HashMap<String, String> query = new HashMap<>();
        query.put("key", GOOGLE_CIVIC_API_KEY);

        requestJSONObject("https://www.googleapis.com/civicinfo/v2/representatives/" + ocdDivisionId, RequestMethod.GET, CONTENT_HEADERS, query, new CompletionHandler() {
            @Override
            public void handleJSONObject(JSONObject json) {
                final JSONArray offices = json.getJSONArray("offices"), officials = json.getJSONArray("officials");
                int index = 0;
                for(Object obj : offices) {
                    final JSONObject office = new JSONObject(obj.toString());
                    final JSONObject person = new JSONObject(officials.get(index).toString());
                    WLLogger.log(Level.INFO, office.getString("name") + " - " + person.getString("name"));
                    index += 1;
                }
            }
        });
    }
}
