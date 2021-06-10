package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.FileType;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.usa.federal.PreCongressBill;
import me.randomhashtags.worldlaws.country.usa.federal.USCongress;
import me.randomhashtags.worldlaws.law.LegislationType;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.people.PoliticalParty;
import me.randomhashtags.worldlaws.people.Politician;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public final class USPolitician implements Politician {
    private final HumanName name;
    private final PoliticalParty party;
    private final String district, governedTerritory, imageURL, url, website;
    private HashMap<LegislationType, HashMap<Integer, String>> signedLegislation;

    public USPolitician(HumanName name, String governedTerritory, String district, PoliticalParty party, String imageURL, String url, String website) {
        this.name = name;
        this.governedTerritory = governedTerritory;
        this.district = district;
        this.party = party;
        this.imageURL = imageURL;
        this.url = url;
        this.website = website;
    }
    public USPolitician(JSONObject json) {
        name = json.has("name") ? new HumanName(json.getJSONObject("name")) : null;
        governedTerritory = json.has("governedTerritory") ? json.getString("governedTerritory") : null;
        district = json.has("district") ? json.getString("district") : null;
        party = PoliticalParty.valueOf(json.getString("party").toUpperCase());
        imageURL = json.has("imageURL") ? "https://www.congress.gov/img/member/" + json.getString("imageURL") : null;
        url = json.has("url") ? "https://www.congress.gov/member/" + json.getString("url") : null;
        website = json.has("website") ? json.getString("website") : null;
    }

    @Override
    public HumanName getName() {
        return name;
    }
    @Override
    public String getGovernedTerritory() {
        return governedTerritory;
    }
    @Override
    public String getDistrict() {
        return district;
    }
    @Override
    public PoliticalParty getCurrentParty() {
        return party;
    }
    @Override
    public String getImageURL() {
        return imageURL;
    }
    @Override
    public String getURL() {
        return url;
    }
    @Override
    public String getWebsite() {
        return website;
    }

    @Override
    public String getSignedLegislationJSON(LegislationType type, int administration) {
        if(signedLegislation == null) {
            signedLegislation = new HashMap<>();
        }
        if(signedLegislation.containsKey(type) && signedLegislation.get(type).containsKey(administration)) {
            return signedLegislation.get(type).get(administration);
        } else {
            final long started = System.currentTimeMillis();
            if(!signedLegislation.containsKey(type)) {
                signedLegislation.put(type, new HashMap<>());
            }
            final String targetURL = url + "?pageSize=250&q=%7B%22sponsorship%22%3A%22" + type.name().toLowerCase() + "%22%7D";
            final USCongress congress = USCongress.getCongress(administration);
            final Elements table = Jsoupable.getStaticDocumentElements(FileType.LAWS_USA_MEMBERS, targetURL, true, "main.content div.main-wrapper div.search-row div.search-column-main ol.basic-search-results-list li.expanded");
            final StringBuilder builder = new StringBuilder("[");
            boolean isFirst = true;
            for(Element element : table) {
                final PreCongressBill bill = congress.getPreCongressBillFrom(element);
                builder.append(isFirst ? "" : ",").append(bill.toString());
                isFirst = false;
            }
            builder.append("]");
            final String string = builder.toString();
            signedLegislation.get(type).put(administration, string);
            final HumanName name = getName();
            WLLogger.log(Level.INFO, "USPolitician - loaded \"" + name.getFirstName() + " " + name.getMiddleName() + " " + name.getLastName() + "\"'s " + type.name() + " legislation for administration " + administration + " (took " + (System.currentTimeMillis()-started) + "ms)");
            return string;
        }
    }
}