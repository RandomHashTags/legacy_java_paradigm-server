package me.randomhashtags.worldlaws.service.usa;

import me.randomhashtags.worldlaws.BillStatus;
import me.randomhashtags.worldlaws.LocalServer;

public enum USBillStatus implements BillStatus {
    INTRODUCED,
    COMMITTEE_CONSIDERATION("committee"),
    FLOOR_CONSIDERATION("floor"),
    FAILED_ONE_CHAMBER("failed-one"),
    PASSED_ONE_CHAMBER("passed-one"),
    PASSED_BOTH_CHAMBERS("passed-both"),
    RESOLVING_DIFFERENCES("resolving"),
    TO_PRESIDENT("president"),
    BECAME_LAW("law"),
    VETOED("veto"),
    ;

    private String searchID;

    USBillStatus() {
    }
    USBillStatus(String searchID) {
        this.searchID = searchID;
    }

    @Override
    public String getID() {
        return name().toLowerCase();
    }

    public String getSearchID() {
        return searchID == null ? name().toLowerCase() : searchID;
    }
    @Override
    public String getName() {
        return LocalServer.toCorrectCapitalization(name());
    }

    @Override
    public String getPageName() {
        final String prefix = "Legislature: ";
        switch (this) {
            case INTRODUCED: return prefix + "Introduced";
            case COMMITTEE_CONSIDERATION: return prefix + "Committee Consideration";
            case FLOOR_CONSIDERATION: return prefix + "Floor Consideration";
            case FAILED_ONE_CHAMBER: return prefix + "Failed One Chamber";
            case PASSED_ONE_CHAMBER: return prefix + "Passed One Chamber";
            case PASSED_BOTH_CHAMBERS: return prefix + "Passed Both Chambers";
            case RESOLVING_DIFFERENCES: return prefix + "Resolving Differences";
            case TO_PRESIDENT: return prefix + "Presented to President";
            case BECAME_LAW: return prefix + "Became Law";
            case VETOED: return prefix + "Vetoed";
            default: return null;
        }
    }
}
