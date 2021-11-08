package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.country.usa.state.recode.Vermont;

import java.util.HashMap;

public final class Laws implements WLServer {

    private HashMap<String, LawController> countries;

    private final LawController[] CONTROLLERS = new LawController[] {
            USLaws.INSTANCE
    };

    public static void main(String[] args) {
        new Laws();
    }

    private Laws() {
        test();
        //load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.LAWS;
    }

    private void test() {
        final Vermont minnesota = Vermont.INSTANCE;
        minnesota.getStatute("16APPENDIX", "1", "6", new CompletionHandler() {
            @Override
            public void handleString(String string) {
                WLLogger.logInfo("Laws;test;string=" + string);
            }
        });
    }

    @Override
    public void load() {
        countries = new HashMap<>();
        for(LawController country : CONTROLLERS) {
            countries.put(country.getCountry().getBackendID(), country);
        }
        startServer();
    }

    @Override
    public void getServerResponse(APIVersion version, String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "recent_activity":
                if(values.length >= 2) {
                    final LawController controller = countries.get(values[1]);
                    if(controller != null) {
                        controller.getRecentActivity(version, handler);
                        return;
                    }
                }
                handler.handleString(null);
                break;
            default:
                if(values.length >= 2) {
                    final LawController controller = countries.get(key);
                    final int keyLength = key.length()+1;
                    final String targetKey = values[1];
                    if(targetKey.matches("[0-9]+")) {
                        final int administration = Integer.parseInt(targetKey);
                        controller.getGovernmentResponse(version, administration, target.substring(keyLength+targetKey.length()+1), handler);
                    } else {
                        controller.getResponse(version, target.substring(keyLength), handler);
                    }
                } else {
                    handler.handleString(null);
                }
                break;
        }
    }

    @Override
    public String[] getHomeRequests() {
        return null;
    }

    @Override
    public AutoUpdateSettings getAutoUpdateSettings() {
        return null;
    }
}
