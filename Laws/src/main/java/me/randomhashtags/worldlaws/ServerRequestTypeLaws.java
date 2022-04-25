package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.request.ServerRequestType;
import me.randomhashtags.worldlaws.request.WLHttpHandler;

import java.util.HashMap;

public enum ServerRequestTypeLaws implements ServerRequestType {
    RECENT_ACTIVITY,
    ;

    private static final LawController[] CONTROLLERS = new LawController[] {
            USLaws.INSTANCE
    };
    private static final HashMap<String, LawController> COUNTRIES = new HashMap<>();

    @Override
    public WLHttpHandler getHandler(APIVersion version) {
        switch (version) {
            case v1: return getV1Handler(version);
            default: return null;
        }
    }

    public static WLHttpHandler getDefaultHandler() {
        return httpExchange -> {
            final String[] values = httpExchange.getPathValues();
            if(values.length >= 3) {
                final APIVersion version = httpExchange.getAPIVersion();
                final String key = values[1];
                final LawController controller = getController(key);
                if(controller != null) {
                    final int keyLength = key.length() + 1;
                    final String targetKey = values[2];
                    final String path = httpExchange.getPath().substring(values[0].length() + 1);
                    if(targetKey.matches("[0-9]+")) {
                        final int administration = Integer.parseInt(targetKey);
                        return controller.getGovernmentResponse(version, administration, httpExchange.getPathValuesExcludingFirst(3));
                    } else {
                        return controller.getResponse(version, path.substring(keyLength));
                    }
                }
            }
            return null;
        };
    }

    private WLHttpHandler getV1Handler(APIVersion version) {
        switch (this) {
            case RECENT_ACTIVITY:
                return httpExchange -> {
                    final String[] values = httpExchange.getShortPathValues();
                    if(values.length >= 1) {
                        final LawController controller = getController(values[0]);
                        if(controller != null) {
                            return controller.getRecentActivity(version);
                        }
                    }
                    return null;
                };
            default:
                return null;
        }
    }

    private static LawController getController(String countryBackendID) {
        if(COUNTRIES.isEmpty()) {
            for(LawController country : CONTROLLERS) {
                COUNTRIES.put(country.getCountry().getBackendID(), country);
            }
        }
        return COUNTRIES.get(countryBackendID);
    }
}
