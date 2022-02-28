package me.randomhashtags.worldlaws;

import me.randomhashtags.worldlaws.country.*;
import me.randomhashtags.worldlaws.country.subdivisions.SubdivisionType;
import me.randomhashtags.worldlaws.history.CountryHistory;
import me.randomhashtags.worldlaws.info.availability.CountryAvailabilities;
import me.randomhashtags.worldlaws.info.rankings.CountryRankingService;
import me.randomhashtags.worldlaws.service.CountryService;
import me.randomhashtags.worldlaws.service.CountryServices;
import me.randomhashtags.worldlaws.service.SovereignStateService;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public final class CustomCountry implements SovereignState {

    public static boolean LOADED_NON_STATIC_INFORMATION = false;

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
    public String getInformation(APIVersion version) {
        if(informationCache == null) {
            final long started = System.currentTimeMillis();
            final JSONObject json = getJSONObject(Folder.COUNTRIES_INFORMATION, shortName, new CompletionHandler() {
                @Override
                public String loadJSONObjectString() {
                    final ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values = new ConcurrentHashMap<>();
                    final WLCountry country = getWLCountry();
                    if(country == null) {
                        return "{}";
                    } else {
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

                        final WLCountry[] neighbors = country.getNeighbors();
                        if(neighbors != null) {
                            final HashSet<String> array = new HashSet<>();
                            for(WLCountry neighbor : neighbors) {
                                array.add("\"" + neighbor.getBackendID() + "\"");
                            }
                            values.put(SovereignStateInformationType.NEIGHBORS, array);
                        }

                        final WLCurrency[] currencies = country.getCurrencies();
                        if(currencies != null) {
                            final HashSet<String> array = new HashSet<>();
                            for(WLCurrency currency : currencies) {
                                array.add("\"" + currency.name() + "\"");
                            }
                            values.put(SovereignStateInformationType.CURRENCIES, array);
                        }
                        return loadNew(country, services, values);
                    }
                }
            });

            if(information == null) {
                information = new SovereignStateInformation(json);
            }
            updateNonStaticInformation();
            WLLogger.logInfo("CustomCountry - loaded information for country \"" + name + "\" (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return informationCache;
    }

    public void updateNonStaticInformation() {
        if(information == null) {
            return;
        }
        if(!LOADED_NON_STATIC_INFORMATION) {
            LOADED_NON_STATIC_INFORMATION = true;
            new CompletableFutures<CountryService>().stream(CountryServices.NONSTATIC_SERVICES, SovereignStateService::loadData);
        }
        final SovereignStateInformationType resourcesType = SovereignStateInformationType.RESOURCES_NONSTATIC;
        final String backendID = getBackendID();

        for(SovereignStateInformationType type : SovereignStateInformationType.values()) {
            if(type.isNonStatic()) {
                information.remove(type);
            }
        }
        final WLCountry country = getWLCountry();
        new CompletableFutures<CountryService>().stream(CountryServices.NONSTATIC_SERVICES, service -> {
            final SovereignStateInfo info = service.getInfo();
            final String countryIdentifier;
            String string = null;
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
                final EventSources resources = service.getResources(countryIdentifier);
                if(resources != null && !resources.isEmpty()) {
                    information.putIfAbsent(resourcesType, new HashSet<>());
                    for(EventSource resource : resources) {
                        information.get(resourcesType).add(resource.toString());
                    }
                }
                try {
                    string = service.getCountryValue(countryIdentifier);
                } catch (Exception e) {
                    WLUtilities.saveException(e);
                }
            }
            if(string != null && !string.equals("null")) {
                final SovereignStateInformationType type = service.getInformationType();
                information.putIfAbsent(type, new HashSet<>());
                information.get(type).add(string);
            }
        });
        informationCache = information.toString(true);
    }

    private String loadNew(WLCountry country, HashSet<CountryService> services, ConcurrentHashMap<SovereignStateInformationType, HashSet<String>> values) {
        final SovereignStateInformationType resourcesInformationType = SovereignStateInformationType.RESOURCES_STATIC;
        final String backendID = getBackendID();

        new CompletableFutures<CountryService>().stream(services, service -> {
            final SovereignStateInfo info = service.getInfo();
            final String countryIdentifier;
            String string = null;
            switch (info) {
                case SERVICE_WIKIPEDIA:
                    countryIdentifier = shortName;
                    break;
                case SERVICE_COUNTRY_HISTORY:
                    countryIdentifier = null;
                    string = ((CountryHistory) service).getCountryValue(country);
                    break;
                case AVAILABILITIES:
                    countryIdentifier = null;
                    string = ((CountryAvailabilities) service).getCountryAvailabilities(backendID);
                    break;
                default:
                    countryIdentifier = backendID;
                    break;
            }
            if(countryIdentifier != null) {
                final EventSources resources = service.getResources(countryIdentifier);
                if(resources != null && !resources.isEmpty()) {
                    values.putIfAbsent(resourcesInformationType, new HashSet<>());
                    for(EventSource resource : resources) {
                        values.get(resourcesInformationType).add(resource.toString());
                    }
                }
                string = service.getCountryValue(countryIdentifier);
            }
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
        });
        information = new SovereignStateInformation(values);
        return information.toString(true);
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
            new CompletableFutures<SovereignStateSubdivision>().stream(Arrays.asList(subdivisions), subdivision -> {
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
