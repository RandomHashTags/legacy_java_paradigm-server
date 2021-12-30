package me.randomhashtags.worldlaws.law;

import me.randomhashtags.worldlaws.WLUtilities;
import me.randomhashtags.worldlaws.country.WLCountry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class LawUtilities {

    public static int getCurrentAdministrationVersion(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return getCurrentUSAdministrationVersion();
            default: return -1;
        }
    }
    public static HashSet<Integer> getAdministrationVersions(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return getUSAdministrationVersions();
            default: return null;
        }
    }
    private static HashSet<Integer> getAdministrationVersions(int startingInt, int endingInt, Integer...excludedValues) {
        final HashSet<Integer> set = new HashSet<>();
        final List<Integer> excluded = Arrays.asList(excludedValues);
        for(int i = startingInt; i <= endingInt; i++) {
            if(!excluded.contains(i)) {
                set.add(i);
            }
        }
        return set;
    }

    private static int getCurrentUSAdministrationVersion() {
        final int startingYear = 1973, startingCongress = 93, todayYear = WLUtilities.getTodayYear();
        final int difference = todayYear - startingYear;
        return startingCongress + (difference/2);
    }
    private static HashSet<Integer> getUSAdministrationVersions() {
        return getAdministrationVersions(6, getCurrentUSAdministrationVersion(), 12, 43, 44, 45, 46, 47, 48,49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68);
    }

    public static boolean hasSubdivisionGovernmentsSupported(WLCountry country) {
        switch (country) {
            case UNITED_STATES: return true;
            default: return false;
        }
    }
}
