package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;

public final class TopClassActionSettlement {
    private final String title, description, claimDeadline, eligibility, potentialAward, proofOfPurchase, caseName, settlementWebsite;
    private final boolean contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor;

    public TopClassActionSettlement(String title, String description, String claimDeadline, String eligibility, String potentialAward, String proofOfPurchase, String caseName, String settlementWebsite, boolean contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor) {
        this.title = title;
        this.description = description;
        this.claimDeadline = claimDeadline;
        this.eligibility = eligibility;
        this.potentialAward = potentialAward;
        this.proofOfPurchase = proofOfPurchase;
        this.caseName = caseName;
        this.settlementWebsite = settlementWebsite;
        this.contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor = contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor;
    }

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("title", "description", "eligibility", "potentialAward", "proofOfPurchase", "caseName");
        json.put("title", title);
        json.put("description", description);
        json.put("claimDeadline", claimDeadline);
        json.put("eligibility", eligibility);
        json.put("potentialAward", potentialAward);
        json.put("proofOfPurchase", proofOfPurchase);
        json.put("caseName", caseName);
        json.put("settlementWebsite", settlementWebsite);
        json.put("contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor", contentHasBeenSponsoredAndEditedForClarityInCollaborationWithTheSponsor);
        return json;
    }
}
