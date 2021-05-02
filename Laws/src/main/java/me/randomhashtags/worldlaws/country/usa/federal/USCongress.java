package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.Politicians;
import me.randomhashtags.worldlaws.country.PreEnactedBill;
import me.randomhashtags.worldlaws.country.usa.BillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.EventDate;
import org.apache.logging.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;

public enum USCongress implements Jsoupable, Jsonable {
    INSTANCE;

    private HashMap<BillStatus, String> statuses;
    private HashMap<String, String> bills;
    private HashMap<Integer, String> enactedBills;
    private int version;

    public static USCongress getCongress(int version) {
        final USCongress congress = USCongress.INSTANCE;
        congress.version = Math.max(version, 93);
        return congress;
    }
    private static int getCurrentAdministrationVersion() {
        final int startingYear = 1973, startingCongress = 93, todayYear = WLUtilities.getTodayYear();
        final int difference = todayYear - startingYear;
        return startingCongress + (difference/2);
    }
    private boolean isCurrentAdministration() {
        return version == getCurrentAdministrationVersion();
    }

    private String getVersion() {
        return "" + version;
    }
    private String getVersioned() {
        final String version = getVersion();
        return version.endsWith("3") && !version.endsWith("13") ? version + "rd" : version + "th";
    }

    public void getBillsByStatus(BillStatus status, CompletionHandler handler) {
        if(statuses == null) {
            statuses = new HashMap<>();
        }
        if(statuses.containsKey(status)) {
            handler.handle(statuses.get(status));
        } else {
            final FileType fileType = FileType.LAWS_USA_CONGRESS;
            final String folderName = fileType.getFolderName(false).replace("%congress%", "" + version);
            fileType.setCustomFolderName(folderName);
            getJSONArray(fileType, "bill status: " + status.name().toLowerCase(), new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    getBillsBySearch(status, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            final String value = (String) object;
                            handler.handle(value);
                        }
                    });
                }

