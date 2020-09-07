package me.randomhashtags.worldlaws.event.space;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.RequestMethod;
import me.randomhashtags.worldlaws.event.EventDate;
import me.randomhashtags.worldlaws.event.EventSource;
import me.randomhashtags.worldlaws.event.EventSources;
import me.randomhashtags.worldlaws.event.USAEventController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Date;
import java.util.HashMap;

public enum SpaceX implements USAEventController {
    INSTANCE;

    private String json;
    private boolean isFirst;
    private HashMap<String, String> launchpads;

    SpaceX() {
        launchpads = new HashMap<>();
    }

    @Override
    public String getIdentifier() {
        return "spacex";
    }

    @Override
    public void getUpcomingEvents(CompletionHandler handler) {
        if(json != null) {
            handler.handle(json);
        } else {
            refreshUpcomingLaunches(handler);
        }
    }

    private void refreshUpcomingLaunches(CompletionHandler handler) {
        final String url = "https://api.spacexdata.com/v4/launches/upcoming";
        requestJSON(url, RequestMethod.GET, new CompletionHandler() {
            @Override
            public void handle(Object object) {
                final StringBuilder builder = new StringBuilder("[");
                final JSONArray array = new JSONArray(object.toString());
                final EventSource source = new EventSource("SpaceX GitHub", "https://github.com/r-spacex/SpaceX-API");
                final EventSources sources = new EventSources(source);
                final Object last = array.get(array.length()-1);
                isFirst = true;
                for(Object obj : array) {
                    final JSONObject json = (JSONObject) obj;
                    final String title = json.getString("name");
                    final String description = json.get("details") instanceof String ? json.getString("details") : "null";
                    final long dateUnix = json.getLong("date_unix");
                    final EventDate date = fromUnix(dateUnix);
                    final String launchpadID = json.getString("launchpad");
                    getLaunchpad(launchpadID, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final JSONObject launchpadJSON = new JSONObject(object.toString());
                            final String location = launchpadJSON.getString("locality") + ", " + launchpadJSON.getString("region");
                            final SpaceEvent event = new SpaceEvent(date, title, description, location, sources);
                            builder.append(isFirst ? "" : ",").append(event.toJSON());
                            isFirst = false;

                            if(obj.equals(last)) {
                                builder.append("]");
                                final String string = builder.toString();
                                SpaceX.this.json = string;
                                handler.handle(string);
                            }
                        }
                    });
                }
            }
        });
    }
    private EventDate fromUnix(long unix) {
        final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        final String dateString = formatter.format(new Date(unix*1000));
        final String[] values = dateString.split(" "), dates = values[0].split("-"), times = values[1].split(":");
        final Month month = Month.of(Integer.parseInt(dates[0]));
        final int day = Integer.parseInt(dates[1]), year = Integer.parseInt(dates[2]);
        return new EventDate(month, day, year);
    }
    private void getLaunchpad(String id, CompletionHandler handler) {
        if(launchpads.containsKey(id)) {
            handler.handle(launchpads.get(id));
        } else {
            final String launchpadURL = "https://api.spacexdata.com/v4/launchpads/" + id;
            requestJSON(launchpadURL, RequestMethod.GET, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    final String string = object.toString();
                    launchpads.put(id, string);
                    handler.handle(string);
                }
            });
        }
    }
}
