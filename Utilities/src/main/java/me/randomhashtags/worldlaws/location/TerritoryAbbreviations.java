package me.randomhashtags.worldlaws.location;

import java.util.HashMap;

public final class TerritoryAbbreviations {
    private static HashMap<String, String> AMERICAN_TERRITORIES;
    public static HashMap<String, String> getAmericanTerritories() {
        if(AMERICAN_TERRITORIES == null) {
            AMERICAN_TERRITORIES = new HashMap<>() {{
                put("AL", "Alabama");
                put("AK", "Alaska");
                put("AZ", "Arizona");
                put("AR", "Arkansas");
                put("CA", "California");
                put("CO", "Colorado");
                put("CT", "Connecticut");
                put("DE", "Delaware");
                put("FL", "Florida");
                put("GA", "Georgia");
                put("HI", "Hawaii");
                put("ID", "Idaho");
                put("IL", "Illinois");
                put("IN", "Indiana");
                put("IA", "Iowa");
                put("KS", "Kansas");
                put("KY", "Kentucky");
                put("LA", "Louisiana");
                put("ME", "Maine");
                put("MD", "Maryland");
                put("MA", "Massachusetts");
                put("MI", "Michigan");
                put("MN", "Minnesota");
                put("MS", "Mississippi");
                put("MO", "Missouri");
                put("MT", "Montana");
                put("NE", "Nebraska");
                put("NV", "Nevada");
                put("NH", "New Hampshire");
                put("NJ", "New Jersey");
                put("NM", "New Mexico");
                put("NY", "New York");
                put("NC", "North Carolina");
                put("ND", "North Dakota");
                put("OH", "Ohio");
                put("OK", "Oklahoma");
                put("OR", "Oregon");
                put("PA", "Pennsylvania");
                put("RI", "Rhode Island");
                put("SC", "South Carolina");
                put("SD", "South Dakota");
                put("TN", "Tennessee");
                put("TX", "Texas");
                put("UT", "Utah");
                put("VT", "Vermont");
                put("VA", "Virginia");
                put("WA", "Washington");
                put("WV", "West Virginia");
                put("WI", "Wisconsin");
                put("WY", "Wyoming");

                put("DC", "District of Columbia");
                put("AS", "American Samoa");
                put("GU", "Guam");
                put("MP", "Northern Mariana Islands");
                put("PR", "Puerto Rico");
                put("VI", "U.S. Virgin Islands");
            }};
        }
        return AMERICAN_TERRITORIES;
    }
}
