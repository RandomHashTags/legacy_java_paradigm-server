package me.randomhashtags.worldlaws;

public enum APIVersion {
    v1,
    ;

    public int getVersion() {
        return Integer.parseInt(name().substring(1));
    }

    public static APIVersion valueOfInput(String input) {
        if(input != null) {
            for(APIVersion version : values()) {
                if(input.equals(version.name())) {
                    return version;
                }
            }
        }
        return null;
    }
    public static APIVersion valueOfVersion(int version) {
        for(APIVersion apiVersion : values()) {
            if(version == apiVersion.getVersion()) {
                return apiVersion;
            }
        }
        return null;
    }
}
