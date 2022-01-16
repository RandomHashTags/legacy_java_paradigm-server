package me.randomhashtags.worldlaws.smartphones;

public enum PhonesSamsung implements SmartphoneCompany {
    INSTANCE;

    @Override
    public String getBackendID() {
        return "samsung";
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
