package me.randomhashtags.worldlaws.people;

import me.randomhashtags.worldlaws.LocalServer;
import org.json.JSONObject;

public final class HumanName {
    private final String firstName, middleName, lastName;

    public HumanName(String firstName, String middleName, String lastName) {
        this.firstName = LocalServer.fixEscapeValues(firstName);
        this.middleName = LocalServer.fixEscapeValues(middleName);
        this.lastName = LocalServer.fixEscapeValues(lastName);
    }
    public HumanName(JSONObject json) {
        firstName = LocalServer.fixEscapeValues(json.getString("firstName"));
        middleName = json.has("middleName") ? LocalServer.fixEscapeValues(json.getString("middleName")) : null;
        lastName = LocalServer.fixEscapeValues(json.getString("lastName"));
    }

    public String getFirstName() {
        return firstName;
    }
    public String getMiddleName() {
        return middleName;
    }
    public String getLastName() {
        return lastName;
    }

    @Override
    public String toString() {
        return "{\"firstName\":\"" + firstName + "\"," +
                (middleName != null ? "\"middleName\":\"" + middleName + "\"," : "") +
                "\"lastName\":\"" + lastName + "\"" +
                "}";
    }
}
