package me.randomhashtags.worldlaws.info;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.location.CountryInfo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum NationalAnimals implements CountryNationalService {
    INSTANCE;

    private HashMap<String, String> countries;

    @Override
    public CountryInfo getInfo() {
        return CountryInfo.NATIONAL_ANIMALS;
    }

    @Override
    public HashMap<String, String> getCountries() {
        return countries;
    }

    @Override
    public void refresh(CompletionHandler handler) {
        countries = new HashMap<>();
        final String url = "https://en.wikipedia.org/wiki/List_of_national_animals";
        final String title = getInfo().getTitle();
        final Elements trs = getNationalDocumentElements(url, "div.mw-parser-output table.wikitable", 0).select("tbody tr");
        trs.remove(0);
        trs.removeIf(row -> row.select("td").size() < 2);
        final EventSource source = new EventSource("Wikipedia: List of national animals", url);
        final EventSources sources = new EventSources(source);
        final int yearOfData = WLUtilities.getTodayYear();
        String previousCountry = null;
        CountryInfoKey previousKey = null;
        for(Element element : trs) {
            final Elements tds = element.select("td");
            final int size = tds.size();
            final Element firstElement = tds.get(0);
            final boolean hasRowspan = firstElement.hasAttr("rowspan");
            if(previousKey != null && hasRowspan) {
                countries.put(previousCountry, previousKey.toString());
                previousCountry = null;
                previousKey = null;
            }
            if(size == 4) {
                final NationalAnimal value = getAnimal(firstElement, tds.get(2).selectFirst("img"));
                previousKey.addValue(value);
            } else {
                final String country = firstElement.select("a").get(0).text().toLowerCase().replace(" ", "");
                final CountryInfoValue animal = getAnimal(tds.get(1), tds.get(3).selectFirst("img"));
                final CountryInfoKey key = new CountryInfoKey(title, null, yearOfData, sources, animal);
                if(!hasRowspan) {
                    countries.put(country, key.toString());
                } else {
                    previousCountry = country;
                    previousKey = key;
                }
            }
        }
        if(previousKey != null) {
            countries.put(previousCountry, previousKey.toString());
        }
        handler.handle(null);
    }
    private NationalAnimal getAnimal(Element nameElement, Element thumbnailElement) {
        final String text = nameElement.text();
        final boolean hasParenthesis = text.contains(" (");
        final String name = hasParenthesis ? text.split(" \\(")[0] : text;
        final String[] titleValues = hasParenthesis ? text.split("\\(")[1].split("\\)")[0].split(" ") : null;
        String title = null;
        if(hasParenthesis) {
            title = "";
            boolean isFirst = true;
            for(String value : titleValues) {
                if(!value.isEmpty()) {
                    final String string = value.equalsIgnoreCase("of") ? value : value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
                    title = title.concat(isFirst ? "" : " ").concat(string);
                    isFirst = false;
                }
            }
            if(title.isEmpty()) {
                title = null;
            }
        }
        final String href = "https:" + thumbnailElement.attr("src");
        final String[] values = href.split("/");
        final String suffix = "/" + values[values.length-1], url = href.replace("thumb/", "").split(suffix)[0];
        return new NationalAnimal(title, name, url);
    }

    private class NationalAnimal extends CountryInfoValue {
        private final String title, name, url;

        private NationalAnimal(String title, String name, String url) {
            super(null, null, null);
            this.title = LocalServer.fixEscapeValues(title);
            this.name = LocalServer.fixEscapeValues(name);
            this.url = url;
        }

        @Override
        public String toString() {
            return "{" +
                    (title != null ? "\"title\":\"" + title + "\"," : "") +
                    "\"name\":\"" + name + "\"," +
                    "\"url\":\"" + url + "\"," +
                    "}";
        }
    }
}
