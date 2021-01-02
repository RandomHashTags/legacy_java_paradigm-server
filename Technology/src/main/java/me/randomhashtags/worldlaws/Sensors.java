package me.randomhashtags.worldlaws;

public interface Sensors {
    boolean hasLIDARSensor();
    boolean hasProximitySensor();
    boolean hasThreeAxisGyro();
    boolean hasAccelerometer();
    boolean hasAmbientLightSensor();
    boolean hasBarometer();
}
