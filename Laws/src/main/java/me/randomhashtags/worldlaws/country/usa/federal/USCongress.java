package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.PreEnactedBill;
import me.randomhashtags.worldlaws.country.WLCountry;
import me.randomhashtags.worldlaws.country.usa.USBillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum USCongress implements Jsoupable, Jsonable {
    INSTANCE;

    private ConcurrentHashMap<Integer, HashMap<USBillStatus, JSONObjectTranslatable>> statuses;
    private ConcurrentHashMap<String, JSONObjectTranslatable> bills;
    private ConcurrentHashMap<Integer, JSONObjectTranslatable> enactedBills;
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
            final WLCountry country = WLCountry.UNITED_STATES;
            final String statusName = status.name(), suffix = " bills with status " + statusName + " for congress " + version + " (took %time%ms)";
            final Folder folder = Folder.LAWS_COUNTRY_CONGRESS;
            final String fileName = "bill status: " + statusName.toLowerCase();
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", country.getBackendID()).replace("%version%", "" + version));
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
                    json.put(key, localJSON.get(key), true);
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
                    billsJSON.put(id, bill.toJSONObject(), true);
                }
                dateStringJSON.put(dateString, billsJSON, true);
            }
            json.put(chamber, dateStringJSON, true);
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
            enactedBills = new ConcurrentHashMap<>();
        }
        if(enactedBills.containsKey(version)) {
            return enactedBills.get(version);
        } else if(isCurrentAdministration()) {
            WLLogger.logError(this, "getEnactedBills - tried to get enacted bills for current administration, which is illegal!");
            return null;
        } else {
            final long started = System.currentTimeMillis();
            final WLCountry country = WLCountry.UNITED_STATES;
            final int versionInt = version;
            final String version = Integer.toString(versionInt);
            final Folder folder = Folder.LAWS_COUNTRY_CONGRESS;
            final String fileName = "enacted bills";
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%country%", country.getBackendID()).replace("%version%", version));
            final JSONObject localJSON = getJSONObject(folder, fileName, null);
            final JSONObjectTranslatable json;
            if(localJSON == null) {
                json = loadEnactedBills();
            } else {
                json = JSONObjectTranslatable.copy(localJSON, true);
                json.setFolder(folder);
                json.setFileName(fileName);
            }
            if(json != null) {
                enactedBills.put(versionInt, json);
            }
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
                        enactedBillsJSON.put(id, value.toJSONObject(), true);
                    }
                    chamberJSON.put(chamber, enactedBillsJSON, true);
                }
                json.put(dateString, chamberJSON, true);
            }
            return json;
        } else {
            WLLogger.logError(this, "getEnactedBills - failed to get enacted bills document for congress " + version + "!");
            return null;
        }
    }

    public JSONObjectTranslatable getBill(USChamber chamber, String id) {
        if(bills == null) {
            bills = new ConcurrentHashMap<>();
        }
        if(!bills.containsKey(id)) {
            final long started = System.currentTimeMillis();
            final WLCountry country = WLCountry.UNITED_STATES;
            final Folder folder = Folder.LAWS_COUNTRY_CONGRESS;
            final String chamberName = chamber.name(), chamberNameLowercase = chamberName.toLowerCase();
            folder.setCustomFolderName(id, folder.getFolderName().replace("%country%", country.getBackendID()).replace("%version%", "" + version) + File.separator + chamberNameLowercase);
            final JSONObject localJSON = getLocalFileJSONObject(folder, id);
            final CongressBill bill;
            if(localJSON != null) {
                final JSONObjectTranslatable json = JSONObjectTranslatable.copy(localJSON, true);
                bill = new CongressBill(json);
                bill.setFolder(folder);
                bill.setFileName(id);
            } else {
                final String targetURL = "https://www.congress.gov/bill/" + getVersioned() + "-congress/" + chamberNameLowercase + "-bill/" + id + "/all-info";
                bill = CongressBill.loadFromURL(targetURL, folder, version, id);
            }
            final boolean success = bill != null;
            if(success) {
                bills.put(id, bill);
            }
            WLLogger.logInfo("USCongress - " + (success ? "loaded" : "failed to load") + " bill from chamber \"" + chamberName + "\" with id \"" + id + "\" for congress " + version + " (took " + WLUtilities.getElapsedTime(started) + ")");
        }
        return bills.get(id);
    }
}
