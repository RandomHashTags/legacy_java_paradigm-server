package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.LocalServer;

public final class TopClassActionSettlement {
    private final String title, description, claimDeadline, eligibility, potentialAward, proofOfPurchase, caseName, settlementWebsite;
    private final boolean contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor;

    public TopClassActionSettlement(String title, String description, String claimDeadline, String eligibility, String potentialAward, String proofOfPurchase, String caseName, String settlementWebsite, boolean contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor) {
        this.title = LocalServer.fixEscapeValues(title);
        this.description = LocalServer.fixEscapeValues(description);
        this.claimDeadline = claimDeadline;
        this.eligibility = LocalServer.fixEscapeValues(eligibility);
        this.potentialAward = LocalServer.fixEscapeValues(potentialAward);
        this.proofOfPurchase = LocalServer.fixEscapeValues(proofOfPurchase);
        this.caseName = LocalServer.fixEscapeValues(caseName);
        this.settlementWebsite = settlementWebsite;
        this.contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor = contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor;
    }

    @Override
    public String toString() {
        return "{" +
                "\"title\":\"" + title + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"claimDeadline\":\"" + claimDeadline + "\"," +
                "\"eligibility\":\"" + eligibility + "\"," +
                "\"potentialAward\":\"" + potentialAward + "\"," +
                "\"proofOfPurchase\":\"" + proofOfPurchase + "\"," +
                "\"caseName\":\"" + caseName + "\"," +
                "\"settlementWebsite\":\"" + settlementWebsite + "\"," +
                "\"contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor\":" + contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor +
                "}";
    }
}
