package me.randomhashtags.worldlaws.planet;

import me.randomhashtags.worldlaws.TemperatureRange;

public interface PlanetPhysicalCharacteristics {
    String getRadius();
    String getSurfaceArea();
    String getVolume();
    String getMass();
    String getMeanDensity();
    String getSurfaceGravity();
    String getSiderealRotationPeriod();
    TemperatureRange getTemperatureRange();
}
