package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.*;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
import me.randomhashtags.worldlaws.history.CountryHistory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilities;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.info.service.CountryService;
import me.randomhashtags.worldlaws.info.service.CountryServices;
import me.randomhashtags.worldlaws.law.LawUtilities;
import me.randomhashtags.worldlaws.stream.ParallelStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomCountry implements SovereignState {

    private final String unStatus, sovereigntyDispute, shortName, name;
    private String flagEmoji;
    private HashSet<Integer> governmentAdministrations;
    private SovereignStateInformation information;
    private String informationCache;
    private WLCountry wlCountryCache;

    public CustomCountry(String tag, String unStatus, String sovereigntyDispute, Document page) {
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
        loadCountryDetails();
    }
    public CustomCountry(String tag, JSONObject json) {
        this.name = tag;
        unStatus = json.has("unStatus") ? json.getString("unStatus") : null;
        sovereigntyDispute = json.has("sovereigntyDispute") ? json.getString("sovereigntyDispute") : null;
        shortName = json.has("shortName") ? json.getString("shortName") : tag;
        flagEmoji = json.has("flagEmoji") ? json.getString("flagEmoji") : null;

        loadCountryDetails();
    }
    private void loadCountryDetails() {
        final WLCountry wlcountry = getWLCountry();
        if(wlcountry != null) {
            if(flagEmoji == null) {
                flagEmoji = wlcountry.getFlagEmoji();
            }
            governmentAdministrations = LawUtilities.getAdministrationVersions(wlcountry);
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
    public String getISOAlpha2() {
        return null;
    }
    @Override
    public String getFlagURL() {
        return null;
    }
    public String getFlagEmoji() {
        return flagEmoji;
    }

    @Override
    public void getInformation(APIVersion version, CompletionHandler handler) {
        if(informationCache != null) {
            handler.handleString(informationCache);
        } else {
            final long started = System.currentTimeMillis();
            getJSONObject(Folder.COUNTRIES_INFORMATION, shortName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values = new ConcurrentHashMap<>();
                    final WLCountry country = getWLCountry();
                    if(country != null) {
                        final HashSet<CountryService> services = new HashSet<>(CountryServices.STATIC_SERVICES);
                        final EventSources resources = new EventSources();

                        final String website = country.getGovernmentWebsite();
                        if(website != null) {
                            resources.add(new EventSource("Government Website", country.getGovernmentWebsite()));
                        }
                        final HashSet<String> set = new HashSet<>();
                        for(EventSource resource : resources) {
                            set.add(resource.toString());
                        }
                        values.put(SovereignStateInformationType.RESOURCES_STATIC, set);

                        final WLCountry[] neighbors = WLCountryNeighbors.get(country);
                        if(neighbors != null) {
                            final HashSet<String> array = new HashSet<>();
                            for(WLCountry neighbor : neighbors) {
                                array.add("\"" + neighbor.getBackendID() + "\"");
                            }
                            values.put(SovereignStateInformationType.NEIGHBORS, array);
                        }

                        final WLCurrency[] currencies = WLCountryCurrencies.get(country);
                        if(currencies != null) {
                            final HashSet<String> array = new HashSet<>();
                            for(WLCurrency currency : currencies) {
                                array.add("\"" + currency.name() + "\"");
                            }
                            values.put(SovereignStateInformationType.CURRENCIES, array);
                        }
                        loadNew(country, services, values, handler);
                    } else {
                        handler.handleString("{}");
                    }
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    if(information == null) {
                        information = new SovereignStateInformation(json);
                    }
                    updateNonStaticInformation();
                    informationCache = information.toString();
                    WLLogger.logInfo("CustomCountry - loaded information for country \"" + name + "\" (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(informationCache);
                }
            });
        }
    }

    public void updateNonStaticInformation() {
        final SovereignStateInformationType resourcesType = SovereignStateInformationType.RESOURCES_NONSTATIC;
        final String backendID = getBackendID();
        final CompletionHandler serviceHandler = new CompletionHandler() {
            @Override
            public void handleServiceResponse(CountryService service, String string) {
                if(string != null && !string.equals("null")) {
                    final SovereignStateInformationType type = service.getInformationType();
                    information.putIfAbsent(type, new HashSet<>());
                    information.get(type).add(string);
                }
            }
        };

        for(SovereignStateInformationType type : SovereignStateInformationType.values()) {
            if(type.isNonStatic()) {
                information.remove(type);
            }
        }
        ParallelStream.stream(CountryServices.NONSTATIC_SERVICES, serviceObj -> {
            final CountryService service = (CountryService) serviceObj;
            final WLCountry country = getWLCountry();
            final SovereignStateInfo info = service.getInfo();
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
                    countryIdentifier = targetName;
                    break;
                default:
                    countryIdentifier = backendID;
                    break;
            }
            if(countryIdentifier != null) {
                service.loadData();
                service.getCountryValue(countryIdentifier, new CompletionHandler() {
                    @Override
                    public void handleServiceResponse(CountryService service, String string) {
                        final EventSources resources = service.getResources(countryIdentifier);
                        if(resources != null && !resources.isEmpty()) {
                            information.putIfAbsent(resourcesType, new HashSet<>());
                            for(EventSource resource : resources) {
                                information.get(resourcesType).add(resource.toString());
                            }
                        }
                        serviceHandler.handleServiceResponse(service, string);
                    }
                });
            }
        });
        informationCache = information.toString();
    }

    private void loadNew(WLCountry country, HashSet<CountryService> services, ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values, CompletionHandler handler) {
        final SovereignStateInformationType resourcesInformationType = SovereignStateInformationType.RESOURCES_STATIC;
        final String backendID = getBackendID();
        final CompletionHandler serviceHandler = new CompletionHandler() {
            @Override
            public void handleServiceResponse(CountryService service, String string) {
                if(string != null && !string.equals("null")) {
                    final SovereignStateInformationType type = service.getInformationType();
                    values.putIfAbsent(type, new HashSet<>());

                    // TODO: make all CountryService's conform to this
                    if(service instanceof CountryRankingService) {
                        final String key = "\"" + service.getInfo().getTitle() + "\":";
                        if(!string.startsWith(key)) {
                            string = key + string;
                        }
                    }
                    values.get(type).add(string);
                }
            }
        };

        ParallelStream.stream(services, serviceObj -> {
            final CountryService service = (CountryService) serviceObj;
            final SovereignStateInfo info = service.getInfo();
            final String countryIdentifier;
            switch (info) {
                case SERVICE_TRAVEL_BRIEFING:
                case SERVICE_WIKIPEDIA:
                    countryIdentifier = shortName;
                    break;
                case SERVICE_COUNTRY_HISTORY:
                    countryIdentifier = null;
                    ((CountryHistory) service).getCountryValue(country, serviceHandler);
                    break;
                case AVAILABILITIES:
                    countryIdentifier = null;
                    ((CountryAvailabilities) service).getCountryAvailabilities(backendID, serviceHandler);
                    break;
                default:
                    countryIdentifier = backendID;
                    break;
            }
            if(countryIdentifier != null) {
                service.getCountryValue(countryIdentifier, new CompletionHandler() {
                    @Override
                    public void handleServiceResponse(CountryService service, String string) {
                        final EventSources resources = service.getResources(countryIdentifier);
                        if(resources != null && !resources.isEmpty()) {
                            values.putIfAbsent(resourcesInformationType, new HashSet<>());
                            for(EventSource resource : resources) {
                                values.get(resourcesInformationType).add(resource.toString());
                            }
                        }
                        serviceHandler.handleServiceResponse(service, string);
                    }
                });
            }
        });
        information = new SovereignStateInformation(values);
        handler.handleString(information.toString());
    }

    public WLCountry getWLCountry() {
        if(wlCountryCache == null) {
            wlCountryCache = returnWLCountry();
        }
        return wlCountryCache;
    }
    private WLCountry returnWLCountry() {
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

    private JSONArray getGovernmentAdministrationsJSONArray() {
        return new JSONArray(governmentAdministrations);
    }

    private JSONObject getSubdivisionsJSON() {
        final WLCountry wlcountry = getWLCountry();
        JSONObject returnedJSON = null;
        final SovereignStateSubdivision[] subdivisions = wlcountry != null ? wlcountry.getSubdivisions() : null;
        if(subdivisions != null) {
            final JSONObject json = new JSONObject();
            final SubdivisionType defaultType = subdivisions[0].getDefaultType();
            json.put("default_type_name_plural", defaultType.getPluralName());
            json.put("default_type_name_singular", defaultType.getSingularName());
            if(LawUtilities.hasSubdivisionGovernmentsSupported(wlcountry)) {
                json.put("default_supports_government", true);
            }
            final JSONObject subdivisionsJSON = new JSONObject();
            ParallelStream.stream(Arrays.asList(subdivisions), subdivisionObj -> {
                final SovereignStateSubdivision subdivision = (SovereignStateSubdivision) subdivisionObj;
                String name = subdivision.getRealName();
                if(name == null) {
                    name = subdivision.getName();
                }
                subdivisionsJSON.put(name, subdivision.toJSONObject());
            });
            json.put("subdivisions", subdivisionsJSON);
            returnedJSON = json;
        }
        return returnedJSON;
    }

    public JSONObject toJSONObject() {
        final boolean hasGovernmentAdministrations = governmentAdministrations != null;
        final WLCountry wlcountry = getWLCountry();
        HashSet<String> aliases = null;
        WLTimeZone[] timezones = null;
        String isoAlpha2 = null, isoAlpha3 = null;
        int currentGovernmentAdministration = -1;
        if(wlcountry != null) {
            aliases = wlcountry.getAliases();
            timezones = wlcountry.getTimeZones();
            isoAlpha2 = wlcountry.getISOAlpha2();
            isoAlpha3 = wlcountry.getISOAlpha3();
            currentGovernmentAdministration = LawUtilities.getCurrentAdministrationVersion(wlcountry);
        }
        final JSONObject subdivisions = getSubdivisionsJSON();

        final JSONObject json = new JSONObject();
        if(aliases != null) {
            aliases.removeIf(alias -> alias.equalsIgnoreCase(shortName) || alias.equalsIgnoreCase(name));
            if(!aliases.isEmpty()) {
                final JSONArray array = new JSONArray(aliases);
                json.put("aliases", array);
            }
        }
        if(isoAlpha2 != null) {
            json.put("isoAlpha2", isoAlpha2);
        }
        if(isoAlpha3 != null) {
            json.put("isoAlpha3", isoAlpha3);
        }
        if(unStatus != null) {
            json.put("unStatus", unStatus);
        }
        if(sovereigntyDispute != null) {
            json.put("sovereigntyDispute", sovereigntyDispute);
        }
        if(hasGovernmentAdministrations) {
            json.put("currentGovernmentAdministration", currentGovernmentAdministration);
            json.put("governmentAdministrations", getGovernmentAdministrationsJSONArray());
        }
        if(!name.equals(shortName)) {
            json.put("shortName", shortName);
        }
        if(timezones != null) {
            json.put("timezones", getTimeZonesJSONArray(timezones));
        }
        if(subdivisions != null) {
            json.put("subdivisions", subdivisions);
        }
        json.put("flagEmoji", flagEmoji);
        return json;
    }
}
