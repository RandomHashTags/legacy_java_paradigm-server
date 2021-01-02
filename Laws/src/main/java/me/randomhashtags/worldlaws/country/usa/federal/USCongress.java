package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.LocalServer;
import me.randomhashtags.worldlaws.WLLogger;
import me.randomhashtags.worldlaws.country.usa.BillStatus;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.country.usa.USPolitician;
import me.randomhashtags.worldlaws.country.usa.service.congress.USCongressPoliticians;
import me.randomhashtags.worldlaws.law.PreEnactedBill;
import me.randomhashtags.worldlaws.people.Politician;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

public enum USCongress implements Jsoupable {
    INSTANCE;

    private StringBuilder enactedBills;
    private String pdfURL;
    private HashMap<BillStatus, String> statuses;
    private HashMap<String, String> bills;
    private int version;
    private volatile int COMPLETED_HANDLERS;

    public static USCongress getCongress(int version) {
        final USCongress congress = USCongress.INSTANCE;
        congress.version = Math.max(version, 93);
        return congress;
    }

    private String getVersion() {
        return "" + version;
    }
    private String getVersioned() {
        final String version = getVersion();
        return version.endsWith("3") && !version.endsWith("13") ? version + "rd" : version + "th";
    }

    public String getBillsByStatus(BillStatus status) {
        if(statuses == null) {
            statuses = new HashMap<>();
        }
        if(statuses.containsKey(status)) {
            return statuses.get(status);
        } else {
            final String value = getBillsBySearch(status);
            statuses.put(status, value);
            return value;
        }
    }
    private String getBillsBySearch(BillStatus status) {
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
        return introducedBills.toString();
    }
    public PreCongressBill getPreCongressBillFrom(Element element) {
        final String heading = element.select("span.result-heading").get(0).select("a[href]").text();
        final boolean isHouse = heading.startsWith("H.R.");
        final USChamber chamber = isHouse ? USChamber.HOUSE : USChamber.SENATE;
        final String id = heading.split("\\.")[isHouse ? 2 : 1];

        final String title = element.select("span.result-title").get(0).text();
        final Elements resultItems = element.select("span.result-item");
        final int results = resultItems.size();

        final String committees = "";//resultItems.get(results-3).text().split(" - ")[1];

        //final String dateString = resultItems.get(results-2).text().split(" - ")[1].split(" ")[0];
        //final String[] dateValues = dateString.split("/");
        //final int year = Integer.parseInt(dateValues[2]), month = Integer.parseInt(dateValues[0]), day = Integer.parseInt(dateValues[1]);
        final LocalDate date = LocalDate.of(2000, 1, 1);

        return new PreCongressBill(null, chamber, id, title, committees, date);
    }

