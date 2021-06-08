package me.randomhashtags.worldlaws.country.usa.federal;

import java.util.Arrays;
import java.util.HashSet;

public enum PolicyArea {
    // Found @ https://www.congress.gov/help/field-values/policy-area
    AGRICULTURE_AND_FOOD,
    ANIMALS,
    ARMED_FORCES_AND_NATIONAL_SECURITY,
    ARTS_CULTURE_RELIGION("Arts, Culture, Religion"),
    CIVIL_RIGHTS_AND_LIBERTIES_MINORITY_ISSUES("Civil Rights and Liberties, Minority Issues"),
    COMMERCE,
    CONGRESS,
    CRIME_AND_LAW_ENFORCEMENT,
    ECONOMICS_AND_PUBLIC_FINANCE,
    EDUCATION,
    EMERGENCY_MANAGEMENT,
    ENERGY,
    ENVIRONMENTAL_PROTECTION,
    FAMILIES,
    FINANCE_AND_FINANCIAL_SECTOR,
    FOREIGN_TRADE_AND_INTERNATIONAL_FINANCE,
    GOVERNMENT_OPERATIONS_AND_POLITICS,
    HEALTH,
    HOUSING_AND_COMMUNITY_DEVELOPMENT,
    IMMIGRATION,
    INTERNATIONAL_AFFAIRS,
    LABOR_AND_EMPLOYMENT,
    LAW,
    NATIVE_AMERICANS,
    PUBLIC_LANDS_AND_NATURAL_RESOURCES,
    SCIENCE_TECHNOLOGY_COMMUNICATIONS("Science, Technology, Communications"),
    SOCIAL_SCIENCES_AND_HISTORY,
    SOCIAL_WELFARE,
    SPORTS_AND_RECREATION,
    TAXATION,
    TRANSPORTATION_AND_PUBLIC_WORKS,
    WATER_RESOURCES_DEVELOPMENT,
    ;

    private static HashSet<String> EXCLUDED;

    private String tag;

    PolicyArea() {
        setupTag();
    }
    PolicyArea(String tag) {
        this.tag = tag;
    }
    private void setupTag() {
        if(EXCLUDED == null) {
            EXCLUDED = new HashSet<>(Arrays.asList("and"));
        }
        final String[] values = name().split("_");
        final StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for(String value : values) {
            final String lowercase = value.toLowerCase();
            final String string = EXCLUDED.contains(lowercase) ? lowercase : lowercase.substring(0, 1).toUpperCase() + lowercase.substring(1);
            builder.append(isFirst ? "" : " ").append(string);
            isFirst = false;
        }
        this.tag = builder.toString();
    }

    public String getTag() {
        return tag;
    }

    public static PolicyArea fromTag(String input) {
        for(PolicyArea area : PolicyArea.values()) {
            if(input.equalsIgnoreCase(area.getTag())) {
                return area;
            }
        }
        return null;
    }
}
