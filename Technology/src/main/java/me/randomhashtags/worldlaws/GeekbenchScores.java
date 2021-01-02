package me.randomhashtags.worldlaws;

public final class GeekbenchScores {
    private final int singleCore, multiCore, metal;

    public GeekbenchScores(int singleCore, int multiCore, int metal) {
        this.singleCore = singleCore;
        this.multiCore = multiCore;
        this.metal = metal;
    }

    public int getSingleCore() {
        return singleCore;
    }
    public int getMultiCore() {
        return multiCore;
    }
    public int getMetal() {
        return metal;
    }

    @Override
    public String toString() {
        return "{" +
                "\"singleCore\":" + singleCore + "," +
                "\"multiCore\":" + multiCore + "," +
                "\"metal\":" + metal +
                "}";
    }
}
