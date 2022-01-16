package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.country.usa.state.recode.Minnesota;

import java.util.HashMap;

public final class Laws implements WLServer {
    public static final Laws INSTANCE = new Laws();
    private HashMap<String, LawController> countries;

    private final LawController[] CONTROLLERS = new LawController[] {
            USLaws.INSTANCE
    };

    public static void main(String[] args) {
        INSTANCE.init();
    }

    private void init() {
        //test();
        load();
    }

    @Override
    public TargetServer getServer() {
        return TargetServer.LAWS;
    }

    private void test() {
        final Minnesota minnesota = Minnesota.INSTANCE;
        final String string = minnesota.getIndexes();
        WLLogger.logInfo("Laws;test;string=" + string);
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
    public String getServerResponse(APIVersion version, String target) {
        final String[] values = target.split("/");
        final String key = values[0];
        switch (key) {
            case "recent_activity":
                if(values.length >= 2) {
                    final LawController controller = countries.get(values[1]);
                    if(controller != null) {
                        return controller.getRecentActivity(version);
                    }
                }
                return null;
            default:
                if(values.length >= 2) {
                    final LawController controller = countries.get(key);
                    final int keyLength = key.length()+1;
                    final String targetKey = values[1];
                    if(targetKey.matches("[0-9]+")) {
                        final int administration = Integer.parseInt(targetKey);
                        return controller.getGovernmentResponse(version, administration, target.substring(keyLength+targetKey.length()+1));
                    } else {
                        return controller.getResponse(version, target.substring(keyLength));
                    }
                }
                return null;
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
