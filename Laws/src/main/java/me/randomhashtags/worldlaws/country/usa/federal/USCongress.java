package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.PreEnactedBill;
import me.randomhashtags.worldlaws.country.usa.USBillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.country.usa.USLaws;
import me.randomhashtags.worldlaws.country.usa.USPoliticians;
import org.apache.logging.log4j.Level;
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
import java.util.concurrent.atomic.AtomicInteger;

public enum USCongress implements Jsoupable, Jsonable {
    INSTANCE;

    private HashMap<USBillStatus, String> statuses;
    private HashMap<String, String> bills;
    private HashMap<Integer, String> enactedBills;
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

    public void getBillsByStatus(USBillStatus status, CompletionHandler handler) {
        if(statuses == null) {
            statuses = new HashMap<>();
        }
        if(statuses.containsKey(status)) {
            handler.handleString(statuses.get(status));
        } else if(version == USLaws.INSTANCE.getCurrentAdministrationVersion()) {
            getBillsBySearch(status, handler);
        } else {
            final long started = System.currentTimeMillis();
            final String statusName = status.name(), suffix = " bills with status " + statusName + " for congress " + version + " (took %time%ms)";
            final Folder folder = Folder.LAWS_USA_CONGRESS;
            final String fileName = "bill status: " + statusName.toLowerCase();
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%version%", "" + version));
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    getBillsBySearch(status, handler);
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final String string = json.toString();
                    statuses.put(status, string);
                    WLLogger.log(Level.INFO, "USCongress - loaded" + suffix.replace("%time%", Long.toString(System.currentTimeMillis()-started)));
                    handler.handleString(string);
                }
            });
        }
    }
    private void getBillsBySearch(USBillStatus status, CompletionHandler handler) {
        getPreCongressBillsBySearch(status, new CompletionHandler() {
            @Override
            public void handleObject(Object object) {
                if(object != null) {
                    @SuppressWarnings({ "unchecked" })
                    final HashSet<PreCongressBill> bills = (HashSet<PreCongressBill>) object;

                    final String string = getPreCongressBillsJSON(bills);
                    handler.handleString(string);
                } else {
                    handler.handleString(null);
                }
            }
        });
    }
    public static String getPreCongressBillsJSON(HashSet<PreCongressBill> bills) {
        final HashMap<String, HashMap<String, StringBuilder>> map = new HashMap<>();
        for(PreCongressBill bill : bills) {
            final String chamber = bill.getChamber().getName();
            map.putIfAbsent(chamber, new HashMap<>());

            final String dateString = bill.getDate().getDateString();
            final boolean isFirst = !map.get(chamber).containsKey(dateString);
            map.get(chamber).putIfAbsent(dateString, new StringBuilder());
            map.get(chamber).get(dateString).append(isFirst ? "" : ",").append(bill.toString());
        }
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirstDateString = true;
        for(Map.Entry<String, HashMap<String, StringBuilder>> hashmap : map.entrySet()) {
            final String dateString = hashmap.getKey();
            builder.append(isFirstDateString ? "" : ",").append("\"").append(dateString).append("\":{");
            boolean isFirstChamber = true;
            for(Map.Entry<String, StringBuilder> builderMap : hashmap.getValue().entrySet()) {
                final String chamber = builderMap.getKey();
                builder.append(isFirstChamber ? "" : ",").append("\"").append(chamber).append("\":{").append(builderMap.getValue()).append("}");
                isFirstChamber = false;
            }
            builder.append("}");
            isFirstDateString = false;
        }
        builder.append("}");
        return builder.toString();
    }
    public void getPreCongressBillsBySearch(USBillStatus status, CompletionHandler handler) {
        final String version = getVersion();
        final String url = "https://www.congress.gov/search?searchResultViewType=expanded&pageSize=250&q=%7B%22source%22%3A%22legislation%22%2C%22congress%22%3A%22" + version + "%22%2C%22bill-status%22%3A%22" + status.getSearchID() + "%22%7D";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements items = doc.select("main.content div.basic-search-results-wrapper div.main-wrapper div.search-row div.search-column-main ol.basic-search-results-lists li.expanded");
            final int max = items.size();
            if(max == 0) {
                handler.handleObject(null);
            } else {
                final HashSet<PreCongressBill> bills = new HashSet<>();
                final AtomicInteger completed = new AtomicInteger(0);
                items.parallelStream().forEach(element -> {
                    final PreCongressBill bill = getPreCongressBillFrom(element);
                    bills.add(bill);
                    if(completed.addAndGet(1) == max) {
                        handler.handleObject(bills);
                    }
                });
            }
        } else {
            handler.handleObject(null);
        }
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

    public void getEnactedBills(CompletionHandler handler) {
        if(enactedBills == null) {
            enactedBills = new HashMap<>();
        }
        if(enactedBills.containsKey(version)) {
            handler.handleString(enactedBills.get(version));
        } else if(isCurrentAdministration()) {
            WLLogger.log(Level.ERROR, "USCongress - tried to get enacted bills for current administration, which is illegal!");
            handler.handleString(null);
        } else {
            final long started = System.currentTimeMillis();
            final int versionInt = version;
            final String version = Integer.toString(versionInt);
            final Folder folder = Folder.LAWS_USA_CONGRESS;
            final String fileName = "enacted bills";
            folder.setCustomFolderName(fileName, folder.getFolderName().replace("%version%", version));
            getJSONObject(folder, fileName, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final String version = getVersioned();
                    final String url = "https://www.congress.gov/public-laws/" + version + "-congress";
                    final Document doc = getDocument(url);
                    if(doc != null) {
                        final Elements table = doc.select("table.item_table tbody tr td");
                        table.removeIf(element -> element.text().startsWith("PL"));
                        String previousTitle = null;
                        int index = 1;
                        final HashMap<String, HashMap<USChamber, HashSet<String>>> preEnactedBills = new HashMap<>();
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
                                preEnactedBills.get(dateString).get(chamber).add(bill.toString());
                            } else {
                                previousTitle = text;
                            }
                            index++;
                        }

                        final StringBuilder builder = new StringBuilder("{");
                        boolean isFirstDate = true;
                        for(Map.Entry<String, HashMap<USChamber, HashSet<String>>> datesMap : preEnactedBills.entrySet()) {
                            final String dateString = datesMap.getKey();
                            builder.append(isFirstDate ? "" : ",").append("\"").append(dateString).append("\":{");
                            isFirstDate = false;

                            boolean isFirstChamber = true;
                            for(Map.Entry<USChamber, HashSet<String>> map : datesMap.getValue().entrySet()) {
                                final String chamber = map.getKey().name();
                                builder.append(isFirstChamber ? "" : ",").append("\"").append(chamber).append("\":{");
                                final HashSet<String> values = map.getValue();
                                boolean isFirst = true;
                                for(String value : values) {
                                    builder.append(isFirst ? "" : ",").append(value);
                                    isFirst = false;
                                }
                                builder.append("}");
                                isFirstChamber = false;
                            }
                            builder.append("}");
                        }
                        builder.append("}");
                        handler.handleString(builder.toString());
                    } else {
                        WLLogger.log(Level.ERROR, "USCongress - failed to get enacted bills document for congress " + version + "!");
                        handler.handleString("{}");
                    }
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final String string = json.toString();
                    enactedBills.put(versionInt, string);
                    WLLogger.log(Level.INFO, "USCongress - loaded enacted bills for congress " + version + " (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(string);
                }
            });
        }
    }
    public void getBill(USChamber chamber, String id, CompletionHandler handler) {
        if(bills == null) {
            bills = new HashMap<>();
        }
        if(bills.containsKey(id)) {
            handler.handleString(bills.get(id));
        } else {
            final long started = System.currentTimeMillis();
            final int versionInt = this.version;
            final String version = getVersioned();
            final Folder folder = Folder.LAWS_USA_CONGRESS;
            final String[] title = new String[1];
            final String chamberName = chamber.name(), chamberNameLowercase = chamberName.toLowerCase();
            folder.setCustomFolderName(id, folder.getFolderName().replace("%version%", "" + versionInt) + File.separator + chamberNameLowercase);
            getJSONObject(folder, id, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final String targetURL = "https://www.congress.gov/bill/" + version + "-congress/" + chamberNameLowercase + "-bill/" + id + "/all-info";
                    final Document doc = getDocument(targetURL);
                    if(doc != null) {
                        final String[] splitValues = doc.select("h1.legDetail").get(0).textNodes().get(0).text().split(id + " - ");
                        title[0] = splitValues[1];
                        final String pdfURL, billTypeText = splitValues[0].substring("All Information (Except Text) for ".length()).toUpperCase();
                        final String billType = billTypeText.startsWith("S.J.RES.") ? "sjres" : billTypeText.startsWith("S.") ? "s" : billTypeText.startsWith("H.R.") ? "hr" : billTypeText.startsWith("H.J.RES.") ? "hjres" : null;
                        if(billType == null) {
                            pdfURL = null;
                            WLLogger.log(Level.ERROR, "USCongress - failed to get billType for bill \"" + id + "\" (" + title[0] + "), billTypeText=\"" + billTypeText + "\"!");
                        } else {
                            pdfURL = "https://www.congress.gov/" + versionInt + "/bills/" + billType + id + "/BILLS-" + versionInt + billType + id + "enr.pdf";
                        }
                        final Element element = doc.select("div.overview_wrapper div.overview table.standard01 tbody tr td a[href]").get(0);
                        final String profileSlug = element.attr("href");
                        USPoliticians.INSTANCE.get(element, profileSlug, new CompletionHandler() {
                            @Override
                            public void handleString(String sponsor) {
                                final String summary = getBillSummary(doc);
                                final Elements allInfoContent = doc.select("main.content div.main-wrapper div.all-info-content");
                                final PolicyArea policyArea = getBillPolicyArea(allInfoContent);
                                final String subjects = getBillSubjects(allInfoContent);
                                getBillCosponsors(allInfoContent, new CompletionHandler() {
                                    @Override
                                    public void handleString(String cosponsors) {
                                        final String actions = getBillActions(allInfoContent);
                                        final CongressBill bill = new CongressBill(sponsor, summary, policyArea, subjects, cosponsors, actions, targetURL, pdfURL, null);
                                        handler.handleString(bill.toString());
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    final String string = json.toString();
                    bills.put(id, string);
                    WLLogger.log(Level.INFO, "USCongress - loaded bill from chamber \"" + chamberName + "\" with title \"" + title[0] + "\" for congress " + versionInt + " (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handleString(string);
                }
            });
        }
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
        table.parallelStream().forEach(element -> {
            subjects.add(element.text());
        });
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String subject : subjects) {
            builder.append(isFirst ? "" : ",").append("\"").append(subject).append("\"");
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private void getBillCosponsors(Elements allInfoContent, CompletionHandler handler) {
        final Elements table = allInfoContent.get(3).select("div.main-wrapper table.item_table tbody tr td.actions a[href]");
        if(table.size() > 0) {
            table.remove(0);
            if(!table.isEmpty()) {
                final int max = table.size();
                final USPoliticians politicians = USPoliticians.INSTANCE;
                final HashSet<String> cosponsors = new HashSet<>();
                final AtomicInteger completed = new AtomicInteger(0);
                table.parallelStream().forEach(elements -> {
                    final String profileSlug = elements.attr("href").split("https://www\\.congress\\.gov")[1];
                    politicians.get(elements, profileSlug, new CompletionHandler() {
                        @Override
                        public void handleString(String string) {
                            cosponsors.add(string);
                            if(completed.addAndGet(1) == max) {
                                String value = null;
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
                                handler.handleString(value);
                            }
                        }
                    });
                });
            } else {
                handler.handleString(null);
            }
        } else {
            handler.handleString(null);
        }
    }
    private String getBillActions(Elements allInfoContent) {
        final Elements table = allInfoContent.get(2).select("table.expanded-actions tbody tr");
        final HashSet<String> actions = new HashSet<>();
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
            actions.add(action.toString());
        });
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(String action : actions) {
            builder.append(isFirst ? "" : ",").append(action);
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
