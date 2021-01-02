package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.Connectivity;

public final class SmartphoneConnectivity implements Connectivity {

    private String wifi, nfc, expressCards, bluetooth, cellular, simCard;
    private boolean mimo, ultraWidebandChip, voLTE, assistedGPS, glonass;

    @Override
    public String getWIFI() {
        return wifi;
    }
    public void setWIFI(String wifi) {
        this.wifi = wifi;
    }

    @Override
    public boolean hasMIMO() {
        return mimo;
    }
    public void setMIMO(boolean mimo) {
        this.mimo = mimo;
    }

    @Override
    public String getNFC() {
        return nfc;
    }
    public void setNFC(String nfc) {
        this.nfc = nfc;
    }

    @Override
    public String getExpressCards() {
        return expressCards;
    }

    @Override
    public String getBluetooth() {
        return bluetooth;
    }

    @Override
    public boolean hasUltraWidebandChip() {
        return ultraWidebandChip;
    }

    @Override
    public String getCellular() {
        return cellular;
    }

    @Override
    public boolean hasVoLTE() {
        return voLTE;
    }

    @Override
    public boolean hasAssistedGPS() {
        return assistedGPS;
    }

    @Override
    public boolean hasGLONASS() {
        return glonass;
    }

    @Override
    public String getSimCard() {
        return simCard;
    }

    @Override
    public String toString() {
        return "{" +
                "\"wifi\":\"" + wifi + "\"," +
                "\"hasMIMO\":" + mimo + "," +
                "\"nfc\":\"" + nfc + "\"," +
                "\"expressCards\":\"" + expressCards + "\"," +
                "\"bluetooth\":\"" + bluetooth + "\"," +
                "\"hasUltraWidebandChip\":" + ultraWidebandChip + "," +
                "\"cellular\":\"" + cellular + "\"," +
                "\"hasVoLTE\":" + voLTE + "," +
                "\"hasAssistedGPS\":" + assistedGPS + "," +
                "\"hasGLONASS\":" + glonass + "," +
                "\"simCard\":\"" + simCard + "\"" +
                "}";
    }
}
