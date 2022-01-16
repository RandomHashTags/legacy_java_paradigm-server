package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.Jsoupable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public interface SmartphoneCompany extends Jsoupable {
    String getBackendID();
    String getSmartphoneListJSON();
    String getSmartphoneDetails(String model);

    default List<String> getRowValues(Elements row) {
        final List<String> values = new ArrayList<>();
        for(Element element : row) {
            final int span = element.hasAttr("colspan") ? Integer.parseInt(element.attr("colspan")) : -1;
            final String text = element.text();
            if(span == -1) {
                values.add(text);
            } else {
                for(int i = 0; i < span; i++) {
                    values.add(text);
                }
            }
        }
        return values;
    }
}