                @Override
                public void handleJSONArray(JSONArray array) {
                    fileType.resetCustomFolderName();
                    final String string = array.toString();
                    statuses.put(status, string);
                    handler.handle(string);
                }
            });
        }
    }
    private void getBillsBySearch(BillStatus status, CompletionHandler handler) {
        final long started = System.currentTimeMillis();
        final StringBuilder introducedBills = new StringBuilder("[");
        final String version = getVersion();
        final String url = "https://www.congress.gov/search?searchResultViewType=expanded&pageSize=250&q=%7B%22source%22%3A%22legislation%22%2C%22congress%22%3A%22" + version + "%22%2C%22bill-status%22%3A%22" + status.getBackendID() + "%22%7D";
        final Document doc = getDocument(url);
        if(doc != null) {
            final Elements items = doc.select("main.content div.main-wrapper div.search-row div.search-column-main ol.basic-search-results-lists li.expanded");
            boolean isFirst = true;
            for(Element element : items) {
                final PreCongressBill bill = getPreCongressBillFrom(element);
                bill.setStatus(status);
                introducedBills.append(isFirst ? "" : ",").append(bill.toString());
                isFirst = false;
            }
        }
        introducedBills.append("]");
        WLLogger.log(Level.INFO, "USCongress - loaded " + status.name() + " bills for congress " + version + " (took " + (System.currentTimeMillis()-started) + "ms)");
        handler.handle(introducedBills.toString());
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
        final int substringCount = latestActionElement.select("strong").get(0).text().length();
        final String latestActionString = targetActionText.substring(substringCount);
        final String[] latestActionSpaces = latestActionString.split(" ");
        final String target = latestActionSpaces[1];
        final boolean isHouseOrSenate = target.equals("House") || target.equals("Senate");
        final String dateString = latestActionSpaces[isHouseOrSenate ? 1 : 0];
        final String[] dateValues = dateString.split("/");
        final int year = Integer.parseInt(dateValues[2]), month = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[1]);
        final EventDate date = new EventDate(Month.of(month), day, year);

        return new PreCongressBill(null, chamber, id, title, committees, notes, date);
    }

    private void loadBillsHashMap() {
        if(bills == null) {
            bills = new HashMap<>();
        }
    }
    public void getEnactedBills(CompletionHandler handler) {
        loadBillsHashMap();
        if(enactedBills == null) {
            enactedBills = new HashMap<>();
        }
        if(enactedBills.containsKey(version)) {
            handler.handle(enactedBills.get(version));
        } else if(isCurrentAdministration()) {
            handler.handle(null);
        } else {
            final long started = System.currentTimeMillis();
            final int versionInt = version;
            final String version = "" + versionInt;
            final FileType fileType = FileType.LAWS_USA_CONGRESS;
            final String folderName = fileType.getFolderName(false).replace("%version%", version);
            fileType.setCustomFolderName(folderName);
            getJSONArray(fileType, "enacted bills", new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final StringBuilder builder = new StringBuilder("[");
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
                                final EventDate date = new EventDate(Month.of(month), day, year);

                                final String[] values = previousTitle.split(" - ");
                                final String prefix = values[0], title = previousTitle.split(prefix + " - ")[1];
                                final boolean isHouse = prefix.startsWith("H.R.");
                                final USChamber chamber = isHouse ? USChamber.HOUSE : USChamber.SENATE;
                                final String id = prefix.split("\\.")[isHouse ? 2 : 1];
                                final PreEnactedBill bill = new PreEnactedBill(chamber, id, title, date);
                                builder.append(isFirst ? "" : ",").append(bill.toString());
                                isFirst = false;
                            } else {
                                previousTitle = text;
                            }
                            index++;
                        }
                        builder.append("]");
                        handler.handle(builder.toString());
                    }
                }

                @Override
                public void handleJSONArray(JSONArray array) {
                    fileType.resetCustomFolderName();
                    final String string = array.toString();
                    enactedBills.put(versionInt, string);
                    WLLogger.log(Level.INFO, "USCongress - loaded enacted bills for congress " + version + " (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(array.toString());
                }
            });
        }
    }
    public void getBill(USChamber chamber, String id, CompletionHandler handler) {
        loadBillsHashMap();
        if(bills.containsKey(id)) {
            handler.handle(bills.get(id));
        } else {
            final long started = System.currentTimeMillis();
            final int versionInt = this.version;
            final String version = getVersioned();
            final FileType fileType = FileType.LAWS_USA_CONGRESS;
            final String chamberName = chamber.name(), chamberNameLowercase = chamberName.toLowerCase(), folderName = fileType.getFolderName(false).replace("%version%", "" + versionInt) + File.separator + chamberNameLowercase;
            fileType.setCustomFolderName(folderName);
            getJSONObject(fileType, id, new CompletionHandler() {
                @Override
                public void load(CompletionHandler handler) {
                    final String targetURL = "https://www.congress.gov/bill/" + version + "-congress/" + chamberNameLowercase + "-bill/" + id + "/all-info";
                    final Document billDoc = getDocument(targetURL);
                    if(billDoc != null) {
                        final String[] splitValues = billDoc.select("h1.legDetail").get(0).textNodes().get(0).text().split(id + " - ");
                        final String title = splitValues[1];
                        final String pdfURL, billTypeText = splitValues[0].substring("All Information (Except Text) for ".length()).toUpperCase();
                        final String billType = billTypeText.startsWith("S.J.RES.") ? "sjres" : billTypeText.startsWith("S.") ? "s" : billTypeText.startsWith("H.R.") ? "hr" : billTypeText.startsWith("H.J.RES.") ? "hjres" : null;
                        if(billType == null) {
                            pdfURL = null;
                            WLLogger.log(Level.ERROR, "USCongress - failed to get billType for bill \"" + id + "\" (" + title + "), billTypeText=\"" + billTypeText + "\"!");
                        } else {
                            pdfURL = "https://www.congress.gov/" + versionInt + "/bills/" + billType + id + "/BILLS-" + versionInt + billType + id + "enr.pdf";
                        }
                        final Element element = billDoc.select("div.overview_wrapper div.overview table.standard01 tbody tr td a[href]").get(0);
                        final String profileSlug = element.attr("href");
                        Politicians.INSTANCE.getUSA(element, profileSlug, new CompletionHandler() {
                            @Override
                            public void handle(Object object) {
                                final String sponsor = object.toString();
                                final String summary = getBillSummary(billDoc);
                                final Elements allInfoContent = billDoc.select("main.content div.main-wrapper div.all-info-content");
                                final PolicyArea policyArea = getBillPolicyArea(allInfoContent);
                                final String subjects = getBillSubjects(allInfoContent);
                                getBillCosponsors(allInfoContent, new CompletionHandler() {
                                    @Override
                                    public void handle(Object cosponsorObject) {
                                        final String cosponsors = cosponsorObject.toString();
                                        final String actions = getBillActions(allInfoContent);
                                        final CongressBill bill = new CongressBill(chamberName, id, title, sponsor, summary, policyArea, subjects, cosponsors, actions, targetURL, pdfURL, null);
                                        handler.handle(bill.toString());
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void handleJSONObject(JSONObject json) {
                    fileType.resetCustomFolderName();
                    final String id = json.getString("id"), title = json.getString("title"), string = json.toString();
                    bills.put(id, string);
                    WLLogger.log(Level.INFO, "USCongress - loaded bill from chamber \"" + chamberName + "\" with title \"" + title + "\" for congress " + versionInt + " (took " + (System.currentTimeMillis()-started) + "ms)");
                    handler.handle(string);
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
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : table) {
            final String text = element.text();
            builder.append(isFirst ? "" : ",").append("\"").append(text).append("\"");
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
                final Politicians politicians = Politicians.INSTANCE;
                final StringBuilder builder = new StringBuilder("[");
                final HashSet<String> cosponsors = new HashSet<>();
                table.parallelStream().forEach(elements -> {
                    final String profileSlug = elements.attr("href").split("https://www\\.congress\\.gov")[1];
                    politicians.getUSA(elements, profileSlug, new CompletionHandler() {
                        @Override
                        public void handle(Object object) {
                            cosponsors.add(object.toString());
                            if(cosponsors.size() == max) {
                                boolean isFirst = true;
                                for(String cosponsor : cosponsors) {
                                    builder.append(isFirst ? "" : ",").append(cosponsor);
                                    isFirst = false;
                                }
                                builder.append("]");
                                handler.handle(builder.toString());
                            }
                        }
                    });
                });
            } else {
                handler.handle(null);
            }
        } else {
            handler.handle(null);
        }
    }
    private String getBillActions(Elements allInfoContent) {
        final Elements table = allInfoContent.get(2).select("table.expanded-actions tbody tr");
        final StringBuilder builder = new StringBuilder("[");
        boolean isFirst = true;
        for(Element element : table) {
            final String text = element.text();
            final String[] values = text.split(" ");
            final String value0 = values[0], dateString = value0.split("-")[0], dateTime = value0.contains("-") ? value0.split("-")[1] : null, chamberString = values[1];
            final USChamber chamber = getChamber(chamberString);
            String title = text.split(value0 + " " + (chamber != null ? chamberString + " " : ""))[1];
            if(title.contains("Became Public Law No:")) {
                title = "Became Public Law.";
            }
            final String[] dateValues = dateString.split("/");
            final int dateHour = dateTime != null ? Integer.parseInt(dateTime.split(":")[0]) : 0, dateMinute = dateTime != null ? Integer.parseInt(dateTime.split(":")[1].substring(0, 2)) : 0;
            final LocalDateTime date = LocalDateTime.of(Integer.parseInt(dateValues[2]), Integer.parseInt(dateValues[0]), Integer.parseInt(dateValues[1]), dateHour, dateMinute);
            final BillAction action = new BillAction(chamber, title, date);
            builder.append(isFirst ? "" : ",").append(action.toString());
            isFirst = false;
        }
        builder.append("]");
        return builder.toString();
    }
    private USChamber getChamber(String input) {
        try {
            return USChamber.valueOf(input.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }
}
