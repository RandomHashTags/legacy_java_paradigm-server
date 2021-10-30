package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.country.usa.state.Minnesota;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;

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
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.LAWS;
    }

    private void test() {
        final Minnesota minnesota = Minnesota.INSTANCE;
        final JSONObject json = new JSONObject(minnesota.getIndexesJSON());
        for(String key : json.keySet()) {
            final String string = minnesota.getTableOfChapters(key);
            WLLogger.log(Level.INFO, "Laws;test;string=" + string);
            break;
        }
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
                    final LawController controller = valueOfCountry(values[1]);
                    if(controller != null) {
                        controller.getRecentActivity(version, handler);
                        return;
                    }
                }
                handler.handleString("{}");
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

    private LawController valueOfCountry(String countryBackendID) {
        for(LawController controller : CONTROLLERS) {
            if(controller.getCountry().getBackendID().equals(countryBackendID)) {
                return controller;
            }
        }
        return null;
    }
}
