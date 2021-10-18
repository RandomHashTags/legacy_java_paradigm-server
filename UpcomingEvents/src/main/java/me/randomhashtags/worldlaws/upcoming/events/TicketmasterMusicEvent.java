package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import org.json.JSONObject;

import java.util.HashSet;

public final class TicketmasterMusicEvent extends UpcomingEvent {
    private final JSONObject ticketLimit, priceRange;
    private final String seatMapURL;
    private final HashSet<TicketmasterVenue> venues;

    public TicketmasterMusicEvent(String title, String description, String imageURL, JSONObject ticketLimit, JSONObject priceRange, String seatMapURL, HashSet<TicketmasterVenue> venues, EventSources sources) {
        super(title, description, imageURL, null, null, sources);
        this.ticketLimit = ticketLimit;
        this.priceRange = priceRange;
        this.seatMapURL = seatMapURL;
        this.venues = venues;
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.TICKETMASTER_MUSIC_EVENT;
    }

    private String getVenuesJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(TicketmasterVenue venue : venues) {
            builder.append(isFirst ? "" : ",").append(venue.toString());
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String getPropertiesJSONObject() {
        return "{" +
                "\"seatMapURL\":\"" + seatMapURL + "\"," +
                "\"ticketLimit\":" + ticketLimit.toString() + "," +
                "\"priceRange\":" + priceRange.toString() + "," +
                "\"venues\":" + getVenuesJSON() +
                "}";
    }
}
