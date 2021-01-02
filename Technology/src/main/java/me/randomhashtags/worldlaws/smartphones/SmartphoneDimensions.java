package me.randomhashtags.worldlaws.smartphones;

public final class SmartphoneDimensions {
    private final String height, width, depth;

    public SmartphoneDimensions(String height, String width, String depth) {
        this.height = height;
        this.width = width;
        this.depth = depth;
    }

    public String getHeight() {
        return height;
    }
    public String getWidth() {
        return width;
    }
    public String getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "{" +
                "\"height\":\"" + height + "\"," +
                "\"width\":\"" + width + "\"," +
                "\"depth\":\"" + depth + "\"" +
                "}";
    }
}
