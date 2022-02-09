package me.randomhashtags.worldlaws.space.planet;

import me.randomhashtags.worldlaws.space.AstronomicalBody;

public interface Planet {
    PlanetAtmosphere getAtmosphere();
    PlanetPhysicalCharacteristics getPhysicalCharacteristics();
    AstronomicalBody[] getSatellites();
}
