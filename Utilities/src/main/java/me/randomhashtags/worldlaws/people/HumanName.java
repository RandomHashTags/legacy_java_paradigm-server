package me.randomhashtags.worldlaws.people;

public final class HumanName {
    private final String firstName, middleName, lastName;

    public HumanName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
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
                "\"middleName\":\"" + middleName + "\"," +
                "\"lastName\":\"" + lastName + "\"" +
                "}";
    }
}
