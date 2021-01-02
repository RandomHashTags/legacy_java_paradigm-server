package me.randomhashtags.worldlaws.info.list;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.AppleFeatureAvailability;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public enum Flyover implements AppleFeatureAvailability {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LIST_APPLE_MAPS_FLYOVER;
    }

    @Override
    public void getValue(String countryBackendID, CompletionHandler handler) {
        if(countries == null) {
            load();
        }
        handler.handle(getValue(countryBackendID));
    }

    private String getValue(String countryBackendID) {
        return countries.getOrDefault(countryBackendID, "null");
    }

    private void load() {
        final long started = System.currentTimeMillis();
        countries = new HashMap<>();
        final Elements elements = getSectionValues("maps-flyover").select("div.section-content ul li");
        final HashMap<String, List<FlyoverObj>> flyovers = new HashMap<>();
        for(Element element : elements) {
            final String[] values = element.text().split(", ");
            final int max = values.length;
            if(max == 2) {
                String country = values[1].replace("England", "United Kingdom");
                final String territory;
                if(country.length() == 2) {
                    territory = country;
                    country = "United States";
                } else {
                    territory = "null";
                }
                country = country.toLowerCase().replace(" ", "");
                final String city = values[0].split("/")[0];
                if(!flyovers.containsKey(country)) {
                    flyovers.put(country, new ArrayList<>());
                }
                Stream<FlyoverObj> stream = flyovers.get(country).stream().filter(test -> test.getTerritory().equals(territory));
                if(stream.count() == 0) {
                    flyovers.get(country).add(new FlyoverObj(territory, new ArrayList<>()));
                }
                for(FlyoverObj obj : flyovers.get(country)) {
                    if(territory.equals(obj.getTerritory())) {
                        obj.addCity(city);
                        break;
                    }
                }
            }
        }
        boolean isFirst = true;
        for(Map.Entry<String, List<FlyoverObj>> map : flyovers.entrySet()) {
            final String country = map.getKey();
            final StringBuilder builder = new StringBuilder("[");
            isFirst = true;
            for(FlyoverObj obj : map.getValue()) {
                builder.append(isFirst ? "" : ",").append(obj.toString());
                isFirst = false;
            }
            builder.append("]");
            countries.put(country, builder.toString());
        }
        WLLogger.log(Level.INFO, "Flyover - loaded (took " + (System.currentTimeMillis()-started) + "ms)");
    }
}
