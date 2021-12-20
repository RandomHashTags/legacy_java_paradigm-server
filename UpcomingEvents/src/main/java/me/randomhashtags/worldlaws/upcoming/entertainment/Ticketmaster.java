package me.randomhashtags.worldlaws.upcoming.entertainment;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import me.randomhashtags.worldlaws.upcoming.LoadedUpcomingEventController;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.events.TicketmasterMusicEvent;
import me.randomhashtags.worldlaws.upcoming.events.TicketmasterVenue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public enum Ticketmaster implements RestAPI {
    INSTANCE;

    private void getEvents(String segmentID, CompletionHandler handler) {
        final String apiKey = "***REMOVED***";
        final HashMap<String, String> headers = new HashMap<>(CONTENT_HEADERS);
        final HashMap<String, String> query = new HashMap<>();
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
        requestJSONObject(url, RequestMethod.GET, headers, query, handler);
    }
    private String getFormattedDateString(LocalDate date) {
        final int monthValue = date.getMonthValue(), day = date.getDayOfMonth();
        return date.getYear() + "-" + (monthValue < 10 ? "0" : "") + monthValue + "-" + (day < 10 ? "0" : "") + day + "T00:00:00Z";
    }

    private static abstract class TicketmasterController extends LoadedUpcomingEventController {
        public abstract String getSegmentID();

        public void getEvents(CompletionHandler handler) {
            Ticketmaster.INSTANCE.getEvents(getSegmentID(), handler);
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
        public void load(CompletionHandler handler) {
            final UpcomingEventType eventType = getType();
            getEvents(new CompletionHandler() {
                @Override
                public void handleJSONObject(JSONObject json) {
                    if(json != null) {
                        final JSONArray embeddedEvents = json.getJSONObject("_embedded").getJSONArray("events");
                        ParallelStream.stream(embeddedEvents.spliterator(), objectJSON -> {
                            final JSONObject eventJSON = (JSONObject) objectJSON;
                            if(!eventJSON.getBoolean("test")) {
                                final String name = eventJSON.getString("name");
                                final JSONObject dateStartJSON = eventJSON.getJSONObject("dates").getJSONObject("start");
                                if(dateStartJSON.has("dateTime")) {
                                    final String[] dateTimeValues = dateStartJSON.getString("dateTime").split("T"), dateValues = dateTimeValues[0].split("-"), timeValues = dateTimeValues[1].replace("Z", "").split(":");
                                    final int year = Integer.parseInt(dateValues[0]), month = Integer.parseInt(dateValues[1]), day = Integer.parseInt(dateValues[2]);
                                    final LocalDate date = LocalDate.of(year, month, day);
                                    final EventDate eventDate = new EventDate(date);
                                    final String identifier = getEventDateIdentifier(eventDate.getDateString(), name);
                                    final String eventURL = eventJSON.getString("url"), imageURL = getImageURLFrom(eventJSON.getJSONArray("images"));
                                    if(eventJSON.has("priceRanges") && eventJSON.has("ticketLimit") && eventJSON.has("seatmap")) {
                                        final JSONObject priceRange = getPriceRangeJSONFrom(eventJSON.getJSONArray("priceRanges"));
                                        final JSONObject ticketLimit = eventJSON.getJSONObject("ticketLimit");
                                        final String seatMapURL = eventJSON.getJSONObject("seatmap").getString("staticUrl");

                                        final JSONObject eventEmbeddedJSON = eventJSON.getJSONObject("_embedded");
                                        final JSONArray venuesArray = eventEmbeddedJSON.getJSONArray("venues");

                                        final EventSources sources = new EventSources(new EventSource("Ticketmaster: " + name, eventURL));
                                        final HashSet<TicketmasterVenue> venues = getVenuesFrom(venuesArray);
                                        final TicketmasterMusicEvent event = new TicketmasterMusicEvent(name, null, imageURL, ticketLimit, priceRange, seatMapURL, venues, sources);
                                        putLoadedPreUpcomingEvent(identifier, event.toPreUpcomingEventJSON(eventType, identifier, null));
                                        putUpcomingEvent(identifier, event.toString());
                                    }
                                }
                            }
                        });
                    }
                    handler.handleString(null);
                }
            });
        }

        private String getImageURLFrom(JSONArray imagesArray) {
            String imageURL = null;
            int maxWidth = -1;
            for(Object obj : imagesArray) {
                final JSONObject imageJSON = (JSONObject) obj;
                if(imageJSON.getString("ratio").equals("16_9")) {
                    final int width = imageJSON.getInt("width");
                    if(maxWidth == -1 || width > maxWidth) {
                        maxWidth = width;
                        imageURL = imageJSON.getString("url");
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
        private HashSet<TicketmasterVenue> getVenuesFrom(JSONArray array) {
            final HashSet<TicketmasterVenue> venues = new HashSet<>();
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

                    final JSONObject locationJSON = json.getJSONObject("location");
                    final double latitude = locationJSON.getDouble("latitude"), longitude = locationJSON.getDouble("longitude");
                    final Location location = new Location(latitude, longitude);

                    String generalRule = null, childRule = null;
                    if(json.has("generalInfo")) {
                        final JSONObject generalInfo = json.getJSONObject("generalInfo");
                        generalRule = generalInfo.has("generalRule") ? generalInfo.getString("generalRule") : null;
                        childRule = generalInfo.has("childRule") ? generalInfo.getString("childRule") : null;
                    }

                    final String parkingDetail = json.has("parkingDetail") ? json.getString("parkingDetail") : null;
                    final String accessibleSeatingDetail = json.has("accessibleSeatingDetail") ? json.getString("accessibleSeatingDetail") : null;
                    final TicketmasterVenue venue = new TicketmasterVenue(name, imageURL, location, countryCode, subdivisionName, cityName, generalRule, childRule, parkingDetail, accessibleSeatingDetail);
                    venues.add(venue);
                }
            }
            return venues;
        }
    }
}
