package me.randomhashtags.worldlaws.country.cities.u.unitedstates;

import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.cities.City;
import me.randomhashtags.worldlaws.country.cities.CityController;
import me.randomhashtags.worldlaws.country.subdivisions.u.SubdivisionsUnitedStates;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.WikipediaDocument;
import org.json.JSONObject;

import java.util.HashSet;

public enum CitiesUnitedStatesMinnesota implements CityController {
    INSTANCE;

    @Override
    public HashSet<City> load() {
        final String url = "https://en.wikipedia.org/wiki/List_of_cities_in_Minnesota";
        final WikipediaDocument doc = new WikipediaDocument(url);
        if(doc.exists()) {
            final HashSet<City> cities = new HashSet<>();
            return cities;
        }
        return null;
    }

    @Override
    public JSONObjectTranslatable parse(JSONObject json) {
        return null;
    }

    @Override
    public SovereignStateSubdivision getSubdivision() {
        return SubdivisionsUnitedStates.MINNESOTA;
    }
}
