package me.randomhashtags.worldlaws.country;

import java.util.HashSet;

public enum WLTourWalking {
    INSTANCE;

    /* SOURCES
    https://www.youtube.com/c/ProWalks/videos
     */

    public HashSet<WLTour> get(WLCountry country) {
        switch (country) {
            case ITALY: return null;
            default: return null;
        }
    }
}
