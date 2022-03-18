package me.randomhashtags.worldlaws.globalwarming;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public enum GlobalWarming {
    INSTANCE;
    ;

    public JSONObjectTranslatable getResponse(APIVersion version, String[] values) {
        final String key = values[0];
        switch (key) {
            case "causes":
                return getCauses(version);
            case "projections":
                return getProjections(version);
            default:
                return null;
        }
    }

    private JSONObjectTranslatable getCauses(APIVersion version) {
        return null;
    }

    private JSONObjectTranslatable getProjections(APIVersion version) {
        return null;
    }
}
