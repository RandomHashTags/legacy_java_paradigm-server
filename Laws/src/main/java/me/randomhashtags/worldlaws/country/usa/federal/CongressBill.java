package me.randomhashtags.worldlaws.country.usa.federal;

import me.randomhashtags.worldlaws.*;
import me.randomhashtags.worldlaws.country.usa.USChamber;
import me.randomhashtags.worldlaws.country.usa.USPoliticians;
import me.randomhashtags.worldlaws.locale.JSONArrayTranslatable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import me.randomhashtags.worldlaws.stream.CompletableFutures;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public final class CongressBill extends JSONObjectTranslatable {
    public CongressBill(JSONObjectTranslatable json) {
        super(json);
    }
    public CongressBill(JSONObjectTranslatable sponsor, String summary, PolicyArea policyArea, JSONArrayTranslatable subjects, JSONArrayTranslatable cosponsors, JSONArrayTranslatable actions, List<CongressBill> relatedBills, EventSources sources) {
        put("sponsor", sponsor, true);
        put("summary", summary, true);
        put("subjects", subjects, true);
        put("actions", actions, true);
        if(policyArea != null) {
            put("policyArea", policyArea.getTag(), true);
        }
        if(cosponsors != null) {
            put("cosponsors", cosponsors, true);
        }
        put("sources", sources.toJSONObject());
    }

    static CongressBill loadFromURL(String targetURL, Folder folder, int version, String id) {
        final Document doc = Jsoupable.getStaticDocument(folder, targetURL, false);
        if(doc != null) {
            final String[] splitValues = doc.select("h1.legDetail").get(0).textNodes().get(0).text().split(id + " - ");
            final String title = splitValues[1];
            final String pdfURL, billTypeText = splitValues[0].substring("All Information (Except Text) for ".length()).toUpperCase();
            final String billType = billTypeText.startsWith("S.J.RES.") ? "sjres" : billTypeText.startsWith("S.") ? "s" : billTypeText.startsWith("H.R.") ? "hr" : billTypeText.startsWith("H.J.RES.") ? "hjres" : null;
            if(billType == null) {
                pdfURL = null;
                WLLogger.logError("CongressBill", "getBill - failed to get billType for bill \"" + id + "\" (" + title + "), billTypeText=\"" + billTypeText + "\"!");
            } else {
                pdfURL = "https://www.congress.gov/" + version + "/bills/" + billType + id + "/BILLS-" + version + billType + id + "enr.pdf";
            }
            final Element element = doc.select("div.overview_wrapper div.overview table.standard01 tbody tr td a[href]").get(0);
            final String profileSlug = element.attr("href");
            final JSONObjectTranslatable sponsor = USPoliticians.INSTANCE.get(element, profileSlug);
            final String summary = getBillSummary(doc);
            final Elements allInfoContent = doc.select("main.content div.main-wrapper div.all-info-content");
            final PolicyArea policyArea = getBillPolicyArea(allInfoContent);
            final JSONArrayTranslatable subjects = getBillSubjects(allInfoContent);
            final EventSources sources = new EventSources(
                    new EventSource("US Congress: Bill URL", targetURL),
                    new EventSource("US Congress: Bill PDF", pdfURL)
            );
            final JSONArrayTranslatable cosponsors = getBillCosponsors(allInfoContent), actions = getBillActions(allInfoContent);
            final CongressBill bill = new CongressBill(sponsor, summary, policyArea, subjects, cosponsors, actions, null, sources);
            if(summary != null && !summary.toLowerCase().contains("a summary is in progress")) {
                Jsonable.setFileJSONObject(folder, id, bill);
            }
            return bill;
        }
        return null;
    }
    private static String getBillSummary(Document doc) {
        final Element table = doc.select("div.all-info-content div.main-wrapper").last();
        if(table != null) {
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
                    final String text = element.text();
                    builder.append(isFirst ? "" : "\n").append(text);
                    isFirst = false;
                }
                return builder.toString();
            }
        }
        return null;
    }
    private static PolicyArea getBillPolicyArea(Elements allInfoContent) {
        final Elements table = allInfoContent.get(6).select("div.search-column-nav ul.plain li a[href]");
        final Element target = table.size() > 0 ? table.get(0) : null;
        final String text = target != null ? target.text() : "";
        return PolicyArea.fromTag(text);
    }
    private static JSONArrayTranslatable getBillSubjects(Elements allInfoContent) {
        final Elements table = allInfoContent.get(6).select("div.search-column-main div ul.plain li a[href]");
        final HashSet<String> subjects = new HashSet<>();
        table.forEach(element -> subjects.add(element.text()));
        final JSONArrayTranslatable array = new JSONArrayTranslatable();
        array.putAll(subjects);
        return array;
    }
    private static JSONArrayTranslatable getBillCosponsors(Elements allInfoContent) {
        final Elements table = allInfoContent.get(3).select("div.main-wrapper table.item_table tbody tr td.actions a[href]");
        if(table.size() > 0) {
            table.remove(0);
            if(!table.isEmpty()) {
                final JSONArrayTranslatable array = new JSONArrayTranslatable();
                final USPoliticians politicians = USPoliticians.INSTANCE;
                new CompletableFutures<Element>().stream(table, elements -> {
                    final String profileSlug = elements.attr("href").split("https://www\\.congress\\.gov")[1];
                    final JSONObjectTranslatable string = politicians.get(elements, profileSlug);
                    array.put(string);
                });
                return array;
            }
        }
        return null;
    }
    private static JSONArrayTranslatable getBillActions(Elements allInfoContent) {
        final Elements table = allInfoContent.get(2).select("table.expanded-actions tbody tr");
        final JSONArrayTranslatable actions = new JSONArrayTranslatable();
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
            actions.put(action.toJSONObject());
        }
        return actions;
    }
    private static USChamber getChamber(String input) {
        input = input.toUpperCase();
        for(USChamber chamber : USChamber.values()) {
            if(input.equals(chamber.getName())) {
                return chamber;
            }
        }
        return null;
    }
}
