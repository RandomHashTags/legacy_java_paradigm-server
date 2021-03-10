package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.LawController;
import me.randomhashtags.worldlaws.country.ca.CanadaLaws;
import me.randomhashtags.worldlaws.country.usa.USLaws;

import java.util.HashMap;

public final class Laws implements DataValues {

    private HashMap<String, LawController> countries;

    private final LawController[] CONTROLLERS = new LawController[] {
            CanadaLaws.INSTANCE,
            USLaws.INSTANCE
    };

    public static void main(String[] args) {
        new Laws();
    }

    private Laws() {
        //test();
        load();
    }

    private void test() {
    }
    private void load() {
        countries = new HashMap<>();
        for(LawController country : CONTROLLERS) {
            countries.put(country.getCountry().getBackendID(), country);
        }
        LocalServer.start("Laws", WL_LAWS_PORT, new CompletionHandler() {
            @Override
            public void handleClient(WLClient client) {
                final String target = client.getTarget();
                getResponse(target, new CompletionHandler() {
                    @Override
                    public void handle(Object object) {
                        final String string = object.toString();
                        client.sendResponse(string);
                    }
                });
            }
        });
    }

    private void getResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        countries.get(key).getResponse(target.substring(key.length()+1), handler);
    }
}
