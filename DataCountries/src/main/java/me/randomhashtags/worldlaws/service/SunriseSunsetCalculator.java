package me.randomhashtags.worldlaws.service;

import me.randomhashtags.worldlaws.WLLogger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.JulianFields;

public enum SunriseSunsetCalculator {
    // https://en.wikipedia.org/wiki/Sunrise_equation
    // https://en.wikipedia.org/wiki/Julian_day#Julian_date_calculation
    ;

    private static double toDegree(double radians) {
        return Math.toDegrees(radians);
    }
    private static double mod(double value, double modulo) {
        final double test = value % modulo;
        return test < 0 ? (test + modulo) : test;
    }

    public static void test() {
        final double longitude = -98.583, latitude = 39.833;
        final long julianDay = getCurrentJulianDay(), elevationInFeet = 0;
        final double sunrise = getJulianSunrise(julianDay, longitude, latitude, elevationInFeet);
        final double sunset = getJulianSunset(julianDay, longitude, latitude, elevationInFeet);
        final LocalDateTime sunriseDate = LocalDateTime.MIN.with(JulianFields.JULIAN_DAY, julianDay);
        final LocalDateTime sunsetDate = LocalDateTime.MIN.with(JulianFields.JULIAN_DAY, julianDay);
        WLLogger.logInfo("SunriseSunsetCalculator;test;julianDay=" + julianDay + ";sunrise=" + sunrise + ";sunset=" + sunset);
        WLLogger.logInfo("SunriseSunsetCalculator;test;julianDay=" + julianDay + ";sunriseDate=" + sunriseDate.toString() + ";sunsetDate=" + sunsetDate.toString());
    }

    public static long getCurrentJulianDay() {
        //return (long) (getJulianDate() - 2451545.0 + getFractionalJulianDay());
        return LocalDate.now().getLong(JulianFields.JULIAN_DAY);
    }
    private static long getJulianDate() {
        final LocalDate now = LocalDate.now();
        final long year = now.getYear(), month = now.getMonthValue(), day = now.getDayOfMonth();
        return (1461 * (year + 4800 + (month - 14)/12))/4 + (367 * (month - 2 - 12 * ((month - 14)/12)))/12 - (3 * ((year + 4900 + (month - 14)/12)/100))/4 + day - 32075;
    }
    private static double getFractionalJulianDay() {
        return 69.184 / 86400;
    }

    private static double getMeanSolarTime(long julianDay, double longitude) {
        return julianDay - (longitude/toDegree(360));
    }
    private static double getMeanAnomaly(long julianDay, double longitude) {
        return mod(357.5291 + 0.98560028 * getMeanSolarTime(julianDay, longitude), 360);
    }
    private static double getEquationOfTheCenter(long julianDay, double longitude) {
        final double anomaly = getMeanAnomaly(julianDay, longitude);
        return (1.9148 * Math.sin(anomaly)) + (0.0200 * Math.sin(2 * anomaly)) + (0.0003 * Math.sin(3 * anomaly));
    }
    private static double getEclipticLongitude(long julianDay, double longitude) {
        final double anomaly = getMeanAnomaly(julianDay, longitude);
        final double center = getEquationOfTheCenter(julianDay, longitude);
        return mod(anomaly + center + 180 + 102.9372, 360);
    }
    private static double getSolarTransit(long julianDay, double longitude) {
        final double meanSolarTime = getMeanSolarTime(julianDay, longitude);
        final double anomaly = getMeanAnomaly(julianDay, longitude);
        final double eclipticLongitude = getEclipticLongitude(julianDay, longitude);
        return 2451545.0 + meanSolarTime + (0.0053 * Math.sin(anomaly)) - (0.0069 * Math.sin(2 * eclipticLongitude));
    }
    private static double getDeclinationOfTheSun(long julianDay, double longitude) {
        final double eclipticLongitude = getEclipticLongitude(julianDay, longitude);
        return Math.sin(eclipticLongitude) * Math.sin(toDegree(23.44));
    }
    private static double getHourAngle(long julianDay, double longitude, double latitude, long elevationInFeet) {
        final double elevationOfObserverCorrectionDegrees = (toDegree(-1.15) * Math.sqrt(elevationInFeet)) / 60;
        final double declinationOfTheSun = getDeclinationOfTheSun(julianDay, longitude);
        final double left = Math.sin(toDegree(-0.83 + elevationOfObserverCorrectionDegrees)) - Math.sin(latitude) * Math.sin(declinationOfTheSun);
        final double right = Math.cos(latitude) * Math.cos(declinationOfTheSun);
        return left / right;
    }

    public static double getJulianSunrise(long julianDay, double longitude, double latitude, long elevationInFeet) {
        return getJulianValue(julianDay, longitude, latitude, elevationInFeet, true);
    }
    public static double getJulianSunset(long julianDay, double longitude, double latitude, long elevationInFeet) {
        return getJulianValue(julianDay, longitude, latitude, elevationInFeet, false);
    }
    private static double getJulianValue(long julianDay, double longitude, double latitude, long elevationInFeet, boolean sunrise) {
        final double hourAngle = getHourAngle(julianDay, longitude, latitude, elevationInFeet);
        final double solarTransit = getSolarTransit(julianDay, longitude);
        final double right = hourAngle / toDegree(360);
        return solarTransit + (sunrise ? -right : right);
    }
}
