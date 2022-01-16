package me.randomhashtags.worldlaws.country.ca;

import me.randomhashtags.worldlaws.APIVersion;
import me.randomhashtags.worldlaws.BillStatus;
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
    public String refreshRecentActivity(APIVersion version) {
        return null;
    }

    @Override
    public String getResponse(APIVersion version, String value) {
        return null;
    }

    @Override
    public String getGovernmentResponse(APIVersion version, int administration, String value) {
        return null;
    }
}
