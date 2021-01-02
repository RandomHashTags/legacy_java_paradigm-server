package me.randomhashtags.worldlaws;

public interface Connectivity {
    String getWIFI();
    boolean hasMIMO();
    String getNFC();
    String getExpressCards();
    String getBluetooth();
    boolean hasUltraWidebandChip();
    String getCellular();
    boolean hasVoLTE();
    boolean hasAssistedGPS();
    boolean hasGLONASS();
    String getSimCard();
}
