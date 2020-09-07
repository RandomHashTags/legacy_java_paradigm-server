package me.randomhashtags.worldlaws.federal;

import me.randomhashtags.worldlaws.Chamber;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.people.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public enum Congress implements Jsoupable {
    v93,
    v94,
    v95,
    v96,
    v97,
    v98,
    v99,
    v100,
    v101,
    v102,
    v103,
    v104,
    v105,
    v106,
    v107,
    v108,
    v109,
    v110,
    v111,
    v112,
    v113,
    v114,
    v115,
    v116,
    v117,
    v118,
    v119,
    v120,
    v121,
    v122,
    v123,
    v124,
    v125,
    v126,
    v127,
    v128,
    v129,
    v130,

    ;

    private StringBuilder politicians, enactedBills, introducedBills;
    private String pdfURL;
    private HashMap<String, Politician> politicianMap;
    private HashMap<String, String> enactedBill, introducedBill;

    private String getVersion() {
        return name().substring(1);
    }
    private String getVersioned() {
        final String version = getVersion();
        return version.endsWith("3") && !version.endsWith("13") ? version + "rd" : version + "th";
    }

    public String getPoliticians() {
        if(politicians == null) {
            politicianMap = new HashMap<>();
            politicians = new StringBuilder("[");
            final String docURL = "https://www.congress.gov/members?q={%22congress%22:" + getVersion() + "}&searchResultViewType=expanded&pageSize=250&page=%page%";
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
                        final Politician prePolitician = getPolitician(element, profileSlug, true);
                        politicians.append(isFirst ? "" : ",").append(prePolitician.toString());
                        politicianMap.put(profileSlug, prePolitician);
                        isFirst = false;
                    }
                }
            }
            politicians.append("]");
        }
        return politicians.toString();
    }
    private Politician getPolitician(Element elements, String profileSlug, boolean fromList) {
        if(politicianMap == null) {
            politicianMap = new HashMap<>();
        }
        if(politicianMap.containsKey(profileSlug)) {
            return politicianMap.get(profileSlug);
        } else {
            final Politician prePolitician = fromList ? getPoliticianFromList(profileSlug, elements) : getPoliticianFromBill(profileSlug, elements);
            politicianMap.put(profileSlug, prePolitician);
            return prePolitician;
        }
    }
    private Politician getPoliticianFromList(String profileSlug, Element element) {
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

        return new Politician(name, Gender.UNKNOWN, governedTerritory, district, party, image == null ? "" : "https://www.congress.gov/" + image, null);
    }
    private Politician getPoliticianFromBill(String profileSlug, Element element) {
        final String text = element.text();
        final HumanName name = new HumanName("", "", "");
        final String[] describingValues = text.split(" \\[")[1].split("]")[0].split("-");
        final PoliticalParty party = PoliticalParty.fromAbbreviation(describingValues[0]);
        final String governedTerritory = describingValues[1];
        final String district = describingValues.length > 2 ? describingValues[2] : null;

        final String url = "https://www.congress.gov/" + profileSlug;
        final Document doc = getDocument(url);
        String imageURL = null;
        if(doc != null) {
            final Elements images = doc.select("div.overview_wrapper div.overview div.overview-member-row div.overview-member-column-picture a[href] img");
            imageURL = images.isEmpty() ? null : images.get(0).attr("src");
        }
        return new Politician(name, Gender.UNKNOWN, governedTerritory, district, party, imageURL == null ? "null" : "https://www.congress.gov" + imageURL, null);
    }

    private Politician getRealPolitician(String profileSlug) {
        if(politicianMap.containsKey(profileSlug)) {
            return politicianMap.get(profileSlug);
        } else {
            final String url = "https://www.congress.gov/member/%slug%";
            final Document doc = getDocument(url.replace("%slug%", profileSlug));
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

            final Politician politician = new Politician(name, Gender.UNKNOWN, governedTerritory, district, party, image == null ? "" : "https://www.congress.gov/img/member/" + image, website);
            politicianMap.put(profileSlug, politician);
            return politician;
        }
    }
    public String getPolitician(String profileSlug) {
        return getRealPolitician(profileSlug).toString();
    }

    public String getIntroducedBills() {
        if(introducedBills == null) {
            introducedBills = new StringBuilder("[");
            final String version = getVersion();
            final String url = "https://www.congress.gov/search?searchResultViewType=expanded&pageSize=250&q=%7B%22source%22%3A%22legislation%22%2C%22congress%22%3A%22" + version + "%22%2C%22bill-status%22%3A%22introduced%22%7D";
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements items = doc.select("main.content div.main-wrapper div.search-row div.search-column-main ol.basic-search-results-lists li.expanded");
                boolean isFirst = true;
                for(Element element : items) {
                    final String heading = element.select("span.result-heading").get(0).select("a[href]").text();
                    final boolean isHouse = heading.startsWith("H.R.");
                    final Chamber chamber = isHouse ? Chamber.HOUSE : Chamber.SENATE;
                    final String id = heading.split("\\.")[isHouse ? 2 : 1];

                    final String title = element.select("span.result-title").get(0).text();
                    final Elements resultItems = element.select("span.result-item");
                    final int results = resultItems.size();

                    final String committees = resultItems.get(results-3).text().split(" - ")[1];

                    final String dateString = resultItems.get(results-2).text().split(" - ")[1].split(" ")[0];
                    final String[] dateValues = dateString.split("/");
                    final int year = Integer.parseInt(dateValues[2]), month = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[1]);
                    final LocalDate date = LocalDate.of(year, month, day);

                    final IntroducedBill bill = new IntroducedBill(chamber, id, title, committees, date);
                    introducedBills.append(isFirst ? "" : ",").append(bill.toString());
                    isFirst = false;
                }
            }
            introducedBills.append("]");
        }
        return introducedBills.toString();
    }

    public String getEnactedBills() {
        if(enactedBills == null) {
            enactedBill = new HashMap<>();
            enactedBills = new StringBuilder("[");

            final String version = getVersioned();
            final String url = "https://www.congress.gov/public-laws/" + version + "-congress";
            final Document doc = getDocument(url);
            if(doc != null) {
                final Elements table = doc.select("table.item_table tbody tr td");
                table.removeIf(element -> element.text().startsWith("PL"));
                boolean isFirst = true;
                String previousTitle = null;
                int index = 1;
                for(Element element : table) {
                    final String text = element.text();
                    if(index % 2 == 0) {
                        final String[] dateValues = text.split("/");
                        final int year = Integer.parseInt(dateValues[2]), month = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[1]);
                        final LocalDate date = LocalDate.of(year, month, day);

                        final String[] values = previousTitle.split(" - ");
                        final String prefix = values[0], title = previousTitle.split(prefix + " - ")[1];
                        final boolean isHouse = prefix.startsWith("H.R.");
                        final Chamber chamber = isHouse ? Chamber.HOUSE : Chamber.SENATE;
                        final String id = prefix.split("\\.")[isHouse ? 2 : 1];
                        final PreEnactedBill bill = new PreEnactedBill(chamber, id, title, date);
                        enactedBills.append(isFirst ? "" : ",").append(bill.toString());
                        isFirst = false;
                    } else {
                        previousTitle = text;
                    }
                    index++;
                }
                enactedBills.append("]");
            }
        }
        return enactedBills.toString();
    }
    public String getEnactedBill(Chamber chamber, String id) {
        if(enactedBill.containsKey(id)) {
            return enactedBill.get(id);
        } else {
            final String version = getVersioned();
            final String targetURL = "https://www.congress.gov/bill/" + version + "-congress/" + chamber.name().toLowerCase() + "-bill/" + id + "/all-info";
            final Document billDoc = getDocument(targetURL);
            if(billDoc != null) {
                final String title = LocalServer.fixEscapeValues(billDoc.select("h1.legDetail").get(0).text().split(id + " - ")[1].split(version)[0]);

                final Element element = billDoc.select("div.overview_wrapper div.overview table.standard01 tbody tr td a[href]").get(0);
                final String profileSlug = element.attr("href");
                final Politician sponsor = getPolitician(element, profileSlug, false);
                final String summary = getBillSummary(billDoc);
                final PolicyArea policyArea = getBillPolicyArea(billDoc);
                final HashSet<String> subjects = getBillSubjects(billDoc);
                final List<Politician> cosponsors = getBillCosponsors(billDoc);
                final List<BillAction> actions = getBillActions(billDoc);
                final EnactedBill bill = new EnactedBill(title, sponsor, summary, policyArea, subjects, cosponsors, actions, pdfURL, null);
                final String string = bill.toString();
                enactedBill.put(id, string);
                return string;
            } else {
                return null;
            }
        }
    }
    private String getBillSummary(Document doc) {
        final Element table = doc.select("div.all-info-content div.main-wrapper").last();
        final Element first = table.select("h3.currentVersion + div").first();
        if(first == null) {
            return LocalServer.fixEscapeValues(table.text());
        }
        final Elements elements = first.select("*");
        if(elements.size() == 2) {
            return LocalServer.fixEscapeValues(elements.get(0).text());
        } else {
            for(int i = 1; i <= 3; i++) {
                elements.remove(0);
            }
            final StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for(Element element : elements) {
                final String text = LocalServer.fixEscapeValues(element.text());
                builder.append(isFirst ? "" : "\\n").append(text);
                isFirst = false;
            }
            return builder.toString();
        }
    }
    private PolicyArea getBillPolicyArea(Document doc) {
        final Elements table = doc.select("main.content div.main-wrapper div.all-info-content").get(6).select("div.search-column-nav ul.plain li a[href]");
        final Element target = table.get(0);
        final String text = target.text();
        return PolicyArea.fromTag(text);
    }
    private HashSet<String> getBillSubjects(Document doc) {
        final HashSet<String> subjects = new HashSet<>();
        final Elements table = doc.select("main.content div.main-wrapper div.all-info-content").get(6).select("div.search-column-main div ul.plain li a[href]");
        for(Element element : table) {
            final String text = element.text();
            subjects.add("\"" + text + "\"");
        }
        return subjects;
    }
    private List<Politician> getBillCosponsors(Document doc) {
        final List<Politician> cosponsors = new ArrayList<>();
        final Elements table = doc.select("main.content div.main-wrapper div.all-info-content").get(3).select("div.main-wrapper table.item_table tbody tr td.actions a[href]");
        table.remove(0);
        if(!table.isEmpty()) {
            for(Element element : table) {
                final String profileSlug = element.attr("href").split("https://www\\.congress\\.gov")[1];
                final Politician politician = getPoliticianFromBill(profileSlug, element);
                cosponsors.add(politician);
            }
        }
        return cosponsors;
    }
    private List<BillAction> getBillActions(Document doc) {
        final List<BillAction> actions = new ArrayList<>();
        final Elements table = doc.select("div.all-info-content").get(2).select("table.expanded-actions tbody tr");
        pdfURL = "https://www.congress.gov" + table.select("a[href]").get(1).attr("href");
        for(Element element : table) {
            final String text = element.text();
            final String[] values = text.split(" ");
            final String value0 = values[0], dateString = value0.split("-")[0], dateTime = value0.contains("-") ? value0.split("-")[1] : null, chamberString = values[1];
            final Chamber chamber = getChamber(chamberString);
            final String title = text.split(value0 + " " + (chamber != null ? chamberString + " " : ""))[1];
            final String[] dateValues = dateString.split("/");
            final int dateHour = dateTime != null ? Integer.parseInt(dateTime.split(":")[0]) : 0, dateMinute = dateTime != null ? Integer.parseInt(dateTime.split(":")[1].substring(0, 2)) : 0;
            final LocalDateTime date = LocalDateTime.of(Integer.parseInt(dateValues[2]), Integer.parseInt(dateValues[0]), Integer.parseInt(dateValues[1]), dateHour, dateMinute);
            final BillAction action = new BillAction(chamber, title, date);
            actions.add(action);
        }
        return actions;
    }
    private Chamber getChamber(String input) {
        try {
            return Chamber.valueOf(input.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
}
