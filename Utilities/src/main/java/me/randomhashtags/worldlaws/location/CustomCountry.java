package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class CustomCountry implements Jsoupable {

    private static final Elements TIMEZONE_ELEMENTS = Jsoupable.getStaticDocumentElements("https://en.wikipedia.org/wiki/List_of_time_zones_by_country", "table.wikitable tbody tr");
    private static final Elements FLAG_ELEMENTS = Jsoupable.getStaticDocument("https://emojipedia.org/flags/", true).select("ul.emoji-list li a[href]");
    private static final Elements ISO_ALPHA_2_ELEMENTS = Jsoupable.getStaticDocumentElements("https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2", "div.mw-parser-output table.wikitable", 2).select("tbody tr");

    static {
        ISO_ALPHA_2_ELEMENTS.remove(0);
    }

    private Elements infobox;
    private String backendID, tag, shortName, name, flagURL, flagEmoji, isoAlpha2;
    private CountryDaylightSavingsTime daylightSavingsTime;
    private Set<UTCOffset> timezones;
    private Territories territories;

    private String information;

    public CustomCountry(String tag, Document page) {
        this.tag = tag;
        infobox = page.select("table.infobox tbody tr");

        final String sname = tag.equalsIgnoreCase("artsakh") ? tag
                : page.select("h1.firstHeading").get(0).text()
                .replaceFirst("Democratic Republic of the ", "")
                .replaceFirst("Democratic Republic of ", "")
                .replaceFirst("Republic of the ", "")
                .replaceFirst("Republic of ", "")
                .replaceFirst(" and the ", " & ")
                .replaceFirst(" and ", " & ")
                .replaceFirst("Saint ", "St. ")
                .replaceFirst("The ", "")

                .replaceFirst("Northern ", "")
                .replaceFirst("Federated States of ", "")
                .replaceFirst(" \\(country\\)", "")
                ;
        shortName = sname;

        backendID = sname.toLowerCase().replace(" ", "");
        name = infobox.size() > 0 ? infobox.get(0).select("th div.fn").text() : shortName;

        setupFlagEmoji();
        setupTimeZones();
        setupFlagURL();
    }
    public CustomCountry(JSONObject information) {
        backendID = information.getString("backendID");
        tag = information.getString("tag");
        shortName = information.getString("shortName");
        name = information.getString("name");
        flagURL = information.getString("flagURL");
        flagEmoji = information.getString("flagEmoji");
        timezones = new HashSet<>();
        final JSONArray timezones = information.getJSONArray("timezones");
        for(Object obj : timezones) {
            final JSONObject timezoneJSON = (JSONObject) obj;
            final UTCOffset offset = new UTCOffset(timezoneJSON);
            this.timezones.add(offset);
        }
    }

    public String getTag() {
        return tag;
    }
    public String getBackendID() {
        return backendID;
    }
    public String getShortName() {
        return shortName;
    }
    public String getName() {
        return name;
    }
    public String getFlagURL() {
        return flagURL;
    }
    public String getFlagEmoji() {
        return flagEmoji;
    }
    public String getISOAlpha2() {
        return isoAlpha2;
    }
    public Territories getTerritories() {
        return territories;
    }
    public void setTerritories(Territories territories) {
        this.territories = territories;
    }

    private void setupFlagEmoji() {
        final String name = shortName.toLowerCase();
        final Stream<Element> elements = FLAG_ELEMENTS.parallelStream().filter(row -> {
            return row.text().toLowerCase().split(" \\(")[0].endsWith(name);
        });
        final Optional<Element> firstElement = elements.findFirst();
        if(firstElement.isPresent()) {
            final Element target = firstElement.get().select("span.emoji").get(0);
            flagEmoji = target.text();
        }
    }
    private void setupTimeZones() {
        timezones = new HashSet<>();
        final String name = shortName.toLowerCase().replace("&", "and");
        final List<Element> elements = new ArrayList<>(TIMEZONE_ELEMENTS);
        elements.removeIf(row -> {
            return !row.text().toLowerCase().contains(name) || !row.select("td a[href]").get(0).text().equalsIgnoreCase(name);
        });
        final long size = elements.size();
        if(size > 0) {
            final DaylightSavingsTime DST = DaylightSavingsTime.INSTANCE;
            elements.parallelStream().forEach(element -> {
                final Elements tds = element.select("td");
                final int timeZones = Integer.parseInt(tds.get(1).text());
                final Element element3 = tds.get(2);
                if(timeZones == 1) {
                    final UTCOffset offset = getTimeZone(element3.text());
                    this.daylightSavingsTime = DST.getFrom(shortName, offset);
                    timezones.add(offset);
                } else {
                    final Elements timezoneElements = element3.select("a");
                    timezoneElements.removeIf(tzElement -> !tzElement.attr("title").startsWith("UTC"));
                    for(Element timeZone : timezoneElements) {
                        final String value = timeZone.text();
                        final UTCOffset offset = getTimeZone(value);
                        timezones.add(offset);
                    }
                }
            });
        }
    }
    private UTCOffset getTimeZone(String value) {
        final String[] splits = value.split(" — ");
        final String zones = splits[0].split(" \\(")[0].substring("UTC".length());
        final String[] values = zones.split(":");
        final String value0 = values[0];
        boolean isNegative = value0.startsWith("−");
        int hours = Integer.parseInt(value0.substring(1));
        final int minutes = Integer.parseInt(values[1]);
        if(isNegative) {
            hours *= -1;
        }
        return new UTCOffset(hours, minutes, splits.length == 1 ? "Whole country" : splits[1].split("\\[")[0]);
    }
    private void setupFlagURL() {
        final Stream<Element> elements = ISO_ALPHA_2_ELEMENTS.parallelStream().filter(row -> {
            final Elements tds = row.select("td");
            if(tds.size() < 2) {
                return true;
            }
            final String country = tds.get(1).text().toLowerCase();
            return !(backendID.equalsIgnoreCase(country) || shortName.equalsIgnoreCase(country) || name.equalsIgnoreCase(country));
        });
        final Optional<Element> firstElement = elements.findFirst();
        if(firstElement.isPresent()) {
            isoAlpha2 = firstElement.get().select("td").get(0).text();
            flagURL = "https://raw.githubusercontent.com/stsrki/country-flags/master/png1000px/" + isoAlpha2.toLowerCase() + ".png";
        }
    }

    public void getInformation(CompletionHandler handler) {
        if(information != null) {
            handler.handle(information);
        } else {
            final long started = System.currentTimeMillis();
            final HashMap<CountryInfo, String> values = new HashMap<>();
            final List<CountryResource> resources = new ArrayList<>();

            final boolean hasTerritories = territories != null;
            if(hasTerritories) {
                final String territoriesURL = territories.getWikipageURL();
                if(territoriesURL != null) {
                    resources.add(new CountryResource("Territories", territoriesURL));
                }
            }
            values.put(CountryInfo.TERRITORIES, hasTerritories ? territories.getTerritoriesJSONArray() : "null");

            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(CountryResource resource : resources) {
                builder.append(isFirst ? "" : ",").append(resource.toString());
                isFirst = false;
            }
            builder.append("]");
            values.put(CountryInfo.RESOURCES, builder.toString());

            final HashSet<CountryService> services = new HashSet<>(CountryServices.SERVICES);
            final int max = services.size()+2;
            loadNew(started, services, max, values, handler);
        }
    }

    private void loadNew(long started, HashSet<CountryService> services, int max, HashMap<CountryInfo, String> values, CompletionHandler handler) {
        services.parallelStream().forEach(service -> {
            final CountryInfo info = service.getInfo();
            final boolean isCIA = info == CountryInfo.SERVICE_CIA_VALUES;
            service.getValue(isCIA ? shortName : backendID, new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    values.put(info, object.toString());
                    completeInformation(max, started, values, handler);
                }
            });
        });
    }

    private void completeInformation(int max, long started, HashMap<CountryInfo, String> values, CompletionHandler handler) {
        if(values.size() == max) {
            final CountryInformation countryInformation = new CountryInformation(values);
            final String string = countryInformation.toString();
            information = string;

            final List<String> missingValues = new ArrayList<>();
            for(Map.Entry<CountryInfo, String> entry : values.entrySet()) {
                final String value = entry.getValue();
                if(value == null || value.equals("null")) {
                    missingValues.add(entry.getKey().name());
                }
            }
            final String missingString = missingValues.isEmpty() ? "" : " (missing " + missingValues.toString() + ")";
            WLLogger.log(Level.INFO, "CustomCountry - loaded information for country \"" + backendID + "\" (took " + (System.currentTimeMillis()-started) + "ms)" + missingString);
            handler.handle(string);
        }
    }

    private String getGovernmentDetails() {
        try {
            final GovernmentDetails details = GovernmentDetails.valueOf(shortName.toUpperCase().replace(" ", "_"));
            return details.toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"backendID\":\"" + backendID + "\"," +
                "\"tag\":\"" + tag + "\"," +
                "\"shortName\":\"" + shortName + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"flagURL\":\"" + flagURL + "\"," +
                "\"flagEmoji\":\"" + flagEmoji + "\"," +
                "\"governmentDetails\":" + getGovernmentDetails() + "," +
                "\"hasTerritories\":" + (territories != null) + "," +
                "\"daylightSavingsTime\":" + (daylightSavingsTime != null ? daylightSavingsTime.toString() : "null") + "," +
                "\"timezones\":" + timezones.toString() +
                "}";
    }

    private final class CountryResource {
        private final String name, url;

        CountryResource(String name, String url) {
            this.name = LocalServer.fixEscapeValues(name);
            this.url = url;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"name\":\"" + name + "\"," +
                    "\"url\":\"" + url + "\"" +
                    "}";
        }
    }
}
