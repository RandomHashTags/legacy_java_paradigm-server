package me.randomhashtags.worldlaws.info.list;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.info.availability.tech.AppleFeatureAvailability;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Flyover implements AppleFeatureAvailability {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.LIST_APPLE_MAPS_FLYOVER;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
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
                final FlyoverObj flyoverObj = new FlyoverObj(country, territory, city);
                flyovers.get(country).add(flyoverObj);
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
        handler.handle(null);
    }
}
