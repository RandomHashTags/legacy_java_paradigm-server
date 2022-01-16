package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum TravelAdvisories implements CountryService {
    INSTANCE;

    private HashMap<String, String> usTravelAdvisories;

    TravelAdvisories() {
        usTravelAdvisories = new HashMap<>();
    }

    @Override
    public EventSources getResources(String countryBackendID) {
        final EventSources sources = new EventSources();
        if(usTravelAdvisories.containsKey(countryBackendID)) {
            final String url = "https://travel.state.gov" + usTravelAdvisories.get(countryBackendID);
            sources.add(new EventSource("U.S. Department of State: Travel", url));
        }
        return sources;
    }

    @Override
    public String loadData() {
        final String url = "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories.html/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Element element = doc.selectFirst("div.tsg-rwd-main-copy-body-frame div.tsg-rwd-content-page-parsysxxx div.edit_datatable div.table-data table tbody");
            if(element != null) {
                final Elements trs = element.select("tr");
                trs.remove(0);
                final HashMap<String, String> urls = new HashMap<>();
                for(Element tr : trs) {
                    final Elements tds = tr.select("td");
                    final Element advisoryElement = tds.get(0);
                    final String advisory = advisoryElement.text();
                    if(!advisory.toLowerCase().contains("worldwide")) {
                        String targetCountry = advisory.split(" Travel Advisory")[0].replace(" ", "").split("\\(")[0].toLowerCase();
                        switch (targetCountry) {
                            case "thekyrgyzrepublic":
                                targetCountry = "kyrgyzstan";
                                break;
                            default:
                                break;
                        }
                        final WLCountry wlcountry = WLCountry.valueOfBackendID(targetCountry);
                        if(wlcountry != null) {
                            final String href = advisoryElement.selectFirst("a[href]").attr("href");
                            urls.put(wlcountry.getBackendID(), href);
                        }
                    }
                }
                this.usTravelAdvisories = urls;
            }
        }
        return null;
    }

    @Override
    public SovereignStateInformationType getInformationType() {
        return SovereignStateInformationType.SERVICES_NONSTATIC;
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_TRAVEL_ADVISORIES;
    }

    @Override
    public String getCountryValue(String countryBackendID) {
        final TravelAdvisory advisory = new TravelAdvisory();
        if(usTravelAdvisories.containsKey(countryBackendID)) {
            final String url = "https://travel.state.gov" + usTravelAdvisories.get(countryBackendID);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Element htmlElement = doc.selectFirst("div.tsg-rwd-content-page-parsysxxx div.EmergencyAlert div.tsg-rwd-emergency-alert-frame div.tsg-rwd-emergency-alert-text");
                if(htmlElement != null) {
                    final UnitedStatesTravelStateGovAdvisory usTravelAdvisory = new UnitedStatesTravelStateGovAdvisory(htmlElement.html());
                    advisory.setUSTravelAdvisory(usTravelAdvisory);
                }
            }
        }
        String string = null;
        if(!advisory.isEmpty()) {
            string = "\"Travel Advisories\":" + advisory.toString();
        }
        return string;
    }

    private final class TravelAdvisory {
        private UnitedStatesTravelStateGovAdvisory usTravelAdvisory;

        public void setUSTravelAdvisory(UnitedStatesTravelStateGovAdvisory usTravelAdvisory) {
            this.usTravelAdvisory = usTravelAdvisory;
        }
        public boolean isEmpty() {
            return usTravelAdvisories == null;
        }

        @Override
        public String toString() {
            return toJSONObject().toString();
        }
        public JSONObject toJSONObject() {
            final JSONObject json = new JSONObject();
            json.put("U.S. Department of State", usTravelAdvisory.toJSONObject());
            return json;
        }
    }
    private final class UnitedStatesTravelStateGovAdvisory {
        private final String html;

        public UnitedStatesTravelStateGovAdvisory(String html) {
            this.html = LocalServer.fixEscapeValues(html);
        }

        public JSONObject toJSONObject() {
            final JSONObject json = new JSONObject();
            json.put("html", html);
            return json;
        }
    }
}
