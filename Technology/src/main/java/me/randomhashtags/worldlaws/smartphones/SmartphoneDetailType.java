package me.randomhashtags.worldlaws.smartphones;

public enum SmartphoneDetailType {
    BRAND,
    MODEL,
    MODEL_BACKEND_ID,
    IMAGE_URL,

    DATE_RELEASED,
    DATE_DISCONTINUED,

    AUTHENTICATION_FINGERPRINT,
    AUTHENTICATION_FACE,

    CONNECTIVITY,
    DIMENSIONS,
    DISPLAY,
    PROCESSOR,
    SENSORS,

    CAMERA_FRONT,
    CAMERA_REAR,

    RAM_TOTAL,
    RAM_TYPE,

    STORAGE_OPTIONS,
    STORAGE_TYPE,

    OPERATING_SYSTEM_INITIAL,
    OPERATING_SYSTEM_LATEST,

    POWER,
    FAST_CHARGING,
    WIRELESS_CHARGING,
    RESISTANCE,

    GEEKBENCH_SCORES,

    SOURCES,

    OTHER,
    ;

    public String getKey() {
        return name().toLowerCase();
    }
}
