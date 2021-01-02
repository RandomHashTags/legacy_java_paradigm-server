package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.Sensors;

public final class SmartphoneSensors implements Sensors {
    private boolean lidarSensor, proximitySensor, threeAxisGyro, accelerometer, ambientLightSensor, barometer;

    public SmartphoneSensors() {
    }

    @Override
    public boolean hasLIDARSensor() {
        return lidarSensor;
    }
    public void setLidarSensor(boolean lidarSensor) {
        this.lidarSensor = lidarSensor;
    }

    @Override
    public boolean hasProximitySensor() {
        return proximitySensor;
    }
    public void setProximitySensor(boolean proximitySensor) {
        this.proximitySensor = proximitySensor;
    }

    @Override
    public boolean hasThreeAxisGyro() {
        return threeAxisGyro;
    }
    public void setThreeAxisGyro(boolean threeAxisGyro) {
        this.threeAxisGyro = threeAxisGyro;
    }

    @Override
    public boolean hasAccelerometer() {
        return accelerometer;
    }
    public void setAccelerometer(boolean accelerometer) {
        this.accelerometer = accelerometer;
    }

    @Override
    public boolean hasAmbientLightSensor() {
        return ambientLightSensor;
    }
    public void setAmbientLightSensor(boolean ambientLightSensor) {
        this.ambientLightSensor = ambientLightSensor;
    }

    @Override
    public boolean hasBarometer() {
        return barometer;
    }
    public void setBarometer(boolean barometer) {
        this.barometer = barometer;
    }

    @Override
    public String toString() {
        return "{" +
                "\"hasLIDARSensor\":" + lidarSensor + "," +
                "\"hasProximitySensor\":" + proximitySensor + "," +
                "\"hasThreeAxisGyro\":" + threeAxisGyro + "," +
                "\"hasAccelerometer\":" + accelerometer + "," +
                "\"hasAmbientLightSensor\":" + ambientLightSensor + "," +
                "\"hasBarometer\":" + barometer +
                "}";
    }
}
