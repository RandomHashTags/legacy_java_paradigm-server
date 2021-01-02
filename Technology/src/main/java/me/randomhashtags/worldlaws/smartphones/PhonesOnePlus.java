package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.CompletionHandler;

public enum PhonesOnePlus implements SmartphoneCompany {
    INSTANCE;

    @Override
    public String getBackendID() {
        return "oneplus";
    }

    @Override
    public void getSmartphoneListJSON(CompletionHandler handler) {

    }

    @Override
    public void getSmartphoneDetails(String model, CompletionHandler handler) {

    }
}
