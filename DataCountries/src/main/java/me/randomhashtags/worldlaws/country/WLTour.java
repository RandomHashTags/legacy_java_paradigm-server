package me.randomhashtags.worldlaws.country;

public final class WLTour {

    private final String subdivision, city, youtubeVideoID;

    public WLTour(String subdivision, String city, String youtubeVideoID) {
        this.subdivision = subdivision;
        this.city = city;
        this.youtubeVideoID = youtubeVideoID;
    }

    public String getSubdivision() {
        return subdivision;
    }
    public String getCity() {
        return city;
    }
    public String getYouTubeVideoID() {
        return youtubeVideoID;
    }
}
