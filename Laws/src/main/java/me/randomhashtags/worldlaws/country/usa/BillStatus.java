package me.randomhashtags.worldlaws.country.usa;

public enum BillStatus {
    INTRODUCED,
    COMMITTEE_CONSIDERATION("committee"),
    FLOOR_CONSIDERATION("floor"),
    FAILED_ONE_CHAMBER("failed-one"),
    PASSED_ONE_CHAMBER("passed-one"),
    PASSED_BOTH_CHAMBERS("passed-both"),
    RESOLVING_DIFFERENCES("resolving"),
    TO_PRESIDENT("president"),
    VETO_ACTIONS("veto"),
    ;

    private final String backendID;

    BillStatus() {
        backendID = name().toLowerCase();
    }
    BillStatus(String backendID) {
        this.backendID = backendID;
    }

    public String getBackendID() {
        return backendID;
    }
}
