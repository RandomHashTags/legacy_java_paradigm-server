package me.randomhashtags.worldlaws.location;

import org.apache.logging.log4j.Level;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Stream;

public final class CustomCountry implements Jsoupable, ServerObject {

    private static Elements TIMEZONE_ELEMENTS, FLAG_ELEMENTS, ISO_ALPHA_2_ELEMENTS;

    private final String tag, shortName, name;
    private String flagURL, flagEmoji;
    private CountryDaylightSavingsTime daylightSavingsTime;
    private HashSet<UTCOffset> timezones;
    private String information;

    public CustomCountry(String tag, Document page) {
        if(TIMEZONE_ELEMENTS == null) {
            TIMEZONE_ELEMENTS = Jsoupable.getStaticDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/List_of_time_zones_by_country", true, "table.wikitable tbody tr");
            FLAG_ELEMENTS = Jsoupable.getStaticDocument(FileType.COUNTRIES, "https://emojipedia.org/flags/", true).select("ul.emoji-list li a[href]");
            ISO_ALPHA_2_ELEMENTS = Jsoupable.getStaticDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2", true, "div.mw-parser-output table.wikitable", 2).select("tbody tr");
            ISO_ALPHA_2_ELEMENTS.remove(0);
        }
        this.tag = tag;
        final Elements infobox = page.select("table.infobox tbody tr");

        shortName = tag.equalsIgnoreCase("artsakh") ? tag
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
                .replaceFirst(" \\(country\\)", "");
        name = infobox.size() > 0 ? removeReferences(infobox.get(0).select("th div.fn").text()) : shortName;

        setupFlagEmoji();
        setupTimeZones();
        setupFlagURL();
    }
    public CustomCountry(JSONObject json) {
        tag = json.getString("tag");
        shortName = json.has("shortName") ? json.getString("shortName") : tag;
        name = json.has("name") ? json.getString("name") : tag;
        flagURL = json.has("flagURL") && !json.getString("flagURL").equals("null") ? json.getString("flagURL") : null;
        flagEmoji = json.getString("flagEmoji");
        timezones = new HashSet<>();
        if(json.has("timezones")) {
            for(Object obj : json.getJSONArray("timezones")) {
                final JSONObject jsonObject = (JSONObject) obj;
                final int hour = jsonObject.has("hour") ? jsonObject.getInt("hour") : 0;
                final int minute = jsonObject.has("minute") ? jsonObject.getInt("minute") : 0;
                final String regions = jsonObject.getString("regions");
                final UTCOffset offset = new UTCOffset(hour, minute, regions);
                timezones.add(offset);
            }
        }
        if(json.has("daylightSavingsTime")) {
            final JSONObject dstJson = json.getJSONObject("daylightSavingsTime");
            final int startMonth = dstJson.getInt("startMonth");
            final int startDay = dstJson.getInt("startDay");
            final int endMonth = dstJson.getInt("endMonth");
            final int endDay = dstJson.getInt("endDay");
            daylightSavingsTime = new CountryDaylightSavingsTime(startMonth, startDay, endMonth, endDay);
        }
    }

