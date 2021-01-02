package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.CompletionHandler;

public enum PhonesHuawei implements SmartphoneCompany {
    INSTANCE;

    @Override
    public String getBackendID() {
        return "huawei";
    }

    @Override
    public void getSmartphoneListJSON(CompletionHandler handler) {

    }

    @Override
    public void getSmartphoneDetails(String model, CompletionHandler handler) {

    }
}
