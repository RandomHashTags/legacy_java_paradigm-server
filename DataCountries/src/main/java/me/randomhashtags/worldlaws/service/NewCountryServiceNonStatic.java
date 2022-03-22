package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.Folder;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public interface NewCountryServiceNonStatic extends NewCountryService {

    @Override
    default Folder getFolder() {
        return Folder.COUNTRIES_SERVICES;
    }

    @Override
    default SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_NONSTATIC;
    }

    @Override
    default JSONObjectTranslatable loadData() {
        return null;
    }

    @Override
    default JSONObjectTranslatable parseData(JSONObject json) {
        return null;
    }
}
