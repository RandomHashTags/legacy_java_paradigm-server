package me.randomhashtags.worldlaws.weather;

import me.randomhashtags.worldlaws.country.Location;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONArray;

import java.util.List;

public final class WeatherZone {
    private final String name, nameSuffix, subdivision;
    private final List<Location> geometry;

    public WeatherZone(String name, String nameSuffix, String subdivision, List<Location> geometry) {
        this.name = name;
        this.nameSuffix = nameSuffix;
        this.subdivision = subdivision;
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }
    public String getSubdivision() {
        return subdivision;
    }

    private JSONArray getGeometryArray() {
        final JSONArray array = new JSONArray();
        for(Location point : geometry) {
            if(point != null) {
                array.put(point);
            }
        }
        return array;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("name");
        json.put("name", name);
        if(nameSuffix != null) {
            json.put("nameSuffix", nameSuffix);
        }
        json.put("subdivision", subdivision);
        json.put("geometry", getGeometryArray());
        return json;
    }
}
