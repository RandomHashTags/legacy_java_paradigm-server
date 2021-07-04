package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class CustomCountry implements SovereignState, ServerObject {

    private static Elements TIMEZONE_ELEMENTS, FLAG_ELEMENTS, ISO_ALPHA_2_ELEMENTS;

    private final String tag, unStatus, sovereigntyDispute, shortName, name;
    private String flagURL, flagEmoji;
    private WLTimeZone[] timezones;
    private String information;

    public CustomCountry(String tag, String unStatus, String sovereigntyDispute, Document page) {
        this.tag = tag;
        this.unStatus = unStatus;
        this.sovereigntyDispute = sovereigntyDispute;
        if(TIMEZONE_ELEMENTS == null) {
            TIMEZONE_ELEMENTS = Jsoupable.getStaticDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/List_of_time_zones_by_country", true, "table.wikitable tbody tr");
            FLAG_ELEMENTS = Jsoupable.getStaticDocument(FileType.COUNTRIES, "https://emojipedia.org/flags/", true).select("ul.emoji-list li a[href]");
            ISO_ALPHA_2_ELEMENTS = Jsoupable.getStaticDocumentElements(FileType.COUNTRIES, "https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2", true, "div.mw-parser-output table.wikitable", 2).select("tbody tr");
            ISO_ALPHA_2_ELEMENTS.remove(0);
        }
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
        name = infobox.size() > 0 ? removeReferences(infobox.get(0).select("th div.fn").get(0).text()) : shortName;

        setupFlagEmoji();
        setupFlagURL();
        final WLCountry wlcountry = getWLCountry();
        if(wlcountry != null) {
            timezones = wlcountry.getTimeZones();
        }
    }
    public CustomCountry(JSONObject json) {
        tag = json.getString("tag");
        unStatus = json.has("unStatus") ? json.getString("unStatus") : null;
        sovereigntyDispute = json.has("sovereigntyDispute") ? json.getString("sovereigntyDispute") : null;
        shortName = json.has("shortName") ? json.getString("shortName") : tag;
        name = json.has("name") ? json.getString("name") : tag;
        flagURL = json.has("flagURL") && !json.getString("flagURL").equals("null") ? json.getString("flagURL") : null;
        flagEmoji = json.getString("flagEmoji");
        final WLCountry wlcountry = getWLCountry();
        if(wlcountry != null) {
            timezones = wlcountry.getTimeZones();
        }
    }

    @Override
    public String getShortName() {
        return shortName;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getFlagURL() {
        return flagURL;
    }
    public String getFlagEmoji() {
        return StringEscapeUtils.escapeJava(flagEmoji);
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

    @Override
    public void getInformation(APIVersion version, CompletionHandler handler) {
        if(information != null) {
            handler.handleString(information);
        } else {
            final long started = System.currentTimeMillis();
            getJSONObject(FileType.COUNTRIES_INFORMATION, shortName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final ConcurrentHashMap<InformationType, HashSet<String>> values = new ConcurrentHashMap<>();
                    final WLCountry country = getWLCountry();
                    if(country != null) {
                        final HashSet<CountryService> services = new HashSet<>(CountryServices.SERVICES);
                        final List<CountryResource> resources = new ArrayList<>();

                        if(country.hasTerritories()) {
                            final TerritoryDetails details = TerritoryDetails.INSTANCE;
                            services.add(details);
                            resources.add(new CountryResource("Territories", details.getURL(country)));
                        }

                        final String website = country.getGovernmentWebsite();
                        if(website != null) {
                            resources.add(new CountryResource("Government Website", country.getGovernmentWebsite()));
                        }
                        final HashSet<String> set = new HashSet<>();
                        for(CountryResource resource : resources) {
                            set.add(resource.toString());
                        }
                        values.put(CountryInformationType.RESOURCES, set);
                        loadNew(country, services, values, handler);
                    } else {
                        handler.handleString("{}");
                    }
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    information = json.toString();
                    WLLogger.log(Level.INFO, "CustomCountry - loaded information for country \"" + name + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(information);
                }
            });
        }
    }

    private void loadNew(WLCountry country, HashSet<CountryService> services, ConcurrentHashMap<InformationType, HashSet<String>> values, CompletionHandler handler) {
        final String backendID = getBackendID();
        final AtomicInteger completed = new AtomicInteger(0);
        final int max = services.size();
        services.parallelStream().forEach(service -> {
            final CountryInfo info = service.getInfo();
            final CompletionHandler serviceHandler = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final CountryInformationType informationType = service.getInformationType();
                    values.putIfAbsent(informationType, new HashSet<>());
                    values.get(informationType).add(string);
                    if(completed.addAndGet(1) == max) {
                        completeInformation(values, handler);
                    }
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
                service.getCountryValue(countryIdentifier, serviceHandler);
            }
        });
    }

    private void completeInformation(ConcurrentHashMap<InformationType, HashSet<String>> values, CompletionHandler handler) {
        final SovereignStateInformation info = new SovereignStateInformation(values);
        handler.handleString(info.toString());
    }

    private WLCountry getWLCountry() {
        switch (shortName.toLowerCase()) {
            case "east timor": return WLCountry.TIMOR_LESTE;
            case "são tomé & príncipe": return WLCountry.SAO_TOME_AND_PRINCIPE;
            case "st. vincent & grenadines": return WLCountry.SAINT_VINCENT_AND_THE_GRENADINES;
            case "state of palestine": return WLCountry.PALESTINE;
            case "equatorial guinea":
            case "mauritania":
            case "sahrawi arab democratic republic":
            case "south ossetia":
                return null;
            default:
                switch (name.toLowerCase()) {
                    case "democratic republic of the congo": return WLCountry.DEMOCRATIC_REPUBLIC_OF_THE_CONGO;
                    case "republic of the congo": return WLCountry.REPUBLIC_OF_THE_CONGO;
                    default:
                        return WLCountry.valueOf(shortName.toUpperCase().replace("ST. ", "SAINT ").replace("&", "AND").replace(" ", "_").replace("-", "_"));
                }
        }
    }

    private String getTimeZonesJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(WLTimeZone timezone : timezones) {
            builder.append(isFirst ? "" : ",").append("\"").append(timezone.getIdentifier()).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "\"" + name + "\":{" +
                (unStatus != null ? "\"unStatus\":\"" + unStatus + "\"," : "") +
                (sovereigntyDispute != null ? "\"sovereigntyDispute\":\"" + sovereigntyDispute + "\"," : "") +
                (!name.equals(shortName) ? "\"shortName\":\"" + shortName + "\"," : "") +
                (timezones != null ? "\"timezones\":" + getTimeZonesJSON() + "," : "") +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                "\"flagEmoji\":\"" + getFlagEmoji() + "\"" +
                "}";
    }

    @Override
    public String toServerJSON() {
        return "{" +
                "\"tag\":\"" + tag + "\"," +
                (unStatus != null ? "\"unStatus\":\"" + unStatus + "\"," : "") +
                (sovereigntyDispute != null ? "\"sovereigntyDispute\":\"" + sovereigntyDispute + "\"," : "") +
                (!name.equals(tag) ? "\"name\":\"" + name + "\"," : "") +
                (!shortName.equals(tag) ? "\"shortName\":\"" + shortName + "\"," : "") +
                (timezones != null ? "\"timezones\":" + getTimeZonesJSON() + "," : "") +
                (flagURL != null ? "\"flagURL\":\"" + flagURL + "\"," : "") +
                "\"flagEmoji\":\"" + getFlagEmoji() + "\"" +
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
            return "\"" + name + "\":{" +
                    "\"url\":\"" + url + "\"" +
                    "}";
        }
    }
}
