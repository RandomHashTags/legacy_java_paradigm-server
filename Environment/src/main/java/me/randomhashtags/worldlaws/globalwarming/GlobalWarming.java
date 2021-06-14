package me.randomhashtags.worldlaws.globalwarming;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;

public enum GlobalWarming {
    CAUSES,
    PROJECTIONS,
    ;


    public void get(APIVersion version, CompletionHandler handler) {
        switch (this) {
            case CAUSES:
                getCauses(version, handler);
                break;
            case PROJECTIONS:
                getProjections(version, handler);
                break;
            default:
                break;
        }
    }

    private void getCauses(APIVersion version, CompletionHandler handler) {
    }

    private void getProjections(APIVersion version, CompletionHandler handler) {
    }
}
