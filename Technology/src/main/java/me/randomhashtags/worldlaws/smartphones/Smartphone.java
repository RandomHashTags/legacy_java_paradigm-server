package me.randomhashtags.worldlaws.smartphones;

import java.util.HashMap;
import java.util.Map;

public final class Smartphone {

    private HashMap<SmartphoneDetailType, String> details;

    public Smartphone() {
    }
    public void setDetail(SmartphoneDetailType type, String detail) {
        if(this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(type, detail);
    }

    public String getDetail(SmartphoneDetailType type) {
        return details.getOrDefault(type, null);
    }

    private boolean isOnlyString(SmartphoneDetailType type) {
        switch (type) {
            case BRAND:
            case MODEL:
            case MODEL_BACKEND_ID:
            case IMAGE_URL:
            case RAM_TOTAL:
            case RAM_TYPE:
            case OPERATING_SYSTEM_INITIAL:
            case OPERATING_SYSTEM_LATEST:
            case POWER:
            case FAST_CHARGING:
            case WIRELESS_CHARGING:
            case RESISTANCE:
            case STORAGE_OPTIONS:
            case STORAGE_TYPE:
                return true;
            default:
                return false;
        }
    }

    private String getDetailsJSON() {
        final StringBuilder builder = new StringBuilder("{");
        boolean isFirst = true;
        for(Map.Entry<SmartphoneDetailType, String> map : details.entrySet()) {
            final SmartphoneDetailType type = map.getKey();
            final boolean isOnlyString = isOnlyString(type);
            final String value = map.getValue();
            builder.append(isFirst ? "" : ",").append("\"").append(type.getKey()).append("\":").append(isOnlyString ? "\"" : "").append(value).append(isOnlyString ? "\"" : "");
            isFirst = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return getDetailsJSON();
    }
}
