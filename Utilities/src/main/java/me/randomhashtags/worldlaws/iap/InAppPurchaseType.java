package me.randomhashtags.worldlaws.iap;

public enum InAppPurchaseType {
    CONSUMABLE,
    NON_CONSUMABLE,

    AUTO_RENEWING_SUBSCRIPTION,
    NON_RENEWING_SUBSCRIPTION,

    UNKNOWN,
    ;

    public String getName() {
        switch (this) {
            case CONSUMABLE:
                return "Consumable Addons";
            case NON_CONSUMABLE:
                return "One-time Purchases";
            case AUTO_RENEWING_SUBSCRIPTION:
                return "Auto-renewing Subscriptions";
            case NON_RENEWING_SUBSCRIPTION:
                return "Non-renewing Subscriptions";
            case UNKNOWN:
                return "Other";
            default:
                return "null";
        }
    }
}
