package me.randomhashtags.worldlaws.globalwarming;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.CompletionHandler;

public enum GlobalWarming {
    INSTANCE;
    ;

    public void getResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "causes":
                getCauses(version, handler);
                break;
            case "projections":
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
