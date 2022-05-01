package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class TicketmasterMusicEvent extends UpcomingEvent {
    private final JSONObject ticketLimit, priceRange;
    private final String seatMapURL;
    private final List<TicketmasterVenue> venues;

    public TicketmasterMusicEvent(JSONObject json) {
        super(json);
        final JSONObject properties = json.getJSONObject("properties");
        seatMapURL = properties.getString(UpcomingEventValue.TICKETMASTER_MUSIC_SEAT_MAP_URL.getKey());
        ticketLimit = properties.getJSONObject("ticketLimit");
        priceRange = properties.getJSONObject("priceRange");
        venues = new ArrayList<>();
        final JSONArray venuesArray = properties.getJSONArray(UpcomingEventValue.TICKETMASTER_MUSIC_VENUES.getKey());
        for(Object obj : venuesArray) {
            final JSONObject venueJSON = (JSONObject) obj;
            final TicketmasterVenue venue = new TicketmasterVenue(venueJSON);
            venues.add(venue);
        }
    }
    public TicketmasterMusicEvent(EventDate date, String title, String description, String imageURL, JSONObject ticketLimit, JSONObject priceRange, String seatMapURL, List<TicketmasterVenue> venues, EventSources sources) {
        super(date, title, description, imageURL, null, null, sources);
        this.ticketLimit = ticketLimit;
        this.priceRange = priceRange;
        this.seatMapURL = seatMapURL;
        this.venues = venues;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TICKETMASTER_MUSIC_EVENT;
    }

    private JSONObject getVenuesJSON() {
        final JSONObject json = new JSONObject();
        for(TicketmasterVenue venue : venues) {
            json.put(venue.getName(), venue.toJSONObject());
        }
        return json;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        json.put(UpcomingEventValue.TICKETMASTER_MUSIC_SEAT_MAP_URL.getKey(), seatMapURL);
        json.put("ticketLimit", ticketLimit);
        json.put("priceRange", priceRange);
        json.put(UpcomingEventValue.TICKETMASTER_MUSIC_VENUES.getKey(), getVenuesJSON());
        return json;
    }
}
