package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;

public interface Story {
    WLCountry getCountry();
    SovereignStateSubdivision getSubdivision();

    String getTitle();
    String getDescription();
    String getImageURL();
    String getVideoURL();
    EventSources getSources();

    default String toPreStory() {
        final String description = LocalServer.fixEscapeValues(getDescription()), imageURL = getImageURL(), videoURL = getVideoURL();
        final WLCountry country = getCountry();
        final SovereignStateSubdivision subdivision = getSubdivision();
        return "\"" + LocalServer.fixEscapeValues(getTitle()) + "\":{" +
                (description != null ? "\"description\":\"" + description + "\"," : "") +
                (imageURL != null ? "\"imageURL\":\"" + imageURL + "\"," : "") +
                (videoURL != null ? "\"videoURL\":\"" + videoURL + "\"," : "") +
                (country != null ? "\"country\":\"" + country.getBackendID() + "\"," : "") +
                (subdivision != null ? "\"subdivision\":\"" + subdivision.getBackendID() + "\"," : "") +
                "\"sources\":" + getSources().toString() +
                "}";
    }
}
