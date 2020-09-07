package me.randomhashtags.worldlaws;

public final class PopulationEstimate {
    private int population;
    private int estimationYear;

    public PopulationEstimate(int population, int estimationYear) {
        this.population = population;
        this.estimationYear = estimationYear;
    }

    public int getPopulation() {
        return population;
    }
    public int getEstimationYear() {
        return estimationYear;
    }

    @Override
    public String toString() {
        return "{\"population\":" + population + "," +
                "\"estimationYear\":" + estimationYear +
                "}";
    }
}
