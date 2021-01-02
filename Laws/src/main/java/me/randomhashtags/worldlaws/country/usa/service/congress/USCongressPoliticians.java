package me.randomhashtags.worldlaws.country.usa.service.congress;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.people.HumanName;
import me.randomhashtags.worldlaws.people.PoliticalParty;
import me.randomhashtags.worldlaws.country.usa.USPolitician;
import me.randomhashtags.worldlaws.people.Politician;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.logging.Level;

public enum USCongressPoliticians implements Jsoupable {
    INSTANCE;

    private StringBuilder politicians;
    public static final HashMap<String, Politician> POLITICIANS = new HashMap<>();

    /*public String getAllPoliticians(int version) {
        if(politicians == null) {
            final long started = System.currentTimeMillis();
            politicians = new StringBuilder("[");
            final String docURL = "https://www.congress.gov/members?q={%22congress%22:" + version + "}&searchResultViewType=expanded&pageSize=250&page=%page%";
            Document doc = getDocument(docURL.replace("%page%", "1"));
            if(doc != null) {
                final Elements number = doc.select("div.basic-search-tune div.pagination span.results-number");
                final int pages = !number.isEmpty() ? Integer.parseInt(number.text().split("of ")[1]) : 1;
                boolean isFirst = true;

                for(int i = 1; i <= pages; i++) {
                    if(i != 1) {
                        doc = getDocument(docURL.replace("%page%", Integer.toString(i)));
                    }
                    final Elements table = doc.select("ol.basic-search-results-lists li.expanded");
                    for(Element element : table) {
                        final String profileSlug = element.select("span.result-heading a[href]").get(0).attr("href");
                        final USPolitician prePolitician = getPolitician(element, profileSlug, true);
                        final HumanName name = prePolitician.getName();
                        final String uniqueID = name.getFirstName() + name.getMiddleName() + name.getLastName();
                        politicians.append(isFirst ? "" : ",").append(prePolitician.toJSON());
                        POLITICIANS.put(uniqueID, prePolitician);
                        isFirst = false;
                    }
                }
            }
            politicians.append("]");
            WLLogger.log(Level.INFO, "USCongressPoliticians - loaded politicians for congress " + version + " (took " + (System.currentTimeMillis()-started) + "ms)");
        }
        return politicians.toString();
    }*/
    public String getPolitician(String uuid) {
        return POLITICIANS.get(uuid).toJSON();
    }
    public void getPolitician(Element elements, String profileSlug, boolean fromList, CompletionHandler handler) {
        if(POLITICIANS.containsKey(profileSlug)) {
            handler.handlePolitician(POLITICIANS.get(profileSlug));
        } else {
            final CompletionHandler completion = new CompletionHandler() {
                @Override
                public void handlePolitician(Politician politician) {
                    POLITICIANS.put(profileSlug, politician);
                    handler.handlePolitician(politician);
                }
            };
            if(fromList) {
                getPoliticianFromList(profileSlug, elements);
            } else {
                USPolitician.getFromBill(profileSlug, elements, completion);
            }
        }
    }
    private USPolitician getPoliticianFromList(String profileSlug, Element element) {
        final String congress = "https://www.congress.gov/";
        final Elements images = element.select("div.member-image img");
        final String image = images.size() > 0 ? images.get(0).attr("src") : null;
        final String text = element.text();
        final PoliticalParty party = PoliticalParty.valueOf(text.split("Party: ")[1].split(" Served")[0].toUpperCase());

        final boolean containsDistrict = text.contains("District:");
        final String governedTerritory = text.split(" State: ")[1].split(" " + (containsDistrict ? "District:" : "Party:"))[0].replace(" ", "_").toUpperCase();

        final String district = containsDistrict ? text.split("District: ")[1].split(" Party")[0] : "";

        final boolean isSenator = text.contains("Senator"), isHouse = text.contains("Representative"), isResidentCommissioner = text.contains("Resident Commissioner"), isDelegate = text.contains("Delegate");
        final String[] nameValues = text.split((isSenator ? "Senator" : isHouse ? "Representative" : isResidentCommissioner ? "Resident Commissioner" : isDelegate ? "Delegate" : "") + " ")[1].split(" State:")[0].replace(",", "").split(" ");
        final HumanName name = new HumanName(nameValues[1], nameValues.length > 2 ? nameValues[2] : "", nameValues[0]);

        return new USPolitician(name, governedTerritory, district, party, image == null ? "" : congress + image, congress + profileSlug.substring(1), null);
    }

    /*
    private USPolitician getRealPolitician(String profileSlug) {
        if(POLITICIANS.containsKey(profileSlug)) {
            return POLITICIANS.get(profileSlug);
        } else {
            final String url = "https://www.congress.gov/member/" + profileSlug;
            final Document doc = getDocument(url);
            final Elements table = doc.select("div.container_shadow main.content div.featured div.overview_wrapper div.overview div.overview-member-row");
            final Elements profile = table.select("div.overview-member-column-profile");
            final Elements images = table.select("div.overview-member-column-picture a[href]");
            final String image = images.size() > 0 ? images.get(0).attr("src") : null;

            final Elements profileRows = profile.select("table.standard01 tbody tr td");
            final String website = profileRows.select("a[href]").get(0).text();
            final String text = "District: 1";
            final PoliticalParty party = PoliticalParty.valueOf(profileRows.get(2).text().toUpperCase());

            final boolean containsDistrict = text.contains("District:");
            final String governedTerritory = text.split(" State: ")[1].split(" " + (containsDistrict ? "District:" : "Party:"))[0].replace(" ", "_").toUpperCase();

            final String district = containsDistrict ? text.split("District: ")[1].split(" Party")[0] : "";

            final String nameText = doc.select("h1.legDetail").get(0).text().split(" \\(")[0];
            final boolean isSenator = nameText.contains("Senator"), isHouse = nameText.contains("Representative"), isResidentCommissioner = nameText.contains("Resident Commissioner"), isDelegate = nameText.contains("Delegate");
            final String[] nameValues = nameText.split((isSenator ? "Senator" : isHouse ? "Representative" : isResidentCommissioner ? "Resident Commissioner" : isDelegate ? "Delegate" : "") + " ")[1].split(" State:")[0].replace(",", "").split(" ");
            final HumanName name = new HumanName(nameValues[1], nameValues.length > 2 ? nameValues[2] : "", nameValues[0]);

            final USPolitician politician = new USPolitician(name, governedTerritory, district, party, image == null ? "" : "https://www.congress.gov/img/member/" + image, url, website);
            POLITICIANS.put(profileSlug, politician);
            return politician;
        }
    }*/
}
