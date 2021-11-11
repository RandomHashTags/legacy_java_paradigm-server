package me.randomhashtags.worldlaws.country.ca;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.BillStatus;
import me.randomhashtags.worldlaws.CompletionHandler;
import me.randomhashtags.worldlaws.LawController;
import me.randomhashtags.worldlaws.country.WLCountry;

public final class CanadaLaws extends LawController {
    public static CanadaLaws INSTANCE = new CanadaLaws();

    // https://laws.justice.gc.ca/eng/AnnualStatutes/index2020.html

    @Override
    public WLCountry getCountry() {
        return WLCountry.CANADA;
    }

    @Override
    public BillStatus[] getBillStatuses() {
        return null;
    }

    @Override
    public void refreshRecentActivity(APIVersion version, CompletionHandler handler) {
        handler.handleString(null);
    }

    @Override
    public void getResponse(APIVersion version, String value, CompletionHandler handler) {
        handler.handleString(null);
    }

    @Override
    public void getGovernmentResponse(APIVersion version, int administration, String value, CompletionHandler handler) {
    }
}
