package me.randomhashtags.worldlaws.smartphones;

public enum PhonesHuawei implements SmartphoneCompany {
    INSTANCE;

    @Override
    public String getBackendID() {
        return "huawei";
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
