package me.randomhashtags.worldlaws;

import java.util.List;

public interface Camera {
    String getMegapixels();
    String getType();
    float getAperture();
    boolean hasOpticalImageStabilization();
    boolean hasAutoImageStabilization();
    String getElementLens();
    boolean hasNightMode();
    boolean hasAutoAdjustment();
    String getOpticalZoom();
    String getDigitalZoom();
    String getPanoramaMegapixels();
    boolean hasPortraitMode();
    List<String> getPortraitLighting();
    boolean hasBurstMode();
    boolean hasLivePhotos();
    boolean hasWideColorCapture();
    String getSmartHDRVersion();
    List<String> getVideoRecordingSpeeds();
    int getExtendedDynamicRangeVideoFPS();
}
