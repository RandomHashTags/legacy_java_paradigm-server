package me.randomhashtags.worldlaws.info.service.nonstatic;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.country.SovereignStateInfo;
import me.randomhashtags.worldlaws.country.SovereignStateInformationType;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.info.service.CountryService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum TravelAdvisories implements CountryService {
    INSTANCE;

    private final HashMap<String, String> urls;

    TravelAdvisories() {
        urls = new HashMap<>();
    }

    @Override
    public void loadData(CompletionHandler handler) {
        final String url = "https://travel.state.gov/content/travel/en/traveladvisories/traveladvisories.html/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Element element = doc.selectFirst("div.tsg-rwd-main-copy-body-frame div.tsg-rwd-content-page-parsysxxx div.edit_datatable div.table-data table tbody");
            if(element != null) {
                final Elements trs = element.select("tr");
                trs.remove(0);
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
            }
        }
        handler.handleServiceResponse(this, null);
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
    public void getCountryValue(String countryBackendID, CompletionHandler handler) {
        String string = null;
        if(urls.containsKey(countryBackendID)) {
            final String url = "https://travel.state.gov" + urls.get(countryBackendID);
            final Document doc = getDocument(url);
            if(doc != null) {
                final Element htmlElement = doc.selectFirst( "div.tsg-rwd-content-page-parsysxxx div.EmergencyAlert div.tsg-rwd-emergency-alert-frame div.tsg-rwd-emergency-alert-text");
                if(htmlElement != null) {
                    string = htmlElement.html();
                }
            }
        }
        handler.handleString(string);
    }
}
