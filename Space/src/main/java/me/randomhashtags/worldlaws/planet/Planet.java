package me.randomhashtags.worldlaws.planet;

import me.randomhashtags.worldlaws.AstronomicalBody;

public interface Planet {
    PlanetAtmosphere getAtmosphere();
    PlanetPhysicalCharacteristics getPhysicalCharacteristics();
    AstronomicalBody[] getSatellites();
}
