package me.randomhashtags.worldlaws;

import java.io.File;

public enum FileType {
    AVAILABILITIES("availabilities"),
    COUNTRIES("countries"),
    COUNTRIES_SUBDIVISIONS("countries" + File.separator + "subdivisions"),
    HOLIDAYS("holidays"),
    LEGALITIES("legalities"),
    NATIONAL("national"),
    OTHER(null),
    PEOPLE_POLITICIANS_UNITED_STATES("people" + File.separator + "politicians" + File.separator + "unitedstates"),
    RANKINGS("rankings"),
    RANKINGS_AGRICULTURE("rankings" + File.separator + "agriculture"),
    INFO("info"),
    SERVICES("services"),
    VALUES("values")
    ;

    private final String folderName;

    FileType(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
