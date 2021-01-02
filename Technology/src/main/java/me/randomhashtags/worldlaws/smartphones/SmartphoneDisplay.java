package me.randomhashtags.worldlaws.smartphones;

import me.randomhashtags.worldlaws.Display;
import me.randomhashtags.worldlaws.LocalServer;

public final class SmartphoneDisplay implements Display {

    private boolean multitouch, fingerprintResistantOleophobicCoating, fullSRGB, wideColor, truetone, hdr, hdr10, dolbyVision;
    private String technology, resolution, aspectRatio, maxNitBrightness, contrastRatio, taptics;
    private int pixelsPerInch;

    public SmartphoneDisplay() {
    }
    public SmartphoneDisplay(boolean multitouch, String technology, String resolution, int pixelsPerInch, String aspectRatio, String maxNitBrightness, boolean fingerprintResistantOleophobicCoating, String contrastRatio, boolean fullSRGB, boolean wideColor, boolean truetone, boolean hdr, boolean hdr10, boolean dolbyVision, String taptics) {
        this.multitouch = multitouch;
        this.technology = LocalServer.fixEscapeValues(technology);
        this.resolution = LocalServer.fixEscapeValues(resolution);
        this.pixelsPerInch = pixelsPerInch;
        this.aspectRatio = LocalServer.fixEscapeValues(aspectRatio);
        this.maxNitBrightness = maxNitBrightness;
        this.fingerprintResistantOleophobicCoating = fingerprintResistantOleophobicCoating;
        this.contrastRatio = LocalServer.fixEscapeValues(contrastRatio);
        this.fullSRGB = fullSRGB;
        this.wideColor = wideColor;
        this.truetone = truetone;
        this.hdr = hdr;
        this.hdr10 = hdr10;
        this.dolbyVision = dolbyVision;
        this.taptics = LocalServer.fixEscapeValues(taptics);
    }

    @Override
    public boolean hasMultitouch() {
        return multitouch;
    }
    public void setMultitouch(boolean multitouch) {
        this.multitouch = multitouch;
    }

    @Override
    public String getTechnology() {
        return technology;
    }
    public void setTechnology(String technology) {
        this.technology = technology;
    }

    @Override
    public String getResolution() {
        return resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public int getPixelsPerInch() {
        return pixelsPerInch;
    }
    public void setPixelsPerInch(int pixelsPerInch) {
        this.pixelsPerInch = pixelsPerInch;
    }

    @Override
    public String getAspectRatio() {
        return aspectRatio;
    }
    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    @Override
    public String getMaxNitBrightness() {
        return maxNitBrightness;
    }
    public void setMaxNitBrightness(String maxNitBrightness) {
        this.maxNitBrightness = maxNitBrightness;
    }

    @Override
    public boolean hasFingerprintResistantOleophobicCoating() {
        return fingerprintResistantOleophobicCoating;
    }
    public void setFingerprintResistantOleophobicCoating(boolean fingerprintResistantOleophobicCoating) {
        this.fingerprintResistantOleophobicCoating = fingerprintResistantOleophobicCoating;
    }

    @Override
    public String getContrastRatio() {
        return contrastRatio;
    }
    public void setContrastRatio(String contrastRatio) {
        this.contrastRatio = contrastRatio;
    }

    @Override
    public boolean hasFullSRGB() {
        return fullSRGB;
    }
    public void setFullSRGB(boolean fullSRGB) {
        this.fullSRGB = fullSRGB;
    }

    @Override
    public boolean hasWideColor() {
        return wideColor;
    }
    public void setWideColor(boolean wideColor) {
        this.wideColor = wideColor;
    }

    @Override
    public boolean hasTrueTone() {
        return truetone;
    }
    public void setTruetone(boolean truetone) {
        this.truetone = truetone;
    }

    @Override
    public boolean hasHDR() {
        return hdr;
    }
    public void setHDR(boolean hdr) {
        this.hdr = hdr;
    }

    @Override
    public boolean hasHDR10() {
        return hdr10;
    }
    public void setHDR10(boolean hdr10) {
        this.hdr10 = hdr10;
    }

    @Override
    public boolean hasDolbyVision() {
        return dolbyVision;
    }
    public void setDolbyVision(boolean dolbyVision) {
        this.dolbyVision = dolbyVision;
    }

    @Override
    public String getTaptics() {
        return taptics;
    }
    public void setTaptics(String taptics) {
        this.taptics = taptics;
    }

    @Override
    public String toString() {
        return "{" +
                "\"hasMultitouch\":" + multitouch + "," +
                "\"technology\":\"" + technology + "\"," +
                "\"resolution\":\"" + resolution + "\"," +
                "\"pixelsPerInch\":" + pixelsPerInch + "," +
                "\"aspectRatio\":\"" + aspectRatio + "\"," +
                "\"maxNitBrightness\":\"" + maxNitBrightness + "\"," +
                "\"hasFingerprintResistantOleophobicCoating\":" + fingerprintResistantOleophobicCoating + "," +
                "\"contrastRatio\":\"" + contrastRatio + "\"," +
                "\"hasFullSRGB\":" + fullSRGB + "," +
                "\"hasWideColor\":" + wideColor + "," +
                "\"hasTruetone\":" + truetone + "," +
                "\"hasHDR\":" + hdr + "," +
                "\"hasHDR10\":" + hdr10 + "," +
                "\"hasDolbyVision\":" + dolbyVision + "," +
                "\"taptics\":\"" + taptics + "\"" +
                "}";
    }
}
