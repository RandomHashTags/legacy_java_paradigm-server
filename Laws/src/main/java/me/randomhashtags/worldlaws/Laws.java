package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.country.usa.state.recode.Minnesota;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.request.ServerRequest;
import me.randomhashtags.worldlaws.request.server.ServerRequestTypeLaws;

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
        final JSONObjectTranslatable string = minnesota.getIndexes();
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
    public JSONObjectTranslatable getServerResponse(APIVersion version, String identifier, ServerRequest request) {
        final ServerRequestTypeLaws type = (ServerRequestTypeLaws) request.getType();
        final String target = request.getTarget();
        final String[] values = target.split("/");
        if(type == null) {
            if(values.length >= 2) {
                final String key = values[0];
                final LawController controller = countries.get(key);
                if(controller != null) {
                    final int keyLength = key.length() + 1;
                    final String targetKey = values[1];
                    if(targetKey.matches("[0-9]+")) {
                        final int administration = Integer.parseInt(targetKey);
                        return controller.getGovernmentResponse(version, administration, target.substring(keyLength+targetKey.length()+1));
                    } else {
                        return controller.getResponse(version, target.substring(keyLength));
                    }
                }
            }
            return null;
        }
        switch (type) {
            case RECENT_ACTIVITY:
                if(values.length >= 1) {
                    final LawController controller = countries.get(values[0]);
                    if(controller != null) {
                        return controller.getRecentActivity(version);
                    }
                }
                return null;
            default:
                return null;
        }
    }
}