    public String getEnactedBills() {
        if(enactedBills == null) {
            final long started = System.currentTimeMillis();
            bills = new HashMap<>();
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
                        final USChamber chamber = isHouse ? USChamber.HOUSE : USChamber.SENATE;
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
            WLLogger.log(Level.INFO, "USCongress - loaded enacted bills for congress " + this.version + " (took " + (System.currentTimeMillis()-started) + "ms)");
        }
        return enactedBills.toString();
    }
    public void getBill(USChamber chamber, String id, CompletionHandler handler) {
        if(bills.containsKey(id)) {
            handler.handle(bills.get(id));
        } else {
            final long started = System.currentTimeMillis();
            final int versionInt = this.version;
            final String version = getVersioned();
            final String targetURL = "https://www.congress.gov/bill/" + version + "-congress/" + chamber.name().toLowerCase() + "-bill/" + id + "/all-info";
            final Document billDoc = getDocument(targetURL);
            if(billDoc != null) {
                final String title = LocalServer.fixEscapeValues(billDoc.select("h1.legDetail").get(0).text().split(id + " - ")[1].split(version)[0]);

                final String chamberString = chamber.name();
                final Element element = billDoc.select("div.overview_wrapper div.overview table.standard01 tbody tr td a[href]").get(0);
                final String profileSlug = element.attr("href");
                USCongressPoliticians.INSTANCE.getPolitician(element, profileSlug, false, new CompletionHandler() {
                    @Override
                    public void handlePolitician(Politician sponsor) {
                        final String summary = getBillSummary(billDoc);
                        final PolicyArea policyArea = getBillPolicyArea(billDoc);
                        final HashSet<String> subjects = getBillSubjects(billDoc);
                        getBillCosponsors(billDoc, new CompletionHandler() {
                            @Override
                            public void handlePoliticianCosponsors(List<Politician> cosponsors) {
                                final List<BillAction> actions = getBillActions(billDoc);
                                final CongressBill bill = new CongressBill(chamberString, id, title, sponsor, summary, policyArea, subjects, cosponsors, actions, targetURL, pdfURL, null);
                                final String string = bill.toString();
                                bills.put(id, string);
                                WLLogger.log(Level.INFO, "USCongress - loaded bill from chamber \"" + chamberString + "\" with title \"" + title + "\" for congress " + versionInt + " (took " + (System.currentTimeMillis()-started) + "ms)");
                                handler.handle(string);
                            }
                        });
                    }
                });
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
        final Element target = table.size() > 0 ? table.get(0) : null;
        final String text = target != null ? target.text() : "";
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
    private void getBillCosponsors(Document doc, CompletionHandler handler) {
        final List<Politician> cosponsors = new ArrayList<>();
        final Elements table = doc.select("main.content div.main-wrapper div.all-info-content").get(3).select("div.main-wrapper table.item_table tbody tr td.actions a[href]");
        if(table.size() > 0) {
            table.remove(0);
            if(!table.isEmpty()) {
                final int max = table.size();
                COMPLETED_HANDLERS = 0;
                for(Element element : table) {
                    final String profileSlug = element.attr("href").split("https://www\\.congress\\.gov")[1];
                    new Thread(() -> USPolitician.getFromBill(profileSlug, element, new CompletionHandler() {
                        @Override
                        public void handlePolitician(Politician politician) {
                            completedHandler();
                            cosponsors.add(politician);
                            if(getCompletedHandlers() == max) {
                                handler.handlePoliticianCosponsors(cosponsors);
                            }
                        }
                    })).start();
                }
            } else {
                handler.handlePoliticianCosponsors(cosponsors);
            }
        } else {
            handler.handlePoliticianCosponsors(cosponsors);
        }
    }
    private List<BillAction> getBillActions(Document doc) {
        final List<BillAction> actions = new ArrayList<>();
        final Elements table = doc.select("div.all-info-content").get(2).select("table.expanded-actions tbody tr");
        final Elements hrefs = table.select("a[href]");
        pdfURL = "https://www.congress.gov" + (hrefs.size() > 1 ? table.select("a[href]").get(1).attr("href") : "");
        for(Element element : table) {
            final String text = element.text();
            final String[] values = text.split(" ");
            final String value0 = values[0], dateString = value0.split("-")[0], dateTime = value0.contains("-") ? value0.split("-")[1] : null, chamberString = values[1];
            final USChamber chamber = getChamber(chamberString);
            final String title = text.split(value0 + " " + (chamber != null ? chamberString + " " : ""))[1];
            final String[] dateValues = dateString.split("/");
            final int dateHour = dateTime != null ? Integer.parseInt(dateTime.split(":")[0]) : 0, dateMinute = dateTime != null ? Integer.parseInt(dateTime.split(":")[1].substring(0, 2)) : 0;
            final LocalDateTime date = LocalDateTime.of(Integer.parseInt(dateValues[2]), Integer.parseInt(dateValues[0]), Integer.parseInt(dateValues[1]), dateHour, dateMinute);
            final BillAction action = new BillAction(chamber, title, date);
            actions.add(action);
        }
        return actions;
    }
    private USChamber getChamber(String input) {
        try {
            return USChamber.valueOf(input.toUpperCase());
        } catch (Exception ignored) {
            return null;
        }
    }

    private synchronized void completedHandler() {
        COMPLETED_HANDLERS += 1;
    }
    private synchronized int getCompletedHandlers() {
        return COMPLETED_HANDLERS;
    }
}
