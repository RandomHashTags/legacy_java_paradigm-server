package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.RestAPI;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.settings.Settings;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.TicketmasterMusicEvent;
import me.randomhashtags.worldlaws.upcoming.events.TicketmasterVenue;
import me.randomhashtags.worldlaws.upcoming.events.UpcomingEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public enum Ticketmaster implements RestAPI {
    INSTANCE;

    private JSONObject getEvents(String segmentID) {
        final String apiKey = Settings.PrivateValues.Ticketmaster.getAPIKey();
        final LinkedHashMap<String, String> query = new LinkedHashMap<>();
        final LocalDate now = LocalDate.now();
        final String startDateTime = getFormattedDateString(now);
        final String endDateTime = getFormattedDateString(now.plusDays(7));
        query.put("apikey", apiKey);
        query.put("locale", "*");
        query.put("startDateTime", startDateTime);
        query.put("endDateTime", endDateTime);
        query.put("page", "0");
        query.put("size", "500");
        query.put("segmentId", segmentID);

        final String url = "https://app.ticketmaster.com/discovery/v2/events";
        return requestJSONObject(url, GET_CONTENT_HEADERS, query);
    }
    private String getFormattedDateString(LocalDate date) {
        final int monthValue = date.getMonthValue(), day = date.getDayOfMonth();
        return date.getYear() + "-" + (monthValue < 10 ? "0" : "") + monthValue + "-" + (day < 10 ? "0" : "") + day + "T00:00:00Z";
    }

    private static abstract class TicketmasterController extends LoadedUpcomingEventController {
        public abstract String getSegmentID();

        public JSONObject getEvents() {
            return Ticketmaster.INSTANCE.getEvents(getSegmentID());
        }
    }

    public static final class Music extends TicketmasterController {

        @Override
        public String getSegmentID() {
            return "KZFzniwnSyZfZ7v7nJ";
        }

        @Override
        public UpcomingEventType getType() {
            return UpcomingEventType.TICKETMASTER_MUSIC_EVENT;
        }

        @Override
        public void load() {
            final UpcomingEventType eventType = getType();
            final JSONObject json = getEvents();
            if(json != null && json.has("_embedded")) {
                final JSONArray embeddedEvents = json.getJSONObject("_embedded").getJSONArray("events");
                new CompletableFutures<JSONObject>().stream(embeddedEvents, eventJSON -> {
                    if(!eventJSON.getBoolean("test") && eventJSON.has("priceRanges") && eventJSON.has("ticketLimit") && eventJSON.has("seatmap")) {
                        final JSONObject dateStartJSON = eventJSON.getJSONObject("dates").getJSONObject("start");
                        if(dateStartJSON.has("dateTime")) {
                            final JSONObject priceRange = getPriceRangeJSONFrom(eventJSON.getJSONArray("priceRanges"));
                            final JSONObject ticketLimit = eventJSON.getJSONObject("ticketLimit");
                            final String seatMapURL = eventJSON.getJSONObject("seatmap").getString("staticUrl");
                            final JSONObject eventEmbeddedJSON = eventJSON.getJSONObject("_embedded");
                            final JSONArray venuesArray = eventEmbeddedJSON.getJSONArray("venues");

                            final String[] dateTimeValues = dateStartJSON.getString("dateTime").split("T"), dateValues = dateTimeValues[0].split("-"), timeValues = dateTimeValues[1].replace("Z", "").split(":");
                            final int year = Integer.parseInt(dateValues[0]), month = Integer.parseInt(dateValues[1]), day = Integer.parseInt(dateValues[2]);
                            final EventDate eventDate = new EventDate(Month.of(month), day, year);

                            final String name = eventJSON.getString("name");
                            final String identifier = getEventDateIdentifier(eventDate.getDateString(), name);

                            final String eventURL = eventJSON.getString("url"), imageURL = getImageURLFrom(eventJSON.getJSONArray("images"));
                            final EventSources sources = new EventSources(new EventSource("Ticketmaster: " + name, eventURL));
                            final List<TicketmasterVenue> venues = getVenuesFrom(venuesArray);
                            final TicketmasterMusicEvent event = new TicketmasterMusicEvent(eventDate, name, null, imageURL, ticketLimit, priceRange, seatMapURL, venues, sources);
                            putLoadedPreUpcomingEvent(event.toPreUpcomingEventJSON(eventType, identifier, null));
                            putUpcomingEvent(identifier, event);
                        }
                    }
                });
            }
        }

        @Override
        public UpcomingEvent parseUpcomingEvent(JSONObject json) {
            return new TicketmasterMusicEvent(json);
        }

        private String getImageURLFrom(JSONArray imagesArray) {
            String imageURL = null;
            int maxWidth = -1;
            for(Object obj : imagesArray) {
                final JSONObject imageJSON = (JSONObject) obj;
                if(imageJSON.has("ratio")) {
                    if(imageJSON.getString("ratio").equals("16_9")) {
                        final int width = imageJSON.getInt("width");
                        if(maxWidth == -1 || width > maxWidth) {
                            maxWidth = width;
                            imageURL = imageJSON.getString("url");
                        }
                    }
                }
            }
            return imageURL;
        }
        private JSONObject getPriceRangeJSONFrom(JSONArray array) {
            final JSONObject json = array.getJSONObject(0);
            json.remove("type");
            return json;
        }
        private List<TicketmasterVenue> getVenuesFrom(JSONArray array) {
            final List<TicketmasterVenue> venues = new ArrayList<>();
            for(Object obj : array) {
                final JSONObject json = (JSONObject) obj;
                if(!json.getBoolean("test")) {
                    final String name = json.getString("name");
                    final String url = json.getString("url");

                    String imageURL = null;
                    int width = 0;
                    if(json.has("images")) {
                        for(Object imageObj : json.getJSONArray("images")) {
                            final JSONObject imageJSON = (JSONObject) imageObj;
                            final int imageWidth = imageJSON.getInt("width");
                            if(width == 0 || imageWidth > 0) {
                                width = imageWidth;
                                imageURL = imageJSON.getString("url");
                            }
                        }
                    }

                    final String countryCode = json.getJSONObject("country").getString("countryCode");
                    final String subdivisionName = json.has("state") ? json.getJSONObject("state").getString("name") : null;
                    final String cityName = json.getJSONObject("city").getString("name");

                    Location location = null;
                    if(json.has("location")) {
                        final JSONObject locationJSON = json.getJSONObject("location");
                        final double latitude = locationJSON.getDouble("latitude"), longitude = locationJSON.getDouble("longitude");
                        location = new Location(latitude, longitude);
                    }

                    String generalRule = null, childRule = null;
                    if(json.has("generalInfo")) {
                        final JSONObject generalInfo = json.getJSONObject("generalInfo");
                        generalRule = generalInfo.optString("generalRule", null);
                        childRule = generalInfo.optString("childRule", null);
                    }

                    final String parkingDetail = json.optString("parkingDetail", null);
                    final String accessibleSeatingDetail = json.optString("accessibleSeatingDetail", null);
                    final TicketmasterVenue venue = new TicketmasterVenue(name, imageURL, location, countryCode, subdivisionName, cityName, generalRule, childRule, parkingDetail, accessibleSeatingDetail);
                    venues.add(venue);
                }
            }
            return venues;
        }
    }
}
