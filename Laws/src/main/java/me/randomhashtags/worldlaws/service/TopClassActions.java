package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.EventSource;
import me.randomhashtags.worldlaws.Jsoupable;
import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public enum TopClassActions implements Jsoupable { // https://topclassactions.com
    INSTANCE;

    private final HashMap<String, String> settlementURLs;
    private final HashMap<String, JSONObjectTranslatable> settlements;

    TopClassActions() {
        settlementURLs = new HashMap<>();
        settlements = new HashMap<>();
    }

    public String refreshSettlements() {
        final JSONObject json = new JSONObject();
        final String url = "https://topclassactions.com/category/lawsuit-settlements/open-lawsuit-settlements/";
        final Document doc = getDocument(url);
        if(doc != null) {
            final EventSource originalSource = new EventSource("Top Class Actions", "https://topclassactions.com");

            final Elements featuredSettlementElements = doc.selectFirst("div.wrapper div.container section div.section").select("div.col");
            for(Element featuredSettlementElement : featuredSettlementElements) {
            }
        }
        return json.isEmpty() ? null : json.toString();
    }

    public JSONObjectTranslatable getSettlement(String id) {
        if(!settlements.containsKey(id)) {
            if(!settlementURLs.containsKey(id)) {
                return null;
            }
            final String url = settlementURLs.get(id);
            final TopClassActionSettlement settlement = getSettlementFrom(url);
            if(settlement != null) {
                settlements.put(id, settlement.toJSONObject());
            }
        }
        return settlements.get(id);
    }


    private TopClassActionSettlement getSettlementFrom(String url) {
        final Document doc = getDocument(url);
        TopClassActionSettlement settlement = null;
        if(doc != null) {
            final String title = doc.selectFirst("div.section div.col div.page-title h1").text();
            String description = null, claimDeadline = null, eligibility = null, potentialAward = null, proofOfPurchase = null, caseName = null, settlementWebsite = null;
            boolean contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor = true;
            final Elements elements = doc.select("div.wp-block-columns");
            for(Element element : elements) {
                final Elements columns = element.select("div.wp-block-column");
                final Element firstElement = columns.get(0), secondElement = columns.get(1);
                final String value = secondElement.text();
                switch (firstElement.text().toLowerCase()) {
                    case "who's eligible":
                        eligibility = value;
                        break;
                    case "potential award":
                        potentialAward = value;
                        break;
                    case "proof of purchase":
                        proofOfPurchase = value;
                        break;
                    case "claim form deadline":
                        claimDeadline = value;
                        break;
                    case "case name":
                        caseName = value;
                        break;
                    case "settlement website":
                        settlementWebsite = secondElement.selectFirst("a[href]").attr("href");
                        break;
                    default:
                        if(secondElement.hasAttr("style") && secondElement.attr("style").contains("height: auto !important;")) {
                            final Elements paragraphs = secondElement.select("p");
                            for(int i = 1; i <= 6; i++) {
                                paragraphs.remove(0);
                            }
                            final StringBuilder builder = new StringBuilder();
                            boolean isFirst = true;
                            for(Element paragraph : paragraphs) {
                                builder.append(isFirst ? "" : "\n\n").append(paragraph.text());
                                isFirst = false;
                            }
                            description = builder.toString();
                        }
                        break;
                }
            }
            if(description != null) {
                final String targetString = "EDITOR'S NOTE: This content has been sponsored and edited for clarity in collaboration with the sponsor";
                final boolean isSponsored = description.contains(targetString);
                if(isSponsored) {
                    description = description.replace(targetString, "");
                }
                contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor = isSponsored;
            }
            settlement = new TopClassActionSettlement(title, description, claimDeadline, eligibility, potentialAward, proofOfPurchase, caseName, settlementWebsite, contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor);
        }
        return settlement;
    }
}
