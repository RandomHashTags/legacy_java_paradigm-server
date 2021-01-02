package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.Processor;

public final class SmartphoneProcessor implements Processor {

    private final String name, nanometerTechnology, totalCores, highPerformanceCores, energyEfficiencyCores, clockSpeed;

    public SmartphoneProcessor(String name, String nanometerTechnology, String totalCores, String highPerformanceCores, String energyEfficiencyCores, String clockSpeed) {
        this.name = name;
        this.nanometerTechnology = nanometerTechnology;
        this.totalCores = totalCores;
        this.highPerformanceCores = highPerformanceCores;
        this.energyEfficiencyCores = energyEfficiencyCores;
        this.clockSpeed = clockSpeed;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNanometerTechnology() {
        return nanometerTechnology;
    }

    @Override
    public String getTotalCores() {
        return totalCores;
    }

    @Override
    public String getHighPerformanceCores() {
        return highPerformanceCores;
    }

    @Override
    public String getEnergyEfficiencyCores() {
        return energyEfficiencyCores;
    }

    @Override
    public String getClockSpeed() {
        return clockSpeed;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"nanometerTechnology\":\"" + nanometerTechnology + "\"," +
                "\"totalCores\":\"" + totalCores + "\"," +
                "\"highPerformanceCores\":\"" + highPerformanceCores + "\"," +
                "\"energyEfficiencyCores\":\"" + energyEfficiencyCores + "\"," +
                "\"clockSpeed\":\"" + clockSpeed + "\"" +
                "}";
    }
}
