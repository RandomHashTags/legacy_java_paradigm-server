package me.randomhashtags.worldlaws.smartphones;

public enum PhonesOnePlus implements SmartphoneCompany {
    INSTANCE;

    @Override
    public String getBackendID() {
        return "oneplus";
    }

    @Override
    public String getSmartphoneListJSON() {
        return null;
    }

    @Override
    public String getSmartphoneDetails(String model) {
        return null;
    }
}
