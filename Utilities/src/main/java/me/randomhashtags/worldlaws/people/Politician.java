package me.randomhashtags.worldlaws.people;

public final class Politician implements Person {
    private HumanName name;
    private Gender gender;
    private PoliticalParty party;
    private String district, governedTerritory, imageURL, website;

    public Politician(HumanName name, Gender gender, String governedTerritory, String district, PoliticalParty party, String imageURL, String website) {
        this.name = name;
        this.gender = gender;
        this.governedTerritory = governedTerritory;
        this.district = district;
        this.party = party;
        this.imageURL = imageURL;
        this.website = website;
    }

    @Override
    public HumanName getName() {
        return name;
    }

    @Override
    public Gender getGender() {
        return gender;
    }

    public String getGovernedTerritory() {
        return governedTerritory;
    }
    public String getDistrict() {
        return district;
    }

    public PoliticalParty getParty() {
        return party;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getWebsite() {
        return website;
    }

    @Override
    public String toString() {
        return "{\"name\":" + name.toString() + "," +
                "\"gender\":\"" + gender.name() + "\"," +
                "\"governedTerritory\":\"" + governedTerritory + "\"," +
                "\"district\":\"" + district + "\"," +
                "\"party\":\"" + party.getName() + "\"," +
                "\"imageURL\":\"" + imageURL + "\"," +
                "\"website\":\"" + website + "\"" +
                "}";
    }
}
