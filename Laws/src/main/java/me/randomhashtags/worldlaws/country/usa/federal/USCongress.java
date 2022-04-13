package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.PreEnactedBill;
import me.randomhashtags.worldlaws.country.usa.USBillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.country.usa.USPoliticians;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum USCongress implements Jsoupable, Jsonable {
    INSTANCE;

    private ConcurrentHashMap<Integer, HashMap<USBillStatus, JSONObjectTranslatable>> statuses;
    private HashMap<String, JSONObjectTranslatable> bills;
    private HashMap<Integer, JSONObjectTranslatable> enactedBills;
    private int version;

    public static USCongress getCongress(int version) {
        final USCongress congress = USCongress.INSTANCE;
        congress.version = Math.max(version, 93);
        return congress;
    }

    private boolean isCurrentAdministration() {
        return version == USLaws.INSTANCE.getCurrentAdministrationVersion();
    }

    private String getVersion() {
        return Integer.toString(version);
    }
    private String getVersioned() {
        final String version = getVersion();
        return version.endsWith("3") && !version.endsWith("13") ? version + "rd" : version + "th";
    }

    public JSONObjectTranslatable getBillsByStatus(USBillStatus status) {
        if(statuses == null) {
            statuses = new ConcurrentHashMap<>();
        }
        final int version = this.version;
        if(statuses.containsKey(version) && statuses.get(version).containsKey(status)) {
            return statuses.get(version).get(status);
        } else if(version == USLaws.INSTANCE.getCurrentAdministrationVersion()) {
            final JSONObjectTranslatable string = getBillsBySearch(status);
            statuses.putIfAbsent(version, new HashMap<>());
            statuses.get(version).put(status, string);
            return string;
        } else {
            final long started = System.currentTimeMillis();
            final String statusName = status.name(), suffix = " bills with status " + statusName + " for congress " + version + " (took %time%ms)";
            final Folder folder = Folder.LAWS_USA_CONGRESS;
            final String fileName = "bill status: " + statusName.toLowerCase();
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%version%", "" + version));
            final JSONObject localJSON = getJSONObject(folder, fileName, null);
            final JSONObjectTranslatable json;
            if(localJSON == null) {
                json = getBillsBySearch(status);
                setFileJSON(folder, fileName, json.toString());
            } else {
                json = new JSONObjectTranslatable();
                json.setFolder(folder);
                json.setFileName(fileName);
                for(String key : localJSON.keySet()) {
                    json.put(key, localJSON.get(key));
                    json.addTranslatedKey(key);
                }
            }

            statuses.putIfAbsent(version, new HashMap<>());
            statuses.get(version).put(status, json);
            WLLogger.logInfo("USCongress - loaded" + suffix.replace("%time%", WLUtilities.getElapsedTime(started)));
            return json;
        }
    }
    private JSONObjectTranslatable getBillsBySearch(USBillStatus status) {
        final HashSet<PreCongressBill> bills = getPreCongressBillsBySearch(status);
        JSONObjectTranslatable json = null;
        if(bills != null) {
            json = getPreCongressBillsJSON(bills);
        }
        return json;
    }
    public static JSONObjectTranslatable getPreCongressBillsJSON(HashSet<PreCongressBill> bills) {
        final HashMap<String, HashMap<String, HashSet<PreCongressBill>>> map = new HashMap<>();
        for(PreCongressBill bill : bills) {
            final String chamber = bill.getChamber().getName();
            map.putIfAbsent(chamber, new HashMap<>());

            final String dateString = bill.getDate().getDateString();
            map.get(chamber).putIfAbsent(dateString, new HashSet<>());
            map.get(chamber).get(dateString).add(bill);
        }

        final JSONObjectTranslatable json = new JSONObjectTranslatable();
        for(Map.Entry<String, HashMap<String, HashSet<PreCongressBill>>> hashmap : map.entrySet()) {
            final JSONObjectTranslatable dateStringJSON = new JSONObjectTranslatable();
            final String chamber = hashmap.getKey();
            for(Map.Entry<String, HashSet<PreCongressBill>> entry : hashmap.getValue().entrySet()) {
                final String dateString = entry.getKey();
                final HashSet<PreCongressBill> values = entry.getValue();
                final JSONObjectTranslatable billsJSON = new JSONObjectTranslatable();
                for(PreCongressBill bill : values) {
                    final String id = bill.getID();
                    billsJSON.put(id, bill.toJSONObject());
                    billsJSON.addTranslatedKey(id);
                }
                dateStringJSON.put(dateString, billsJSON);
                dateStringJSON.addTranslatedKey(dateString);
            }
            json.put(chamber, dateStringJSON);
            json.addTranslatedKey(chamber);
        }
        return json;
    }
    public HashSet<PreCongressBill> getPreCongressBillsBySearch(USBillStatus status) {
        final String version = getVersion();
        final String url = "https://www.congress.gov/search?searchResultViewType=expanded&pageSize=250&q=%7B%22source%22%3A%22legislation%22%2C%22congress%22%3A%22" + version + "%22%2C%22bill-status%22%3A%22" + status.getSearchID() + "%22%7D";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements items = doc.select("main.content div.basic-search-results-wrapper div.main-wrapper div.search-row div.search-column-main ol.basic-search-results-lists li.expanded");
            final int max = items.size();
            if(max > 0) {
                final HashSet<PreCongressBill> bills = new HashSet<>();
                new CompletableFutures<Element>().stream(items, element -> {
                    final PreCongressBill bill = getPreCongressBillFrom(element);
                    bills.add(bill);
                });
                return bills;
            }
        }
        return null;
    }
    public PreCongressBill getPreCongressBillFrom(Element element) {
        final String heading = element.select("span.result-heading").get(0).select("a[href]").text();
        final boolean isHouse = heading.startsWith("H.R.");
        final boolean isHouseJoint = heading.startsWith("H.J.Res."), isSenateJoint = heading.startsWith("S.J.Res.");
        final USChamber chamber = isHouse ? USChamber.HOUSE : USChamber.SENATE;
        final String id = heading.split("\\.")[isHouseJoint || isSenateJoint ? 3 : isHouse ? 2 : 1];

        final String title = element.select("span.result-title").get(0).text();
        final Elements resultItems = element.select("span.result-item");
        final int results = resultItems.size();

        final String committees = "";//resultItems.get(results-3).text().split(" - ")[1];

        String notes = null;
        Element latestActionElement = resultItems.get(results-2);
        String targetActionText = latestActionElement.text();
        if(targetActionText.contains("Notes:")) {
            notes = targetActionText.substring("Notes: ".length());
            latestActionElement = resultItems.get(results-3);
        }
        targetActionText = latestActionElement.text();
        final int substringCount = latestActionElement.select("strong").get(0).text().length()+1;
        final String latestActionString = targetActionText.substring(substringCount);
        final String[] latestActionSpaces = latestActionString.split(" ");
        final String target = latestActionSpaces[0];
        final boolean isHouseOrSenate = target.equals("House") || target.equals("Senate");
        final String dateString = latestActionSpaces[isHouseOrSenate ? 2 : 0];
        final String[] dateValues = dateString.split("/");
        final int year = Integer.parseInt(dateValues[2]), month = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[1]);
        final EventDate date = new EventDate(Month.of(month), day, year);
        return new PreCongressBill(chamber, id, title, committees, notes, date);
    }

    public JSONObjectTranslatable getEnactedBills() {
        if(enactedBills == null) {
            enactedBills = new HashMap<>();
        }
        if(enactedBills.containsKey(version)) {
            return enactedBills.get(version);
        } else if(isCurrentAdministration()) {
            WLLogger.logError(this, "getEnactedBills - tried to get enacted bills for current administration, which is illegal!");
            return null;
        } else {
            final long started = System.currentTimeMillis();
            final int versionInt = version;
            final String version = Integer.toString(versionInt);
            final Folder folder = Folder.LAWS_USA_CONGRESS;
            final String fileName = "enacted bills";
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%version%", version));
            final JSONObject localJSON = getJSONObject(folder, fileName, null);
            final JSONObjectTranslatable json;
            if(localJSON == null) {
                json = loadEnactedBills();
            } else {
                json = new JSONObjectTranslatable();
                json.setFolder(folder);
                json.setFileName(fileName);
                for(String key : localJSON.keySet()) {
                    json.put(key, localJSON.get(key));
                    json.addTranslatedKey(key);
                }
            }
            enactedBills.put(versionInt, json);
            WLLogger.logInfo("USCongress - loaded enacted bills for congress " + version + " (took " + WLUtilities.getElapsedTime(started) + ")");
            return json;
        }
    }
    private JSONObjectTranslatable loadEnactedBills() {
        final String version = getVersioned();
        final String url = "https://www.congress.gov/public-laws/" + version + "-congress";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements table = doc.select("table.item_table tbody tr td");
            table.removeIf(element -> element.text().startsWith("PL"));
            String previousTitle = null;
            int index = 1;
            final HashMap<String, HashMap<USChamber, HashSet<PreEnactedBill>>> preEnactedBills = new HashMap<>();
            for(Element element : table) {
                final String text = element.text();
                if(index % 2 == 0) {
                    final String[] dateValues = text.split("/");
                    final int year = Integer.parseInt(dateValues[2]), month = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[1]);
                    final EventDate date = new EventDate(Month.of(month), day, year);
                    final String dateString = date.getDateString();
                    preEnactedBills.putIfAbsent(dateString, new HashMap<>());

                    final String[] values = previousTitle.split(" - ");
                    final String prefix = values[0], title = previousTitle.split(prefix + " - ")[1];
                    final boolean isHouse = prefix.startsWith("H.R."), isJoint = prefix.contains(".J.Res.");
                    final USChamber chamber = isHouse ? USChamber.HOUSE : USChamber.SENATE;
                    final String id = prefix.split("\\.")[isJoint ? 3 : isHouse ? 2 : 1];
                    final PreEnactedBill bill = new PreEnactedBill(id, title);
                    preEnactedBills.get(dateString).putIfAbsent(chamber, new HashSet<>());
                    preEnactedBills.get(dateString).get(chamber).add(bill);
                } else {
                    previousTitle = text;
                }
                index++;
            }

            final JSONObjectTranslatable json = new JSONObjectTranslatable();
            for(Map.Entry<String, HashMap<USChamber, HashSet<PreEnactedBill>>> datesMap : preEnactedBills.entrySet()) {
                final String dateString = datesMap.getKey();
                final JSONObjectTranslatable chamberJSON = new JSONObjectTranslatable();
                for(Map.Entry<USChamber, HashSet<PreEnactedBill>> map : datesMap.getValue().entrySet()) {
                    final String chamber = map.getKey().name();
                    final JSONObjectTranslatable enactedBillsJSON = new JSONObjectTranslatable();
                    final HashSet<PreEnactedBill> values = map.getValue();
                    for(PreEnactedBill value : values) {
                        final String id = value.getID();
                        enactedBillsJSON.put(id, value.toJSONObject());
                        enactedBillsJSON.addTranslatedKey(id);
                    }
                    chamberJSON.put(chamber, enactedBillsJSON);
                    chamberJSON.addTranslatedKey(chamber);
                }
                json.put(dateString, chamberJSON);
                json.addTranslatedKey(dateString);
            }
            return json;
        } else {
            WLLogger.logError(this, "getEnactedBills - failed to get enacted bills document for congress " + version + "!");
            return null;
        }
    }

    public JSONObjectTranslatable getBill(USChamber chamber, String id) {
        if(bills == null) {
            bills = new HashMap<>();
        }
        if(!bills.containsKey(id)) {
            final long started = System.currentTimeMillis();
            final Folder folder = Folder.LAWS_USA_CONGRESS;
            final String[] title = new String[1];
            final String chamberName = chamber.name(), chamberNameLowercase = chamberName.toLowerCase();
            folder.setCustomFolderName(id, folder.getFolderName().replace("%version%", "" + version) + File.separator + chamberNameLowercase);
            final JSONObject localJSON = getLocalFileJSONObject(folder, id);
            JSONObjectTranslatable json = null;
            if(localJSON != null) {
                json = new JSONObjectTranslatable();
                json.setFolder(folder);
                json.setFileName(id);
                for(String key : localJSON.keySet()) {
                    json.put(key, localJSON.get(key));
                    json.addTranslatedKey(key);
                }
            } else {
                final String targetURL = "https://www.congress.gov/bill/" + getVersioned() + "-congress/" + chamberNameLowercase + "-bill/" + id + "/all-info";
                final Document doc = getDocument(targetURL);
                if(doc != null) {
                    final String[] splitValues = doc.select("h1.legDetail").get(0).textNodes().get(0).text().split(id + " - ");
                    title[0] = splitValues[1];
                    final String pdfURL, billTypeText = splitValues[0].substring("All Information (Except Text) for ".length()).toUpperCase();
                    final String billType = billTypeText.startsWith("S.J.RES.") ? "sjres" : billTypeText.startsWith("S.") ? "s" : billTypeText.startsWith("H.R.") ? "hr" : billTypeText.startsWith("H.J.RES.") ? "hjres" : null;
                    if(billType == null) {
                        pdfURL = null;
                        WLLogger.logError(this, "getBill - failed to get billType for bill \"" + id + "\" (" + title[0] + "), billTypeText=\"" + billTypeText + "\"!");
                    } else {
                        pdfURL = "https://www.congress.gov/" + version + "/bills/" + billType + id + "/BILLS-" + version + billType + id + "enr.pdf";
                    }
                    final Element element = doc.select("div.overview_wrapper div.overview table.standard01 tbody tr td a[href]").get(0);
                    final String profileSlug = element.attr("href");
                    final String sponsor = USPoliticians.INSTANCE.get(element, profileSlug);
                    final String summary = getBillSummary(doc);
                    final Elements allInfoContent = doc.select("main.content div.main-wrapper div.all-info-content");
                    final PolicyArea policyArea = getBillPolicyArea(allInfoContent);
                    final String subjects = getBillSubjects(allInfoContent);
                    final EventSources sources = new EventSources();
                    sources.add(new EventSource("US Congress: Bill URL", targetURL));
                    sources.add(new EventSource("US Congress: Bill PDF", pdfURL));
                    final String cosponsors = getBillCosponsors(allInfoContent);
                    final String actions = getBillActions(allInfoContent);
                    final CongressBill bill = new CongressBill(sponsor, summary, policyArea, subjects, cosponsors, actions, null, sources);
                    json = bill.toJSONObject();
                    if(!summary.toLowerCase().contains("a summary is in progress")) {
                        final String billString = bill.toString();
                        setFileJSON(folder, id, billString);
                    }
                }
            }
            bills.put(id, json);
            WLLogger.logInfo("USCongress - loaded bill from chamber \"" + chamberName + "\" with title \"" + title[0] + "\" for congress " + version + " (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return bills.get(id);
    }
    private String getBillSummary(Document doc) {
        final Element table = doc.select("div.all-info-content div.main-wrapper").last();
        final Element first = table.select("h3.currentVersion + div").first();
        if(first == null) {
            return table.text();
        }
        final Elements elements = first.select("*");
        if(elements.size() == 2) {
            return elements.get(0).text();
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
    private PolicyArea getBillPolicyArea(Elements allInfoContent) {
        final Elements table = allInfoContent.get(6).select("div.search-column-nav ul.plain li a[href]");
        final Element target = table.size() > 0 ? table.get(0) : null;
        final String text = target != null ? target.text() : "";
        return PolicyArea.fromTag(text);
    }
    private String getBillSubjects(Elements allInfoContent) {
        final Elements table = allInfoContent.get(6).select("div.search-column-main div ul.plain li a[href]");
        final HashSet<String> subjects = new HashSet<>();
        table.forEach(element -> subjects.add(element.text()));
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String subject : subjects) {
            builder.append(isFirst ? "" : ",").append("\"").append(subject).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private String getBillCosponsors(Elements allInfoContent) {
        final Elements table = allInfoContent.get(3).select("div.main-wrapper table.item_table tbody tr td.actions a[href]");
        String value = null;
        if(table.size() > 0) {
            table.remove(0);
            if(!table.isEmpty()) {
                final USPoliticians politicians = USPoliticians.INSTANCE;
                final HashSet<String> cosponsors = new HashSet<>();
                new CompletableFutures<Element>().stream(table, elements -> {
                    final String profileSlug = elements.attr("href").split("https://www\\.congress\\.gov")[1];
                    final String string = politicians.get(elements, profileSlug);
                    cosponsors.add(string);
                });

                if(!cosponsors.isEmpty()) {
                    final StringBuilder builder = new StringBuilder("[");
                    boolean isFirst = true;
                    for(String cosponsor : cosponsors) {
                        builder.append(isFirst ? "" : ",").append(cosponsor);
                        isFirst = false;
                    }
                    builder.append("]");
                    value = builder.toString();
                }
            }
        }
        return value;
    }
    private String getBillActions(Elements allInfoContent) {
        final Elements table = allInfoContent.get(2).select("table.expanded-actions tbody tr");
        final HashSet<JSONObjectTranslatable> actions = new HashSet<>();
        table.parallelStream().forEach(element -> {
            final String text = element.text();
            final String[] values = text.split(" ");
            final String value0 = values[0], dateString = value0.split("-")[0], dateTime = value0.contains("-") ? value0.split("-")[1] : null, chamberString = values[1];
            final USChamber chamber = getChamber(chamberString);
            String title = text.split(value0 + " " + (chamber != null ? chamberString + " " : ""))[1];
            if(title.contains("Became Public Law No:")) {
                title = "Became Public Law.";
            }

            final String[] dateValues = dateString.split("/");
            final int dateHour, dateMinute;
            if(dateTime != null) {
                final String[] dateTimeValues = dateTime.split(":");
                dateHour = Integer.parseInt(dateTimeValues[0]);
                dateMinute = Integer.parseInt(dateTimeValues[1].substring(0, 2));
            } else {
                dateHour = 0;
                dateMinute = 0;
            }
            final LocalDateTime date = LocalDateTime.of(Integer.parseInt(dateValues[2]), Integer.parseInt(dateValues[0]), Integer.parseInt(dateValues[1]), dateHour, dateMinute);
            final BillAction action = new BillAction(chamber, title, date);
            actions.add(action.toJSONObject());
        });
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(JSONObjectTranslatable action : actions) {
            builder.append(isFirst ? "" : ",").append(action.toString());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private USChamber getChamber(String input) {
        input = input.toUpperCase();
        for(USChamber chamber : USChamber.values()) {
            if(input.equals(chamber.getName())) {
                return chamber;
            }
        }
        return null;
    }
}
