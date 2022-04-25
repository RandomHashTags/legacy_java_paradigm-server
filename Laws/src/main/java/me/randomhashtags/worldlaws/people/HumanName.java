package me.randomhashtags.worldlaws.people;

import me.randomhashtags.worldlaws.locale.JSONObjectTranslatable;
import org.json.JSONObject;

public final class HumanName {
    private final String firstName, middleName, lastName;

    public HumanName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }
    public HumanName(JSONObject json) {
        firstName = json.getString("firstName");
        middleName = json.optString("middleName", null);
        lastName = json.getString("lastName");
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

    public JSONObjectTranslatable toJSONObject() {
        final JSONObjectTranslatable json = new JSONObjectTranslatable("firstName", "middleName", "lastName");
        json.put("firstName", firstName);
        if(middleName != null) {
            json.put("middleName", middleName);
        }
        json.put("lastName", lastName);
        return json;
    }
}
