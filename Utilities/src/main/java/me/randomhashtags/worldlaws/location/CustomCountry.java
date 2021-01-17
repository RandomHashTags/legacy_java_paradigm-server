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

public final class CustomCountry implements Jsoupable {

    private static HashMap<String, String> CIA_SUMMARY_ISO;
    private static final Elements TIMEZONE_ELEMENTS = Jsoupable.getStaticDocumentElements("https://en.wikipedia.org/wiki/List_of_time_zones_by_country", "table.wikitable tbody tr");
    private static final Elements FLAG_ELEMENTS = Jsoupable.getStaticDocument("https://emojipedia.org/flags/").select("ul.emoji-list li a[href]");
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

        if(CIA_SUMMARY_ISO == null) {
            CIA_SUMMARY_ISO = new HashMap<>();
            new Thread(this::initLegacyCIA).start();
        }
        setupFlag();
        setupTimeZone();
        setupISOAlpha2();
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

    private void setupFlag() {
        final Elements elements = new Elements(FLAG_ELEMENTS);
        final String name = shortName.toLowerCase();
        elements.removeIf(row -> !row.text().toLowerCase().split(" \\(")[0].endsWith(name));
        final int size = elements.size();
        if(size > 0) {
            final Element element = elements.get(0), target = element.select("span.emoji").get(0);
            flagEmoji = target.text();
        }
    }
    private void setupTimeZone() {
        timezones = new HashSet<>();
        final Elements elements = new Elements(TIMEZONE_ELEMENTS);
        final String name = shortName.toLowerCase().replace("&", "and");
        elements.removeIf(row -> !row.text().toLowerCase().contains(name) || !row.select("td a[href]").get(0).text().equalsIgnoreCase(name));
        final int size = elements.size();
        if(size > 0) {
            final DaylightSavingsTime DST = DaylightSavingsTime.INSTANCE;
            for(Element element : elements) {
                final Elements td = element.select("td");
                final int timeZones = Integer.parseInt(td.get(1).text());
                final Element element3 = td.get(2);
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
            }
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
    private void setupISOAlpha2() {
        final Elements elements = new Elements(ISO_ALPHA_2_ELEMENTS);
        elements.removeIf(row -> {
            final Elements tds = row.select("td");
            if(tds.size() < 2) {
                return true;
            }
            final String country = tds.get(1).text().toLowerCase();
            return !(backendID.equalsIgnoreCase(country) || shortName.equalsIgnoreCase(country) || name.equalsIgnoreCase(country));
        });
        if(!elements.isEmpty()) {
            isoAlpha2 = elements.first().select("td").get(0).text();
            flagURL = "https://raw.githubusercontent.com/stsrki/country-flags/master/png1000px/" + isoAlpha2.toLowerCase() + ".png";
        }
    }
    private void initLegacyCIA() {
        /*final Elements elements = getDocumentElements("https://www.cia.gov/library/publications/resources/the-world-factbook/docs/one_page_summaries.html", "section.reference_content ul.nav-list li");
        for(Element element : elements) {
            final Element hrefElement = element.select("div.country-name").get(0).select("a[href]").get(0);
            final String href = hrefElement.attr("href"), country = hrefElement.text().toLowerCase();
            final String[] values = href.split("/");
            final String iso = values[values.length-1].substring(0, 2);
            CIA_SUMMARY_ISO.put(country, iso);
        }*/
    }
    private void initCIA() {
        final String url = "https://www.cia.gov/the-world-factbook/countries/" + shortName.toLowerCase().replace(" ", "-") + "/";
        final Elements elements = getDocumentElements(url, null);
    }

    public void getInformation(CompletionHandler handler) {
        if(information != null) {
            handler.handle(information);
        } else {
            final long started = System.currentTimeMillis();
            final HashMap<CountryInfo, String> values = new HashMap<>();
            final String country = shortName.toLowerCase();
            final String iso = CIA_SUMMARY_ISO.get(country);
            final List<CountryResource> resources = new ArrayList<>();
            resources.add(new CountryResource("CIA Summary", "https://www.cia.gov/library/publications/resources/the-world-factbook/attachments/summaries/" + iso + "-summary.pdf"));

            final boolean hasTerritories = territories != null;
            if(hasTerritories) {
                final String territoriesURL = territories.getWikipageURL();
                if(territoriesURL != null) {
                    resources.add(new CountryResource("Territories", territories.getWikipageURL()));
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
            service.getValue(backendID, new CompletionHandler() {
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

    @Override
    public String toString() {
        return "{" +
                "\"backendID\":\"" + backendID + "\"," +
                "\"tag\":\"" + tag + "\"," +
                "\"shortName\":\"" + shortName + "\"," +
                "\"name\":\"" + name + "\"," +
                "\"flagURL\":\"" + flagURL + "\"," +
                "\"flagEmoji\":\"" + flagEmoji + "\"," +
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
