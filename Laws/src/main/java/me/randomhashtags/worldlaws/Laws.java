package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.ca.CanadaLaws;
import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.location.ICountry;

import java.util.HashMap;

public final class Laws implements DataValues {

    private HashMap<String, ICountry> countries;

    public static void main(String[] args) {
        new Laws();
    }

    private Laws() {
        countries = new HashMap<>();

        for(ICountry country : getCountries()) {
            countries.put(country.getCountryBackendID().getValue(), country);
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

    private ICountry[] getCountries() {
        return new ICountry[] {
                CanadaLaws.INSTANCE,
                USLaws.INSTANCE
        };
    }

    private void getResponse(String target, CompletionHandler handler) {
        final String[] values = target.split("/");
        final String key = values[0];
        countries.get(key).getResponse(target.substring(key.length()+1), handler);
    }
}
