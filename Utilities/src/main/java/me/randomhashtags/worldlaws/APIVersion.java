package me.randomhashtags.worldlaws;

public enum APIVersion {
    v1,
    v1_1,
    v1_2,
    v1_3,
    v1_4,
    v1_5,
    v1_6,
    v1_7,
    v1_8,
    v1_9,

    v2,
    ;

    public String getName() {
        return name().replace("_", ".");
    }

    public static APIVersion getCurrent() {
        return APIVersion.v1;
    }

    public static APIVersion valueOfInput(String input) {
        if(input != null) {
            for(APIVersion version : values()) {
                if(input.equals(version.getName())) {
                    return version;
                }
            }
        }
        return null;
    }
}
