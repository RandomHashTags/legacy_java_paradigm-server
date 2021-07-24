package me.randomhashtags.worldlaws.location;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;
import me.randomhashtags.worldlaws.law.LawUtilities;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CustomCountry implements SovereignState, ServerObject {

    private final String tag, unStatus, sovereigntyDispute, shortName, name;
    private String isoAlpha2, flagURL, flagEmoji;
    private int currentGovernmentAdministration;
    private HashSet<Integer> governmentAdministrations;
    private WLTimeZone[] timezones;
    private String information;

    public CustomCountry(String tag, String unStatus, String sovereigntyDispute, Document page) {
        this.tag = tag;
        this.unStatus = unStatus;
        this.sovereigntyDispute = sovereigntyDispute;
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

        final WLCountry wlcountry = getWLCountry();
        if(wlcountry != null) {
            isoAlpha2 = wlcountry.getISOAlpha2();
            if(isoAlpha2 != null) {
                flagURL = "https://raw.githubusercontent.com/stsrki/country-flags/master/png1000px/" + isoAlpha2.toLowerCase() + ".png";
            }
            flagEmoji = wlcountry.getFlagEmoji();
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

    @Override
    public void getInformation(APIVersion version, CompletionHandler handler) {
        if(information != null) {
            handler.handleString(information);
        } else {
            final long started = System.currentTimeMillis();
            getJSONObject(Folder.COUNTRIES_INFORMATION, shortName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values = new ConcurrentHashMap<>();
                    final WLCountry country = getWLCountry();
                    if(country != null) {
                        governmentAdministrations = LawUtilities.getAdministrationVersions(country);
                        if(governmentAdministrations != null) {
                            currentGovernmentAdministration = LawUtilities.getCurrentAdministrationVersion(country);
                        }
                        final HashSet<CountryService> services = new HashSet<>(CountryServices.SERVICES);
                        final List<CountryResource> resources = new ArrayList<>();

                        final SovereignStateSubdivision[] subdivisions = country.getSubdivisions();
                        if(subdivisions != null) {
                            final Stream<String> stream = Arrays.stream(subdivisions).map(SovereignStateSubdivision::toServerJSON);
                            final HashSet<String> territories = stream.parallel().collect(Collectors.toCollection(HashSet::new));
                            values.put(SovereignStateInformationType.TERRITORIES, territories);
                        }

                        final WLConstitution constitution = country.getConstitution();
                        if(constitution != null) {
                            resources.add(new CountryResource("Government Constitution", constitution.getURL()));
                            resources.add(new CountryResource("Government Constitution (Wikipedia)", constitution.getWikipediaURL()));
                        }

                        final String website = country.getGovernmentWebsite();
                        if(website != null) {
                            resources.add(new CountryResource("Government Website", country.getGovernmentWebsite()));
                        }
                        final HashSet<String> set = new HashSet<>();
                        for(CountryResource resource : resources) {
                            set.add(resource.toString());
                        }
                        values.put(SovereignStateInformationType.RESOURCES, set);
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

    private void loadNew(WLCountry country, HashSet<CountryService> services, ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values, CompletionHandler handler) {
        final SovereignStateInformationType resourcesInformationType = SovereignStateInformationType.RESOURCES;
        final String backendID = getBackendID();
        final AtomicInteger completed = new AtomicInteger(0);
        final int max = services.size();
        services.parallelStream().forEach(service -> {
            final SovereignStateInfo info = service.getInfo();
            final CompletionHandler serviceHandler = new CompletionHandler() {
                @Override
                public void handleString(String string) {
                    final SovereignStateInformationType informationType = service.getInformationType();
                    if(string != null && !string.equals("null")) {
                        values.putIfAbsent(informationType, new HashSet<>());
                        values.get(informationType).add(string);
                    }
                    tryCompletingInformation(max, completed, values, handler);
                }
            };
            final String countryIdentifier;
            switch (info) {
                case SERVICE_CIA_VALUES:
                    final String targetName;
                    switch (country) {
                        case BAHAMAS:
                            targetName = "Bahamas the";
                            break;
                        default:
                            targetName = shortName;
                            break;
                    }
                    countryIdentifier = null;
                    service.getResources(targetName, new CompletionHandler() {
                        @Override
                        public void handleCountryResources(HashSet<CountryResource> resources) {
                            if(resources != null && !resources.isEmpty()) {
                                values.putIfAbsent(resourcesInformationType, new HashSet<>());
                                for(CountryResource resource : resources) {
                                    values.get(resourcesInformationType).add(resource.toString());
                                }
                            }
                            tryCompletingInformation(max, completed, values, handler);
                        }
                    });
                    break;
                case SERVICE_TRAVEL_BRIEFING:
                    countryIdentifier = shortName;
                    break;
                case SERVICE_WIKIPEDIA:
                    countryIdentifier = tag;
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

    private void tryCompletingInformation(int max, AtomicInteger completed, ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values, CompletionHandler handler) {
        if(completed.addAndGet(1) == max) {
            final SovereignStateInformation info = new SovereignStateInformation(values);
            handler.handleString(info.toString());
        }
    }

    private WLCountry getWLCountry() {
        switch (shortName.toLowerCase()) {
            case "east timor": return WLCountry.TIMOR_LESTE;
            case "são tomé & príncipe": return WLCountry.SAO_TOME_AND_PRINCIPE;
            case "st. vincent & grenadines": return WLCountry.SAINT_VINCENT_AND_THE_GRENADINES;
            case "state of palestine": return WLCountry.PALESTINE;
            case "equatorial guinea":
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
    private String getGovernmentAdministrationsJSON() {
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(int version : governmentAdministrations) {
            builder.append(isFirst ? "" : ",").append(version);
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        final boolean hasGovernmentAdministrations = governmentAdministrations != null;
        return "\"" + name + "\":{" +
                (isoAlpha2 != null ? "\"isoAlpha2\":\"" + isoAlpha2 + "\"," : "") +
                (unStatus != null ? "\"unStatus\":\"" + unStatus + "\"," : "") +
                (sovereigntyDispute != null ? "\"sovereigntyDispute\":\"" + sovereigntyDispute + "\"," : "") +
                (hasGovernmentAdministrations ? "\"currentGovernmentAdministration\":" + currentGovernmentAdministration + "," : "") +
                (hasGovernmentAdministrations ? "\"governmentAdministrations\":" + getGovernmentAdministrationsJSON() + "," : "") +
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
}
