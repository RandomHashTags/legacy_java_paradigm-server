package me.randomhashtags.worldlaws.country.cities;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.Jsonable;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.country.SovereignStateSubdivision;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

import java.util.HashSet;

public interface CityController extends Jsoupable, Jsonable {
    SovereignStateSubdivision getSubdivision();

    default JSONObjectTranslatable getJSON() {
        final SovereignStateSubdivision subdivision = getSubdivision();
        final WLCountry country = subdivision.getCountry();
        final String countryBackendID = country.getBackendID(), subdivisionBackendID = subdivision.getBackendID();
        final Folder folder = Folder.COUNTRIES_INFORMATION_CITIES;
        final String fileName = "";
        folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", countryBackendID).replace("%subdivision%", subdivisionBackendID));
        return null;
    }

    HashSet<City> load();
    JSONObjectTranslatable parse(JSONObject json);
}
