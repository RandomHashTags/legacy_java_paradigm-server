package me.randomhashtags.worldlaws.upcoming.events;

import me.randomhashtags.worldlaws.EventDate;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventType;
import me.randomhashtags.worldlaws.upcoming.UpcomingEventValue;
import org.json.JSONObject;

public final class ScienceYearReviewEvent extends UpcomingEvent {

    private final JSONObjectTranslatable years;

    public ScienceYearReviewEvent(JSONObject json) {
        super(json);
        final String yearsKey = UpcomingEventValue.SCIENCE_YEAR_REVIEW_YEARS.getKey();
        years = JSONObjectTranslatable.copy(json.getJSONObject(yearsKey), true);
    }
    public ScienceYearReviewEvent(EventDate date, String title, String description, String imageURL, JSONObjectTranslatable years) {
        super(date, title, description, imageURL, null, null, null);
        this.years = years;
        insertProperties();
    }

    @Override
    public UpcomingEventType getType() {
        return UpcomingEventType.SCIENCE_YEAR_REVIEW;
    }

    @Override
    public JSONObjectTranslatable getPropertiesJSONObject() {
        final String yearsKey = UpcomingEventValue.SCIENCE_YEAR_REVIEW_YEARS.getKey();
        final JSONObjectTranslatable json = new JSONObjectTranslatable(yearsKey);
        json.put(yearsKey, years);
        return json;
    }
}