    public String getBackendID() {
        return shortName.toLowerCase().replace(" ", "");
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
                final Element timezoneElement = tds.get(2);
                if(timeZones == 1) {
                    final UTCOffset offset = getTimeZone(timezoneElement.text());
                    this.daylightSavingsTime = DST.getFrom(shortName, offset);
                    timezones.add(offset);
                } else {
                    int lastIndex = 0, targetIndex = 0;
                    final HashMap<Integer, String> text = new HashMap<>();
                    final List<Node> allElements = timezoneElement.childNodes();
                    final int max = allElements.size();
                    final Elements breaks = timezoneElement.select("br");
                    for(Element br : breaks) {
                        final int index = allElements.indexOf(br);
                        final String string = getTimeZoneText(allElements, lastIndex, index);
                        text.put(targetIndex, string);
                        targetIndex += 1;
                        lastIndex = index;
                    }
                    final String string = getTimeZoneText(allElements, lastIndex, max);
                    text.put(targetIndex, string);
                    targetIndex += 1;

                    final Elements timezoneElements = timezoneElement.select("a");
                    timezoneElements.removeIf(tzElement -> !tzElement.attr("title").startsWith("UTC"));
                    for(int i = 0; i < targetIndex; i++) {
                        final String value = text.get(i);
                        final UTCOffset offset = getTimeZone(value);
                        if(offset != null) {
                            timezones.add(offset);
                        }
                    }
                }
            });
        }
    }
    private String getTimeZoneText(List<Node> allElements, int lastIndex, int max) {
        final StringBuilder builder = new StringBuilder();
        for(int i = lastIndex; i < max; i++) {
            final Node node = allElements.get(i);
            final boolean isTextNode = node instanceof TextNode;
            final String string = removeReferences(isTextNode ? ((TextNode) node).text() : ((Element) node).text());
            builder.append(string);
        }
        return builder.toString();
    }
    private UTCOffset getTimeZone(String value) {
        if(value.startsWith(" ")) {
            value = value.substring(1);
        }
        if(value.isEmpty()) {
            return null;
        }
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
        String string = splits.length == 1 ? "Whole country" : splits[1].split(" \\[")[0];
        if(string.endsWith(" ")) {
            string = string.substring(0, string.length()-1);
        }
        return new UTCOffset(hours, minutes, string);
    }
    private void setupFlagURL() {
        final String backendID = getBackendID();
        final Stream<Element> elements = ISO_ALPHA_2_ELEMENTS.parallelStream().filter(row -> {
            final Elements tds = row.select("td");
            if(tds.size() < 2) {
                return false;
            }
            final String country = tds.get(1).text().toLowerCase().split(" \\(")[0];
            return backendID.equalsIgnoreCase(country.replace(" ", "")) || shortName.equalsIgnoreCase(country) || name.equalsIgnoreCase(country);
        });
        final Optional<Element> firstElement = elements.findFirst();
        if(firstElement.isPresent()) {
            final String isoAlpha2 = firstElement.get().select("td").get(0).text();
            flagURL = "https://raw.githubusercontent.com/stsrki/country-flags/master/png1000px/" + isoAlpha2.toLowerCase() + ".png";
        }
    }

    public void getInformation(CompletionHandler handler) {
        if(information != null) {
            handler.handle(information);
        } else {
            final long started = System.currentTimeMillis();
            final HashMap<CountryInfo, String> values = new HashMap<>();
            final WLCountry country = getWLCountry();
            if(country != null) {
                final HashSet<CountryService> services = new HashSet<>(CountryServices.SERVICES);
                final List<CountryResource> resources = new ArrayList<>();

                if(country.hasTerritories()) {
                    final TerritoryDetails details = TerritoryDetails.INSTANCE;
                    services.add(details);
                    resources.add(new CountryResource("Territories", details.getURL(country)));
                }
                final int servicesSize = services.size(), max = servicesSize + 1;

                final String website = country.getGovernmentWebsite();
                if(website != null) {
                    resources.add(new CountryResource("Government Website", country.getGovernmentWebsite()));
                }
                final StringBuilder builder = new StringBuilder("[");
                boolean isFirst = true;
                for(CountryResource resource : resources) {
                    builder.append(isFirst ? "" : ",").append(resource.toString());
                    isFirst = false;
                }
                builder.append("]");
                values.put(CountryInfo.RESOURCES, builder.toString());

                loadNew(started, country, services, max, values, handler);
            }
        }
    }

    private void loadNew(long started, WLCountry country, HashSet<CountryService> services, int max, HashMap<CountryInfo, String> values, CompletionHandler handler) {
        final String backendID = getBackendID();
        services.parallelStream().forEach(service -> {
            final CountryInfo info = service.getInfo();
            final CompletionHandler serviceHandler = new CompletionHandler() {
                @Override
                public void handle(Object object) {
                    completeInformation(info, object, max, started, values, handler);
                }
            };
            final String countryIdentifier;
            switch (info) {
                case SERVICE_CIA_VALUES:
                case SERVICE_TRAVEL_BRIEFING:
                    countryIdentifier = shortName;
                    break;
                case SERVICE_WIKIPEDIA:
                    countryIdentifier = tag;
                    break;
                case TERRITORIES:
                    countryIdentifier = null;
                    ((TerritoryDetails) service).getValues(country, serviceHandler);
                    break;
                default:
                    countryIdentifier = backendID;
                    break;
            }
            if(countryIdentifier != null) {
                service.getValue(countryIdentifier, serviceHandler);
            }
        });
    }

    private synchronized void completeInformation(CountryInfo info, Object object, int max, long started, HashMap<CountryInfo, String> values, CompletionHandler handler) {
        values.put(info, object.toString());
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
            WLLogger.log(Level.INFO, "CustomCountry - loaded information for country \"" + name + "\" (took " + (System.currentTimeMillis()-started) + "ms)" + missingString);
            handler.handle(string);
        }
    }

    private WLCountry getWLCountry() {
        try {
            return WLCountry.valueOf(shortName.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                (!name.equals(shortName) ? "\"shortName\":\"" + shortName + "\"," : "") +
                (daylightSavingsTime != null ? "\"daylightSavingsTime\":" + daylightSavingsTime.toString() + "," : "") +
                (!timezones.isEmpty() ? "\"timezones\":" + timezones.toString() + "," : "") +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                "\"flagEmoji\":\"" + flagEmoji + "\"" +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"tag\":\"" + tag + "\"," +
                (!name.equals(tag) ? "\"name\":\"" + name + "\"," : "") +
                (!shortName.equals(tag) ? "\"shortName\":\"" + shortName + "\"," : "") +
                (daylightSavingsTime != null ? "\"daylightSavingsTime\":" + daylightSavingsTime.toString() + "," : "") +
                (!timezones.isEmpty() ? "\"timezones\":" + timezones.toString() + "," : "") +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                "\"flagEmoji\":\"" + flagEmoji + "\"" +
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
