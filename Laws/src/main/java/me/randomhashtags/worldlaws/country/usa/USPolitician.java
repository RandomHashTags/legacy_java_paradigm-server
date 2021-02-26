package me.randomhashtags.worldlaws.country.usa;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.usa.federal.PreCongressBill;
import me.randomhashtags.worldlaws.country.usa.federal.USCongress;
import me.randomhashtags.worldlaws.country.usa.service.congress.USCongressPoliticians;
import me.randomhashtags.worldlaws.law.LegislationType;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.people.PoliticalParty;
import me.randomhashtags.worldlaws.people.Politician;
import org.apache.logging.log4j.Level;
import org.jsoup.nodes.Document;
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
            final Elements table = Jsoupable.getStaticDocumentElements(FileType.PEOPLE_POLITICIANS_UNITED_STATES, targetURL, "main.content div.main-wrapper div.search-row div.search-column-main ol.basic-search-results-list li.expanded");
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

    public static void getFromBill(String profileSlug, Element element, CompletionHandler handler) {
        final String text = element.text();
        final String[] describingValues = text.split(" \\[")[1].split("]")[0].split("-");
        final PoliticalParty party = PoliticalParty.fromAbbreviation(describingValues[0]);
        final String governedTerritory = describingValues[1];
        final String district = describingValues.length > 2 ? describingValues[2] : null;

        final String congressURLPrefix = "https://www.congress.gov";
        final String url = congressURLPrefix + profileSlug;
        final Document doc = Jsoupable.getStaticDocument(FileType.PEOPLE_POLITICIANS_UNITED_STATES, url, true);
        String imageURL = null;
        HumanName name = null;
        if(doc != null) {
            final String tag = doc.select("h1.legDetail").get(0).text().split("\\(")[0].replace("Representative ", "").replace("Senator ", "");
            final String[] values = tag.split(" ");
            final int length = values.length;
            final boolean hasMiddleName = length == 3, hasExtension = length == 4;
            final String firstName = LocalServer.fixEscapeValues(values[0]);
            final String middleName = hasMiddleName ? LocalServer.fixEscapeValues(values[1].replace("\\.", "")) : "";
            final String lastName = LocalServer.fixEscapeValues(hasMiddleName || hasExtension ? values[2] : values[1]);
            name = new HumanName(firstName, middleName, lastName);

            final Elements images = doc.select("div.overview_wrapper div.overview div.overview-member-row div.overview-member-column-picture a[href] img");
            imageURL = images.isEmpty() ? null : images.get(0).attr("src");
        }
        final USPolitician politician = new USPolitician(name, governedTerritory, district, party, imageURL == null ? "null" : congressURLPrefix + imageURL, url, null);
        final String uniqueID = name != null ? name.getFirstName() + name.getMiddleName() + name.getLastName() : "";
        USCongressPoliticians.POLITICIANS.put(uniqueID, politician);
        handler.handlePolitician(politician);
    }
}
