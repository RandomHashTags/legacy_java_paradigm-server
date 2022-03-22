package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.EventSources;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.service.NewCountryServiceNonStatic;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ConcurrentHashMap;

public enum TravelAdvisories implements NewCountryServiceNonStatic {
    INSTANCE;
    // US Embassies = https://www.usembassy.gov

    private ConcurrentHashMap<String, String> usTravelAdvisories;

    TravelAdvisories() {
    }

    @Override
    public SovereignStateInfo getInfo() {
        return SovereignStateInfo.SERVICE_TRAVEL_ADVISORIES;
    }

    @Override
    public EventSources getResources(WLCountry country) {
        final String countryBackendID = country.getBackendID();
        final EventSources sources = new EventSources();
        final ConcurrentHashMap<String, String> advisories = getAdvisories();
        if(advisories.containsKey(countryBackendID)) {
            final String url = "https://travel.state.gov" + usTravelAdvisories.get(countryBackendID);
            sources.add(new EventSource("U.S. Department of State: Travel", url));
        }
        return sources;
    }

    @Override
    public JSONObjectTranslatable getJSONObject(WLCountry country) {
        final String countryBackendID = country.getBackendID();
        TravelAdvisory advisory = null;
        final ConcurrentHashMap<String, String> advisories = getAdvisories();
        if(advisories.containsKey(countryBackendID)) {
            final String url = "https://travel.state.gov" + advisories.get(countryBackendID);
            advisory = new TravelAdvisory();
            advisory.setUSTravelAdvisory(new UnitedStatesTravelStateGovAdvisory(url));
        }
        JSONObjectTranslatable json = null;
        if(advisory != null) {
            json = advisory.toJSONObject();
        }
        return json;
    }

    private ConcurrentHashMap<String, String> getAdvisories() {
        if(usTravelAdvisories == null) {
            usTravelAdvisories = new ConcurrentHashMap<>();
            loadAdvisories();
        }
        return usTravelAdvisories;
    }

    private void loadAdvisories() {
        final String url = "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories.html/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Element element = doc.selectFirst("div.tsg-rwd-main-copy-body-frame div.tsg-rwd-content-page-parsysxxx div.edit_datatable div.table-data table tbody");
            if(element != null) {
                final Elements trs = element.select("tr");
                trs.remove(0);
                final ConcurrentHashMap<String, String> urls = new ConcurrentHashMap<>();
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
                        final WLCountry wlcountry = WLCountry.valueOfString(targetCountry);
                        if(wlcountry != null) {
                            final Element hrefElement = advisoryElement.selectFirst("a[href]");
                            if(hrefElement != null) {
                                final String href = hrefElement.attr("href");
                                urls.put(wlcountry.getBackendID(), href);
                            }
                        }
                    }
                }
                this.usTravelAdvisories = urls;
            }
        }
    }

    private final class TravelAdvisory {
        private UnitedStatesTravelStateGovAdvisory usTravelAdvisory;

        public void setUSTravelAdvisory(UnitedStatesTravelStateGovAdvisory usTravelAdvisory) {
            this.usTravelAdvisory = usTravelAdvisory;
        }
        public boolean isEmpty() {
            return usTravelAdvisory == null;
        }

        @Override
        public String toString() {
            return toJSONObject().toString();
        }

        public JSONObjectTranslatable toJSONObject() {
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            json.put("U.S. Department of State", usTravelAdvisory.toJSONObject());
            return json;
        }
    }
    private final class UnitedStatesTravelStateGovAdvisory {
        private final String url;

        public UnitedStatesTravelStateGovAdvisory(String url) {
            this.url = url;
        }

        public JSONObjectTranslatable toJSONObject() {
            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            json.put("url", url);
            return json;
        }
    }
}
