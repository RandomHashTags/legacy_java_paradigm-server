package me.randomhashtags.worldlaws;

public interface BillStatus {
    String getID();
    String getName();
    String getPageName();

    default String toJSON() {
        return "\"" + getID() + "\":{" +
                "\"name\":\"" + getName() + "\"," +
                "\"pageName\":\"" + getPageName() + "\"" +
                "}";
    }
}
