package me.randomhashtags.worldlaws.globalwarming;

import me.randomhashtags.worldlaws.APIVersion;

public enum GlobalWarming {
    INSTANCE;
    ;

    public String getResponse(APIVersion version, String[] values) {
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

    private String getCauses(APIVersion version) {
        return null;
    }

    private String getProjections(APIVersion version) {
        return null;
    }
}
